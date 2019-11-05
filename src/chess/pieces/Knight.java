package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a knight.
 */
public class Knight extends Piece {

    /**
     * Creates a Knight.
     * @param isWhite whether it is white or black.
     * @param tile the tile it is on.
     */
    public Knight(Board board, boolean isWhite, Tile tile) {
        super(board, isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "N";
    }

    @Override
    public double getValue() {
        return 3;
    }

    @Override
    public List<Tile> getPossibleLocations() {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check all L-shaped locations around the knight.
        int[] stepsX = {1, 1, 2, 2, -1, -1, -2, -2};
        int[] stepsY = {2, -2, 1, -1, 2, -2, 1, -1};

        for(int i=0;i<8;i++){
            Tile tile = getOffset( stepsX[i], stepsY[i]);
            if(isEmpty(tile) || containsEnemyPiece(tile)){
                moves.add(tile);
            }
        }

        return moves;
    }
}
