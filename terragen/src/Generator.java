import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

//The main class
public class Generator{
    //The main method, controls al the major functions (or at least delegates them)
    public static void main(String[] args){
        final double FPS = 10; //This is the setting for the FPS, it's a constant
        final int sleepTime = (int)(1000/FPS); //The is the sleepTime calculation, determined by dividing 1 second (1000ms) by the FPS

        World azer = generateWorld(30, 30, 30); //The actual world, with a set size, generated in another method


        Window game = new Window(azer); //Create a window object (The graphics part) giving it the world to display

        while(true) { //Do this infinitely
            game.fixedUpdate(); //Update the graphics
            try{Thread.sleep(sleepTime);}catch(Exception e){}; //Wait for the next update time
        }
    }

    //This method generates a world, given the height
    static World generateWorld(int width, int length, int height){
        World azert = new World(width, length, height); // create the world

        //Add features such as islands and mountains
        //azert.drawIsland(15, 15, (height / 2));
        //azert.drawGround(15);

        int mNum = (int) (Math.random() * 10); //Pick a random number of mountains
        for(int i = 0; i < mNum; i++) //For each mountain
            azert.drawMountain(); //Generate a mountain

        return azert; //Return the world
    }
}

//This class defines all the graphics an I/O aspects of the program
class Window extends JPanel{

    //Constructor
    Window(World land){
        this.frame = createFrame(SCREENSIZE[0], SCREENSIZE[1]); //Create the frame, using the constant SCREENSIZE
        this.place = land; //Specify the world to draw

        // setup keyboard input
        KeyListener keyListener = new KeyListenerMotion(); //Create a new key listener
        this.frame.addKeyListener(keyListener); //Add it to the graphics window
        this.frame.setFocusable(true); //Allow user to select window
    }

    //This method updates the frame
    void fixedUpdate(){
        this.repaint(); //Repaints the scene
    }

    //Method creates the window
    JFrame createFrame(int width, int height)
    {
        JFrame frame = new JFrame("Midget Castle"); //Creates the frame (window)
        frame.add(this); //Adds this to the frame
        frame.setSize(width, height); //Sets the size of the window
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); //Sets the default of exit to close the window
        frame.setVisible(true); //Makes the window visible
        return frame; //Return the frame
    }

    //Method paints the graphics onto the window
    public void paint(Graphics g) {
        //Sets the context of the graphics, as well as some settings
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int shade; //The variable to hold the shade of green for later

        //Draws out the map
        for(int i = 0; i < place.worldMap.length; i++) //For every X of the world
            for(int j = 0; j < place.worldMap[0].length; j++){ //and every Y of the world
                try {
                    //Print the layer 2 below the zIndex
                    if (place.getPoint(i, j, zIndex - 2) != null) { //If an object is there

                        g2d.setColor(new Color(0, 100, 0)); //Set the block to be it's colour
                        g2d.fillRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                    } else {
                        g2d.setColor(Color.BLUE); //Set default blue colour
                        g2d.drawRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                    }
                } catch (Exception e){ System.out.println("Index out of range for layer height");}

                try {
                    //Print the layer Below
                    if(place.getPoint(i, j, zIndex-1) != null) { //If an object is there

                        g2d.setColor(new Color(0, 200, 0)); //Set the block to be it's colour
                        g2d.fillRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                    }
                    else {
                        g2d.setColor(Color.BLUE); //Set default blue colour
                        g2d.drawRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                    }
                } catch (Exception e){ System.out.println("Index out of range for layer height");}

                //Print the current Layer
                if(place.getPoint(i, j, zIndex) != null) { //If an object is there

                    g2d.setColor(new Color(0, 255, 0)); //Set the block to be it's colour
                    g2d.fillRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                }
                else {
                    g2d.setColor(Color.BLUE); //Set default blue colour
                    g2d.drawRect(i * 20, j * 20, 15, 15); //Draw the rectangle
                }
            }
        String output = "Z-index: "+zIndex; //Create the Z-index output string
        g2d.setColor(Color.BLACK);
        g2d.drawString(output, place.worldMap.length * 20, place.worldMap[0].length*10); //Draw the Z-index to the right of the map
    }


    JFrame frame; //The frame
    final int[] SCREENSIZE = new int[]{1200, 800}; //The constant screen size
    World place; //The world
    static int zIndex = 15; //The current viewing Z-index
}

//This class defines how to recive input and what to do with it
class KeyListenerMotion extends KeyAdapter
{
    //Method is run if a key is pressed
    public void keyPressed(KeyEvent e)
    {
        if (e.getKeyCode() == KeyEvent.VK_UP) { //If it is the up key
            Window.zIndex = Window.zIndex + 1; //Add one to the z-index of the view
        }
        else if (e.getKeyCode() == KeyEvent.VK_DOWN) {//else if it is the down key
            Window.zIndex = Window.zIndex - 1; //Subtract one from the z-index of the view
        }

        //Ensure that the z-index is still within the boundries of the map
        if(Window.zIndex < 0)
            Window.zIndex = 0;
        if(Window.zIndex >= 30)
            Window.zIndex = 29;
    }

}
