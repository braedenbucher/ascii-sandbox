import java.util.Arrays;
import java.util.Comparator;

public class blockrenderer {
    private int[][][] blocks; // 3d array
    private char[][] canvas; // printed to screen
    // private static final int X = 22; // alignment values that will be changed
    // private static final int Y = 20; // delete eventually

    public blockrenderer() {
        this(10,8,4);
    }

    public blockrenderer(int hZ, int wX, int dY) { // constructor initializes blocks and canvas
        int cH = 1 + dY + (2*hZ);
        int cW = 1 + (2*dY)+ (3*wX);
        blocks = new int[hZ][wX][dY];
        canvas = new char[cH][cW];
        // all block values to 0 (no block)
        for (int i = 0; i < hZ; i++) {for (int j = 0; j < wX; j++) {for (int k = 0; k < dY; k++) {blocks[i][j][k] = 0;}}}
        // all canvas values to ' ' (no char)
        for (int i = 0; i < cH; i++) {for (int j = 0; j < cW; j++) {canvas[i][j] = ' ';}}
    }

    public int[][][] blocks() {
        return blocks;
    }
    
    public char[][] canvas() {
        return canvas;
    }

    // Returns a list of 3-element tuples which contain the coordinates of each 
    // block that exist, sorted in ascending order by the sum of the elements in each coordinate.
    // This tells the draw function which coordinates to draw first so the blocks
    // in the back are printed first and the blocks in the front are printed last
    public static int[][][][] getSortedCoordinates(int[][][] blocks) {
        int X = blocks[0].length; // length of X
        int Y = blocks[0][0].length; // length of Y
        int Z = blocks.length; // length of Z
        int[][][][] sorted = new int[Z][X][Y][3]; // copy blocks list where each cell is now a 3-element list to put x, y, z coords into
        for (int z = 0; z < Z; z++) {
            for (int x = 0; x < X; x++) {
                for (int y = 0; y < Y; y++) {
                    sorted[z][x][y] = new int[]{z, x, y}; // each tuple element is that cell's coordinates
                }
            }
        }
        Arrays.sort(sorted, Comparator.comparingInt(t -> t[0][0][0] + t[0][0][1] + t[0][0][2])); // sort the list based on sum of the elements in each tuple
        return sorted;
    }

    public static void draw(int[][][] blocks, char[][] canvas, int[][][][] order) {
        for (int i = 0; i < canvas.length; i++) {for(int j = 0; j < canvas[0].length; j++) {canvas[i][j] = ' ';}} // all canvas chars to ' '
        for (int[][][] l1 : order) { // for each 3d list
        for (int[][] l2 : l1) { // for each 2d list
        for (int[] coord : l2) { // for each 1d triplet (coordinate)
            int x = coord[1]; 
            int y = coord[2];
            int z = coord[0];
            if (blocks[z][x][y] == 1) { // if block at current position
                int YY = (canvas.length-1) - ((1+z) * 2) - (blocks[0][0].length - y);
                int XX = (canvas[0].length-1) - ((1+x) * 3) - ((blocks[0][0].length - y) * 2);
    
                // Top of block, builds ___
                if (canvas[YY][XX + 1] == ' ') {
                    canvas[YY][XX + 1] = '_';
                }
                if (canvas[YY][XX + 2] == ' ') {
                    canvas[YY][XX + 2] = '_';
                }
                if (canvas[YY][XX + 3] == ' ') {
                    canvas[YY][XX + 3] = '_';
                }
                
                // Down a row, builds |\__\
                canvas[YY + 1][XX] = '|';
                canvas[YY + 1][XX + 1] = '\\';
                canvas[YY + 1][XX + 2] = '_';
                canvas[YY + 1][XX + 3] = '_';
                canvas[YY + 1][XX + 4] = '\\';
                
                // Down another row, builds | |  |
                canvas[YY + 2][XX] = '|';
                canvas[YY + 2][XX + 1] = ' ';
                canvas[YY + 2][XX + 2] = '|';
                canvas[YY + 2][XX + 3] = ' ';
                canvas[YY + 2][XX + 4] = ' ';
                canvas[YY + 2][XX + 5] = '|';
    
                // Down a third row, builds \|__|
                canvas[YY + 3][XX + 1] = '\\';
                canvas[YY + 3][XX + 2] = '|';
                canvas[YY + 3][XX + 3] = '_';
                canvas[YY + 3][XX + 4] = '_';
                canvas[YY + 3][XX + 5] = '|';
            }
        }
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

    public static void clearScreen() {  // clear the terminal using system code
        System.out.print("\033[H\033[2J"); // no i didnt write it
        System.out.flush();
    }

    public static void wait(int ms) { // pause the threat for X milliseconds
        try {
            Thread.sleep(ms);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt(); // no i didnt write this either
        }
    }

    public static void main(String[] args) {
        blockrenderer b = new blockrenderer(10, 10, 10);
        int[][][][] order = getSortedCoordinates(b.blocks());
        while(true) {
            b.rainBlocks(b.blocks);
            draw(b.blocks(),b.canvas(), order);
            display(b.canvas);
            b.physics(b.blocks);
            wait(10);
        }
    }
}

/*                    0____________
 *             2,3,0->|\__\__\__\__\
 *                    | |\__\__\__\__\
 * [hZ-1][wX-1][dY-1]---> |  |  |  |  |
 *   2     3     1    | |\|__|__|__|__|
 *                    |\| |  ||__|__|
 *                    | |\|__||  |  |
 *                     \| |  ||__|__|<------- 0,0,0
 *                       \|__|  
 * Canvas height (canvas.length): 9
 * Canvas width (canvas[0].length): 17
 * 
 * block[z][x][y] has XX of (canvas()[0].length-1) - ((1+x) * 3) - ((blocks[0][0].length - y) * 2)
 * Because x = 0 on canvas is on the opposite side of x = 0 in the block array, we need to subtract from the length of the array
 * and subtract 1 again (because .length is 1 more than the last index of an array). Then we need to take the number of blocks on 
 * the width axis and multiply it by the chars wide the x side is (3 chars). BUT, the 0th index block still needs to have a width
 * (0 * 3 = 0), we add 1 so that even if it's at index 0 it still is printed with 3 chars. Then, notice that the depth side is inverted. 
 * As we get farther from the camera, the index of depth DECREASES. Because of this, we need to subtract FROM the total number of blocks
 * on the depth axis. What are we subtracing? The depth index of the block. Then multiply by two as the depth axis on a block texture is
 * 2 chars wide. Now if a block is on depth index 0, it knows that 2 sets of depth chars need to be rendered.
 * 
 * block[z][x][y] has YY of (canvas().length-1) - ((1+z) * 2) - (blocks[0][0].length - y)
 * Because y = 0 on canvas is on the opposite side of y = 0 on the block array, we need to subtract from the length of the array
 * and subtract 1 again (because .length is 1 more than the last index of an array). Then, we need to take the number of blocks on
 * the height axis and multiply it by the number of chars on the height axis of each block(2 chars). However, a block on the 0th index
 * of the height axis in the blocks array still exists, we need to count those chars, so we will add 1 to the count. The depth side is
 * even easier. The depth side of each block is only 1 char tall, so all we do is subtract from that. Similar to XX, the depth side is
 * inverted, so a block on the 0th depth index needs 2 chars of depth axis instead of 1. Now, if a block is at depth 0, it will still
 * print all of the blocks' depth axis chars before printing the front side.
 */