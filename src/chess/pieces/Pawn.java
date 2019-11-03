package chess.pieces;

import chess.Board;
import chess.Tile;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a pawn.
 */
public class Pawn extends Piece {

    public static final float UP_VALUE = 0.2f; // Multiplier for how far forward the piece is.

    /**
     * Creates a Pawn.
     * @param isWhite whether it is white or black.
     * @param tile the tile it is on.
     */
    public Pawn(boolean isWhite, Tile tile) {
        super(isWhite, tile);
    }

    @Override
    public String getInitial() {
        return "P";
    }

    @Override
    public Piece getCopy() {
        return new Pawn(isWhite(), null);
    }

    @Override
    public float getValue() {
        return 1;
    }

    @Override
    public List<Tile> getPossibleLocations(Board board) {
        ArrayList<Tile> moves = new ArrayList<>();

        // Check the space in front of it.
        if(isEmpty(getForward(board, 1))){
            moves.add(getForward(board, 1));

            // If the space in front of it is empty, check two spaces in front of it only if it hasn't moved yet.
            if(isEmpty(getForward(board, 2)) && getMoves() == 0){
                moves.add(getForward(board, 2));
            }
        }

        // Check the diagonals for attacking.
        if(containsEnemyPiece(getNEDiagonal(board, 1))){
            moves.add(getNEDiagonal(board, 1));
        }
        if(containsEnemyPiece(getNWDiagonal(board, 1))){
            moves.add(getNWDiagonal(board, 1));
        }

        return moves;
    }

    @Override
    protected float getBonusScore(Board board) {
        float bonus = 0;

        if(isEmpty(getNEDiagonal(board, 1))){
            bonus += Piece.MOVE_VALUE;
        }
        if(isEmpty(getNWDiagonal(board, 1))){
            bonus += Piece.MOVE_VALUE;
        }

        if(isWhite()){
            bonus += (getTile().getY() - 2) * UP_VALUE;
        }else{
            bonus += (7 - getTile().getY()) * UP_VALUE;
        }

        return bonus;
    }

    @Override
    public void onMove(Board board, Tile oldTile, Tile newTile) {
        super.onMove(board, oldTile, newTile);

        // If we hit the edge of the screen, upgrade to queen.
        if(getForward(board, 1) == null){
            newTile.setPiece(new Queen(isWhite(), newTile));
        }
    }
}
