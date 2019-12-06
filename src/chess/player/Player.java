package chess.player;

import chess.Board;
import chess.Move;
import chess.pieces.Piece;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a player that is playing the game. Either white or black player.
 */
public abstract class Player {

    private boolean isWhite; // Whether the player is white or black.

    /**
     * Creates a new player.
     *
     * @param isWhite whether the player is white or black.
     */
    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Returns a list of moves that are possible for the player.
     *
     * @param board         the board to use.
     * @param checkForCheck whether to consider placing the king in check.
     * @return the list of moves.
     */
    public ArrayList<Move> getPossibleMoves(Board board, boolean checkForCheck) {
        ArrayList<Move> possibleMoves = new ArrayList<>();

        List<Piece> pieces = board.getPieces(this); // Store list beforehand to prevent ConcurrentModificationException.
        for (int i = pieces.size() - 1; i >= 0; i--) { // Go through the pieces backwards because otherwise... it doesn't work? Found strange cases where the for loop going forwards skips some possible moves. I do not know why.
            Piece piece = pieces.get(i);
            possibleMoves.addAll(piece.getPossibleMoves(checkForCheck));
        }

        return possibleMoves;
    }

    /**
     * @param board the board to use.
     * @return whether the player has any moves left.
     */
    public boolean canMove(Board board) {
        return !getPossibleMoves(board, true).isEmpty();
    }

    /**
     * The move the player will do to the board.
     *
     * @param board the board to do the move on.
     */
    public abstract void move(Board board);

    /**
     * @return the king piece from the player. Null if not found.
     */
    public Piece getKing(Board board) {
        for (Piece piece : board.getPieces(this)) {
            if (piece.getInitial().equals("K")) {
                return piece;
            }
        }
        return null;
    }

    /**
     * Gets the score of the current player in the given board.
     *
     * @param board the board to use.
     * @return the score as a number.
     */
    public double getScore(Board board) {
        return board.getScore(this) - board.getScore(board.getEnemy(this));
    }

    public boolean isWhite() {
        return isWhite;
    }
}
