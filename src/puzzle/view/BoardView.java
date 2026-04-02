
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class BoardView extends JPanel {

    private GameState state;
    private GameController controller;

    public BoardView() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (state == null || controller == null) return;

                int gridSize = state.getSize();
                int w = getWidth();
                int h = getHeight();
                if (gridSize <= 0) return;

                int tileSize = Math.min(w, h) / gridSize;
                if (tileSize <= 0) return;

                int gridW = tileSize * gridSize;
                int gridH = tileSize * gridSize;
                int startX = (w - gridW) / 2;
                int startY = (h - gridH) / 2;

                int x = e.getX() - startX;
                int y = e.getY() - startY;
                if (x < 0 || y < 0 || x >= gridW || y >= gridH) return;

                int col = x / tileSize;
                int row = y / tileSize;

                controller.onTileClicked(row, col);
            }
        });
    }

    public void setState(GameState state) {
        this.state = state;
        repaint();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (state == null) return;

        int gridSize = state.getSize();
        int w = getWidth();
        int h = getHeight();
        int tileSize = Math.min(w, h) / gridSize;
        if (tileSize <= 0) return;

        int gridW = tileSize * gridSize;
        int gridH = tileSize * gridSize;
        int startX = (w - gridW) / 2;
        int startY = (h - gridH) / 2;

        Graphics2D g2 = (Graphics2D) g.create();
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                int x = startX + col * tileSize;
                int y = startY + row * tileSize;

                Tile tile = state.getBoard().getTile(row, col);

                // Cell background + border
                if (tile.isEmpty()) {
                    g2.setColor(new Color(235, 235, 235));
                    g2.fillRect(x, y, tileSize, tileSize);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRect(x, y, tileSize, tileSize);
                }
                g2.setColor(new Color(180, 180, 180));
                g2.drawRect(x, y, tileSize, tileSize);

                // Draw tile content
                if (!tile.isEmpty()) {
                    if (tile.getImagePiece() != null) {
                        Image img = tile.getImagePiece();
                        g2.drawImage(img, x, y, tileSize, tileSize, null);
                    } else {
                        String label = String.valueOf(tile.getId());
                        g2.setColor(Color.DARK_GRAY);
                        int strW = g2.getFontMetrics().stringWidth(label);
                        int strH = g2.getFontMetrics().getAscent();
                        g2.drawString(label, x + (tileSize - strW) / 2, y + (tileSize + strH) / 2 - 4);
                    }
                }
            }
        }
        g2.dispose();
    }
}
