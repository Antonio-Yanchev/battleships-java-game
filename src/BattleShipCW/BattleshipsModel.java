package BattleShipCW;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Observable;

/**
 * Model for the Battleships game.*/
@SuppressWarnings("deprecation")
public class BattleshipsModel extends Observable {
    public enum GuessResult { HIT, MISS, SUNK, ALREADY_GUESSED }

    private final Board board = new Board();
    private final List<Ship> ships = new ArrayList<>();
    private int moves = 0;
    private int lastSunkLength = 0;

    /** Required ship lengths: exactly one 5, one 4, one 3, and two of length 2. */
    private static final int[] SHIP_LENGTHS = {5, 4, 3, 2, 2};

    /**
     * @return the number of moves made so far.
     */
    public int getMoveCount() {
        return moves;
    }

    /**
     * @return the length of the ship most recently sunk, or zero if none.
     */
    public int getLastSunkLength() {
        return lastSunkLength;
    }

    /**
     * @return the current state of the cell at (x, y).
     * @pre   0 ≤ x, y < Board.SIZE
     * @post the returned State accurately reflects any previous guesses or ship placements.
     * @throws IndexOutOfBoundsException if x or y out of range.
     */
    public Cell.State getCellState(int x, int y) {
        return board.getCell(x, y).getState();
    }

    /**
     * @return true if every ship has been sunk.
     * @post the game is considered over only when all ships' parts have been hit.
     */
    public boolean isGameOver() {
        for (Ship s : ships) {
            if (!s.isSunk()) return false;
        }
        return true;
    }

    /**
     * Reset to a fresh random configuration.
     *
     * @post moves == 0
     * @post lastSunkLength == 0
     * @post ships list holds exactly five ships with lengths {5,4,3,2,2}
     * @post board is cleared and ships are placed randomly without overlap
     */
    public void reset() {
        moves = 0;
        lastSunkLength = 0;
        initialiseShipsRandomly();
        // enforce invariant
        assert moves == 0 : "moves must be zero after reset";
        assert lastSunkLength == 0 : "lastSunkLength must be zero after reset";
        assert ships.size() == SHIP_LENGTHS.length : "exactly five ships expected";
        setChanged();
        notifyObservers();
    }

    /**
     * Randomly place ships of lengths 5,4,3,2,2 with no overlap.
     * @post ships list and board satisfy the class invariant regarding placement and counts
     */
    public void initialiseShipsRandomly() {
        // clear board
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                board.getCell(i, j).setState(Cell.State.EMPTY);
            }
        }
        ships.clear();

        Random rnd = new Random();
        for (int len : SHIP_LENGTHS) {
            boolean placed = false;
            while (!placed) {
                boolean horiz = rnd.nextBoolean();
                int x = rnd.nextInt(Board.SIZE);
                int y = rnd.nextInt(Board.SIZE);
                if (canPlace(x, y, len, horiz)) {
                    place(x, y, len, horiz);
                    placed = true;
                }
            }
        }
        // enforce invariant
        assert ships.size() == SHIP_LENGTHS.length : "exactly five ships must be placed";
    }

    /**
     * Load ships configuration from a text file.
     *
     * @pre    filename is not null and refers to a readable file
     * @post   if returns true:
     *             - ships list holds exactly five ships with lengths one of each 5,4,3 and two of length 2
     *             - board reflects those positions without overlap
     *         if returns false:
     *             - the model's state remains unchanged
     * @param  filename path to the configuration file
     * @return true if the configuration loaded successfully; false on any error
     */
    public boolean loadShipsFromFile(String filename) {
        // Save original state to restore on failure
        List<Ship> originalShips = new ArrayList<>(ships);
        Cell.State[][] originalGrid = new Cell.State[Board.SIZE][Board.SIZE];
        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                originalGrid[i][j] = board.getCell(i, j).getState();
            }
        }
        int originalMoves = moves;
        int originalLast = lastSunkLength;

        Cell.State[][] tempGrid = new Cell.State[Board.SIZE][Board.SIZE];
        for (int i = 0; i < Board.SIZE; i++) {
            Arrays.fill(tempGrid[i], Cell.State.EMPTY);
        }
        List<Ship> tempShips = new ArrayList<>();
        Map<Integer,Integer> lengthCounts = new HashMap<>();
        int linesRead = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                linesRead++;
                String[] parts = line.trim().split("\\s+");
                if (parts.length != 4) throw new IOException("Bad format");

                boolean horizontal = parts[0].equalsIgnoreCase("H");
                int length  = Integer.parseInt(parts[1]);
                int x       = Integer.parseInt(parts[2]);
                int y       = Integer.parseInt(parts[3]);

                // FR4-a: allowed lengths only
                if (!(length==5 || length==4 || length==3 || length==2)) throw new IOException("Invalid ship length");
                // bounds check
                if (x<0||x>=Board.SIZE||y<0||y>=Board.SIZE) throw new IOException("Position out of bounds");

                // ensure no overlap
                for (int k=0; k<length; k++) {
                    int xi = horizontal ? x : x + k;
                    int yi = horizontal ? y + k : y;
                    if (xi<0||xi>=Board.SIZE||yi<0||yi>=Board.SIZE) throw new IOException("Ship extends out of bounds");
                    if (tempGrid[xi][yi] != Cell.State.EMPTY) throw new IOException("Overlap detected");
                }

                Ship s = new Ship(x, y, length, horizontal);
                tempShips.add(s);
                for (int k=0; k<length; k++) {
                    int xi = horizontal ? x : x + k;
                    int yi = horizontal ? y + k : y;
                    tempGrid[xi][yi] = Cell.State.SHIP;
                }
                lengthCounts.merge(length, 1, Integer::sum);
            }
        } catch (IOException | NumberFormatException ex) {
            // restore original state
            restoreState(originalShips, originalGrid, originalMoves, originalLast);
            return false;
        }

        // must read exactly five lines
        if (linesRead != SHIP_LENGTHS.length) {
            restoreState(originalShips, originalGrid, originalMoves, originalLast);
            return false;
        }
        // exact multiset check
        if (lengthCounts.getOrDefault(5, 0) != 1 ||
            lengthCounts.getOrDefault(4, 0) != 1 ||
            lengthCounts.getOrDefault(3, 0) != 1 ||
            lengthCounts.getOrDefault(2, 0) != 2) {
            restoreState(originalShips, originalGrid, originalMoves, originalLast);
            return false;
        }

        // commit to real board
        ships.clear();
        ships.addAll(tempShips);
        for (int i=0; i<Board.SIZE; i++) {
            for (int j=0; j<Board.SIZE; j++) {
                board.getCell(i, j).setState(tempGrid[i][j]);
            }
        }
        moves = 0;
        lastSunkLength = 0;

        // enforce postcondition
        assert ships.size() == SHIP_LENGTHS.length : "exactly five ships after loading";
        setChanged();
        notifyObservers();
        return true;
    }

    /**
     * Process a guess at (x, y).
     *
     * @pre    0 ≤ x,y < Board.SIZE
     * @post   moves is incremented by 1 if this cell was not already guessed
     * @post   cell at (x, y) is set to HIT or MISS
     * @post   if a ship is sunk, lastSunkLength equals that ship's length
     * @param  x row index
     * @param  y column index
     * @return the result of the guess
     * @throws IllegalArgumentException if x or y are out of bounds
     */
    public GuessResult makeGuess(int x, int y) {
        assert x >= 0 && x < Board.SIZE : "precondition: x must be within [0,9]";
        assert y >= 0 && y < Board.SIZE : "precondition: y must be within [0,9]";

        Cell cell = board.getCell(x, y);
        if (cell.getState() == Cell.State.HIT || cell.getState() == Cell.State.MISS) {
            return GuessResult.ALREADY_GUESSED;
        }

        moves++;
        if (cell.getState() == Cell.State.SHIP) {
            cell.setState(Cell.State.HIT);
            Ship sunk = null;
            for (Ship s : ships) {
                if (s.covers(x, y)) {
                    s.hitAt(x, y);
                    if (s.isSunk()) {
                        sunk = s;
                        lastSunkLength = s.getLength();
                    }
                    break;
                }
            }
            setChanged();
            notifyObservers();
            assert cell.getState() == Cell.State.HIT : "postcondition: state should be HIT";
            if (sunk != null) {
                assert lastSunkLength > 0 : "postcondition: lastSunkLength set when sunk";
                return GuessResult.SUNK;
            }
            return GuessResult.HIT;
        } else {
            cell.setState(Cell.State.MISS);
            setChanged();
            notifyObservers();
            assert cell.getState() == Cell.State.MISS : "postcondition: state should be MISS";
            return GuessResult.MISS;
        }
    }

  
    private boolean canPlace(int x, int y, int len, boolean horiz) {
        if (horiz) {
            if (y + len > Board.SIZE) return false;
            for (int k = 0; k < len; k++) {
                if (board.getCell(x, y + k).getState() != Cell.State.EMPTY) return false;
            }
        } else {
            if (x + len > Board.SIZE) return false;
            for (int k = 0; k < len; k++) {
                if (board.getCell(x + k, y).getState() != Cell.State.EMPTY) return false;
            }
        }
        return true;
    }

    private void place(int x, int y, int len, boolean horiz) {
        Ship s = new Ship(x, y, len, horiz);
        ships.add(s);
        for (int k = 0; k < len; k++) {
            int xi = horiz ? x : x + k;
            int yi = horiz ? y + k : y;
            board.getCell(xi, yi).setState(Cell.State.SHIP);
        }
    }

    // restore original state on load failure
    private void restoreState(List<Ship> oldShips, Cell.State[][] oldGrid, int oldMoves, int oldLast) {
        ships.clear();
        ships.addAll(oldShips);
        for (int i=0; i<Board.SIZE; i++) {
            for (int j=0; j<Board.SIZE; j++) {
                board.getCell(i, j).setState(oldGrid[i][j]);
            }
        }
        moves = oldMoves;
        lastSunkLength = oldLast;
    }
}
