
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.ImageIcon;

public class WinDialog extends JDialog {

    public WinDialog(JFrame owner, BufferedImage squareImage, int moves, Runnable onReplaySameImage, Runnable onBackToMenu) {
        super(owner, "You Win!", true);

        setLayout(new BorderLayout());

        JLabel title = new JLabel("You solved it!", SwingConstants.CENTER);
        JLabel movesLabel = new JLabel("Moves: " + moves, SwingConstants.CENTER);

        JPanel top = new JPanel(new java.awt.GridLayout(2, 1));
        top.add(title);
        top.add(movesLabel);

        add(top, BorderLayout.NORTH);

        JLabel imgLabel = new JLabel();
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        if (squareImage != null) {
            int target = 420;
            Image scaled = squareImage.getScaledInstance(target, target, Image.SCALE_SMOOTH);
            imgLabel.setIcon(new ImageIcon(scaled));
            imgLabel.setPreferredSize(new Dimension(target, target));
        }
        add(imgLabel, BorderLayout.CENTER);

        JButton replayBtn = new JButton("Play again (same image)");
        replayBtn.addActionListener(e -> {
            dispose();
            if (onReplaySameImage != null) onReplaySameImage.run();
        });

        JButton backBtn = new JButton("Change image / Menu");
        backBtn.addActionListener(e -> {
            dispose();
            if (onBackToMenu != null) onBackToMenu.run();
        });

        JPanel bottom = new JPanel();
        bottom.add(replayBtn);
        bottom.add(backBtn);
        add(bottom, BorderLayout.SOUTH);

        pack();
    }
}
