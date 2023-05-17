
# Block Renderer
Takes a 3d grid of blocks, and renders them isometrically with ASCII characters.

```java
      ___
     |\__\          
     | |  |                                ___
     |\|__|__                  ___        |\__\
     | |  |__\                |\__\       | |  |        
     |\|__||  |               |\__\|__   |\\|__|        
     | |  ||__|               | |  |__\_ | |  |
     |\|__||  |             __|\|__||\__\|\|__|
    |\__\ ||__|   ___      |\_| |  || |  | |  |
    | |  |||  |  |\__\____ | ||\|__||\|__|\|__|
    |\|__|||__|__|_|  |\__\|\|| |  || |  | |\__\        
    | |  |||\__|\__\__| |  |_||\|__||\|__||\__\ |       
    |\|__|_\ |\| |  | |\|__|_\| |  || |  || |  ||       
 ___| |  |  || |\|__||\__\_|  |\|__||\|__|\\|__|_\_     
|\__|\|__|__|||\__\ || |\__\__| |  || |  |__\| |\__\    
| | | |\__\ ||| |  |||\| |  |__\|__|_\|__|_\__\| |  |   
|\|_|\| |  ||||\|__||| |\|__||  |  |  |\__\ |  |\|__|   
| |\| |\|__||\| |  ||_\| |  ||__|__|__| |  ||__| |  |_  
 \| |\| |\__\ |\|__|_\__\|__||  |\__\ |\|__||\__\|__|_\ 
   \| |\| |  || |  |  |  |  ||__| |  || |  || |  |  |  |
     \| |\|__|_\|__|__|__|__||  |\|__||\|__||\|__|__|__|
       \| |  |  |  |  |  |  ||__| |  || |  || |  |  |  |
         \|__|__|__|__|__|__|    \|__| \|__| \|__|__|__|
```
## Usage
To create a scene to render, construct a blockrenderer object with the height, width, and depth of your space (respectively).
```java
blockrenderer b = new blockrenderer(10, 8, 4);
```
Next, create the sorted coordinate list of your blockrenderer scene, which will allow the printer to know in which order to print blocks. You can skip this step and input the value directly into the draw method call if you wish, but I find this to be easier to read.
```java
int[][][][] order = getSortedCoordinates(b.blocks());
```
Printing the scene is composed of 2 calls. The draw() method maps the 3d scene to the canvas, and then the display() method builds a string of the scene to print out to the terminal.
```java
draw(b.blocks(),b.canvas(), order);
display(b.canvas);
```
To test it's functionality, there are a few methods to simulate a rain-like effect with blocks piling up:
```java
b.randomize(b.blocks);
while(true) {
    b.rainBlocks(b.blocks);
    draw(b.blocks(),b.canvas(), order);
    display(b.canvas);
    b.physics(b.blocks);
    wait(10);
}
```
The segment simply initializes the grid with a random assortment of blocks, draws and displays them to the screen, moves all unsupported blocks one cell downwards, and then pauses the thread for ~10ms. Method explanations below.
## Explanation
Quick summary: Each object has a 3D grid and a 2D canvas for printing. The 2D canvas is created based on the size of the 3D grid when an object is created. Sorted coordinates determine which blocks to print in the back and front, and the printer method appends characters to the canvas based on this order. The display method builds a string to print to the screen. The following modules go more in-depth into each method.

### Scene attributes
A render object has 2 attrbute components, the 3d grid of integers, and the 2d array of chars that is printed to the screen. The grid really only utilizes 0 or 1(empty/block), but other numbers can be used to identify different types of blocks. There are two helper methods to pull the grid and canvas:
- object.blocks()
- object.canvas()

### The Constructor
```java
public blockrenderer(int hZ, int wX, int dY)
```
The constructor takes in the height, width, and depth of the grid. 
- hZ indicates height & z-axis are the same
- wX indicates width & x-axis are the same
- dY indicates depth & y-axis are the same
Along with this, the signature indicates what the coordinate system is. Blocks are identified by [z,x,y] instead of [x, y, z].

Next, the canvas is defined based on the grid's dimensions. A block texture is depicted below:
```java
 ___       _________
|\__\     |\__\__\__\ 1
| |  |    | |  |  |  |  2   <--- row of blocks
 \|__|   1 \|__|__|__|  1
              1  2  3
```
The canvas height and width are calculated by:
```java
int cH = 1 + dY + (2*hZ);
int cW = 1 + (2*dY)+ (3*wX);
```
For example, the row of blocks above would have a canvas height width of:
```java
4 = 1 + 1 + (2 * 1)
12 = 1 + (2 * 1) + (3 * 3)
```
Other than that, the grid is initialized to 0 (empty tiles) and the canvas is initialized to ' ' (whitespace char).

### Sorting the coordinates
```java
public static int[][][][] getSortedCoordinates(int[][][] blocks)
```
When you view two objects, one in front of the other, which object overlays the other? The object closer to you. When we draw block textures to the canvas, we need to be aware that blocks behind other blocks need to be printed **first**. The way I have implemented this is by *sorting the coordinates*. All we do is to take in the 3d grid, and throw out a list where each cell is it's own coordinates. That way our draw function can make sure that the first blocks printed are printed to the back.

For this method, I created an X, Y, and Z variable to store the length of that axis.
Then, I initialized a 4d list. 
```java
int[][][][] sorted = new int[Z][X][Y][3];
```
The first 3 dimensions are the same dimensions as the 3d grid. Now, instead of ints being stored in each element, ANOTHER list is stored. This list contains only 3 elements, which are that cell's coordinates.

Then, I assigned each cell(the 3-element list) to the coordinates of that cell.
```java
sorted[z][x][y] = new int[]{z, x, y};
```
Finally, the array class sorts the 4d list based on a comparator being the sum of the elements in each tuple. This means that the first element is [0,0,0] and so on.
```java
Arrays.sort(sorted, Comparator.comparingInt(t -> t[0][0][0] + t[0][0][1] + t[0][0][2]));
```
So when returned to a variable, the printer method can make sure to print blocks in the far back first.

### The printer method
```java
public static void draw(int[][][] blocks, char[][] canvas, int[][][][] order)
```
First thing's first, the canvas is wiped so every char is back to being ' '. Then, 3 for-each loops scroll through the 3d list to each 3-element coordinate. Convenience variables are assigned to the coordinate's z, x, and y values.

Next, we need to take the 3d coordinates we were given, and throw out the x and y coordinates on the 2d canvas to print the block. These x and y coordinates are referred to as XX and YY:
```java
int YY = (canvas.length-1) - ((1+z) * 2) - (blocks[0][0].length - y);
```
Because y = 0 on canvas is on the opposite side of y = 0 on the block array, we need to subtract from the length of the array and subtract 1 again (because .length is 1 more than the last index of an array). Then, we need to take the number of blocks on the height axis and multiply it by the number of chars on the height axis of each block(2 chars). However, a block on the 0th index of the height axis in the blocks array still exists, we need to count those chars, so we will add 1 to the count. The depth side is even easier. The depth side of each block is only 1 char tall, so all we do is subtract from that. Similar to XX, the depth side is inverted, so a block on the 0th depth index needs 2 chars of depth axis instead of 1. Now, if a block is at depth 0, it will still print all of the blocks' depth axis chars before printing the front side.
```java
int XX = (canvas[0].length-1) - ((1+x) * 3) - (blocks[0][0].length - y) * 2;
```

Because x = 0 on canvas is on the opposite side of x = 0 in the block array, we need to subtract from the length of the array and subtract 1 again (because .length is 1 more than the last index of an array). Then we need to take the number of blocks on the width axis and multiply it by the chars wide the x side is (3 chars). BUT, the 0th index block still needs to have a width (0 * 3 = 0), we add 1 so that even if it's at index 0 it still is printed with 3 chars. Then, notice that the depth side is inverted. As we get farther from the camera, the index of depth DECREASES. Because of this, we need to subtract FROM the total number of blocks on the depth axis. What are we subtracing? The depth index of the block. Then multiply by two as the depth axis on a block texture is 2 chars wide. Now if a block is on depth index 0, it knows that 2 sets of depth chars need to be rendered.

Then, each block is printed according to the following alignment(XX and YY is denoted by 0):
```java
0___
|\__\
| |  |
 \|__|
```
It's printed in layers, so each row of the texture is assigned in succession.
Now the canvas list can be displayed to the screen.

### Display to the screen
```java
public static void display(char[][] canvas)
```
The most straightforward out of all of these methods. A stringbuilder object takes a multitude of chars and creates one string out of it. We just initialize the object, and then throw the entirety of the canvas onto it. One println command later and its on the screen.

### Clearing the terminal & wait command
```java
public static void clearScreen()
    System.out.print("\u001b[H\u001b[0J");
    System.out.flush();
```
The first line is where the actual clearing of the screen occurs. It sends two escape codes to the terminal. Th first escape code '\u001b[H' moves the cursor to the top right of the terminal. The second escape code '\u001b[0J' clears the terminal from the cursor to the end of the screen. People who are proficient in ANSI escape codes may question why I don't use '\u001b[2J' which clears the entire screen by default without the need for cursor movement. It is because the terminals will have a siezure with the entire screen 'shaking' in a way. This is because the cursor jumps from 0,0 to the end of the terminal every 'frame', causing an extra line to be aded to the bottom of the terminal where the cursor jumps to, so every other frame the entire text is shifted up one line. This maintains the cursor at 0,0 and prevents it from jumping to the end.

The second line flushes the output stream, ensuring that all characters are written to the terminal before the method returns.

```java
public static void wait(int ms)
    try {
        Thread.sleep(ms);
    } catch(InterruptedException ex) {
        Thread.currentThread().interrupt();
    }
```
The first line simply tells java to run the code, but if a certain error occurs(InterruptedException in this case), then don't exit the program, run the catch sequence. The actual Thread.sleep command uses a *long* type to stop the execution of the current output.
The catch sequence is to... interrupt the interrupt. It's kind of a double negative to cancel out the exception.

### Physics
```java
public void physics(int[][][] blocks)
```
Another straightforward method, it goes through every cell, and if a block is found does the following:
- Add a block below the current one
- Delete the current block
Then, towards the end, it will look through ONLY the bottom layer. It will count every empty space it finds, and if there are no empty spaces (e.g. the entire bottom row is filled):
- Shift the values of every layer down one.
- Wipe the top layer so it's just empty spaces

### Randomization
```java
public void randomize(int[][][] blocks)
public void rainBlocks(int[][][] blocks)
```
To make use of the rendering and physics, I made two methods to simulate a 'rain' of blocks.

The randomize() method goes through every block in the grid, and if a random decimal 0-1 is > .9, then it will add a block at that cell.

The rainBlocks() method goes through every block in the top layer of the grid, and if a random decimal 0-1 is > .99, then ti will add a block at that cell.
## Major Upates - 5/8 [LATEST]
After two weeks, I have finally finished patching the renderer. The equations to find the x and y values on the canvas have been fixed. 

First, The two alignment variables that were used in the previous code are gone, as they were really never necessary. 

Second, the previous canvas coords from 3d coords (called XX and YY) were entirely incorrect in the previous code, and mapped the location blocks needed to start printing at 1 char to the right. I have updated the draw function's texture printer to refled the 1 char alignment difference.

Third and most importantly, the math to plot the canvas coords is fixed. It takes the 3d coords, spits out the upper-leftmost corner of the texture, and never runs out of bounds of the canvas. Renderer usable.

## Major Updates - 4/28
To update the limited capability of the code and optimize it's performace, I am reworking the math needed to run the draw() function. To start, the canvas has been on a set size this entire time. Doing the math, I updated the constructor to initialize a canvas that fits the necessary block chars perfectly(Based on a block being 3 chars on x, 2 chars on z, and 1 char on y). This breaks the function as the math to take 3d coordinates and print them on a 2d canvas now no longer works and repeatedly throws out of bounds errors. Renderer currently unusable

## Major Updates - 4/24
I have removed the clear() method entirely. This means that the canvas attribute can be non-static without throwing issues. Realizing that the clear() method is only used **once** in the file (in the draw() method), I removed it's call and simply cleared the array manually **in** the draw() method. Renderer usable, but limited.

## Major updates - 4/24
My first task was to organize it, so I decided to rework tuple system. While it *technically* works, it's incredibly inefficient, hard to deal with, and the sortedCoordinates() method (the only method that uses the class) is very hard to read. I have removed the class, and added 1 extra dimension to the output of sortedCoordinates(). Instead of returning a 3d array of tuple elements, sortedCoordinates() returns a 4d array where the extra dimension is a 3-element integer list with the respective blocks' coordinates. Renderer still unusable

## Major Updates - 4/10
I have converted the initial python code to java, but it's still a mess. I needed to mimick the tuple data type with a nested private class, the size and bounds of the grid are extremely restricted, and the draw function is incredibly unoptimized. Renderer usable but very limited, slow, and buggy.