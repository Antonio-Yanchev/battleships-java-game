// BattleshipsView.java
package BattleShipCW;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

/**
 * Swing GUI for Battleships.
 * Shows H/M on hits/misses and I also added Reset/Load/Exit at bottom.
 */
@SuppressWarnings("deprecation")
public class BattleshipsView extends JFrame implements Observer {
    private final BattleshipsController controller;
    private final BattleshipsModel      model;
    private final JButton[][]           buttons;

    public BattleshipsView(BattleshipsModel model, BattleshipsController controller) {
        this.model      = model;
        this.controller = controller;
        this.buttons    = new JButton[Board.SIZE][Board.SIZE];
        model.addObserver(this);
        initComponents();
    }

    private void initComponents() {
        setTitle("Battleships");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // --- Center grid ---
        JPanel grid = new JPanel(new GridLayout(Board.SIZE+1, Board.SIZE+1));
        grid.add(new JLabel(""));
        for (int c=1; c<=Board.SIZE; c++)
            grid.add(new JLabel(String.valueOf(c), SwingConstants.CENTER));

        for (int r=0; r<Board.SIZE; r++) {
            grid.add(new JLabel(String.valueOf((char)('A'+r)), SwingConstants.CENTER));
            for (int c=0; c<Board.SIZE; c++) {
                JButton b = new JButton();
                b.setOpaque(true);
                final int x=r, y=c;
                b.addActionListener(evt -> {
                    BattleshipsModel.GuessResult res = controller.handleGuess(x,y);
                    if (res == BattleshipsModel.GuessResult.SUNK) {
                        JOptionPane.showMessageDialog(
                            this,
                            "You sank a ship of length " + model.getLastSunkLength() + "!",
                            "Ship Sunk",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                    if (model.isGameOver()) {
                        JOptionPane.showMessageDialog(
                            this,
                            "All ships sunk in " + model.getMoveCount() + " moves!",
                            "Game Over",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                });
                buttons[r][c] = b;
                grid.add(b);
            }
        }
        add(grid, BorderLayout.CENTER);

        // --- Bottom controls ---
        JPanel controls = new JPanel();
        JButton reset = new JButton("Reset");
        JButton load  = new JButton("Load...");
        JButton exit  = new JButton("Exit");
        controls.add(reset);
        controls.add(load);
        controls.add(exit);
        add(controls, BorderLayout.SOUTH);

        reset.addActionListener(e -> controller.resetGame());
        exit .addActionListener(e -> System.exit(0));
        load .addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File f = chooser.getSelectedFile();
                if (!controller.loadFromFile(f.getPath())) {
                    controller.resetGame();
                    JOptionPane.showMessageDialog(
                        this,
                        "Invalid ship file!\nRandom configuration used.",
                        "Load Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    /** Redraw grid on every model change. */
    @Override
    public void update(Observable o, Object arg) {
        for (int i=0; i<Board.SIZE; i++) {
            for (int j=0; j<Board.SIZE; j++) {
                Cell.State st = model.getCellState(i,j);
                JButton  b  = buttons[i][j];
                b.setEnabled(true);
                b.setBackground(null);
                b.setText("");

                if (st == Cell.State.HIT) {
                    b.setBackground(Color.RED);
                    b.setText("H");
                    b.setEnabled(false);
                } else if (st == Cell.State.MISS) {
                    b.setText("M");
                    b.setEnabled(false);
                }
            }
        }
    }
}
