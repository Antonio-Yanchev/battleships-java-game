// Board.java
package BattleShipCW;

/**
 * Represents a 10×10 grid of Cells.
 */
public class Board {
    /** Board width/height. */
    public static final int SIZE = 10;

    private final Cell[][] grid;

    public Board() {
        grid = new Cell[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                grid[i][j] = new Cell();
            }
        }
    }

    /**
     * @param x row index [0,9]
     * @param y column index [0,9]
     * @return the Cell at (x,y)
     */
    public Cell getCell(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) {
            throw new IndexOutOfBoundsException("Board.getCell: indices out of range");
        }
        return grid[x][y];
    }
}
