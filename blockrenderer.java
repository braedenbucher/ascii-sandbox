
import java.util.ArrayList; // for tuple
import java.util.Collections; // for sorted coordinates
import java.util.List; // for tuple

public class blockrenderer {
    private static class Tuple<X, Y, Z> { // mimic tuple variable in python, quick 3-element list
        public final X x; // first element
        public final Y y; // second element
        public final Z z; // third element
        public Tuple(X x, Y y, Z z) { // constructor to initialize elements
            this.x = x;
            this.y = y;
            this.z = z;
        }
        public int sum() { // sum the elements
            return ((Integer) x) + ((Integer) y) + ((Integer) z);
        }
    }

    private int[][][] blocks; // 3d array
    private static char[][] canvas; // printed to screen
    private static int X = 22; // alignment values
    private static int Y = 20;

    public blockrenderer() {
        this(10,8,4);
    }

    public blockrenderer(int hZ, int wX, int dY) { // constructor initializes blocks and canvas
        int cH = 3 + dY + (2*hZ);
        int cW = 19 + (2*dY)+ (3*wX);
        this.blocks = new int[hZ][wX][dY];
        canvas = new char[cH][cW];
        // all block values to 0 (no block)
        for (int i = 0; i < hZ; i++) {for (int j = 0; j < wX; j++) {for (int k = 0; k < dY; k++) {blocks[i][j][k] = 0;}}}
        // all canvas values to ' ' (no char)
        for (int i = 0; i < cH; i++) {for (int j = 0; j < cW; j++) {canvas[i][j] = ' ';}}
    }

    public int[][][] blocks() {
        return blocks;
    }
    
    public static char[][] canvas() {
        return canvas;
    }

    public static void clear() { // all chars on canvas to ' '
        for(int i = 0; i < canvas.length; i++) {
            for(int j = 0; j < canvas[0].length; j++) {
                canvas[i][j] = ' '; // for all chars make them ' '
            }
        }
    }

    // Returns a list of 3-element tuples which contain the coordinates of each 
    // block that exist, sorted in ascending order by the sum of the elements in each coordinate.
    // This tells the draw function which coordinates to draw first so the blocks
    // in the back are printed first and the blocks in the front are printed last */
    public static List<Tuple<Integer, Integer, Integer>> getSortedCoordinates(int[][][] blocks) {
        int X = blocks[0].length; // length of X
        int Y = blocks[0][0].length; // length of Y
        int Z = blocks.length; // length of Z
        List<Tuple<Integer, Integer, Integer>> mylist = new ArrayList<>(); // make a list of 3 element tuples of the same dimensions
        for (int z = 0; z < Z; z++) {
            for (int y = 0; y < Y; y++) {
                for (int x = 0; x < X; x++) {
                    mylist.add(new Tuple<>(x, y, z)); // each tuple element is that cell's coordinates
                }
            }
        }
        Collections.sort(mylist, (t1, t2) -> Integer.compare(t1.sum(), t2.sum())); // command to sort tuple list I copied from internet
        return mylist;
    }

    public static void draw(int[][][] blocks, char[][] canvas, List<Tuple<Integer, Integer, Integer>> order) {
        clear(); // wipe existing canvas
        for (Tuple<Integer, Integer, Integer> tuple : order) { // for each coord in sorted coord system
            int x = tuple.x; 
            int y = tuple.y;
            int z = tuple.z;
            if (blocks[z][x][y] == 1) { // if block at current position
                int YY = Y - (2 * z) + y; // alignment i can explain not in text
                int XX = X - (3 * x) + (2 * y); // ^^^^
    
                // Top of block, builds ___
                if (canvas[YY][XX] == ' ') {
                    canvas[YY][XX] = '_';
                }
                if (canvas[YY][XX + 1] == ' ') {
                    canvas[YY][XX + 1] = '_';
                }
                if (canvas[YY][XX + 2] == ' ') {
                    canvas[YY][XX + 2] = '_';
                }
                
                // Down a row, builds |\__\
                canvas[YY + 1][XX - 1] = '|';
                canvas[YY + 1][XX] = '\\';
                canvas[YY + 1][XX + 1] = '_';
                canvas[YY + 1][XX + 2] = '_';
                canvas[YY + 1][XX + 3] = '\\';
                
                // Down another row, builds | |  |
                canvas[YY + 2][XX - 1] = '|';
                canvas[YY + 2][XX] = ' ';
                canvas[YY + 2][XX + 1] = '|';
                canvas[YY + 2][XX + 2] = ' ';
                canvas[YY + 2][XX + 3] = ' ';
                canvas[YY + 2][XX + 4] = '|';
    
                // Down a third row, builds \|__|
                canvas[YY + 3][XX] = '\\';
                canvas[YY + 3][XX + 1] = '|';
                canvas[YY + 3][XX + 2] = '_';
                canvas[YY + 3][XX + 3] = '_';
                canvas[YY + 3][XX + 4] = '|';
            }
        }
    }

    // For each frame, add a block under every current block,
    // and eliminate the upper block, also empties rows
    public void physics(int[][][] blocks) {
        for (int y = 0; y < blocks[0][0].length; y++) { // for all cells
            for (int x = 0; x < blocks[0].length; x++) {
                for (int z = 0; z < blocks.length - 1; z++) {
                    if (blocks[z + 1][x][y] == 1) { // if upper cell has block
                        if (blocks[z][x][y] == 0) { // but this cell has no block
                            blocks[z][x][y] += blocks[z + 1][x][y]; // this cell now has block
                            blocks[z + 1][x][y] = 0; // upper cell now has no block
                        }
                    }
                }
            }
        }
        int emptyCount = 0; // count of empty cells on bottom layer
        for (int[] row : blocks[0]) {
            for (int block : row) {
                if (block == 0) {
                    emptyCount++;
                }
            }
        }
        if (emptyCount == 0) { // if bottom layer is all filled
            // Delete the bottom layer by shifting all the layers above it down by one
            for (int i = 0; i < blocks.length - 1; i++) {
                for (int j = 0; j < blocks[0].length; j++) {
                    System.arraycopy(blocks[i + 1][j], 0, blocks[i][j], 0, blocks[0][0].length);
                }
            }
            // Fill the top layer with zeros
            for (int i = 0; i < blocks[0].length; i++) {
                for (int j = 0; j < blocks[0][0].length; j++) {
                    blocks[blocks.length - 1][i][j] = 0;
                }
            }
        }
    }

    public static void clearScreen() {  // code to help clear the terminal using system code
        System.out.print("\033[H\033[2J"); // no i didnt write it
        System.out.flush();
    }

    public static void wait(int ms) { // to pause for some milliseconds
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
    
    public static void display(char[][] canvas) {
        StringBuilder temp = new StringBuilder(); // some way to turn chars into a string
        for (char[] line : canvas) { // for all lines on canvas
            for (char pixel : line) { // for all chars on line
                temp.append(pixel); // append current char to output
            }
            temp.append("\n"); // new line every time theres... a new line
        }
        clearScreen(); // call that clear screen method
        System.out.print(temp.toString()); // print total result
    } 
    
    // randomly throws blocks around the canvas
    public void randomize(int[][][] blocks) {
        for(int z = 0; z < blocks.length; z++) {
            for (int x = 0; x < blocks[0].length; x++) {
                for(int y = 0; y < blocks[0][0].length; y++) {
                    if (Math.random() > .9)
                        blocks[z][x][y] = 1;
                }
            }
        }
    }
    // randomly adds blocks only to top layer for rain mode
    public void rainBlocks(int[][][] blocks) {
        int z = blocks.length-1;
        for(int x = 0; x < blocks[0].length; x++) {
            for(int y = 0; y < blocks[0][0].length; y++) {
                if (Math.random() > .99) {
                    blocks[z][x][y] = 1;
                } else {
                    blocks[z][x][y] = 0;
                }
            }
        }
    }

    public static void main(String[] args) {
        blockrenderer b = new blockrenderer();
        List<Tuple<Integer,Integer,Integer>> order = getSortedCoordinates(b.blocks());
        while(true) {
            b.rainBlocks(b.blocks);
            draw(b.blocks(),canvas(), order);
            display(blockrenderer.canvas);
            b.physics(b.blocks);
            wait(10);
        }
    }
}