package puzzle;

import java.awt.image.BufferedImage;

class Tile {
    int id;
    int correctRow, correctCol;
    int currentRow, currentCol;
    BufferedImage piece;
    boolean isEmpty;

    public  Tile(int id, int correctRow, int correctCol, BufferedImage piece, boolean isEmpty) {
        this.id = id;
        this.correctRow = correctRow;
        this.correctCol = correctCol;
        this.currentRow = correctRow;
        this.currentCol = correctCol;
        this.piece = piece;
        this.isEmpty = isEmpty;
    }

    public boolean isCorrect() {
        return currentRow == correctRow && currentCol == correctCol;
    }
}

