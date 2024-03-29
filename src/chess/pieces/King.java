package chess.pieces;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a king.
 */
public class King extends Piece {

    private static final double KING_CASTLE_BONUS = 0.5f; // Bonus points for the king not moving.

    // King's piece square table for early/mid game.
    public static final double[][] PIECE_SQUARE_TABLE_MID_GAME = {
            {-0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3},
            {-0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3},
            {-0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3},
            {-0.3, -0.4, -0.4, -0.5, -0.5, -0.4, -0.4, -0.3},
            {-0.2, -0.3, -0.3, -0.4, -0.4, -0.3, -0.3, -0.2},
            {-0.1, -0.2, -0.2, -0.2, -0.2, -0.2, -0.2, -0.1},
            {0.2, 0.2, 0, 0, 0, 0, 0.2, 0.2},
            {0.2, 0.3, 0.1, 0, 0, 0.1, 0.3, 0.2},
    };

    // King's piece square table for end game.
    public static final double[][] PIECE_SQUARE_TABLE_END_GAME = {
            {-0.5, -0.4, -0.3, -0.2, -0.2, -0.3, -0.4, -0.5},
            {-0.3, -0.2, -0.1, 0, 0, -0.1, -0.2, -0.3},
            {-0.3, -0.1, 0.2, 0.3, 0.3, 0.2, -0.1, -0.3},
            {-0.3, -0.1, 0.3, 0.4, 0.4, 0.3, -0.1, -0.3},
            {-0.3, -0.1, 0.3, 0.4, 0.4, 0.3, -0.1, -0.3},
            {-0.3, -0.1, 0.2, 0.3, 0.3, 0.2, -0.1, -0.3},
            {-0.3, -0.3, 0, 0, 0, 0, -0.3, -0.3},
            {-0.5, -0.3, -0.3, -0.3, -0.3, -0.3, -0.3, -0.5},
    };

    /**
     * Creates a King.
     *
     * @param isWhite whether it is white or black.
     * @param tile    the tile it is on.
     */
    public King(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "K";
    }

    @Override
    public double getValue() {
        return 1000;
    }

    @Override
    public List<Move> getPossibleLocations() {
        ArrayList<Move> moves = new ArrayList<>();

        // Check all the spaces directly around the king.
        int[] stepsX = {-1, -1, -1, 0, 1, 1, 1, 0};
        int[] stepsY = {-1, 0, 1, 1, 1, 0, -1, -1};

        for (int i = 0; i < 8; i++) {
            Tile tile = getOffset(stepsX[i], stepsY[i]);
            if (isEmpty(tile)) {
                moves.add(move(tile));
            } else if (containsEnemyPiece(tile)) {
                moves.add(0, capture(tile));
            }
        }

        if (getBoard().shouldConsiderCastle()) {

            // Prevent the other king from considering castling.
            getBoard().setConsiderCastle(false);

            // If the board wants us to consider castling, check whether castling is possible.
            if (getMoves() == 0 && canCastleRight(getBoard()) && !inCheck(getBoard())) {
                moves.add(move(getRight(2)));
            }
            if (getMoves() == 0 && canCastleLeft(getBoard()) && !inCheck(getBoard())) {
                moves.add(move(getLeft(2)));
            }

            // Allow the other king to consider castling.
            getBoard().setConsiderCastle(true);
        }

        return moves;
    }

    @Override
    protected double getBonusScore(Board board) {
        double bonus = 0f;

//        if (getMoves() == 0 && canCastleRight(board))
//            bonus += KING_CASTLE_BONUS;
//        if (getMoves() == 0 && canCastleLeft(board))
//            bonus += KING_CASTLE_BONUS;

        bonus += getPieceSquareTableScore(PIECE_SQUARE_TABLE_MID_GAME);

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
            return isEmpty(getRight(1)) && isEmpty(getRight(2)) && containsAllyPiece(getRight(3)) && board.get(getRight(3)).getInitial().equals("R") && !board.get(getRight(3)).hasMoved();
        } else {
            return isEmpty(getRight(1)) && isEmpty(getRight(2)) && isEmpty(getRight(3)) && containsAllyPiece(getRight(4)) && board.get(getRight(4)).getInitial().equals("R") && !board.get(getRight(4)).hasMoved();
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
            return isEmpty(getLeft(1)) && isEmpty(getLeft(2)) && isEmpty(getLeft(3)) && containsAllyPiece(getLeft(4)) && board.get(getLeft(4)).getInitial().equals("R") && !board.get(getLeft(4)).hasMoved();
        } else {
            return isEmpty(getLeft(1)) && isEmpty(getLeft(2)) && containsAllyPiece(getLeft(3)) && board.get(getLeft(3)).getInitial().equals("R") && !board.get(getLeft(3)).hasMoved();
        }
    }


    @Override
    public void onMove(Board board, Move move) {
        super.onMove(board, move);

        // If the king just castled, adjust the position of the rook.
        if (isWhite()) {
            if (move.getDestination().getX() - move.getSource().getX() == 2) {
                board.movePiece(new Move(getRight(1), getLeft(1)), false, false);
            }
            if (move.getDestination().getX() - move.getSource().getX() == -2) {
                board.movePiece(new Move(getLeft(2), getRight(1)), false, false);
            }
        } else {
            if (move.getDestination().getX() - move.getSource().getX() == -2) {
                board.movePiece(new Move(getRight(2), getLeft(1)), false, false);
            }
            if (move.getDestination().getX() - move.getSource().getX() == 2) {
                board.movePiece(new Move(getLeft(1), getRight(1)), false, false);
            }
        }
    }

    @Override
    public void onUnMove(Board board, Move move) {
        super.onUnMove(board, move);

        Move rookMove;

        // If the king just castled, adjust the position of the rook.
        if (isWhite()) {
            if (move.getDestination().getX() - move.getSource().getX() == 2) {
                rookMove = new Move(getRight(1), getRight(3));
                board.get(getRight(1)).onUnMove(board, rookMove);
                board.movePiece(rookMove, false, false);
                board.get(getRight(3)).onUnMove(board, rookMove);
            }
            if (move.getDestination().getX() - move.getSource().getX() == -2) {
                rookMove = new Move(getLeft(1), getLeft(4));
                board.get(getLeft(1)).onUnMove(board, rookMove);
                board.movePiece(rookMove, false, false);
                board.get(getLeft(4)).onUnMove(board, rookMove);
            }
        } else {
            if (move.getDestination().getX() - move.getSource().getX() == -2) {
                rookMove = new Move(getRight(1), getRight(4));
                board.get(getRight(1)).onUnMove(board, rookMove);
                board.movePiece(rookMove, false, false);
                board.get(getRight(4)).onUnMove(board, rookMove);
            }
            if (move.getDestination().getX() - move.getSource().getX() == 2) {
                rookMove = new Move(getLeft(1), getLeft(3));
                board.get(getLeft(1)).onUnMove(board, rookMove);
                board.movePiece(rookMove, false, false);
                board.get(getLeft(3)).onUnMove(board, rookMove);
            }
        }
    }
}
