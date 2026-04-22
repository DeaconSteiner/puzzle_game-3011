
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.Timer;

public class BoardView extends JPanel {

    private static final int ANIM_DURATION_MS = 150;
    private static final int ANIM_TICK_MS = 16;

    private GameState state;
    private GameController controller;

    private Timer animTimer;
    private boolean animation;
    private int fromRow;
    private int fromCol;
    private int toRow;
    private int toCol;
    private BufferedImage slideImage;
    private int slideId;
    private long animStartTimeMs;
    private Runnable pendingOnComplete;

    public BoardView() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (state == null || controller == null) return;
                if (animation) return;

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

    /**
     * Plays slide animation from (row,col) to the empty cell, then runs onComplete (typically applies the move).
     */
    public void startSlideAnimation(int row, int col, Runnable onComplete) {
        if (state == null) return;
        Board board = state.getBoard();
        if (!board.canMove(row, col)) {
            return;
        }

        cancelSlideAnimation();

        Tile tile = board.getTile(row, col);
        if (tile.isEmpty()) return;

        fromRow = row;
        fromCol = col;
        toRow = board.getEmptyRow();
        toCol = board.getEmptyCol();
        slideImage = tile.getImagePiece();
        slideId = tile.getId();
        pendingOnComplete = onComplete;
        animation = true;
        animStartTimeMs = System.currentTimeMillis();

        if (animTimer == null) {
            animTimer = new Timer(ANIM_TICK_MS, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!animation) return;
                    long elapsed = System.currentTimeMillis() - animStartTimeMs;
                    if (elapsed >= ANIM_DURATION_MS) {
                        animTimer.stop();
                        animation = false;
                        Runnable done = pendingOnComplete;
                        pendingOnComplete = null;
                        slideImage = null;
                        if (done != null) {
                            done.run();
                        }
                        repaint();
                    } else {
                        repaint();
                    }
                }
            });
        }
        animTimer.start();
        repaint();
    }

    public void cancelSlideAnimation() {
        if (animTimer != null) {
            animTimer.stop();
        }
        animation = false;
        pendingOnComplete = null;
        slideImage = null;
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
        applyDrawQualityHints(g2);
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                drawCell(g2, row, col, tileSize, startX, startY);
            }
        }

        if (animation) {
            float t = Math.min(1f,
                (System.currentTimeMillis() - animStartTimeMs) / (float) ANIM_DURATION_MS);
            float sx = startX + fromCol * tileSize;
            float sy = startY + fromRow * tileSize;
            float ex = startX + toCol * tileSize;
            float ey = startY + toRow * tileSize;
            float px = sx + (ex - sx) * t;
            float py = sy + (ey - sy) * t;
            int ix = Math.round(px);
            int iy = Math.round(py);

            if (slideImage != null) {
                g2.drawImage(slideImage, ix, iy, tileSize, tileSize, null);
            } else {
                g2.setColor(Color.WHITE);
                g2.fillRect(ix, iy, tileSize, tileSize);
                g2.setColor(new Color(180, 180, 180));
                g2.drawRect(ix, iy, tileSize, tileSize);
                String label = String.valueOf(slideId);
                g2.setColor(Color.DARK_GRAY);
                int strW = g2.getFontMetrics().stringWidth(label);
                int strH = g2.getFontMetrics().getAscent();
                g2.drawString(label, ix + (tileSize - strW) / 2, iy + (tileSize + strH) / 2 - 4);
            }
        }

        g2.dispose();
    }

    private static void applyDrawQualityHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
    }

    private void drawCell(Graphics2D g2, int row, int col, int tileSize, int startX, int startY) {
        int x = startX + col * tileSize;
        int y = startY + row * tileSize;

        Tile tile = state.getBoard().getTile(row, col);

        boolean hideMoving = animation && row == fromRow && col == fromCol;

        if (tile.isEmpty() || hideMoving) {
            g2.setColor(new Color(235, 235, 235));
            g2.fillRect(x, y, tileSize, tileSize);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRect(x, y, tileSize, tileSize);
        }
        g2.setColor(new Color(180, 180, 180));
        g2.drawRect(x, y, tileSize, tileSize);

        if (!tile.isEmpty() && !hideMoving) {
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
