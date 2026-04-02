
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessor {

    public static class ProcessedImage {
        public final BufferedImage squareImage;
        // tilePieces[0] corresponds to tile id=1, ... tilePieces[size*size-2] corresponds to id=size*size-1.
        public final BufferedImage[] tilePieces;

        public ProcessedImage(BufferedImage squareImage, BufferedImage[] tilePieces) {
            this.squareImage = squareImage;
            this.tilePieces = tilePieces;
        }
    }

    // Loads, center-crops to square, scales to a deterministic size, then splits into grid tiles.
    // The lower-right tile is removed (treated as the empty space).
    public static ProcessedImage process(File imageFile, int gridSize) {
        if (imageFile == null) {
            throw new IllegalArgumentException("imageFile is null");
        }
        if (gridSize < 2 || gridSize > 5) {
            throw new IllegalArgumentException("gridSize must be between 2 and 5");
        }

        BufferedImage input;
        try {
            input = ImageIO.read(imageFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read image: " + imageFile.getAbsolutePath(), e);
        }

        if (input == null) {
            throw new RuntimeException("Unsupported/invalid image: " + imageFile.getAbsolutePath());
        }

        BufferedImage square = cropToSquareCenter(input);

        // Use a fixed target size so saved/opened games produce identical tile pieces.
        final int targetSide = 500;
        BufferedImage scaled = scaleToSquare(square, targetSide);

        int tileCount = gridSize * gridSize - 1;
        BufferedImage[] tilePieces = new BufferedImage[tileCount];

        int tileW = targetSide / gridSize;
        int tileH = tileW;

        int id = 1;
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                boolean isEmptyCell = (row == gridSize - 1 && col == gridSize - 1);
                if (isEmptyCell) {
                    continue;
                }

                int x = col * tileW;
                int y = row * tileH;
                tilePieces[id - 1] = scaled.getSubimage(x, y, tileW, tileH);
                id++;
            }
        }

        return new ProcessedImage(scaled, tilePieces);
    }

    private static BufferedImage cropToSquareCenter(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        int side = Math.min(w, h);

        int x0 = (w - side) / 2;
        int y0 = (h - side) / 2;

        BufferedImage square = img.getSubimage(x0, y0, side, side);

        // Ensure we have an RGB image for more consistent drawing/saving.
        BufferedImage rgb = new BufferedImage(side, side, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = rgb.createGraphics();
        g2.drawImage(square, 0, 0, null);
        g2.dispose();
        return rgb;
    }

    private static BufferedImage scaleToSquare(BufferedImage square, int targetSide) {
        BufferedImage scaled = new BufferedImage(targetSide, targetSide, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = scaled.createGraphics();
        g2.drawImage(square, 0, 0, targetSide, targetSide, null);
        g2.dispose();
        return scaled;
    }
}
