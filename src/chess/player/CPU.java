package chess.player;

import chess.Board;
import chess.Move;
import chess.Pair;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Represents a CPU player. Uses algorithms to find the best move to play against the human.
 */
public class CPU extends Player {

    private static final boolean DEBUG = true;

    private static final int COMPLEXITY = 3; // The number of its turns it looks ahead in the future to decide its next move.

    public CPU(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public void move(Board board) {

        Pair<Move, Double> highestMove = calculate(board, this, COMPLEXITY, null);

        // Check for null.
        if (highestMove == null) {
            System.out.println("NO MOVES LEFT FOR CPU");
            return;
        }

        // Print out the move the CPU just did.
        System.out.println("\n\n");
        System.out.println(highestMove.getFirst());

        // Perform the move!
        board.movePiece(highestMove.getFirst());

//        if(DEBUG) Toolkit.getDefaultToolkit().beep();
    }

    /**
     * Uses minimax algorithm to calculate the move-score the player would most optimally choose.
     * @param board the board to use.
     * @param curr the current player.
     * @param layersLeft the number of layers left.
     * @return the move-score with the most likelihood of being chosen.
     */
    private Pair<Move, Double> calculate(Board board, Player curr, int layersLeft, Move rootMove) {

        ArrayList<Pair<Move, Double>> moveScores = new ArrayList<>();

        ArrayList<Move> possibleMoves = curr.getPossibleMoves(board, true);
        for (Move move : possibleMoves) {
            board.movePiece(move, false);

            if (layersLeft == 0) {
                moveScores.add(new Pair<>(rootMove, getScore(board)));
            } else {
                moveScores.add(calculate(board, board.getEnemy(curr), layersLeft - 1, rootMove == null ? move : rootMove));
            }

            if(layersLeft == COMPLEXITY && DEBUG){
                System.out.println(move + " (" + String.format("%.2f",moveScores.get(moveScores.size()-1).getSecond()) + ")");
            }

            board.undoMove();
        }

        if(moveScores.isEmpty()){
            return new Pair<>(rootMove, getScore(board));
        }
        if (curr == this) {
            return getBestMove(moveScores);
        } else {
            return getWorstMove(moveScores);
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
