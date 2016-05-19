//Java SE 1.8

import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;

/*

Statecraft: Soldiers & Succsession
by Eric Mikulin

*/

public class Statecraft{
    public static void main(String[] args){
        //Read the file that contains all the data, map the data
        HashMap<String, State> UnitedStates = null;
        UnitedStates = readFile();

        //Create the graphics window
        GameWindow gamew = new GameWindow();

        //Iterate through the hashmap and print out for debug
        Iterator thit = UnitedStates.entrySet().iterator();
        while (thit.hasNext()) {
            Map.Entry pair = (Map.Entry)thit.next();
            State theState = (State) pair.getValue();
            System.out.println(pair.getKey() + " == " + theState.getName() + "=" + theState.getAbbr());
        }

        //Commence the main loop of the game
        boolean lost;
        do{
            lost = turn();
        }while(!lost);
    }

    //This function is run for every turn of the game
    static boolean turn(){
        return false;
    }

    //Function reads the file of states and info
    static HashMap readFile(){
        HashMap<String, State> UnSt = new HashMap<String, State>(); //Temporary HashMap

        BufferedReader bufread = null; //Ceate a buffered reader
        try {
            bufread = new BufferedReader(new FileReader("United.states"));

            //While a string is availible, clean then add to map
            String curString;
            while ((curString = bufread.readLine()) != null) {
                System.out.println(curString);
                String[] strang = curString.split(":"); //Split into pieces of data
                //Clean those strings up
                for(int x = 0; x < strang.length; x++){
                    strang[x] = strang[x].trim(); //Trim away the whitespace
                }
                UnSt.put(strang[0], new State(strang[0], strang[1])); //Add the hashmap
            }
        } catch (Exception e){
            System.out.println("It broke: "+e);
        }
        return UnSt; //Return the hashmap
    }
}

//This class defines what a state is and its varouis other things
class State{
    State(String name, String abbrev){
        this.abb = abbrev; //Set state abbbreveation
        this.statename = name;
        findFlag();
    }

    //Returns the name of the State
    String getName(){
        return statename;
    }

    //Returns the abbreviation of the State
    String getAbbr(){
        return abb;
    }

    //Search the resources directory for the state flag
    void findFlag(){
    }

    String statename; //Name of State
    String abb; //State abbreviation
    boolean inUnion = true; //Current status as part of the union
}

//The main window of the game
class GameWindow extends JPanel{
    //Constructor creates a frame
    GameWindow(){
        //Create the main game window
        mainFrame = new JFrame();

        //Create the panel with the fancy map
        displayPan = new JPanel();
        displayPan.setBackground(new Color(0,0,255));
        mainFrame.add(displayPan, BorderLayout.CENTER);

        //Create the pnel with the controls to actually game!
        controlPan = new JPanel();
        controlPan.setBackground(new Color(255,0,0));
        endButton = new JButton("End Turn");
        controlPan.add(endButton);
        ecoButton = new JButton("Economy");
        controlPan.add(ecoButton);
        milButton = new JButton("Military");
        controlPan.add(milButton);
        mainFrame.add(controlPan, BorderLayout.EAST);

        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 400);
        mainFrame.setVisible(true);
    }

    JFrame mainFrame; //The main frame
    JPanel displayPan;
    JPanel controlPan;
    JButton endButton;
    JButton ecoButton;
    JButton milButton;
}
