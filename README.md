# bb-blockrenderer
Create and modify isometric 3d grid of cubes, and print them to screen using ASCII chars.

Draw function explanation:

Simpler than it looks. Takes a 3d coordinate, and plots
where the UPPER LEFT corner of the BLOCK should be on the
2d canvas(XX & YY). Then builds the block by incrementing 
through the canvas and adding the characters to build the block
    XX -------> XX+
YY   0__
 |  |\__\
 |  | |  | canvas[YY][XX] is denoted by 0
 |   \|__|
 V
YY+
			
The two below equations[NOT CURRENTLY WORKING] define the printed offset space between blocks in x, y, and z
YY = Y - (2 * z) + y
XX = X - (3 * x) + (2 * y)

 In the diagrams below, 0 denotes where XX and YY = 0, with the incrementing numbers showing
 their value in the equation affecting the offset of blocks in the x, y, or z direction
  12		  123	      1234           12345  (values of y at distances)
 0__		 0__		 0__	   	    0__
|\_0\_		|\__0__		|\__\0__	   |\__\ 0__
| |\__\	    | ||\__\	| | |\__\	   | |  |\__\ 	[number * y]
 \| |  |	 \|| |  |	 \|_| |  |		\|__| |  |	defines offset of ROWS between depth slices
   \|__|        \|__|        \|__|           \|__|

  123       1234       12345       123456     (values of x at distances)
 0__0__    0__ 0__    0__  0__    0__   0__
|\__\__\  |\__\\__\  |\__\ \__\  |\__\ |\__\
| |  |  | | |  ||  | | |  | |  | | |  || |  |	[number * x]
 \|__|__|  \|__||__|  \|__|\|__|  \|__| \|__|  defines offset of COLUMNS between width slices

 2  0__   3  0__   4  0__   5  0__    (values of z at distances)
 1 |\__\  2 |\__\  3 |\__\  4 |\__\
   |0|  | 1 | |  | 2 | |  | 3 | |  |
   |\|__|    0|__| 1  \|__| 2  \|__|
   | |  |   |\__\     0__   1        [number * z]
    \|__|   | |  |   |\__\     0__  defines offset of LAYERS between height slices
             \|__|   | |  |   |\__\
                      \|__|   | |  |
                               \|__|