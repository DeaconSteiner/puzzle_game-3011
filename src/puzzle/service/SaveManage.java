
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class SaveManage {

    private static final String SAVE_DIR = "asset/saves";
    private static final String SAVE_FILE = SAVE_DIR + "/savegame.properties";

    public static class SaveData {
        public final int size;
        public final int moves;
        public final String imageFileName;
        public final int[][] openingLayoutIds;
        public final int[][] currentLayoutIds;

        public SaveData(int size, int moves, String imageFileName, int[][] openingLayoutIds, int[][] currentLayoutIds) {
            this.size = size;
            this.moves = moves;
            this.imageFileName = imageFileName;
            this.openingLayoutIds = openingLayoutIds;
            this.currentLayoutIds = currentLayoutIds;
        }
    }

    public static boolean hasSave() {
        return new File(SAVE_FILE).exists();
    }

    public static void save(GameState state) {
        if (state == null) return;
        if (state.getImageFileName() == null) return;
        ensureDir(new File(SAVE_DIR));

        Properties p = new Properties();
        p.setProperty("version", "1");
        p.setProperty("size", String.valueOf(state.getSize()));
        p.setProperty("moves", String.valueOf(state.getMoves()));
        p.setProperty("imageFileName", state.getImageFileName());
        p.setProperty("opening", layoutToString(state.exportOpeningLayoutIds()));
        p.setProperty("current", layoutToString(state.exportCurrentLayoutIds()));

        try (FileOutputStream out = new FileOutputStream(SAVE_FILE)) {
            p.store(out, "puzzle_game save");
        } catch (IOException e) {
            throw new RuntimeException("Failed to save game to " + SAVE_FILE, e);
        }
    }

    public static SaveData load() {
        Properties p = new Properties();
        try (FileInputStream in = new FileInputStream(SAVE_FILE)) {
            p.load(in);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load save file: " + SAVE_FILE, e);
        }

        int size = Integer.parseInt(p.getProperty("size"));
        int moves = Integer.parseInt(p.getProperty("moves"));
        String imageFileName = p.getProperty("imageFileName");
        int[][] opening = parseLayout(p.getProperty("opening"), size);
        int[][] current = parseLayout(p.getProperty("current"), size);

        return new SaveData(size, moves, imageFileName, opening, current);
    }

    private static void ensureDir(File dir) {
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private static String layoutToString(int[][] ids) {
        if (ids == null) return "";
        int size = ids.length;
        StringBuilder sb = new StringBuilder(size * size * 2);
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (sb.length() > 0) sb.append(',');
                sb.append(ids[r][c]);
            }
        }
        return sb.toString();
    }

    private static int[][] parseLayout(String s, int size) {
        if (s == null || s.trim().isEmpty()) {
            throw new IllegalArgumentException("layout string is empty");
        }
        String[] parts = s.split(",");
        if (parts.length != size * size) {
            throw new IllegalArgumentException("layout length mismatch: expected " + (size * size) + ", got " + parts.length);
        }
        int[][] ids = new int[size][size];
        int idx = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                ids[r][c] = Integer.parseInt(parts[idx]);
                idx++;
            }
        }
        return ids;
    }
}
