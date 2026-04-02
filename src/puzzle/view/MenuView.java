
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuView extends JFrame {

    private static final String USER_IMAGES_DIR = "asset/user_images";

    private final JComboBox<String> imageCombo;
    private final JComboBox<Integer> gridSizeCombo;

    private MenuController controller;

    public MenuView() {
        super("New Game - Puzzle");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel title = new JLabel("Choose an image and difficulty", SwingConstants.CENTER);

        imageCombo = new JComboBox<>();
        gridSizeCombo = new JComboBox<>(new Integer[]{2, 3, 4, 5});

        JPanel center = new JPanel(new FlowLayout(FlowLayout.CENTER));
        center.add(new JLabel("Image:"));
        center.add(imageCombo);
        center.add(new JLabel("Grid:"));
        center.add(gridSizeCombo);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addImageBtn = new JButton("Add Image");
        addImageBtn.addActionListener(e -> onAddImageClicked());

        JButton startBtn = new JButton("Start");
        startBtn.addActionListener(e -> {
            if (controller != null) controller.startNewGame();
        });

        bottom.add(addImageBtn);
        bottom.add(startBtn);

        add(title, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        setPreferredSize(new Dimension(720, 200));
        pack();
        setLocationRelativeTo(null);

        reloadImageList();
    }

    public void setController(MenuController controller) {
        this.controller = controller;
    }

    public String getSelectedImageFileName() {
        Object v = imageCombo.getSelectedItem();
        if (v == null) return null;
        return v.toString();
    }

    public int getSelectedGridSize() {
        Object v = gridSizeCombo.getSelectedItem();
        if (v == null) return 4;
        return (Integer) v;
    }

    public void reloadImageList() {
        File dir = new File(USER_IMAGES_DIR);
        if (!dir.exists()) dir.mkdirs();

        File[] files = dir.listFiles();
        imageCombo.removeAllItems();

        if (files == null) return;

        for (File f : files) {
            if (!f.isFile()) continue;
            String name = f.getName().toLowerCase();
            boolean ok = name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".bmp") || name.endsWith(".gif");
            if (ok) {
                imageCombo.addItem(f.getName());
            }
        }
    }

    public void selectImageFileName(String fileName) {
        if (fileName == null) return;
        imageCombo.setSelectedItem(fileName);
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void onAddImageClicked() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res != JFileChooser.APPROVE_OPTION) return;

        File selected = chooser.getSelectedFile();
        if (selected == null) return;

        if (controller != null) {
            controller.addImage(selected);
        }
    }
}
