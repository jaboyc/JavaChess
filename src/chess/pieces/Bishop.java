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
    public Bishop(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "B";
    }

    @Override
    public double getValue() {
        return 3;
    }

    @Override
    public List<Tile> getPossibleLocations() {
        ArrayList<Tile> moves = new ArrayList<>();

        // Look at all diagonals.
        for(int i=1;i<8;i++){
//            if(i == 1 && getNEDiagonal(board, i) != null && getNEDiagonal(board, i).getPiece() != null && getNEDiagonal(board, i).getPiece().getInitial().equals("K")){
//                System.out.println("Somehow got here!!!");
//
//            }
            if(containsEnemyPiece(getNEDiagonal( i))){
                moves.add(getNEDiagonal( i));
            }

            if(!isEmpty(getNEDiagonal( i))){
                break;
            }
            moves.add(getNEDiagonal( i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getNWDiagonal( i))){
                moves.add(getNWDiagonal( i));
            }

            if(!isEmpty(getNWDiagonal( i))){
                break;
            }
            moves.add(getNWDiagonal( i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getSEDiagonal( i))){
                moves.add(getSEDiagonal( i));
            }

            if(!isEmpty(getSEDiagonal( i))){
                break;
            }
            moves.add(getSEDiagonal( i));
        }

        for(int i=1;i<8;i++){
            if(containsEnemyPiece(getSWDiagonal( i))){
                moves.add(getSWDiagonal( i));
            }

            if(!isEmpty(getSWDiagonal( i))){
                break;
            }
            moves.add(getSWDiagonal( i));
        }

        return moves;
    }
}
