package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bishop.
 */
public class Bishop extends Piece {

    /**
     * Creates a Bishop.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public Bishop(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "B";
    }

    @Override
    public double getValue() {
        return 3;
    }

    @Override
    public List<Move> getPossibleLocations() {
        ArrayList<Move> moves = new ArrayList<>();

        // Look at all diagonals.
        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getNEDiagonal(i))) {
                moves.add(0,capture(getNEDiagonal(i)));
            }

            if (!isEmpty(getNEDiagonal(i))) {
                break;
            }
            moves.add(move(getNEDiagonal(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getNWDiagonal(i))) {
                moves.add(0, capture(getNWDiagonal(i)));
            }

            if (!isEmpty(getNWDiagonal(i))) {
                break;
            }
            moves.add(move(getNWDiagonal(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getSEDiagonal(i))) {
                moves.add(0, capture(getSEDiagonal(i)));
            }

            if (!isEmpty(getSEDiagonal(i))) {
                break;
            }
            moves.add(move(getSEDiagonal(i)));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getSWDiagonal(i))) {
                moves.add(0, capture(getSWDiagonal(i)));
            }

            if (!isEmpty(getSWDiagonal(i))) {
                break;
            }
            moves.add(move(getSWDiagonal(i)));
        }

        return moves;
    }
}
