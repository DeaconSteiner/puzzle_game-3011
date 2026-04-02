import java.awt.image.BufferedImage;
import java.io.File;

public class GameState {

    private Board board;
    private int moves;
    private boolean solved;
    private final int size;

    // For saving/loading and win-dialog rendering.
    private String imageFileName; // stored under asset/user_images/
    private int[][] openingLayoutIds; // tile ids after shuffle
    private BufferedImage squareImage; // cropped square of the selected image

    public GameState(int size) {
        this.size = size;
        this.board = new Board(size);
        this.moves = 0;
        this.solved = false;
        this.imageFileName = null;
        this.openingLayoutIds = null;
        this.squareImage = null;
    }

    public void startNewGame(File imageFile) {
        ImageProcessor.ProcessedImage processed = ImageProcessor.process(imageFile, size);
        this.squareImage = processed.squareImage;

        board.initializeSolvedBoard(processed.tilePieces);
        board.shuffle(); // sets board.initialTiles to the opening state

        this.openingLayoutIds = board.exportTileIds();
        this.imageFileName = imageFile.getName();

        moves = 0;
        solved = board.isSolved();
    }

    // Reset back to the "opening state" (the state after shuffle)
    public void reset() {
        board.reset();
        moves = 0;
        solved = board.isSolved();
    }

    // Load a previously saved game.
    public void loadFromSave(File imageFile, int[][] openingIds, int[][] currentIds, int moves) {
        ImageProcessor.ProcessedImage processed = ImageProcessor.process(imageFile, size);
        this.squareImage = processed.squareImage;
        this.imageFileName = imageFile.getName();

        board.initializeSolvedBoard(processed.tilePieces);

        // Set opening state, then capture it as reset target.
        board.setTilesFromIds(openingIds, processed.tilePieces);
        board.captureInitialTiles();

        // Apply current state.
        board.setTilesFromIds(currentIds, processed.tilePieces);

        this.openingLayoutIds = copy2D(openingIds);
        this.moves = moves;
        this.solved = board.isSolved();
    }

    // @return true if the move was valid and applied
    public boolean move(int row, int col) {
        boolean applied = board.moveTile(row, col);
        if (applied) {
            moves++;
            solved = board.isSolved();
        }
        return applied;
    }

    public Board getBoard() {
        return board;
    }

    public int getMoves() {
        return moves;
    }

    public boolean isSolved() {
        return solved;
    }

    public int getSize() {
        return size;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public BufferedImage getSquareImage() {
        return squareImage;
    }

    public int[][] exportOpeningLayoutIds() {
        return copy2D(openingLayoutIds);
    }

    public int[][] exportCurrentLayoutIds() {
        return board.exportTileIds();
    }

    private static int[][] copy2D(int[][] src) {
        if (src == null) return null;
        int[][] copy = new int[src.length][];
        for (int i = 0; i < src.length; i++) {
            copy[i] = src[i].clone();
        }
        return copy;
    }
}
