package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a rook.
 */
public class Rook extends Piece {

    /**
     * Creates a Rook.
     * @param isWhite whether it is white or black.
     * @param tile the tile it is on.
     */
    public Rook(boolean isWhite, Tile tile) {
        super(isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "R";
    }

    @Override
    public Piece getCopy() {
        return new Rook(isWhite(), null);
    }

    @Override
    public float getValue() {
        return 5;
    }

    @Override
    public List<Tile> getPossibleLocations(Board board) {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check all the rows and files.
        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getForward(board, i))){
                moves.add(getForward(board, i));
            }

            if(!isEmpty(getForward(board, i))){
                break;
            }
            moves.add(getForward(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getBackward(board, i))){
                moves.add(getBackward(board, i));
            }

            if(!isEmpty(getBackward(board, i))){
                break;
            }
            moves.add(getBackward(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getRight(board, i))){
                moves.add(getRight(board, i));
            }

            if(!isEmpty(getRight(board, i))){
                break;
            }
            moves.add(getRight(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getLeft(board, i))){
                moves.add(getLeft(board, i));
            }

            if(!isEmpty(getLeft(board, i))){
                break;
            }
            moves.add(getLeft(board, i));
        }

        return moves;
    }
}
