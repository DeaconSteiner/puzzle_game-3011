import java.awt.image.BufferedImage;

public class Tile {
    private int id;
    private int correctRow;
    private int correctCol;
    private int currentRow;
    private int currentCol;
    private BufferedImage imagePiece;
    private boolean isEmpty;

    public Tile(int id, int correctRow, int correctCol, BufferedImage imagePiece, boolean isEmpty) {
        this.id = id;
        this.correctRow = correctRow;
        this.correctCol = correctCol;
        this.currentRow = correctRow;
        this.currentCol = correctCol;
        this.imagePiece = imagePiece;
        this.isEmpty = isEmpty;
    }


    public int getId() {
        return id;
    }

    public int getCorrectRow() {
        return correctRow;
    }

    public int getCorrectCol() {
        return correctCol;
    }

    public int getCurrentRow() {
        return currentRow;
    }

    public int getCurrentCol() {
        return currentCol;
    }

    public BufferedImage getImagePiece() {
        return imagePiece;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setCurrentRow(int currentRow) {
        this.currentRow = currentRow;
    }

    public void setCurrentCol(int currentCol) {
        this.currentCol = currentCol;
    }

    public void setPosition(int row, int col) {
        this.currentRow = row;
        this.currentCol = col;
    }

    public boolean isInCorrectPosition() {
        return currentRow == correctRow && currentCol == correctCol;
    }

    @Override
    public String toString() {
        return "Tile{" +
                "id=" + id +
                ", correct=(" + correctRow + "," + correctCol + ")" +
                ", current=(" + currentRow + "," + currentCol + ")" +
                ", imagePiece='" + imagePiece + '\'' +
                ", isEmpty=" + isEmpty +
                '}';
    }
}