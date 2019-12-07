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

    // Pawn's piece square table.
    public static final double[][] PIECE_SQUARE_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5, 0.5},
            {0.1, 0.1, 0.2, 0.3, 0.3, 0.2, 0.1, 0.1},
            {0.05, 0.05, 0.1, 0.25, 0.25, 0.1, 0.05, 0.05},
            {0, 0, 0, 0.2, 0.2, 0, 0, 0},
            {0.05, -0.05, -0.1, 0, 0, -0.1, -0.05, 0.05},
            {0.05, 0.1, 0.1, -0.2, -0.2, 0.1, 0.1, 0.05},
            {0, 0, 0, 0, 0, 0, 0, 0},
    };

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
    public double getValue() {
        return 1;
    }

    @Override
    public List<Move> getPossibleLocations() {
        ArrayList<Move> moves = new ArrayList<>();

        // Check the space in front of it.
        if (isEmpty(getForward(1))) {
            moves.add(move(getForward(1)));

            // If the space in front of it is empty, check two spaces in front of it only if it hasn't moved yet.
            if (isEmpty(getForward(2)) && getMoves() == 0) {
                moves.add(move(getForward(2)));
            }
        }

        // Check the diagonals for attacking.
        if (containsEnemyPiece(getNEDiagonal(1))) {
            moves.add(0, capture(getNEDiagonal(1)));
        }
        if (containsEnemyPiece(getNWDiagonal(1))) {
            moves.add(0, capture(getNWDiagonal(1)));
        }

        return moves;
    }

    @Override
    protected double getBonusScore(Board board) {
        double bonus = 0;

         bonus += getPieceSquareTableScore(PIECE_SQUARE_TABLE);

        return bonus;
    }

    @Override
    public void onMove(Board board, Move move) {
        super.onMove(board, move);

        // If we hit the edge of the screen, promote to queen.
        if (getForward(1) == null) {
            move.setPromotedPawn(this);

            board.removePiece(move.getDestination());
            board.addPiece(new Queen(board, isWhite(), move.getDestination()));
        }
    }
}
