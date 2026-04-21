# Battleships Java Game

A polished Java implementation of the classic Battleships game, designed to demonstrate strong object-oriented design, clean project structure, and practical game logic.

This project combines an MVC-style architecture with both command-line and GUI entry points, making it suitable as both a learning project and a portfolio showcase.

## Highlights

- Classic Battleships gameplay with turn-based shot handling
- Clean Java codebase organized around core OOP principles
- MVC-style separation of concerns
- Support for both CLI and GUI interfaces
- Encapsulated board, cell, and ship domain models
- Test class included for core model behavior

## Tech Stack

- **Language:** Java
- **Architecture:** MVC-style layering
- **Interfaces:** Command-line + GUI
- **Testing:** Java test class for model validation

## Project Structure

```text
src/BattleShipCW/
├── BattleshipsCLI.java          # Command-line entry/interaction
├── BattleshipsController.java   # Game flow coordination
├── BattleshipsModel.java        # Core game state and rules
├── BattleshipsModelTest.java    # Model test coverage
├── BattleshipsView.java         # CLI/game presentation layer
├── Board.java                   # Board representation and operations
├── Cell.java                    # Individual board cell behavior
├── MainGUI.java                 # GUI entry point
└── Ship.java                    # Ship state and placement logic
```

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 8 or later
- A Java IDE (recommended): IntelliJ IDEA, Eclipse, or VS Code

### Run the Project

1. Clone the repository:
   ```bash
   git clone <your-repository-url>
   cd battleships-java-game
   ```
2. Open the project in your preferred Java IDE.
3. Build the source files in `src/BattleShipCW/`.
4. Run one of the following entry points:
   - `BattleshipsCLI.java` for the command-line version
   - `MainGUI.java` for the GUI version

## Testing

- Run `BattleshipsModelTest.java` from your IDE or test runner to validate model behavior.
- Extend tests to cover additional edge cases such as overlapping placements, repeated shots, and win conditions.

## Screenshots

### CLI Gameplay

Initial CLI board and prompt:

![CLI board and input prompt](C:/Users/anton/.cursor/projects/c-Users-anton-Desktop-battleships-java-game/assets/c__Users_anton_AppData_Roaming_Cursor_User_workspaceStorage_823088c4f2ee34cdfc512105fc50affa_images_image-e6bb51fb-13c8-4610-a2bd-75efe4ba73da.png)

CLI win state after sinking all ships:

![CLI completed game](C:/Users/anton/.cursor/projects/c-Users-anton-Desktop-battleships-java-game/assets/c__Users_anton_AppData_Roaming_Cursor_User_workspaceStorage_823088c4f2ee34cdfc512105fc50affa_images_image-556adcd1-6028-4ccf-92c8-6be04eaed98c.png)

### GUI Gameplay

Initial GUI board:

![GUI initial board](C:/Users/anton/.cursor/projects/c-Users-anton-Desktop-battleships-java-game/assets/c__Users_anton_AppData_Roaming_Cursor_User_workspaceStorage_823088c4f2ee34cdfc512105fc50affa_images_image-64111954-4a5e-4b63-97a7-059303115f37.png)

GUI end-game state:

![GUI completed game](C:/Users/anton/.cursor/projects/c-Users-anton-Desktop-battleships-java-game/assets/c__Users_anton_AppData_Roaming_Cursor_User_workspaceStorage_823088c4f2ee34cdfc512105fc50affa_images_image-4f9ef200-9c6d-4cc5-be44-a8b8ccc2c7f7.png)

## What This Project Demonstrates

- Object-oriented modeling of a board game domain
- Practical separation between model, view, and controller logic
- Maintainable class design with focused responsibilities
- Ability to deliver the same core logic across multiple interfaces
