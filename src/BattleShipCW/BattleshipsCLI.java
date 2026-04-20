package BattleShipCW;
import java.util.Scanner;

/**
 * Command-line interface for Battleships, reusing only the Model.
 */
public class BattleshipsCLI {
    private final BattleshipsModel model;

    /**
     * Construct CLI, loading from filePath if provided, otherwise random placement.
     * @param filePath 
     */
    public BattleshipsCLI(String filePath) {
        model = new BattleshipsModel();
        if (filePath != null && !filePath.isBlank()) {
            if (model.loadShipsFromFile(filePath)) {
                System.out.println("Loaded configuration from " + filePath);
            } else {
                System.out.println("Invalid config file; using random placement.");
                model.initialiseShipsRandomly();
            }
        } else {
            model.initialiseShipsRandomly();
        }
    }

    /**
     * Run the main game loop: display board, read input commands, process guesses directly via the Model.
     */
    public void start() {
        Scanner in = new Scanner(System.in);
        while (!model.isGameOver()) {
            printBoard();
            System.out.print("Enter a guess between(A1 to J10 or load a file path):>");
            String line = in.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String up = line.toUpperCase();
            if (up.startsWith("LOAD ")) {
                String path = line.substring(5).trim();
                if (model.loadShipsFromFile(path)) {
                    System.out.println("Configuration loaded from " + path);
                } else {
                    System.out.println("Invalid config file, retaining current board.");
                }
                continue;
            }
            if (!up.matches("^[A-J]([1-9]|10)$")) {
                System.out.println("Invalid Coordinates! Use A1 to J10.");
                continue;
            }
            int x = up.charAt(0) - 'A';
            int y = Integer.parseInt(up.substring(1)) - 1;
            assert x >= 0 && x < Board.SIZE && y >= 0 && y < Board.SIZE : "Parsed input out of bounds";
            BattleshipsModel.GuessResult result = model.makeGuess(x, y);
            switch (result) {
                case SUNK -> System.out.println("You sank a ship of length " + model.getLastSunkLength() + "!");
                case HIT  -> System.out.println("Hit!");
                case MISS -> System.out.println("Miss!");
                case ALREADY_GUESSED -> System.out.println("Already guessed!");
            }
        }
        printBoard();
        System.out.println("All ships sunk in " + model.getMoveCount() + " moves!");
        in.close();
    }

    /**
     * Print the 10×10 grid of previous guesses: H for hit, M for miss, . for untried.
     */
    private void printBoard() {
        System.out.println("  1 2 3 4 5 6 7 8 9 10");
        for (int i = 0; i < Board.SIZE; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < Board.SIZE; j++) {
                Cell.State state = model.getCellState(i, j);
                char c = (state == Cell.State.HIT ? 'H' : state == Cell.State.MISS ? 'M' : '.');
                System.out.print(c + " ");
            }
            System.out.println();
        }
    }

    /**
     * Entry point: prompts user to choose loading a config file or random placement.
     */
    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        System.out.print("Load a ship configuration file? (Y/N): ");
        String resp = console.nextLine().trim().toUpperCase();
        String path = null;
        if (resp.equals("Y")) {
            System.out.print("Enter path to config file: ");
            path = console.nextLine().trim();
        }
        new BattleshipsCLI(path).start();
        console.close();
    }
}
