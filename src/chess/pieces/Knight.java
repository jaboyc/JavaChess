package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a knight.
 */
public class Knight extends Piece {

    // Knight's piece square table.
    public static final double[][] PIECE_SQUARE_TABLE = {
            {-0.5, -0.4, -0.3, -0.3, -0.3, -0.3, -0.4, -0.5},
            {-0.4, -0.2, 0, 0, 0, 0, -0.2, -0.4},
            {-0.3, 0, 0.1, 0.15, 0.15, 0.1, 0, -0.3},
            {-0.3, 0.05, 0.15, 0.2, 0.2, 0.15, 0.05, -0.3},
            {-0.3, 0, 0.15, 0.2, 0.2, 0.15, 0, -0.3},
            {-0.3, 0.05, 0.1, 0.15, 0.15, 0.1, 0.05, -0.3},
            {-0.4, -0.2, 0, 0.05, 0.05, 0, -0.2, -0.4},
            {-0.5, -0.4, -0.3, -0.3, -0.3, -0.3, -0.4, -0.5},
    };

    /**
     * Creates a Knight.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public Knight(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "N";
    }

    @Override
    public double getValue() {
        return 3.2;
    }

    @Override
    public List<Move> getPossibleLocations() {
        ArrayList<Move> moves = new ArrayList<>();

        // Check all L-shaped locations around the knight.
        int[] stepsX = {1, 1, 2, 2, -1, -1, -2, -2};
        int[] stepsY = {2, -2, 1, -1, 2, -2, 1, -1};

        for (int i = 0; i < 8; i++) {
            Tile tile = getOffset(stepsX[i], stepsY[i]);
            if (isEmpty(tile)) {
                moves.add(move(tile));
            } else if (containsEnemyPiece(tile)) {
                moves.add(0, capture(tile));
            }
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
