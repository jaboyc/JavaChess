package chess;

import chess.pieces.*;
import chess.player.Player;

import java.util.ArrayList;

/**
 * Represents the state of the chess board at one time.
 */
public class Board {
    private Tile[][] tiles; // The matrix of tiles this board has.
    private Chess chess; // The chess game manager attached to this board.

    private ArrayList<Piece> whitePieces; // The list of white pieces on the board.
    private ArrayList<Piece> blackPieces; // The list of white pieces on the board.

    private float whiteScore = -1; // Lazily loaded score for white.
    private float blackScore = -1; // Lazily loaded score for black.

    private boolean considerCastle = true; // Whether this board should consider castling in its possible moveset.

    /**
     * Creates a new board and initializes it.
     *
     * @param chess the chess game manager attached to this board.
     */
    public Board(Chess chess) {
        this(chess, true);
    }

    /**
     * Creates a new board.
     *
     * @param chess      the chess game manager attached to this board.
     * @param initialize whether to initialize this board to its beginning state.
     */
    private Board(Chess chess, boolean initialize) {
        this.chess = chess;

        tiles = new Tile[8][8];
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        generateTiles();

        if (initialize) {
            placePieces();
            setPieceOwners();
        }
    }

    /**
     * @return a copy of this board.
     */
    public Board copy() {
        Board copy = new Board(chess, false);

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                copy.tiles[i][j] = tiles[i][j].copy();

                Piece piece = copy.tiles[i][j].getPiece();
                if (piece != null) {
                    if (piece.isWhite()) {
                        copy.whitePieces.add(piece);
                    } else {
                        copy.blackPieces.add(piece);
                    }
                }
            }
        }
        return copy;
    }

    /**
     * Checks to make sure the given coordinate is valid. If so, returns the tile represented by x and y.
     *
     * @param x the x of the tile to get.
     * @param y the y of the tile to get.
     * @return the tile represented by x and y. Null if out of bounds.
     */
    public Tile get(int x, int y) {
        if (x <= 0 || x > 8 || y <= 0 || y > 8) {
            return null;
        }
        return tiles[y - 1][x - 1];
    }

    /**
     * Returns the piece at the given coordinates.
     *
     * @param x the x of the tile to check.
     * @param y the y of the tile to check.
     * @return the piece at that given coordinate. Null if not found or out of bounds.
     */
    public Piece getPieceAt(int x, int y) {
        Tile tile = get(x, y);
        if (tile == null) return null;
        return tile.getPiece();
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param srcTile  the tile of the piece to move.
     * @param destTile the tile the piece should move to.
     * @return whether the move was applied.
     */
    public boolean movePiece(Tile srcTile, Tile destTile) {
        return movePiece(srcTile, destTile, true);
    }

    /**
     * Moves the piece from srcTile to destTile.
     *
     * @param srcTile    the tile of the piece to move.
     * @param destTile   the tile the piece should move to.
     * @param checkValid check whether the piece is able to move there. If false, moves the piece there without checking.
     * @return whether the move was applied.
     */
    public boolean movePiece(Tile srcTile, Tile destTile, boolean checkValid) {
        // Get the tile from THIS board. Should be optimized!!!
        srcTile = get(srcTile.getX(), srcTile.getY());
        destTile = get(destTile.getX(), destTile.getY());

        Piece piece = srcTile.getPiece();
        if (piece == null) return false;

        // If we want to check whether the move is valid, and the move is invalid for the piece, don't do anything.
        if (checkValid && !piece.isValidMove(this, destTile)){
            return false;
        }

        // Remove the piece from the destination tile.
        removePiece(destTile);

        // Move the piece from the source tile to the destination tile.
        srcTile.setPiece(null);
        destTile.setPiece(piece);
        piece.setTile(destTile);

        // Trigger onMove for the moved piece.
        piece.onMove(this, srcTile, destTile);

        return true;
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
            if (move.getTile().equals(kingSpace)) {
//                System.out.println("CHECK: " + move.getPiece().getTile() + " -> " + move.getTile());
//                System.out.println("===WHAT===" + this);
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
    public ArrayList<Piece> getPieces(Player player) {
        if (player.isWhite()) {
            return whitePieces;
        } else {
            return blackPieces;
        }
    }

    /**
     * Returns the score of the given player.
     * @param player the player to get the score of.
     * @return the score. Does not subtract the score of the enemy.
     */
    public float getScore(Player player){

        // Check if the score is lazily loaded already.
        if(player.isWhite()){
            if(whiteScore != -1) return whiteScore;
        }else{
            if(blackScore != -1) return blackScore;
        }

        // Check for checkmates first.
        if (!getEnemy(player).canMove(this)) {
            return 1000;
        }

        if(!player.canMove(this)){
            return -1000;
        }

        // Calculate total score of each piece.
        float score = 0;
        ArrayList<Piece> pieces = getPieces(player);

        // Add up the scores of all the pieces.
        for(Piece piece : pieces){
            score += piece.getScore(this);
        }

        // Cache the score.
        if(player.isWhite()){
            whiteScore = score;
        }else{
            blackScore = score;
        }

        return score;
    }

    /**
     * Returns the player.
     * @param isWhite whether to get the white or black player.
     * @return the player.
     */
    public Player getPlayer(boolean isWhite){
        return isWhite ? chess.getWhite() : chess.getBlack();
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
     */
    private void removePiece(Tile tile) {
        Piece piece = tile.getPiece();
        if (piece == null) return;

        // Remove the piece from the player associated with it.
        if (piece.isWhite()) {
            whitePieces.remove(piece);
        } else {
            blackPieces.remove(piece);
        }

        // Set the tile's piece to nothing.
        tile.setPiece(null);
    }

    /**
     * Generates the 8x8 grid of tiles.
     */
    private void generateTiles() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tiles[i][j] = new Tile(j + 1, i + 1);
            }
        }
    }

    /**
     * Places the pieces in the correct starting positions.
     */
    private void placePieces() {

        // Place white's pieces.
        tiles[0][0].setPiece(new Rook(true, tiles[0][0]));
        tiles[0][1].setPiece(new Knight(true, tiles[0][1]));
        tiles[0][2].setPiece(new Bishop(true, tiles[0][2]));
        tiles[0][3].setPiece(new Queen(true, tiles[0][3]));
        tiles[0][4].setPiece(new King(true, tiles[0][4]));
        tiles[0][5].setPiece(new Bishop(true, tiles[0][5]));
        tiles[0][6].setPiece(new Knight(true, tiles[0][6]));
        tiles[0][7].setPiece(new Rook(true, tiles[0][7]));
        for (int i = 0; i < 8; i++) {
            tiles[1][i].setPiece(new Pawn(true, tiles[1][i]));
        }

        // Place black's pieces.
        tiles[7][0].setPiece(new Rook(false, tiles[7][0]));
        tiles[7][1].setPiece(new Knight(false, tiles[7][1]));
        tiles[7][2].setPiece(new Bishop(false, tiles[7][2]));
        tiles[7][3].setPiece(new Queen(false, tiles[7][3]));
        tiles[7][4].setPiece(new King(false, tiles[7][4]));
        tiles[7][5].setPiece(new Bishop(false, tiles[7][5]));
        tiles[7][6].setPiece(new Knight(false, tiles[7][6]));
        tiles[7][7].setPiece(new Rook(false, tiles[7][7]));
        for (int i = 0; i < 8; i++) {
            tiles[6][i].setPiece(new Pawn(false, tiles[6][i]));
        }
    }

    /**
     * Set the owners of the placed pieces to the correct player.
     */
    private void setPieceOwners() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 8; j++) {
                whitePieces.add(tiles[i][j].getPiece());
            }
        }

        for (int i = 6; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                blackPieces.add(tiles[i][j].getPiece());
            }
        }
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
        for (int i = 7; i >= 0; i--) {

            // Add the row coordinates.
            output.append(i + 1);

            // Add the chess boundary.
            output.append(" *");

            // Add each tile in the row.
            for (int j = 0; j < 8; j++) {
                output.append(tiles[i][j].formattedForBoard());
            }

            // Add the chess boundary.
            output.append("|* ");

            // Add the row coordinates.
            output.append(i + 1);

            // Add a new line.
            output.append("\n");
        }

        // Add the board boundary.
        output.append("  ***********************************\n");

        // Add the col coordinates.
        output.append("     A   B   C   D   E   F   G   H\n");

        return output.toString();
    }

    public boolean shouldConsiderCastle() {
        return considerCastle;
    }

    public void setConsiderCastle(boolean considerCastle) {
        this.considerCastle = considerCastle;
    }
}
