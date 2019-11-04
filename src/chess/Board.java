package chess;

import chess.pieces.*;
import chess.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents the state of the chess board at one time.
 */
public class Board {
    private Chess chess; // The chess game manager attached to this board.

    private ArrayList<Piece> pieces; // List of pieces on the board.

    private double whiteScore = -1; // Lazily loaded score for white.
    private double blackScore = -1; // Lazily loaded score for black.

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

        pieces = new ArrayList<>();

        if (initialize) {
            placePieces();
        }
    }

    /**
     * @return a copy of this board.
     */
    public Board copy() {
        Board copy = new Board(chess, false);

        for (Piece piece : pieces) {
            copy.pieces.add(piece.copy(copy));
        }
//        copy.pieces.addAll(pieces.stream().map(e->e.copy(copy)).collect(Collectors.toList()));

        return copy;
    }

    /**
     * @param tile the tile to get the piece of.
     * @return the piece represented on the tile. Null if not found.
     */
    public Piece get(Tile tile) {
        for (Piece piece : pieces) {
            if (piece.getTile().equals(tile)) {
                return piece;
            }
        }
        return null;
//        return pieces.stream().filter(e->e.getTile().equals(tile)).findFirst().orElse(null);
    }

    /**
     * @param x the x of the piece to look for.
     * @param y the y of the piece to look for.
     * @return the piece represented by the coordinate. Null if not found.
     */
    public Piece get(int x, int y) {
        for (Piece piece : pieces) {
            if (piece.getTile().getX() == x && piece.getTile().getY() == y) {
                return piece;
            }
        }
        return null;
//        return pieces.stream().filter(e->e.getTile().getX() == x && e.getTile().getY() == y).findFirst().orElse(null);
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

        Piece piece = get(srcTile);
        if (piece == null) return false;

        // If we want to check whether the move is valid, and the move is invalid for the piece, don't do anything.
        if (checkValid && !piece.isValidMove(destTile)) {
            return false;
        }

        // Remove the piece in the destination tile.
        removePiece(destTile);

        // Move the piece from the source tile to the destination tile.
        piece.moveTo(destTile);

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
        return pieces.stream().filter(e -> e.isWhite() == isWhite).collect(Collectors.toList());
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
        double score = getPieces(player).stream().mapToDouble(e -> e.getScore()).sum();

        // Cache the score.
        if (player.isWhite()) {
            whiteScore = score;
        } else {
            blackScore = score;
        }

        return score;
    }

    /**
     * Returns the player.
     *
     * @param isWhite whether to get the white or black player.
     * @return the player.
     */
    public Player getPlayer(boolean isWhite) {
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
    public void removePiece(Tile tile) {
        Piece piece = get(tile);
        if (piece == null) return;

        pieces.remove(piece);
    }

    /**
     * Adds the piece to the board.
     *
     * @param piece the piece to add.
     */
    public void addPiece(Piece piece) {
        pieces.add(piece);
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

        return output.toString();
    }

    public boolean shouldConsiderCastle() {
        return considerCastle;
    }

    public void setConsiderCastle(boolean considerCastle) {
        this.considerCastle = considerCastle;
    }
}
