package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pawn.
 */
public class Pawn extends Piece {

    public static final double UP_VALUE = 0.06f; // Multiplier for how far forward the piece is.

    /**
     * Creates a Pawn.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public Pawn(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "P";
    }

    @Override
    public Piece getCopy(Board board) {
        return new Pawn(board, isWhite(), null);
    }

    @Override
    public double getValue() {
        return 1;
    }

    @Override
    public List<Tile> getPossibleLocations() {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check the space in front of it.
        if (isEmpty(getForward(1))) {
            moves.add(getForward(1));

            // If the space in front of it is empty, check two spaces in front of it only if it hasn't moved yet.
            if (isEmpty(getForward(2)) && getMoves() == 0) {
                moves.add(getForward(2));
            }
        }

        // Check the diagonals for attacking.
        if (containsEnemyPiece(getNEDiagonal(1))) {
            moves.add(getNEDiagonal(1));
        }
        if (containsEnemyPiece(getNWDiagonal(1))) {
            moves.add(getNWDiagonal(1));
        }

        return moves;
    }

    @Override
    protected double getBonusScore(Board board) {
        double bonus = 0;

        if (isEmpty(getNEDiagonal(1))) {
            bonus += Piece.MOVE_VALUE;
        }
        if (isEmpty(getNWDiagonal(1))) {
            bonus += Piece.MOVE_VALUE;
        }

        if (isWhite()) {
            bonus += (getTile().getY() - 2) * UP_VALUE;
        } else {
            bonus += (7 - getTile().getY()) * UP_VALUE;
        }

        return bonus;
    }

    @Override
    public void onMove(Board board, Move move) {
        super.onMove(board, move);

        // If we hit the edge of the screen, upgrade to queen.
        if (getForward(1) == null) {
            board.removePiece(move.getDestination());
            board.addPiece(new Queen(board, isWhite(), move.getDestination()));
        }
    }
}
