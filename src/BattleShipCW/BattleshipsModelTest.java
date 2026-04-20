// src/BattleShipCW/BattleshipsModelTest.java
package BattleShipCW;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.*;


class BattleshipsModelTest {
   private BattleshipsModel model;

   @BeforeEach
   void setUp() {
      model = new BattleshipsModel();
   }

 
   @Test
   void testLoadValidConfig() throws Exception {
      // Write a temp config with exactly lengths 2,2,3,4,5
      File f = File.createTempFile("ships", ".txt");
      f.deleteOnExit();
      try (PrintWriter pw = new PrintWriter(f)) {
         pw.println("H 2 0 0"); // a 2-long at row 0 cols 0–1
         pw.println("V 2 2 2"); // a 2-long at rows 2–3 col 2
         pw.println("H 3 4 4"); // a 3-long at row 4 cols 4–6
         pw.println("V 4 6 6"); // a 4-long at rows 6–9 col 6
         pw.println("H 5 8 0"); // a 5-long at row 8 cols 0–4
      }

      assertTrue(model.loadShipsFromFile(f.getPath()),
            "Valid config should load successfully");

      // Check two cells of the first (length-2) ship
      assertEquals(Cell.State.SHIP, model.getCellState(0, 0));
      assertEquals(Cell.State.SHIP, model.getCellState(0, 1));
   }

 
   @Test
   void testLoadInvalidConfigOverlap() throws Exception {
      File f = File.createTempFile("ships", ".txt");
      f.deleteOnExit();
      try (PrintWriter pw = new PrintWriter(f)) {
         // Overlap: both ships cover (0,0)
         pw.println("H 2 0 0");
         pw.println("V 2 0 0");
         pw.println("H 3 4 4");
         pw.println("V 4 6 6");
         pw.println("H 5 8 0");
      }

      assertFalse(model.loadShipsFromFile(f.getPath()),
            "Overlapping configuration must be rejected");
   }

   @Test
   void testMakeGuessHitMissSunk() throws Exception {
      // Only one 2-long ship plus four dummy ships to satisfy FR4
      File f = File.createTempFile("ships", ".txt");
      f.deleteOnExit();
      try (PrintWriter pw = new PrintWriter(f)) {
         pw.println("H 2 0 0"); // our test ship
         pw.println("H 2 2 2");
         pw.println("H 3 4 4");
         pw.println("H 4 6 6");
         pw.println("H 5 8 0");
      }
      assertTrue(model.loadShipsFromFile(f.getPath()));

      // a) MISS
      BattleshipsModel.GuessResult r1 = model.makeGuess(9, 9);
      assertEquals(BattleshipsModel.GuessResult.MISS, r1);
      assertEquals(Cell.State.MISS, model.getCellState(9, 9));

      // b) first part of ship → HIT
      BattleshipsModel.GuessResult r2 = model.makeGuess(0, 0);
      assertEquals(BattleshipsModel.GuessResult.HIT, r2);
      assertEquals(Cell.State.HIT, model.getCellState(0, 0));

      // c) second part → SUNK
      BattleshipsModel.GuessResult r3 = model.makeGuess(0, 1);
      assertEquals(BattleshipsModel.GuessResult.SUNK, r3);
      assertEquals(2, model.getLastSunkLength(),
            "lastSunkLength must be the length of the ship just sunk");

      // d) guessing the same cell again → ALREADY_GUESSED
      BattleshipsModel.GuessResult r4 = model.makeGuess(0, 0);
      assertEquals(BattleshipsModel.GuessResult.ALREADY_GUESSED, r4);
   }
}
