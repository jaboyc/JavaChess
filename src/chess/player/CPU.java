package chess.player;

import chess.Board;
import chess.Move;
import chess.Pair;
import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a CPU player. Uses algorithms to find the best move to play against the human.
 */
public class CPU extends Player {

    private static final boolean DEBUG = false;

    private static final int COMPLEXITY = 2; // The number of its turns it looks ahead in the future to decide its next move.
    private static final int FUTURE_MAX = 20; // The largest number of futures to keep.
    private static final float TRIM_HEAD = 0.2f; // The percentage of each side of the futures array to keep.

    public CPU(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public void move(Board board) {
        List<Pair<Move, Float>> moveScores = new ArrayList<>(); // Stores a list of moves with their total associated scores.

        for (Move rootMove : getPossibleMoves(board, true)) {

            Board rootFuture = board.copy(); // Get the first future from the given move.
            rootFuture.movePiece(rootFuture.get(rootMove.getPiece().getTile().getX(), rootMove.getPiece().getTile().getY()), rootFuture.get(rootMove.getTile().getX(), rootMove.getTile().getY())); // Move the first piece.

            List<Board> futures = new ArrayList<>(); // Stores a list of future moves possible.
            futures.add(rootFuture); // Adds the current board.

            Player currPlayer = board.getEnemy(this); // The current player it is analyzing.

            // Print options by score.
//            if (currPlayer != this) {
//                System.out.println(rootMove.getPiece().getTile() + "->" + rootMove.getTile() + ", BLACK " + Arrays.asList(futures.stream().map(b -> getScore(b)).toArray()));
//            } else {
//                System.out.println(rootMove.getPiece().getTile() + "->" + rootMove.getTile() + ", WHITE " + Arrays.asList(futures.stream().map(b -> b.getEnemy(this).getScore(b)).toArray()));
//            }

            // Go through each future and consider every single move.
            for (int i = 0; i < 2 * COMPLEXITY - 1; i++) {
                List<Board> nextFutures = new ArrayList<>(); // Store the next layer of futures.

                // Go through each future and generate an even larger set of futures.
                for (Board future : futures) {

                    for (Move move : currPlayer.getPossibleMoves(future, true)) {

                        Board newFuture = future.copy();
                        newFuture.movePiece(newFuture.get(move.getPiece().getTile().getX(), move.getPiece().getTile().getY()), newFuture.get(move.getTile().getX(), move.getTile().getY()));

                        nextFutures.add(newFuture);
                    }

                }

                // Sort by score.
                if (currPlayer == this) {
                    nextFutures.sort((b1, b2) -> (int) ((getScore(b2) - getScore(b1)) * 100));
                } else {
                    nextFutures.sort((b1, b2) -> (int) ((b2.getEnemy(this).getScore(b2) - b1.getEnemy(this).getScore(b1)) * 100));
                }

                // Limit to only 100 moves.
//                nextFutures = nextFutures.subList(0, Math.min(FUTURE_SIZE, nextFutures.size()));

                // Keep only the top quarter and bottom percentile.
                futures = new ArrayList<>();
                futures.addAll(nextFutures.subList(0, Math.min((int) (nextFutures.size() * TRIM_HEAD), FUTURE_MAX / 2)));
                futures.addAll(nextFutures.subList(nextFutures.size() - Math.min((int) (nextFutures.size() * TRIM_HEAD * 3), FUTURE_MAX / 2), nextFutures.size()));

                // Print options by score.
//                if (currPlayer == this) {
//                    System.out.println("(" + futures.size() + ")" + rootMove.getPiece().getTile() + "->" + rootMove.getTile() + ", BLACK " + Arrays.asList(futures.stream().map(b -> getScore(b)).toArray()));
//                } else {
//                    System.out.println("(" + futures.size() + ")" + rootMove.getPiece().getTile() + "->" + rootMove.getTile() + ", WHITE " + Arrays.asList(futures.stream().map(b -> b.getEnemy(this).getScore(b)).toArray()));
//                }

                // Switch the player we are analyzing.
                currPlayer = board.getEnemy(currPlayer);
            }

            // Calculate score of the root move by getting the average of the moves.
            float moveScore = 0; // Stores the score of this move.
            for (Board b : futures) {
                moveScore += getScore(b);
            }
            moveScore = moveScore / futures.size();

            // Add the root move to the move scores list.
            moveScores.add(new Pair<>(rootMove, moveScore));

            if (DEBUG) System.out.println(rootMove + " = " + (moveScore - getScore(board)));
        }

        // Find the highest scoring move.
        Move highestMove = null;
        float highestScore = -1000000;

        for (Pair<Move, Float> pair : moveScores) {
            if (pair.getSecond() > highestScore) {
                highestMove = pair.getFirst();
                highestScore = pair.getSecond();
            }
        }

        // Check for null.
        if (highestMove == null) {
            System.out.println("NO MOVES LEFT FOR CPU");
            return;
        }

        // Print out the move the CPU just did.
        System.out.println("\n\n");
        System.out.println(highestMove.getPiece().getTile().getPosition() + " -> " + highestMove.getTile().getPosition());

        // Perform the move!
        board.movePiece(highestMove.getPiece().getTile(), highestMove.getTile());
    }
}
