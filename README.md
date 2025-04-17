# Isometric ASCII Sandbox
```java
      ___
     |\__\          
     | |  |                               ___
     |\|__|__                  ___       |\__\
     | |  |__\                |\__\      | |  |        
     |\|__||  |               |\__\|__   |\|__|        
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


> [!Warning]
> `blockrenderer.java` is outdated, available only for archive purposes. Utilize `Scene` and `Textures` for sandboxing.

## Installation
No installation required beyond JDK. Ensure you have Java installed.

## Usage
Create a scene by instantiating a `Scene` object. Specify width, heighth, and depth respectively:
```java
Scene template = new Scene(5,5,5);
```
Add a block to the scene using `.add_block()`, specifying coordinates:
```java
template.add_block(0,0,0);
```
Remove a block from the scene using `.remove_block()`, specifying coordinates:
```java
template.remove_block(0,0,0);
```
Update the projection:
```java
template.update_canvas();
```
Generate the canvas as a printable `String`:
```java
template.construct_canvas()
```

## License
This project is licensed under the MIT License.


```mermaid
graph LR

global("Global Files")

else(".folders .json .md")
style else stroke:#BF4D43

source("src/main")
style source stroke:#409e39

vendordeps("vendordeps/")
style vendordeps stroke:#409e39

gradle("Gradle Components")
style gradle stroke:#409e39

global --- else
global --- source
global --- vendordeps
global --- gradle

deploy("deploy")
style deploy stroke:#BF4D43

java("java/frc")
style java stroke:#409e39

source --- deploy
source --- java

utils("utils")
style utils stroke:#BF4D43

robot("robot")
style robot stroke:#409e39

java --- utils
java --- robot

linkStyle 0 color:#85827B
linkStyle 1 color:#85827B
linkStyle 2 color:#85827B
linkStyle 3 color:#85827B
linkStyle 4 color:#85827B
linkStyle 5 color:#85827B
linkStyle 6 color:#85827B
linkStyle 7 color:#85827B
```
