package chess;

import chess.player.CPU;
import chess.player.Human;
import chess.player.Player;

/**
 * Manages the chess game.
 */
public class Chess {
    private Board board; // The current board.

    private Player white; // The white player.
    private Player black; // The black player.

    /**
     * Creates a new Chess game.
     */
    public Chess() {
        white = new CPU(true);
        black = new CPU(false);

        board = new Board(this);
    }

    /**
     * Performs the given move. Moves are represented by srcTile_destTile (i.e. e2e4)
     *
     * @param move the move to perform.
     */
    private void performMove(String move) {
        // Parse the srcTile and destTile.
        String srcTile = move.substring(0, 2);
        String destTile = move.substring(2);

        int srcY = Integer.parseInt("" + srcTile.charAt(1));
        int srcX = (srcTile.charAt(0) - 'a') + 1;

        int destY = Integer.parseInt("" + destTile.charAt(1));
        int destX = (destTile.charAt(0) - 'a') + 1;

        board = board.copy();

        // Move the piece from the srcTile to the destTile.
        board.movePiece(new Move(Tile.pos(srcX, srcY), Tile.pos(destX, destY)));
    }

    /**
     * Starts the chess game.
     */
    private void play() {

        // Start with white.
        Player currPlayer = white;

        // Start off by printing the board.
        System.out.println(board);

        // Go through each player and move.
        while (true) {

            // Copy the board.
            board = board.copy();

            // If the player cannot move, stop the game.
            if (!move(currPlayer)) {
                break;
            }

            // Alternate the player.
            currPlayer = currPlayer == white ? black : white;

            // Print the new board.
            System.out.println(board);
        }

        System.out.println("==========( " + (currPlayer == white ? "BLACK" : "WHITE") + " WON )==========");
    }

    /**
     * Makes the current player perform a move.
     *
     * @return whether they were able to move or not.
     */
    private boolean move(Player currPlayer) {

        // If the player cannot move, return false.
        if (!currPlayer.canMove(board)) {
            return false;
        }

        // Make the player move the board.
        currPlayer.move(board);

        return true;
    }

    public Player getWhite() {
        return white;
    }

    public Player getBlack() {
        return black;
    }

    public static void main(String[] args) {
        Chess chess = new Chess();
//        chess.performMove("e2e4");
//        chess.performMove("e7e5");
//
//        chess.performMove("f1c4");
//        chess.performMove("a7a5");
//
//        chess.performMove("c4b3");
//        chess.performMove("h7h5");

        chess.play();
    }
}

/*
Current limitations

1. NO En passant.
2. You CAN castle the king even if he has been checked before.
3. You CAN castle THROUGH check.
4. Pawns are ONLY upgraded to queens.
 */