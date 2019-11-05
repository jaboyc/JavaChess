package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a queen.
 */
public class Queen extends Piece {

    /**
     * Creates a Queen.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public Queen(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "Q";
    }

    @Override
    public double getValue() {
        return 8;
    }

    @Override
    public List<Tile> getPossibleLocations() {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check all the diagonals.
        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getNEDiagonal(i))) {
                moves.add(getNEDiagonal(i));
            }

            if (!isEmpty(getNEDiagonal(i))) {
                break;
            }
            moves.add(getNEDiagonal(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getNWDiagonal(i))) {
                moves.add(getNWDiagonal(i));
            }

            if (!isEmpty(getNWDiagonal(i))) {
                break;
            }
            moves.add(getNWDiagonal(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getSEDiagonal(i))) {
                moves.add(getSEDiagonal(i));
            }

            if (!isEmpty(getSEDiagonal(i))) {
                break;
            }
            moves.add(getSEDiagonal(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getSWDiagonal(i))) {
                moves.add(getSWDiagonal(i));
            }

            if (!isEmpty(getSWDiagonal(i))) {
                break;
            }
            moves.add(getSWDiagonal(i));
        }

        // Check all the rows and files.
        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getForward(i))) {
                moves.add(getForward(i));
            }

            if (!isEmpty(getForward(i))) {
                break;
            }
            moves.add(getForward(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getBackward(i))) {
                moves.add(getBackward(i));
            }

            if (!isEmpty(getBackward(i))) {
                break;
            }
            moves.add(getBackward(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getRight(i))) {
                moves.add(getRight(i));
            }

            if (!isEmpty(getRight(i))) {
                break;
            }
            moves.add(getRight(i));
        }

        for (int i = 1; i < 8; i++) {
            if (containsEnemyPiece(getLeft(i))) {
                moves.add(getLeft(i));
            }

            if (!isEmpty(getLeft(i))) {
                break;
            }
            moves.add(getLeft(i));
        }

        return moves;
    }
}
