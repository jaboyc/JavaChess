package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a king.
 */
public class King extends Piece {

    private static final float KING_CASTLE_BONUS = 3f; // Bonus points for the king not moving.
    private static final float KING_NESTLED_BONUS = 0.8f; // Bonus points for being surrounded by pieces.

    /**
     * Creates a King.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public King(boolean isWhite, Tile tile) {
        super(isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "K";
    }

    @Override
    public Piece getCopy() {
        return new King(isWhite(), null);
    }

    @Override
    public float getValue() {
        return 0;
    }

    @Override
    public List<Tile> getPossibleLocations(Board board) {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check all the spaces directly around the king.
        int[] stepsX = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] stepsY = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < 8; i++) {
            Tile tile = getOffset(board, stepsX[i], stepsY[i]);
            if (isEmpty(tile) || containsEnemyPiece(tile)) {
                moves.add(tile);
            }
        }

        if (board.shouldConsiderCastle()) {

            // Prevent the other king from considering castling.
            board.setConsiderCastle(false);

            // If the board wants us to consider castling, check whether castling is possible.
            if (getMoves() == 0 && canCastleRight(board) && !inCheck(board)) {
                moves.add(getRight(board, 2));
            }
            if (getMoves() == 0 && canCastleLeft(board) && !inCheck(board)) {
                moves.add(getLeft(board, 2));
            }

            // Allow the other king to consider castling.
            board.setConsiderCastle(true);
        }

        return moves;
    }

    @Override
    protected float getBonusScore(Board board) {
        float bonus = 0f;

        if (getMoves() == 0 && canCastleRight(board))
            bonus += KING_CASTLE_BONUS;
        if (getMoves() == 0 && canCastleLeft(board))
            bonus += KING_CASTLE_BONUS;

        // Check all the spaces directly around the king.
        int[] stepsX = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] stepsY = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < 8; i++) {
            Tile tile = getOffset(board, stepsX[i], stepsY[i]);
            if (tile == null || containsAllyPiece(tile)) {
                bonus += KING_NESTLED_BONUS;
            }
        }

        return bonus;
    }

    /**
     * Whether the king is able to castle on the given board.
     *
     * @param board the board to use.
     * @return whether the king can castle to the right.
     */
    private boolean canCastleRight(Board board) {
        if (isWhite()) {
            return isEmpty(getRight(board, 1)) && isEmpty(getRight(board, 2)) && getRight(board, 3) != null && getRight(board, 3).getPiece() != null && getRight(board, 3).getPiece().getInitial().equals("R") && !getRight(board, 3).getPiece().hasMoved();
        } else {
            return isEmpty(getRight(board, 1)) && isEmpty(getRight(board, 2)) && isEmpty(getRight(board, 3)) && getRight(board, 4) != null && getRight(board, 4).getPiece() != null && getRight(board, 4).getPiece().getInitial().equals("R") && !getRight(board, 4).getPiece().hasMoved();
        }
    }

    /**
     * Whether the king is able to castle on the given board.
     *
     * @param board the board to use.
     * @return whether the king can castle to the right.
     */
    private boolean canCastleLeft(Board board) {
        if (isWhite()) {
            return isEmpty(getLeft(board, 1)) && isEmpty(getLeft(board, 2)) && isEmpty(getLeft(board, 3)) && getLeft(board, 4) != null && getLeft(board, 4).getPiece() != null && getLeft(board, 4).getPiece().getInitial().equals("R") && !getLeft(board, 4).getPiece().hasMoved();
        } else {
            return isEmpty(getLeft(board, 1)) && isEmpty(getLeft(board, 2)) && getLeft(board, 4) != null && getLeft(board, 3).getPiece() != null && getLeft(board, 3).getPiece().getInitial().equals("R") && !getLeft(board, 3).getPiece().hasMoved();
        }
    }


    @Override
    public void onMove(Board board, Tile oldTile, Tile newTile) {
        super.onMove(board, oldTile, newTile);

        // If the king just castled, adjust the position of the rook.
        if (isWhite()) {
            if (newTile.getX() - oldTile.getX() == 2) {
                board.movePiece(getRight(board, 1), getLeft(board, 1), false);
            }
            if (newTile.getX() - oldTile.getX() == -2) {
                board.movePiece(getLeft(board, 2), getRight(board, 1), false);
            }
        } else {
            if (newTile.getX() - oldTile.getX() == -2) {
                board.movePiece(getRight(board, 2), getLeft(board, 1), false);
            }
            if (newTile.getX() - oldTile.getX() == 2) {
                board.movePiece(getLeft(board, 1), getRight(board, 1), false);
            }
        }

    }
}
