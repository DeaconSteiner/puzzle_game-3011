
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class GameView extends JFrame {

    private final GameState state;
    private final BoardView boardView;
    private final JLabel movesLabel;
    private final GameController controller;

    public GameView(GameState state, Runnable onBackToMenu) {
        super("Puzzle Game");
        this.state = state;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        movesLabel = new JLabel("Moves: 0", SwingConstants.CENTER);
        movesLabel.setPreferredSize(new Dimension(200, 30));

        boardView = new BoardView();
        boardView.setState(state);

        controller = new GameController(state, this, onBackToMenu);
        boardView.setController(controller);

        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(e -> controller.onSave());

        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> controller.onReset());

        JButton menuButton = new JButton("Menu");
        menuButton.addActionListener(e -> controller.onBackToMenu());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.CENTER));
        top.add(movesLabel);
        top.add(saveButton);
        top.add(resetButton);
        top.add(menuButton);

        add(top, BorderLayout.NORTH);
        add(boardView, BorderLayout.CENTER);

        setPreferredSize(new Dimension(640, 680));
        pack();
        setLocationRelativeTo(null);
    }

    public void refresh() {
        movesLabel.setText("Moves: " + state.getMoves());
        boardView.repaint();
    }
}
