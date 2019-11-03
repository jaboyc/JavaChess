package chess;

import chess.pieces.Piece;

/**
 * Represents a possible move a piece can do.
 */
public class Move {
    private Piece piece; // The piece this move represents.
    private Tile tile; // The location this piece can go to.

    /**
     * Creates a new move.
     * @param piece the piece this move represents.
     * @param tile the location this piece can go to.
     */
    public Move(Piece piece, Tile tile) {
        this.piece = piece;
        this.tile = tile;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean equals(Object o){
        if(o instanceof Move){
            Move move = (Move) o;
            return piece.getInitial().equals(move.piece.getInitial()) && tile.equals(move.tile);
        }
        return false;
    }

    public String toString(){
        return piece.getTile() + " -> " + tile;
    }

    public Tile getTile() {
        return tile;
    }
}
