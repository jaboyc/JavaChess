package chess;

/**
 * Represents a tile on the board.
 */
public class Tile {

    private static Tile[][] tileGrid; // Grid of tiles.

    // Initialize the tileGrid.
    static {
        tileGrid = new Tile[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                tileGrid[i][j] = new Tile(i + 1, j + 1);
            }
        }
    }

    private int x; // The x of the tile. (1-8)
    private int y; // The y of the tile. (1-8)

    /**
     * Creates a new tile.
     *
     * @param x the x of the tile. (1-8)
     * @param y the y of the tile. (1-8)
     */
    private Tile(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new tile with the given x and y if it is in bounds.
     *
     * @param x the x of the tile. (1-8)
     * @param y the y of the tile (1-8)
     * @return the new tile. Null if out of bounds.
     */
    public static Tile pos(int x, int y) {
        if (x <= 0 || x > 8 || y <= 0 || y > 8) {
            return null;
        }
        return tileGrid[x - 1][y - 1];
    }

    /**
     * @return a formatted version of this tile with the piece initial on it.
     */
    public String toString() {
        return getPosition();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Tile) {
            Tile tile = (Tile) obj;
            return x == tile.x && y == tile.y;
        }
        return false;
    }

    /**
     * @return the formatted coordinate position of this tile.
     */
    public String getPosition() {
        return "" + (char) ((int) 'a' + x - 1) + y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
