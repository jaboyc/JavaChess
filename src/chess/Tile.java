package chess;

import chess.pieces.Piece;

/**
 * Represents a tile on the board.
 */
public class Tile {
    private int x; // The x of the tile. (1-8)
    private int y; // The y of the tile. (1-8)

    private Piece piece; // The piece on the tile.

    /**
     * Creates a new tile.
     *
     * @param x the x of the tile. (1-8)
     * @param y the y of the tile. (1-8)
     */
    public Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return a getCopy of this tile.
     */
    public Tile copy() {
        Tile copy = new Tile(x, y);
        if (piece != null) {
            copy.piece = piece.copy(copy);
        }

        return copy;
    }

    /**
     * @return a formatted version of this tile with the piece initial on it.
     */
    public String toString() {
        return getPosition();
    }

    /**
     * @return the tile formatted to fit in the chess board.
     */
    public String formattedForBoard() {
        // If there is no piece on it, return the left divider with nothing in it.
        if (piece == null) {
            return "|   ";
        } else {

            // If the piece is white, return the left divider with the piece initial in it.
            if (piece.isWhite()) {
                return "| " + piece.getInitial() + " ";
            } else {

                // If the piece is black, return the left divider with the piece initial in it surrounded by parentheses.
                return "|(" + piece.getInitial() + ")";
            }
        }
    }

    /**
     * @return the distance from center.
     */
    public float distanceFromCenter() {
        return Math.abs(getX() - 4.5f) + Math.abs(getY() - 4.5f) - 1f;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Tile){
            Tile tile = (Tile) obj;
            return x == tile.x && y == tile.y;
        }
        return false;
    }

    /**
     * @return the formatted coordinate position of this tile.
     */
    public String getPosition() {
        return "" + (char) ((int) 'a' + x - 1) + y;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
