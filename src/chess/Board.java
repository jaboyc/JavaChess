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

        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        pieceGrid = new Piece[8][8];

        history = new Stack<>();

        placePieces();
    }

    /**
     * @param tile the tile to get the piece of.
     * @return the piece represented on the tile. Null if not found.
     */
    public Piece get(Tile tile) {
        return pieceGrid[tile.getX()-1][tile.getY()-1];
    }

    /**
     * @param x the x of the piece to look for.
     * @param y the y of the piece to look for.
     * @return the piece represented by the coordinate. Null if not found.
     */
    public Piece get(int x, int y) {
        return pieceGrid[x-1][y-1];
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param move the move to perform.
     * @return whether the move was applied.
     */
    public boolean movePiece(Move move) {
        return movePiece(move, true, true);
    }

    /**
     * Moves the piece from srcTile to destTile.
     * @param move the move to perform.
     * @param checkValid whether to check if the move is valid.
     * @return whether the move was applied.
     */
    public boolean movePiece(Move move, boolean checkValid){
        return movePiece(move, checkValid, true);
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param move the move to perform.
     * @param checkValid check whether the piece is able to move there. If false, moves the piece there without checking.
     * @return whether the move was applied.
     */
    public boolean movePiece(Move move, boolean checkValid, boolean addToHistory) {

        // Get the piece to move.
        Piece piece = get(move.getSource());
        if (piece == null) {
            return false;
        }

        // If we want to check whether the move is valid, and the move is invalid for the piece, don't do anything.
        if (checkValid && !piece.isValidMove(move.getDestination())) {
            return false;
        }

        // Remove the piece in the destination tile.
        move.setRemovedPiece(removePiece(move.getDestination()));

        // Move the piece from the source tile to the destination tile.
        pieceGrid[piece.getTile().getX()-1][piece.getTile().getY()-1] = null;
        piece.moveTo(move.getDestination());
        pieceGrid[piece.getTile().getX()-1][piece.getTile().getY()-1] = piece;

        // Trigger onMove for the moved piece.
        piece.onMove(this, move);

        // Add the move to history.
        if(addToHistory) history.push(move);

        // Clear the caches.
        clearPiecesCache();
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

        // If the last move was a pawn promotion, undo the promotion by removing the queen and placing a pawn.
        if(move.getPromotedPawn() != null){
            Pawn promotedPawn = move.getPromotedPawn();

            // Remove the queen from the current tile.
            removePiece(piece.getTile());

            // Add the pawn back.
            addPiece(promotedPawn);
            piece = promotedPawn;
        }

        // Move the piece from the source tile to the destination tile.
        pieceGrid[piece.getTile().getX()-1][piece.getTile().getY()-1] = null;
        piece.moveTo(move.getSource());
        pieceGrid[piece.getTile().getX() - 1][piece.getTile().getY()-1] = piece;

        // If we removed a piece, place it back.
        if(move.getRemovedPiece() != null){
            addPiece(move.getRemovedPiece());
        }

        // Trigger onMove for the moved piece.
        piece.onUnMove(this, move);

        // Clear the caches.
        clearPiecesCache();
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
        if(isWhite){
           return whitePieces;
        }else{
            return blackPieces;
        }
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
        if (getEnemy(player).getKing(this) == null || !getEnemy(player).canMove(this)) {
            return 1000;
        }

        if (player.getKing(this) == null || !player.canMove(this)) {
            return -1000;
        }

        // Calculate total score of each piece.
        double score = 0;

        List<Piece> pieces = getPieces(player);
        for(int i=0;i<pieces.size();i++){
            Piece piece = pieces.get(i);
            score += piece.getScore();
        }

        score -= 1000;

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

        if(piece.isWhite()){
            whitePieces.remove(piece);
        }else{
            blackPieces.remove(piece);
        }
        pieceGrid[piece.getTile().getX()-1][piece.getTile().getY() - 1] = null;

        clearPiecesCache();

        return piece;
    }

    /**
     * Adds the piece to the board.
     *
     * @param piece the piece to add.
     */
    public void addPiece(Piece piece) {
        if(piece.isWhite()){
            whitePieces.add(piece);
        }else{
            blackPieces.add(piece);
        }
        pieceGrid[piece.getTile().getX() - 1][piece.getTile().getY() - 1] = piece;

        clearPiecesCache();
    }

    /**
     * Places the pieces in the correct starting positions.
     */
    private void placePieces() {

        // Place white's pieces.
        addPiece(new Rook(this, true, Tile.pos(1, 1)));
        addPiece(new Knight(this, true, Tile.pos(2, 1)));
        addPiece(new Bishop(this, true, Tile.pos(3, 1)));
        addPiece(new Queen(this, true, Tile.pos(4, 1)));
        addPiece(new King(this, true, Tile.pos(5, 1)));
        addPiece(new Bishop(this, true, Tile.pos(6, 1)));
        addPiece(new Knight(this, true, Tile.pos(7, 1)));
        addPiece(new Rook(this, true, Tile.pos(8, 1)));
        for (int i = 0; i < 8; i++) {
            addPiece(new Pawn(this, true, Tile.pos(i + 1, 2)));
        }

        // Place black's pieces.
        addPiece(new Rook(this, false, Tile.pos(1, 8)));
        addPiece(new Knight(this, false, Tile.pos(2, 8)));
        addPiece(new Bishop(this, false, Tile.pos(3, 8)));
        addPiece(new Queen(this, false, Tile.pos(4, 8)));
        addPiece(new King(this, false, Tile.pos(5, 8)));
        addPiece(new Bishop(this, false, Tile.pos(6, 8)));
        addPiece(new Knight(this, false, Tile.pos(7, 8)));
        addPiece(new Rook(this, false, Tile.pos(8, 8)));
        for (int i = 0; i < 8; i++) {
            addPiece(new Pawn(this, false, Tile.pos(i + 1, 7)));
        }
    }

    /**
     * Clears the caches of all the pieces.
     */
    private void clearPiecesCache(){
        for(Piece piece : whitePieces){
            piece.clearPossibleMovesCache();
        }
        for(Piece piece : blackPieces){
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
