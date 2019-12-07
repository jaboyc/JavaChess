package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a piece on the board.
 */
public abstract class Piece {

    public static final double MOBILITY_MULTIPLIER = 0.05; // Multiplier for the mobility of the piece.

    private Tile tile; // The tile this piece is on.
    private boolean isWhite; // Whether this piece is white or black.
    private int moves; // The number of moves this piece has done throughout the game.
    private Tile[] prevTiles; // The previous 6 tiles this piece was on.
    private Board board; // The board this piece is on.

    private List<Move> possibleMoves; // Lazily loaded list of possible moves. ONLY if checked for check.

    /**
     * Creates a piece.
     *
     * @param board   the board this piece is on.
     * @param isWhite whether the piece is white or black.
     * @param tile    the tile this piece is on.
     */
    public Piece(Board board, boolean isWhite, Tile tile) {
        this.board = board;
        this.isWhite = isWhite;
        this.tile = tile;

        prevTiles = new Tile[6];

        moves = 0;
    }

    /**
     * @return the initial this piece will use to be represented on the board.
     */
    public abstract String getInitial();

    /**
     * @return the score of this piece.
     */
    public abstract double getValue();

    /**
     * Scans through the board and returns a list of all the possible tiles this piece could go on in the next turn. It does not account for putting the king in check.
     *
     * @return the list of tiles it can move in the next turn.
     */
    protected abstract List<Move> getPossibleLocations();

    /**
     * Converts the list of tiles this piece can move in into a list of Moves.
     *
     * @param checkForCheck whether to consider putting the player's king in check.
     * @return the list of moves.
     */
    public List<Move> getPossibleMoves(boolean checkForCheck) {

        if(checkForCheck && possibleMoves != null) return possibleMoves;

        ArrayList<Move> output = new ArrayList<>();
        for (Move move: getPossibleLocations()) {

            // Don't consider this move if the tile is stale.
            if (isStale(move.getDestination())) {
                continue;
            }

            // If we need to check for check, add only the moves that do not result in a check in the future.
            if (checkForCheck) {
                board.movePiece(move, false);
                if (!board.inCheck(isWhite)) {
                    output.add(move);
                }
                board.undoMove();
            } else {
                // Otherwise add the move blindly.
                output.add(move);
            }
        }

        if(checkForCheck) possibleMoves = output;

        return output;
    }

    /**
     * Moves the piece to the given tile.
     *
     * @param tile the tile to move to.
     */
    public void moveTo(Tile tile) {
        this.tile = tile;
    }

    /**
     * @param tile the tile to check.
     * @return whether the piece has been at this tile 3 times in the last 5 moves.
     */
    private boolean isStale(Tile tile) {
        if (tile == null) return false;

        int count = 0;
        for (Tile prev : prevTiles) {
            if (tile.equals(prev)) {
                count++;
            }
        }
        return count == 3;
    }

    /**
     * Returns whether the tile can move to the given destination tile (based on getPossibleLocations()). Considers check.
     *
     * @param dest the destination tile to check for.
     * @return whether it can move there or not.
     */
    public boolean isValidMove(Tile dest) {
        for (Move move : getPossibleMoves(true)) {
            if (move.getDestination().equals(dest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the score of this piece.
     */
    public double getScore() {

        double score = getValue();

        score += getMobility(false) * MOBILITY_MULTIPLIER;

        score += getBonusScore(board);

        return score;
    }

    /**
     * A method that children can override to increase their score.
     *
     * @return bonus score of the piece.
     */
    protected double getBonusScore(Board board) {
        return 0;
    }

    /**
     * Returns the score associated with the piece table. It accounts for blacks being inverted.
     * @param pieceTable the piece table to use.
     * @return the points.
     */
    protected double getPieceSquareTableScore(double[][] pieceTable){
        if(isWhite){
            return pieceTable[8-tile.getY()][tile.getX()-1];
        }else{
            return pieceTable[tile.getY()-1][8-tile.getX()];
        }
    }

    /**
     * Called every time the piece is moved. Can be overridden for further functionality.
     *
     * @param board the board this piece moved in.
     * @param move  the move that was performed.
     */
    public void onMove(Board board, Move move) {
        moves++;

        // Add this to the stale tiles array.
        System.arraycopy(prevTiles, 1, prevTiles, 0, 5);

        prevTiles[5] = move.getDestination();
    }

    /**
     * Called every time the piece is un-moved. Can be overridden for further functionality.
     * @param board the board this piece moved in.
     * @param move the move that was performed.
     */
    public void onUnMove(Board board, Move move){
        moves--;

        // Remove the move from the stale tiles array.
        System.arraycopy(prevTiles, 0, prevTiles, 1, 5);

        prevTiles[0] = move.getSource();
    }

    /**
     * Returns a move that captures a pawn.
     * @param tile the tile it will capture on.
     * @return the move.
     */
    protected Move capture(Tile tile){
        return new Move(this.tile, tile, true);
    }

    /**
     * Returns a move that goes to a tile.
     * @param tile the tile it will move to.
     * @return the move.
     */
    protected Move move(Tile tile){
        return new Move(this.tile, tile, false);
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile has no pieces on it.
     */
    public boolean isEmpty(Tile tile) {
        if (tile == null) return false;
        return board.get(tile) == null;
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile contains an enemy piece.
     */
    protected boolean containsEnemyPiece(Tile tile) {
        if (tile == null) return false;
        if (board.get(tile) == null) return false;
        return isWhite != board.get(tile).isWhite;
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile contains an ally piece.
     */
    protected boolean containsAllyPiece(Tile tile) {
        if (tile == null) return false;
        if (board.get(tile) == null) return false;
        return isWhite == board.get(tile).isWhite;
    }

    /**
     * Considers whether the piece is white or black, and returns the tile from the given offset from the piece.
     *
     * @param stepsX the steps to the right to check.
     * @param stepsY the steps to the left to check.
     * @return the tile from the given offset or null if out of bounds.
     */
    protected Tile getOffset(int stepsX, int stepsY) {
        if (isWhite) {
            return Tile.pos(tile.getX() + stepsX, tile.getY() + stepsY);
        } else {
            return Tile.pos(tile.getX() - stepsX, tile.getY() - stepsY);
        }
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is forward the amount of steps given.
     */
    protected Tile getForward(int steps) {
        return getOffset(0, steps);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is to the right the amount of steps given.
     */
    protected Tile getRight(int steps) {
        return getOffset(steps, 0);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is to the left the amount of steps given.
     */
    protected Tile getLeft(int steps) {
        return getOffset(-steps, 0);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is behind the amount of steps given.
     */
    protected Tile getBackward(int steps) {
        return getOffset(0, -steps);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is north-east the amount of steps given.
     */
    public Tile getNEDiagonal(int steps) {
        return getOffset(steps, steps);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is north-west the amount of steps given.
     */
    protected Tile getNWDiagonal(int steps) {
        return getOffset(-steps, steps);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is south-east the amount of steps given.
     */
    protected Tile getSEDiagonal(int steps) {
        return getOffset(steps, -steps);
    }

    /**
     * @param steps the number of steps to check from.
     * @return the tile that is south-west the amount of steps given.
     */
    public Tile getSWDiagonal(int steps) {
        return getOffset(-steps, -steps);
    }

    /**
     * Returns whether the player this piece belongs to is currently in check or not.
     *
     * @param board the board to check from.
     * @return whether the player is in check.
     */
    protected boolean inCheck(Board board) {
        return board.inCheck(isWhite);
    }

    /**
     * @return whether this piece has moved or not.
     */
    public boolean hasMoved() {
        return moves > 0;
    }

    /**
     * Returns the number of moves this piece can go to.
     * @param checkForCheck whether to check for check in its possible moves.
     * @return the mobility of the piece.
     */
    private int getMobility(boolean checkForCheck){
        return getPossibleMoves(checkForCheck).size();
    }

    /**
     * Clears the cache of the possible moves for this piece.
     */
    public void clearPossibleMovesCache() {
        possibleMoves = null;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public int getMoves() {
        return moves;
    }

    public Board getBoard() {
        return board;
    }
}
