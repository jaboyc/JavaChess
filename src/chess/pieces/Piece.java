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

    public static final float MOVE_VALUE = 0.06f; // Multiplier for the value of a piece for the ability to move.
    public static final float PROTECT_VALUE = 0.4f; // Multiplier for the value of a piece when protecting it.
    public static final float ATTACK_VALUE = 0.25f; // Multiplier for the value of a piece for the ability to attack an enemy piece.

    private Tile tile; // The tile this piece is on.
    private boolean isWhite; // Whether this piece is white or black.
    private int moves; // The number of moves this piece has done throughout the game.
    private Tile[] prevTiles; // The previous 6 tiles this piece was on.

    private float score = -1; // Lazily loaded score.
    public List<Move> possibleMoves; // Lazily loaded list of possible moves.

    /**
     * Creates a piece.
     *
     * @param isWhite whether the piece is white or black.
     * @param tile    the tile this piece is on.
     */
    public Piece(boolean isWhite, Tile tile) {
        this.isWhite = isWhite;
        this.tile = tile;
        prevTiles = new Tile[6];

        moves = 0;
    }

    /**
     * @param tile the tile this piece is now on.
     * @return a copy of the piece.
     */
    public Piece copy(Tile tile) {
        Piece copy = getCopy();
        copy.tile = tile;
        copy.moves = moves;

        for (int i = 0; i < 6; i++) {
            copy.prevTiles[i] = prevTiles[i];
        }

        return copy;
    }

    /**
     * @return the initial this piece will use to be represented on the board.
     */
    public abstract String getInitial();

    /**
     * @return a new copy of the concrete piece.
     */
    protected abstract Piece getCopy();

    /**
     * @return the score of this piece.
     */
    public abstract float getValue();

    /**
     * Scans through the board and returns a list of all the possible tiles this piece could go on in the next turn. It does not account for putting the king in check.
     *
     * @param board the board to use to scan.
     * @return the list of tiles it can move in the next turn.
     */
    protected abstract List<Tile> getPossibleLocations(Board board);

    /**
     * Converts the list of tiles this piece can move in into a list of Moves.
     *
     * @param board         the board needed to scan for possible locations.
     * @param checkForCheck whether to consider putting the player's king in check.
     * @return the list of moves.
     */
    public List<Move> getPossibleMoves(Board board, boolean checkForCheck) {

        possibleMoves = new ArrayList<>();
        for (Tile tile : getPossibleLocations(board)) {

            // Don't consider this move if the tile is stale.
            if (isStale(tile)) {
                continue;
            }

            // If we need to check for check, add only the moves that do not result in a check in the future.
            if (checkForCheck) {
                Board future = board.copy();
                future.movePiece(future.get(getTile().getX(), getTile().getY()), future.get(tile.getX(), tile.getY()), false);
                if (!future.inCheck(isWhite)) {
                    possibleMoves.add(new Move(this, tile));
                }
            } else {
                // Otherwise add the move blindly.
                possibleMoves.add(new Move(this, tile));
            }

        }
        return possibleMoves;
    }

    /**
     * @param tile the tile to check.
     * @return whether the piece has been at this tile 3 times in the last 5 moves.
     */
    private boolean isStale(Tile tile) {
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
     * @param board the board to use to scan.
     * @param dest  the destination tile to check for.
     * @return whether it can move there or not.
     */
    public boolean isValidMove(Board board, Tile dest) {
        for (Move move : getPossibleMoves(board, true)) {
            if (move.getTile().equals(dest)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param board the board to use.
     * @return the score of this piece.
     */
    public float getScore(Board board) {

        score = getValue();

        for (Move move : getPossibleMoves(board, true)) {
            if (isEmpty(move.getTile())) {
                score += MOVE_VALUE;
            } else if (containsEnemyPiece(move.getTile())) {
                score += move.getTile().getPiece().getValue() * ATTACK_VALUE;
            } else if (containsAllyPiece(move.getTile())) {
                score += move.getTile().getPiece().getValue() * PROTECT_VALUE;
            }
        }

        score += getBonusScore(board);

        return score;
    }

    /**
     * A method that children can override to increase their score.
     *
     * @return
     */
    protected float getBonusScore(Board board) {
        return 0;
    }

    /**
     * Called every time the piece is moved. Can be overridden for further functionality.
     *
     * @param board   the board this piece moved in.
     * @param oldTile the tile the piece used to be in.
     * @param newTile the tile the piece is now in.
     */
    public void onMove(Board board, Tile oldTile, Tile newTile) {
        moves++;

        // Add this to the stale tiles array.
        for (int i = 0; i < 5; i++) {
            prevTiles[i] = prevTiles[i + 1];
        }
        prevTiles[5] = newTile;
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile has no pieces on it.
     */
    public boolean isEmpty(Tile tile) {
        if (tile == null) return false;
        return tile.getPiece() == null;
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile contains an enemy piece.
     */
    protected boolean containsEnemyPiece(Tile tile) {
        if (tile == null) return false;
        if (tile.getPiece() == null) return false;
        return isWhite != tile.getPiece().isWhite;
    }

    /**
     * @param tile the tile to check.
     * @return whether the tile contains an ally piece.
     */
    protected boolean containsAllyPiece(Tile tile) {
        if (tile == null) return false;
        if (tile.getPiece() == null) return false;
        return isWhite == tile.getPiece().isWhite;
    }

    /**
     * Considers whether the piece is white or black, and returns the tile from the given offset from the piece.
     *
     * @param board  the board to check.
     * @param stepsX the steps to the right to check.
     * @param stepsY the steps to the left to check.
     * @return the tile from the given offset or null if out of bounds.
     */
    protected Tile getOffset(Board board, int stepsX, int stepsY) {
        if (isWhite) {
            return board.get(tile.getX() + stepsX, tile.getY() + stepsY);
        } else {
            return board.get(tile.getX() - stepsX, tile.getY() - stepsY);
        }
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is forward the amount of steps given.
     */
    protected Tile getForward(Board board, int steps) {
        return getOffset(board, 0, steps);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is to the right the amount of steps given.
     */
    protected Tile getRight(Board board, int steps) {
        return getOffset(board, steps, 0);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is to the left the amount of steps given.
     */
    protected Tile getLeft(Board board, int steps) {
        return getOffset(board, -steps, 0);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is behind the amount of steps given.
     */
    protected Tile getBackward(Board board, int steps) {
        return getOffset(board, 0, -steps);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is north-east the amount of steps given.
     */
    public Tile getNEDiagonal(Board board, int steps) {
        return getOffset(board, steps, steps);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is north-west the amount of steps given.
     */
    protected Tile getNWDiagonal(Board board, int steps) {
        return getOffset(board, -steps, steps);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is south-east the amount of steps given.
     */
    protected Tile getSEDiagonal(Board board, int steps) {
        return getOffset(board, steps, -steps);
    }

    /**
     * @param board the board to check.
     * @param steps the number of steps to check from.
     * @return the tile that is south-west the amount of steps given.
     */
    public Tile getSWDiagonal(Board board, int steps) {
        return getOffset(board, -steps, -steps);
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
}
