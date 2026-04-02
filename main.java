public class main {
    private static final String USER_IMAGES_DIR = "asset/user_images";

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            java.io.File imgDir = new java.io.File(USER_IMAGES_DIR);
            if (!imgDir.exists()) imgDir.mkdirs();

            if (SaveManage.hasSave()) {
                int choice = javax.swing.JOptionPane.showOptionDialog(
                    null,
                    "A saved game was found. Continue?",
                    "Puzzle Game",
                    javax.swing.JOptionPane.YES_NO_OPTION,
                    javax.swing.JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Continue", "New Game"},
                    "Continue"
                );

                if (choice == 0) {
                    showContinue();
                    return;
                }
            }

            showMenu();
        });
    }

    private static void showContinue() {
        SaveManage.SaveData data = SaveManage.load();
        java.io.File imgFile = new java.io.File(USER_IMAGES_DIR, data.imageFileName);

        GameState state = new GameState(data.size);
        state.loadFromSave(imgFile, data.openingLayoutIds, data.currentLayoutIds, data.moves);

        showGame(state);
    }

    private static void showMenu() {
        MenuView menu = new MenuView();
        MenuController controller = new MenuController(menu, state -> showGame(state));
        menu.setController(controller);
        menu.setVisible(true);
    }

    private static void showGame(GameState state) {
        GameView game = new GameView(state, () -> showMenu());
        game.setVisible(true);
    }
}
