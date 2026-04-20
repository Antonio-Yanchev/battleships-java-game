// MainGUI.java
package BattleShipCW;

import javax.swing.SwingUtilities;

/**
 * Launches the GUI version.
 */
public class MainGUI {
    public static void main(String[] args) {
        BattleshipsModel model = new BattleshipsModel();
        if (args.length>0 && !model.loadShipsFromFile(args[0])) {
            System.out.println("Invalid file; using random placement.");
            model.initialiseShipsRandomly();
        } else if (args.length==0) {
            model.initialiseShipsRandomly();
        }
        BattleshipsController ctrl = new BattleshipsController(model);
        SwingUtilities.invokeLater(() -> new BattleshipsView(model, ctrl));
    }
}
