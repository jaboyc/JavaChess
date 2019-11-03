package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a queen.
 */
public class Queen extends Piece {

    /**
     * Creates a Queen.
     * @param isWhite whether it is white or black.
     * @param tile the tile it is on.
     */
    public Queen(boolean isWhite, Tile tile) {
        super(isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "Q";
    }

    @Override
    public Piece getCopy() {
        return new Queen(isWhite(), null);
    }

    @Override
    public float getValue() {
        return 8;
    }

    @Override
    public List<Tile> getPossibleLocations(Board board) {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check all the diagonals.
        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getNEDiagonal(board, i))){
                moves.add(getNEDiagonal(board, i));
            }

            if(!isEmpty(getNEDiagonal(board, i))){
                break;
            }
            moves.add(getNEDiagonal(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getNWDiagonal(board, i))){
                moves.add(getNWDiagonal(board, i));
            }

            if(!isEmpty(getNWDiagonal(board, i))){
                break;
            }
            moves.add(getNWDiagonal(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getSEDiagonal(board, i))){
                moves.add(getSEDiagonal(board, i));
            }

            if(!isEmpty(getSEDiagonal(board, i))){
                break;
            }
            moves.add(getSEDiagonal(board, i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getSWDiagonal(board, i))){
                moves.add(getSWDiagonal(board, i));
            }

            if(!isEmpty(getSWDiagonal(board, i))){
                break;
            }
            moves.add(getSWDiagonal(board, i));
        }

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
