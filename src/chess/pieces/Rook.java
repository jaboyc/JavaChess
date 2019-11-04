package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rook.
 */
public class Rook extends Piece {

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
    public Piece getCopy(Board board) {
        return new Rook(board, isWhite(), null);
    }

    @Override
    public double getValue() {
        return 5;
    }

    @Override
    public List<Tile> getPossibleLocations() {
        ArrayList<Tile> moves = new ArrayList<>();

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
