package chess.player;

import chess.Board;
import chess.Move;
import chess.Tile;

import java.util.Scanner;

/**
 * Represents a human player. They will input what pieces to move where.
 */
public class Human extends Player{
    private Scanner scanner = new Scanner(System.in); // The scanner used to read inputs from.

    /**
     * Creates a new Human Player.
     * @param isWhite whether the player is white or black.
     */
    public Human(boolean isWhite) {
        super(isWhite);
    }

    @Override
    public void move(Board board) {
        Tile srcTile;
        Tile destTile;

        while(true){
            if(board.inCheck(this)) System.out.println("     [CHECK]");
            System.out.print((isWhite() ? "[WHITE]" : "[BLACK]") + " Input the tile to move from:  ");
            while((srcTile = tileFromPos(board, scanner.nextLine())) == null){
                System.out.println("TRY AGAIN\n");
                System.out.print((isWhite() ? "[WHITE]" : "[BLACK]") + " Input the tile to move from:  ");
            }

            System.out.print((isWhite() ? "[WHITE]" : "[BLACK]") + " Input the tile to move to: ");
            while((destTile = tileFromPos(board, scanner.nextLine())) == null){
                System.out.println("TRY AGAIN\n");
                System.out.print((isWhite() ? "[WHITE]" : "[BLACK]") + " Input the tile to move to: ");
            }

            if(board.movePiece(new Move(srcTile, destTile))){
                break;
            }

            System.out.println("\n\n\n");
            System.out.println(board);
            System.out.println("Invalid move!\n");
        }
    }

    /**
     * Returns the tile in the board represented by the user input.
     * @param board the board to use.
     * @param input the input to parse.
     * @return the tile the input represents. Null if incorrect input was given.
     */
    private Tile tileFromPos(Board board, String input){
        if(!isTileInputValid(input)) return null;

        int row = Integer.parseInt("" + input.charAt(1));
        int col = (input.charAt(0) - 'a') + 1;

        return Tile.pos(col, row);
    }

    /**
     * Returns whether the given tile input is valid or not.
     * @param input the input to check.
     * @return whether the tile input is valid or not.
     */
    private boolean isTileInputValid(String input){

        // If the input is 'Q', quit the game.
        if(input.equals("q")){
            System.exit(0);
        }

        // Must be exactly two characters in length.
        if(input.length() != 2) {
            return false;
        }

        return (input.charAt(0) >= 'a' && input.charAt(1) <= 'h') && (input.charAt(1) >= '1' && input.charAt(1) <= '8');
    }
}
