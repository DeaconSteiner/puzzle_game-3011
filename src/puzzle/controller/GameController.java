
import java.io.File;

import javax.swing.JOptionPane;

public class GameController {

    private final GameState state;
    private final GameView gameView;
    private final Runnable onBackToMenu;

    public GameController(GameState state, GameView gameView, Runnable onBackToMenu) {
        this.state = state;
        this.gameView = gameView;
        this.onBackToMenu = onBackToMenu;
    }

    public GameState getState() {
        return state;
    }

    public void onTileClicked(int row, int col) {
        boolean moved = state.move(row, col);
        if (!moved) return;

        gameView.refresh();

        if (state.isSolved()) {
            showWinDialog();
        }
    }

    public void onReset() {
        state.reset();
        gameView.refresh();
    }

    public void onSave() {
        SaveManage.save(state);
        JOptionPane.showMessageDialog(gameView, "Game saved.", "Save", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onBackToMenu() {
        gameView.dispose();
        if (onBackToMenu != null) {
            onBackToMenu.run();
        }
    }

    private void showWinDialog() {
        WinDialog dialog = new WinDialog(
            gameView,
            state.getSquareImage(),
            state.getMoves(),
            () -> replayWithSameImage(),
            () -> onBackToMenu()
        );
        dialog.setLocationRelativeTo(gameView);
        dialog.setVisible(true);
    }

    private void replayWithSameImage() {
        if (state.getImageFileName() == null) return;
        File file = new File("asset/user_images/" + state.getImageFileName());
        if (!file.exists()) {
            JOptionPane.showMessageDialog(gameView, "Image not found: " + file.getAbsolutePath(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        state.startNewGame(file);
        gameView.refresh();
    }
}
