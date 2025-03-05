import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Scene {
    private final ArrayList<int[]> blocks;
    private final char[][] canvas;
    private final int b_depth;
    private final int c_width;
    private final int c_height;

    public Scene(int width, int height, int depth) {
        blocks = new ArrayList<>();
        b_depth = depth;

        c_width = 1 + (2*depth)+ (3*width);
        c_height = 1 + depth + (2*height);
        canvas = new char[c_height][c_width];
        update_canvas();
    }

    public void add_block(int horizontal, int vertical, int layer) {
        int[] block = new int[6];
        block[0] = horizontal;
        block[1] = vertical;
        block[2] = layer;

        block[3] = (c_width - 1) - ((1 + horizontal) * 3) - (b_depth - layer) * 2; // 2d projection top right x-coord
        block[4] = (c_height - 1) - ((1 + vertical) * 2) - (b_depth - layer); // 2d projection top right y-coord

        block[5] = horizontal + vertical + layer; // priority

        int insertion_index = Collections.binarySearch(blocks, block, Comparator.comparingInt(a -> a[5]));
        if (insertion_index < 0) { insertion_index = -insertion_index - 1;}

        blocks.add(insertion_index, block); // insert into sorted position
    }

    public void remove_block(int horizontal, int vertical, int layer) {
        blocks.removeIf(block -> block[0] == horizontal && block[1] == vertical && block[2] == layer);
    }

    public void update_canvas() {
        for (char[] row : canvas) { Arrays.fill(row, ' '); }
        char[][] texture = Textures.default_texture;
        for(int[] block: blocks) {
            for(int y = 0; y < 4; y++) {
                for(int x = 0; x < 6; x++) {
                    if (texture[y][x] == 'X') {continue;}
                    if (y == 0 && canvas[block[4] + y][block[3] + x] != ' ') {continue;}
                    canvas[block[4] + y][block[3] + x] = texture[y][x];
                }
            }
        }
    }

    public String construct_canvas() {
        StringBuilder out = new StringBuilder();
        for (char[] row : canvas) { for (char ch : row) { out.append(ch); } out.append("\n"); }
        return out.toString();
    }
}
