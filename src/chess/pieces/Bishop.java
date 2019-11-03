package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a bishop.
 */
public class Bishop extends Piece {

    /**
     * Creates a Bishop.
     * @param isWhite whether it is white or black.
     * @param tile the tile it is on.
     */
    public Bishop(boolean isWhite, Tile tile) {
        super(isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "B";
    }

    @Override
    public Piece getCopy() {
        return new Bishop(isWhite(), null);
    }

    @Override
    public float getValue() {
        return 3;
    }

    @Override
    public List<Tile> getPossibleLocations(Board board) {
        ArrayList<Tile> moves = new ArrayList<>();

        // Look at all diagonals.
        for(int i=1;i<8;i++){
//            if(i == 1 && getNEDiagonal(board, i) != null && getNEDiagonal(board, i).getPiece() != null && getNEDiagonal(board, i).getPiece().getInitial().equals("K")){
//                System.out.println("Somehow got here!!!");
//
//            }
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

        return moves;
    }
}
