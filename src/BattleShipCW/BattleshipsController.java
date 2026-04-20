// BattleshipsController.java
package BattleShipCW;

import BattleShipCW.BattleshipsModel.GuessResult;

/**
 * Controller: relays valid requests to the Model.
 */
public class BattleshipsController {
    private final BattleshipsModel model;

    public BattleshipsController(BattleshipsModel model) {
        this.model = model;
    }

    /** Handle a guess at (x,y). */
    public GuessResult handleGuess(int x, int y) {
        if (x<0||x>=Board.SIZE||y<0||y>=Board.SIZE)
            return GuessResult.MISS;
        return model.makeGuess(x,y);
    }

    /** Reset to a fresh random game. */
    public void resetGame() {
        model.reset();
    }

    /**
     * Attempt to load ships from file.
     * @return true if valid, false otherwise
     */
    public boolean loadFromFile(String filePath) {
        return model.loadShipsFromFile(filePath);
    }
}
