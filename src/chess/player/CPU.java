package chess.player;

import chess.Board;
import chess.Move;
import chess.Pair;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a CPU player. Uses algorithms to find the best move to play against the human.
 */
public class CPU extends Player {

    private static final boolean DEBUG = true;

    private static final int COMPLEXITY = 4; // The number of its turns it looks ahead in the future to decide its next move.

    public CPU(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public void move(Board board) {

        Pair<Move, Double> highestMove = calculate(board, this, COMPLEXITY, null, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

        // Check for null.
        if (highestMove == null || highestMove.getFirst() == null) {
            System.out.println("NO MOVES LEFT FOR CPU");
            return;
        }

        // Print out the move the CPU just did.
        System.out.println("\n\n");
        System.out.println(highestMove.getFirst());

        // Perform the move!
        board.movePiece(highestMove.getFirst(), false);

//        if(DEBUG) Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Uses minimax algorithm to calculate the move-score the player would most optimally choose.
     *
     * @param board      the board to use.
     * @param curr       the current player.
     * @param layersLeft the number of layers left.
     * @return the move-score with the most likelihood of being chosen.
     */
    private Pair<Move, Double> calculate(Board board, Player curr, double layersLeft, Move rootMove, double alpha, double beta) {

        if (layersLeft <= 0 || getKing(board) == null || board.getEnemy(this).getKing(board) == null || !canMove(board) || !board.getEnemy(this).canMove(board)) {
            return new Pair<>(rootMove, getScore(board));
        }

        ArrayList<Move> possibleMoves = curr.getPossibleMoves(board, true);

        if (curr == this) {
            Pair<Move, Double> bestMove = new Pair<>(null, Double.NEGATIVE_INFINITY);

            for (Move move : possibleMoves) {
                board.movePiece(move, false);

                double depth = (move.isCapture() || board.inCheck(board.getEnemy(curr))) ? layersLeft - 0.5 : layersLeft - 1;

                Pair<Move, Double> result = calculate(board, board.getEnemy(curr), depth, rootMove == null ? move : rootMove, alpha, beta);
                bestMove = bestMove.getSecond() > result.getSecond() ? bestMove : result;

                board.undoMove();

                if (layersLeft == COMPLEXITY && DEBUG) {
                    System.out.println(move + " (" + String.format("%.2f", result.getSecond()) + ")");
                }

                alpha = Math.max(alpha, result.getSecond());
                if (beta <= alpha) {
                    break;
                }
            }

            return bestMove;
        }else{
            Pair<Move, Double> worstMove = new Pair<>(null, Double.POSITIVE_INFINITY);

            for (Move move : possibleMoves) {
                board.movePiece(move, false);

                double depth = (move.isCapture() || board.inCheck(board.getEnemy(curr))) ? layersLeft - 0.5 : layersLeft - 1;

                Pair<Move, Double> result = calculate(board, board.getEnemy(curr), depth, rootMove == null ? move : rootMove, alpha, beta);
                worstMove = worstMove.getSecond() < result.getSecond() ? worstMove : result;

                board.undoMove();

                beta = Math.min(beta, result.getSecond());
                if (beta <= alpha) {
                    break;
                }
            }

            return worstMove;
        }
    }


    /**
     * @param moves the moves to look through.
     * @return the move with the highest score.
     */
    private static Pair<Move, Double> getBestMove(ArrayList<Pair<Move, Double>> moves) {
        Pair<Move, Double> highest = null;

        for (Pair<Move, Double> pair : moves) {
            if (highest == null) {
                highest = pair;
            } else if (pair.getSecond() > highest.getSecond()) {
                highest = pair;
            }
        }

        return highest;
    }

    /**
     * @param moves the moves to look through.
     * @return the move with the lowest score.
     */
    private static Pair<Move, Double> getWorstMove(ArrayList<Pair<Move, Double>> moves) {
        Pair<Move, Double> lowest = null;

        for (Pair<Move, Double> pair : moves) {
            if (lowest == null) {
                lowest = pair;
            } else if (pair.getSecond() < lowest.getSecond()) {
                lowest = pair;
            }
        }

        return lowest;
    }
}
