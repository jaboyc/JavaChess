package chess;

import chess.pieces.*;
import chess.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Represents the state of the chess board at one time.
 */
public class Board {
    private Chess chess; // The chess game manager attached to this board.

    private ArrayList<Piece> pieces; // List of pieces on the board.

    private ArrayList<Piece> whitePieces; // Cached white pieces.
    private ArrayList<Piece> blackPieces; // Cached black pieces.

    private Piece[][] pieceGrid; // Cached grid of pieces.

    private Stack<Move> history; // History of all the moves applied to this board.

    private double whiteScore = -1; // Cached score for white.
    private double blackScore = -1; // Cached loaded score for black.

    private boolean considerCastle = true; // Whether this board should consider castling in its possible moveset.

    /**
     * Creates a new board.
     *
     * @param chess      the chess game manager attached to this board.
     */
    public Board(Chess chess) {
        this.chess = chess;

        pieces = new ArrayList<>();
        history = new Stack<>();

        placePieces();
        constructGrid();
    }

    /**
     * @param tile the tile to get the piece of.
     * @return the piece represented on the tile. Null if not found.
     */
    public Piece get(Tile tile) {
        if(pieceGrid == null) constructGrid();
        return pieceGrid[tile.getX()-1][tile.getY()-1];
    }

    /**
     * @param x the x of the piece to look for.
     * @param y the y of the piece to look for.
     * @return the piece represented by the coordinate. Null if not found.
     */
    public Piece get(int x, int y) {
        if(pieceGrid == null) constructGrid();
        return pieceGrid[x-1][y-1];
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param move the move to perform.
     * @return whether the move was applied.
     */
    public boolean movePiece(Move move) {
        return movePiece(move, true);
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param move the move to perform.
     * @param checkValid check whether the piece is able to move there. If false, moves the piece there without checking.
     * @return whether the move was applied.
     */
    public boolean movePiece(Move move, boolean checkValid) {

        // Get the piece to move.
        Piece piece = get(move.getSource());
        if (piece == null) {
            System.out.println(this);
            System.out.println("Attempted " + move);
            System.out.println("Previous moves: " + history);
            System.out.println("CheckValid? " + checkValid);
            System.out.println("Something went wrong");
            throw new Error();
//            return false;
        }

        // If we want to check whether the move is valid, and the move is invalid for the piece, don't do anything.
        if (checkValid && !piece.isValidMove(move.getDestination())) {
            return false;
        }

        // Remove the piece in the destination tile.
        move.setRemovedPiece(removePiece(move.getDestination()));

        // Move the piece from the source tile to the destination tile.
        piece.moveTo(move.getDestination());

        // Trigger onMove for the moved piece.
        piece.onMove(this, move);

        // Add the move to history.
        history.push(move);

        // Clear the caches.
        clearGridCache();
        clearPiecesCache();
        clearPlayerPiecesCache();
        clearScoreCache();

        return true;
    }

    /**
     * Sets the state of the board to the last move.
     */
    public void undoMove(){

        // Get the last move.
        Move move = history.pop();

        // Get the piece that was moved at that spot.
        Piece piece = get(move.getDestination());

        // Move the piece from the source tile to the destination tile.
        piece.moveTo(move.getSource());

        // If we removed a piece, place it back.
        if(move.getRemovedPiece() != null){
            addPiece(move.getRemovedPiece());
        }

        // Trigger onMove for the moved piece.
        piece.onUnMove(this, move);

        // Clear the caches.
        clearGridCache();
        clearPiecesCache();
        clearPlayerPiecesCache();
        clearScoreCache();
    }

    /**
     * Returns whether the given player is in check or not.
     *
     * @param player the player to check for.
     * @return whether they are in check.
     */
    public boolean inCheck(Player player) {
        //Get the king first.
        Tile kingSpace = player.getKing(this).getTile();

        // Get the enemy player.
        Player enemy = player == chess.getWhite() ? chess.getBlack() : chess.getWhite();

        // Check if any of the enemy's possible moves can attack the king.
        for (Move move : enemy.getPossibleMoves(this, false)) {
            if (move.getDestination().equals(kingSpace)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the given player is in check.
     *
     * @param isWhite whether to check for the white or black player.
     * @return whether the player is in check.
     */
    public boolean inCheck(boolean isWhite) {
        return inCheck(isWhite ? chess.getWhite() : chess.getBlack());
    }

    /**
     * @param player the player to get the pieces of from the board.
     * @return the list of pieces that player has.
     */
    public List<Piece> getPieces(Player player) {
        return getPieces(player.isWhite());
    }

    /**
     * @param isWhite whether to look for the white or black pieces.
     * @return the list of pieces that player has.
     */
    public List<Piece> getPieces(boolean isWhite) {
        // Check if cached pieces exist.
        if(isWhite){
            if(whitePieces != null) return whitePieces;
        }else{
            if(blackPieces != null) return blackPieces;
        }

        ArrayList<Piece> output = new ArrayList<>();
        for(Piece piece : pieces){
            if(piece.isWhite() == isWhite) output.add(piece);
        }

        // Save to cache.
        if(isWhite){
            whitePieces = output;
        }else{
            blackPieces = output;
        }

        return output;
    }

    /**
     * Returns the score of the given player.
     *
     * @param player the player to get the score of.
     * @return the score. Does not subtract the score of the enemy.
     */
    public double getScore(Player player) {

        // Check if the score is lazily loaded already.
        if (player.isWhite()) {
            if (whiteScore != -1) return whiteScore;
        } else {
            if (blackScore != -1) return blackScore;
        }

        // Check for checkmates first.
        if (!getEnemy(player).canMove(this)) {
            return 1000;
        }

        if (!player.canMove(this)) {
            return -1000;
        }

        // Calculate total score of each piece.
        double score = 0;
        for(Piece piece : getPieces(player)){
            score += piece.getScore();
        }

        // Cache the score.
        if (player.isWhite()) {
            whiteScore = score;
        } else {
            blackScore = score;
        }

        return score;
    }

    /**
     * @param player the player to use.
     * @return the enemy of the player.
     */
    public Player getEnemy(Player player) {
        return player == chess.getWhite() ? chess.getBlack() : chess.getWhite();
    }

    /**
     * Removes the piece from the given tile if one exists.
     *
     * @param tile the tile to remove the piece from.
     * @return the piece it removed. Null if none.
     */
    public Piece removePiece(Tile tile) {
        Piece piece = get(tile);
        if (piece == null) return null;

        pieces.remove(piece);

        clearGridCache();
        clearPlayerPiecesCache();
        clearPiecesCache();

        return piece;
    }

    /**
     * Adds the piece to the board.
     *
     * @param piece the piece to add.
     */
    public void addPiece(Piece piece) {
        pieces.add(piece);

        clearGridCache();
        clearPlayerPiecesCache();
        clearPiecesCache();
    }

    /**
     * Places the pieces in the correct starting positions.
     */
    private void placePieces() {

        // Place white's pieces.
        pieces.add(new Rook(this, true, Tile.pos(1, 1)));
        pieces.add(new Knight(this, true, Tile.pos(2, 1)));
        pieces.add(new Bishop(this, true, Tile.pos(3, 1)));
        pieces.add(new Queen(this, true, Tile.pos(4, 1)));
        pieces.add(new King(this, true, Tile.pos(5, 1)));
        pieces.add(new Bishop(this, true, Tile.pos(6, 1)));
        pieces.add(new Knight(this, true, Tile.pos(7, 1)));
        pieces.add(new Rook(this, true, Tile.pos(8, 1)));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(this, true, Tile.pos(i + 1, 2)));
        }

        // Place black's pieces.
        pieces.add(new Rook(this, false, Tile.pos(1, 8)));
        pieces.add(new Knight(this, false, Tile.pos(2, 8)));
        pieces.add(new Bishop(this, false, Tile.pos(3, 8)));
        pieces.add(new Queen(this, false, Tile.pos(4, 8)));
        pieces.add(new King(this, false, Tile.pos(5, 8)));
        pieces.add(new Bishop(this, false, Tile.pos(6, 8)));
        pieces.add(new Knight(this, false, Tile.pos(7, 8)));
        pieces.add(new Rook(this, false, Tile.pos(8, 8)));
        for (int i = 0; i < 8; i++) {
            pieces.add(new Pawn(this, false, Tile.pos(i + 1, 7)));
        }
    }

    /**
     * Constructs the grid of pieces based on the current layout.
     */
    private void constructGrid(){
        pieceGrid = new Piece[8][8];

        for(Piece piece : pieces){
            if(piece == null){
                System.out.println(pieces);
            }
            pieceGrid[piece.getTile().getX()-1][piece.getTile().getY()-1] = piece;
        }
    }

    /**
     * Cleans the grid cache.
     */
    private void clearGridCache(){
        pieceGrid = null;
    }

    /**
     * Cleans the players' piece cache.
     */
    private void clearPlayerPiecesCache(){
        whitePieces = null;
        blackPieces = null;
    }

    /**
     * Clears the caches of all the pieces.
     */
    private void clearPiecesCache(){
        for(Piece piece : pieces){
            piece.clearPossibleMovesCache();
        }
    }

    /**
     * Clears the caches of the scores.
     */
    private void clearScoreCache(){
        whiteScore = -1;
        blackScore = -1;
    }

    /**
     * Formats the board to be printed.
     *
     * @return the formatted board string.
     */
    public String toString() {
        StringBuilder output = new StringBuilder();

        // Add the col coordinates.
        output.append("     A   B   C   D   E   F   G   H\n");

        // Add the board boundary.
        output.append("  ***********************************\n");

        // Go through the rows, starting with the top one first.
        for (int i = 8; i >= 1; i--) {

            // Add the row coordinates.
            output.append(i);

            // Add the chess boundary.
            output.append(" *");

            // Add each tile in the row.
            for (int j = 1; j <= 8; j++) {
                Piece piece = get(j, i);
                if (piece == null) {
                    output.append("|   ");
                } else if (piece.isWhite()) {
                    output.append("| ").append(piece.getInitial()).append(" ");
                } else {
                    output.append("|(").append(piece.getInitial()).append(")");
                }
            }

            // Add the chess boundary.
            output.append("|* ");

            // Add the row coordinates.
            output.append(i);

            // Add a new line.
            output.append("\n");
        }

        // Add the board boundary.
        output.append("  ***********************************\n");

        // Add the col coordinates.
        output.append("     A   B   C   D   E   F   G   H\n");

        // Add scores
        output.append("(").append(String.format("%.1f", getScore(chess.getWhite()))).append(") [");

        int pointsCount = Math.max((int) (getScore(chess.getWhite())/(getScore(chess.getWhite()) + getScore(chess.getBlack())) * 20), 0);
        for(int i=0;i<pointsCount;i++){
            if(i == 10){
                output.append("|");
                continue;
            }
            output.append("X");
        }
        for(int i=pointsCount;i<=20;i++){
            if(i == 10){
                output.append("|");
                continue;
            }
            output.append(".");
        }

        output.append("] (").append(String.format("%.1f", getScore(chess.getBlack()))).append(")");

        return output.toString();
    }

    public boolean shouldConsiderCastle() {
        return considerCastle;
    }

    public void setConsiderCastle(boolean considerCastle) {
        this.considerCastle = considerCastle;
    }


}
