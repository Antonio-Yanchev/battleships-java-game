// Ship.java
package BattleShipCW;

/**
 * A ship of a given length and orientation on the board.
 */
public class Ship {
    private final int startX, startY, length;
    private final boolean horizontal;
    private final boolean[] hitParts;

    /**
     * @param startX     row origin [0,9]
     * @param startY     col origin [0,9]
     * @param length     >0
     * @param horizontal true if horizontal, false if vertical
     */
    public Ship(int startX, int startY, int length, boolean horizontal) {
        assert length > 0 : "Ship length must be positive";
        this.startX = startX;
        this.startY = startY;
        this.length = length;
        this.horizontal = horizontal;
        this.hitParts = new boolean[length];
    }

    /** @return the ship’s length */
    public int getLength() {
        return length;
    }

    /**
     * @return true if this ship covers cell (x,y)
     */
    public boolean covers(int x, int y) {
        if (horizontal) {
            return x == startX && y >= startY && y < startY + length;
        } else {
            return y == startY && x >= startX && x < startX + length;
        }
    }

    /**
     * Mark a hit on this ship part at (x,y).
     * @pre covers(x,y)
     */
    public void hitAt(int x, int y) {
        assert covers(x, y) : "Hit at non-covered cell";
        int idx = horizontal ? (y - startY) : (x - startX);
        hitParts[idx] = true;
    }

    /** @return true if all parts have been hit. */
    public boolean isSunk() {
        for (boolean b : hitParts) if (!b) return false;
        return true;
    }
}
