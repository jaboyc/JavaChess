package chess;

import chess.pieces.Piece;

/**
 * Represents a possible move a piece can do.
 */
public class Move{
    private Tile src; // The source tile.
    private Tile dest; // The destination tile.

    /**
     * Creates a new move.
     * @param src the source tile.
     * @param dest the destination tile.
     */
    public Move(Tile src, Tile dest) {
        this.src = src;
        this.dest = dest;
    }


    public boolean equals(Object o){
        if(o instanceof Move){
            Move move = (Move) o;
            return src.equals(move.src) && dest.equals(move.dest);
        }
        return false;
    }

    public String toString(){
        return src + " -> " + dest;
    }

    public Tile getSource() {
        return src;
    }

    public Tile getDestination() {
        return dest;
    }
}
