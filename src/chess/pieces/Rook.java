package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rook.
 */
public class Rook extends Piece {

    // Rook's piece square table.
    public static final double[][] PIECE_SQUARE_TABLE = {
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0.05, 0.1, 0.1, 0.1, 0.1, 0.1, 0.1, 0.05},
            {-0.05, 0, 0, 0, 0, 0, 0, -0.05},
            {-0.05, 0, 0, 0, 0, 0, 0, -0.05},
            {-0.05, 0, 0, 0, 0, 0, 0, -0.05},
            {-0.05, 0, 0, 0, 0, 0, 0, -0.05},
            {-0.05, 0, 0, 0, 0, 0, 0, -0.05},
            {0, 0, 0, 0.05, 0.05, 0, 0, 0},
    };

    /**
     * Creates a Rook.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public Rook(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "R";
    }

    @Override
    public double getValue() {
        return 5;
    }

    @Override
    public List<Move> getPossibleLocations() {
        ArrayList<Move> moves = new ArrayList<>();

        // Check all the rows and files.
        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getForward(i))) {
                moves.add(0,capture(getForward(i)));
            }

            if (!isEmpty(getForward(i))) {
                break;
            }

            moves.add(move(getForward(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getBackward(i))) {
                moves.add(0,capture(getBackward(i)));
            }

            if (!isEmpty(getBackward(i))) {
                break;
            }

            moves.add(move(getBackward(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getRight(i))) {
                moves.add(0,capture(getRight(i)));
            }

            if (!isEmpty(getRight(i))) {
                break;
            }

            moves.add(move(getRight(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getLeft(i))) {
                moves.add(0,capture(getLeft(i)));
            }

            if (!isEmpty(getLeft(i))) {
                break;
            }

            moves.add(move(getLeft(i)));
        }

        return moves;
    }

    @Override
    protected double getBonusScore(Board board) {
        double bonus = 0;

        bonus += getPieceSquareTableScore(PIECE_SQUARE_TABLE);

        return bonus;
    }
}
