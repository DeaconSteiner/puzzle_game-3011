
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.util.function.Consumer;

public class MenuController {

    private static final String USER_IMAGES_DIR = "asset/user_images";

    private final MenuView menuView;
    private final Consumer<GameState> onStartGame;

    public MenuController(MenuView menuView, Consumer<GameState> onStartGame) {
        this.menuView = menuView;
        this.onStartGame = onStartGame;
    }

    public void startNewGame() {
        String selectedName = menuView.getSelectedImageFileName();
        int size = menuView.getSelectedGridSize();

        if (selectedName == null || selectedName.trim().isEmpty()) {
            menuView.showError("Please select an image.");
            return;
        }

        File imageFile = new File(USER_IMAGES_DIR, selectedName);
        if (!imageFile.exists()) {
            menuView.showError("Image not found: " + imageFile.getAbsolutePath());
            return;
        }

        GameState state = new GameState(size);
        state.startNewGame(imageFile);

        if (onStartGame != null) {
            menuView.dispose();
            onStartGame.accept(state);
        }
    }

    public void addImage(File sourceFile) {
        if (sourceFile == null || !sourceFile.exists()) return;

        File dir = new File(USER_IMAGES_DIR);
        if (!dir.exists()) dir.mkdirs();

        String originalName = sourceFile.getName();
        String targetName = System.currentTimeMillis() + "_" + originalName;
        File targetFile = new File(dir, targetName);

        try {
            Files.copy(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy image to " + targetFile.getAbsolutePath(), e);
        }

        menuView.reloadImageList();
        menuView.selectImageFileName(targetName);
    }
}
