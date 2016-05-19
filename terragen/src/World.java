import elements.*;

//Class defines a world (A 3D space made up of Voxels)
public class World {
    //Default constructor, asks for the size and depth of the world
    World(int x, int y, int z){
        worldMap = new Material[x][y][z];
    }

    //A test method to enure that all thing are still working
    void testLand(){
        worldMap[15][15][15] = new Dirt();
    }

    //This method draws a semi-random mountain
    void drawMountain(){
        MountainFunction mFX = new MountainFunction(); //Create the function for generating a mountain

        for (int i = 0; i < worldMap.length; i++) //For every X
            for (int j = 0; j < worldMap[0].length; j++) //For every Y
                for(int k = 0; k < worldMap[0][0].length; k++){ //and every Z
                    if(k < mFX.solve(i, j)) //Test if the Z value is less than the function at that point
                        worldMap[i][j][k] = new Dirt(); //If yes, fill with dirt
                }
    }

    //Supposed to draw ground, doesn't yet
    void drawGround(){
        GroundFunction gFX = new GroundFunction(); //Create the function for generating a mountain

        for (int i = 0; i < worldMap.length; i++) //For every X
            for (int j = 0; j < worldMap[0].length; j++) //For every Y
                for(int k = 0; k < worldMap[0][0].length; k++){ //and every Z
                    if(k < gFX.solve(i, j)) //Test if the Z value is less than the function at that point
                        worldMap[i][j][k] = new Dirt(); //If yes, fill with dirt
                }
    }

    //This method draws a semi-random island (More of a hill really)
    void drawIsland(int zStart, int xStart, int yStart){

    }

    //Method returns the material at this point
    public Material getPoint(int x, int y, int z){
        return worldMap[x][y][z];
    }

    //This method will determine the material at that location given the specs (For future use)
    Material determineMaterial(int temp){
        return null; //Temporary
    }

    Material[][][] worldMap; //This is the world map, it holds all the data for everything of the materials
    //private int modifier[] = {-1, 0 ,1};
}

class MountainFunction{
    //Constructor
    MountainFunction(){
        //Randomly generate values for the shape and size of the mountains
        height = (int) (Math.random() * 30);
        posX = (int) (Math.random() * 30);
        posY = (int) (Math.random() * 30);
        width = Math.random();
        length = Math.random();
    }

    //This function returns the Z value when given the X and Y value
    int solve(int x, int y){
        int z = (int) (-width*Math.pow((x-posX), 2)); //Calculate with X
        z += (int) (-length*Math.pow((y-posY), 2)); //Add the calculation with Y
        z+= height; //Add the height
        return z;
    }

    //Variables for modifying formula
    int height;
    double width;
    double length;
    int posX;
    int posY;
}

class GroundFunction{
    void GroundFunction(){

    }

    int solve(int x, int y){
        int z = (int) (Math.sin(x)+Math.cos(y) + 5);
        return z;
    }
}