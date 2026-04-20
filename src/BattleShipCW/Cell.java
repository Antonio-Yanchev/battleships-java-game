// Cell.java
package BattleShipCW;

/**
 * A single cell on the Battleships board.
 */
public class Cell {
    /** Possible states of a cell. */
    public enum State { EMPTY, SHIP, HIT, MISS }

    private State state;

    /** Initialize cell to EMPTY. */
    public Cell() {
        this.state = State.EMPTY;
    }

    /** @return the current state of this cell. */
    public State getState() {
        return state;
    }

    /**
     * Set the state of this cell.
     * @param state must not be null
     */
    public void setState(State state) {
        assert state != null : "Cell state cannot be null";
        this.state = state;
    }
}
