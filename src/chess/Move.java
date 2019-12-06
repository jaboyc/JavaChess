package chess;

import chess.pieces.Pawn;
import chess.pieces.Piece;

/**
 * Represents a possible move a piece can do.
 */
public class Move {
    private Tile src; // The source tile.
    private Tile dest; // The destination tile.

    private Piece removedPiece; // The piece this move removed. Null if none.
    private boolean capture; // Whether the move resulted in a capture.
    private Pawn promotedPawn; // The pawn that was promoted. Null if there was no promotion.

    /**
     * Creates a new move.
     *
     * @param src  the source tile.
     * @param dest the destination tile.
     * @param capture whether the move resulted in a capture.
     */
    public Move(Tile src, Tile dest, boolean capture) {
        this.src = src;
        this.dest = dest;
        this.capture = capture;

        promotedPawn = null;
    }

    /**
     * Creates a new move.
     *
     * @param src  the source tile.
     * @param dest the destination tile.
     */
    public Move(Tile src, Tile dest) {
        this(src, dest, false);
    }


    public boolean equals(Object o) {
        if (o instanceof Move) {
            Move move = (Move) o;
            return src.equals(move.src) && dest.equals(move.dest) && removedPiece == move.removedPiece;
        }
        return false;
    }

    public Piece getRemovedPiece() {
        return removedPiece;
    }

    public void setRemovedPiece(Piece removedPiece) {
        this.removedPiece = removedPiece;
    }

    public String toString() {
        return src + " -> " + dest;
    }

    public Tile getSource() {
        return src;
    }

    public Tile getDestination() {
        return dest;
    }

    public boolean isCapture() {
        return capture;
    }

    public Pawn getPromotedPawn() {
        return promotedPawn;
    }

    public void setPromotedPawn(Pawn promotedPawn) {
        this.promotedPawn = promotedPawn;
    }
}
