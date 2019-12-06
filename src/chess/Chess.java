package chess;

import chess.player.CPU;
import chess.player.Human;
import chess.player.Player;

import java.util.stream.Collectors;

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
        white = new Human(true);
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

        // Move the piece from the srcTile to the destTile.
        board.movePiece(new Move(Tile.pos(srcX, srcY), Tile.pos(destX, destY)));
    }

    /**
     * Starts the chess game.
     */
    private void play(Player start) {

        // Start with white.
        Player currPlayer = start;

        // Start off by printing the board.
        System.out.println(board);

        // Go through each player and move.
        while (true) {

            // If the player cannot move, stop the game.
            if (!move(currPlayer)) {
                break;
            }

            // Alternate the player.
            currPlayer = currPlayer == white ? black : white;

            // Print the new board.
            System.out.println(board);
        }

        System.out.println(board.get(4,1));

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
//        chess.performMove("d1h5");
//        chess.performMove("d7d6");
//
//        chess.performMove("g1f3");
//        chess.performMove("g8f6");
//
//        chess.performMove("h5g5");
//        chess.performMove("c8g4");
//
//        chess.performMove("h2h3");
//        chess.performMove("g4f3");
//
//        chess.performMove("g2f3");
//        chess.performMove("b8c6");
//
//        chess.performMove("f1b5");
//        chess.performMove("a7a6");
//
//        chess.performMove("b5c6");
//        chess.performMove("b7c6");
//
//        chess.performMove("d2d3");
//        chess.performMove("h7h6");
//
//        chess.performMove("b2b4");
//        chess.performMove("d6d5");
//
//        chess.performMove("b4b5");
//        chess.performMove("d5e4");
//
//        chess.performMove("b5b6");
//        chess.performMove("e4d3");
//
//        chess.performMove("b6b7");
//        chess.performMove("d3c2");
//
//        chess.performMove("g5e5");
//        chess.performMove("f8e7");
//
//        chess.performMove("b1c3");
//        chess.performMove("d8d1");
//
//        chess.performMove("c3d1");
//
//        chess.play(chess.getBlack());

        chess.play(chess.getWhite());
    }
}

/*
Current limitations

1. NO En passant.
2. You CAN castle THROUGH check.
3. Pawns are ONLY promoted to queens.
 */