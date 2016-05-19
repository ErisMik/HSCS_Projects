//Java SE 1.8

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/*

Statecraft: Soldiers & Succsession
by Eric Mikulin


Battle Engine Compenent

*/


public class CivBattle{
    public static void main(String[] args){
        // Generate the Armies for the battle
        Army anv = (Army) genArmy("The Army of Northern Virginia", 10000, 1.0, false);
        Army apt = (Army) genArmy("The Army of the Potomac", 10000, 1.0, true);

        // Create the battle, and initialize it
        Battle gettysBattle = new Battle(apt, anv, "The Battle of Gettysburg", 2);
        gettysBattle.runBattle();

        // Create the graphical interface of the battle
        BattleWindow gameView = new BattleWindow(gettysBattle);
    }

    // Genertates an army with the given values
    static Formation genArmy(String name, int size, double widthMod, boolean inUnion){
        System.out.println("Info for " + name);

        //Calculate the the width of the army (Size of each formation)
        int companiesNum = size / 100;
        System.out.println(companiesNum + " companies");
        int battalionsNum = companiesNum / 5;
        System.out.println(battalionsNum + " battalions");
        int regimentsNum = battalionsNum / 2;
        System.out.println(regimentsNum + " regiments");
        int brigadesNum = regimentsNum / 2;
        System.out.println(brigadesNum + " brigades");
        int divisionsNum = brigadesNum / 4;
        System.out.println(divisionsNum + " divisons");

        //Create all the companies for the army
        LinkedList<Unit> mans = new LinkedList<Unit>();
        LinkedList<Formation> companies = new LinkedList<Formation>();
        for(int k = 0; k < companiesNum; k++){
            for(int i = 0; i < 100; i++){
                // Randomly generate a man to add to the company
                mans.add(new Soldier(randName(), 0));
            }
            Company c1 = new Company(formName(inUnion, 0), (Soldier) mans.get(0), mans);
            companies.add(c1);
            mans = new LinkedList<Unit>();
        }

        // Create all the battallions
        LinkedList<Formation> comps = new LinkedList<Formation>();
        LinkedList<Formation> battalions = new LinkedList<Formation>();
        for(int k = 0; k < battalionsNum; k++){
            for(int i = 0; i < 5; i++){
                comps.add(companies.get(i));
            }
            Batallion b1 = new Batallion(formName(inUnion, 1), new Soldier(randName(), 4.0), comps);
            battalions.add(b1);
            comps = new LinkedList<Formation>();
        }

        // Create all the Regiments
        LinkedList<Formation> bats = new LinkedList<Formation>();
        LinkedList<Formation> regiments = new LinkedList<Formation>();
        for(int k = 0; k < regimentsNum; k++){
            for(int i = 0; i < 2; i++){
                bats.add(battalions.get(i));
            }
            Regiment r1 = new Regiment(formName(inUnion, 2), new Soldier(randName(), 4.0), bats);
            regiments.add(r1);
            bats = new LinkedList<Formation>();
        }

        // Create all the Brigades
        LinkedList<Formation> regs = new LinkedList<Formation>();
        LinkedList<Formation> brigades = new LinkedList<Formation>();
        for(int k = 0; k < brigadesNum; k++){
            for(int i = 0; i < 2; i++){
                regs.add(regiments.get(i));
            }
            Brigade b2 = new Brigade(formName(inUnion, 3), new Soldier(randName(), 4.0), regs);
            brigades.add(b2);
            regs = new LinkedList<Formation>();
        }

        // Create all the Divisions
        LinkedList<Formation> brigs = new LinkedList<Formation>();
        LinkedList<Formation> divisons = new LinkedList<Formation>();
        for(int k = 0; k < divisionsNum; k++){
            for(int i = 0; i < 4; i++){
                    brigs.add(brigades.get(i));
            }
            Division d1 = new Division(formName(inUnion, 4), new Soldier(randName(), 4.0), brigs);
            divisons.add(d1);
            brigs = new LinkedList<Formation>();
        }

        //Form the army
        Army theArmy = new Army(name, general(5.0, inUnion), divisons);
        return theArmy;
    }

    //Function generates a radom name, returns it as a string
    static String randName(){
        // If names have never been read yet
        if(namesListFirst == null){
            BufferedReader bufread = null; //Ceate a buffered reader
            try {
                // Create a buffered reader from the file
                bufread = new BufferedReader(new FileReader("random.names"));

                // Read the names
                String curString;
                int x = 0;
                namesListFirst = new String[1000];
                namesListLast = new String[1000];
                while ((curString = bufread.readLine()) != null) {
                    // Split the names into first and last for more combinations
                    namesListFirst[x] = curString.split(",")[0];
                    namesListLast[x] = curString.split(",")[1];
                    x++;
                }
            } catch (Exception e){
                System.out.println("It broke: "+e);
            }
        }

        // Find a random name, add them together and output
        int index1 = (int) (Math.random() * 1000);
        int index2 = (int) (Math.random() * 1000);
        String name = namesListFirst[index1] + " " + namesListLast[index2];

        return name;
    }

    //Returns a random name of generals, incuding a rank!
    static Soldier general(double minRank, boolean union){
        //0: Private, 1: Seargent, 2: Captain, 3: Commander, 4: Luetenient, 5: General

        //Read state names if required
        if(officers == null){
            officers = new ArrayList<String>();
            BufferedReader bufread = null; //Ceate a buffered reader
            try {
                bufread = new BufferedReader(new FileReader("characters.people"));
                //While a string is availible, clean then add to arraylist
                String curString;
                while ((curString = bufread.readLine()) != null) {
                    officers.add(curString);
                }
            } catch (Exception e){
                System.out.println("It broke: "+e);
            }
        }

        // Using the bollean, determin what key to search for in the array
        String searchKey;
        if(union)
            searchKey = "U";
        else
            searchKey = "C";

        Soldier outS = null;

        // For each officer in the list
        for(String person : officers){
            String[] info = person.split(","); // Split up the string for processing

            // Trim out the spaces
            for(int no = 0; no < info.length; no++){
                info[no] = info[no].trim();
            }

            // Find the officer that meets the requirments, then return it
            if(info[3].equals("f") && info[1].equals(searchKey) && (Double.parseDouble(info[2]) == minRank)){
                outS = new Soldier(info[0], Double.parseDouble(info[2]), 0.1);
                break;
            }
            // Else return a new officer with a random name
            else{
                outS = new Soldier(randName(), minRank, 0.1);
            }
        }
        return outS;
    }

    //Gives the name of a formation
    static String formName(boolean union, int type){
        //0: company, 1: batallion, 2: Regiment, 3: Brigade, 4: Division, 5: Army
        String[] forms = {"Company", "Batallion", "Regiment", "Brigade", "Division", "Army"};
        int number = (int) (Math.random() * 100);
        int sNum = (int) (Math.random() * 36);

        //Read state names if required
        if(states == null){
            states = new ArrayList<String>();
            BufferedReader bufread = null; //Ceate a buffered reader
            try {
                bufread = new BufferedReader(new FileReader("United.states"));
                //While a string is availible, clean then add to list
                String curString;
                while ((curString = bufread.readLine()) != null) {
                    String[] strang = curString.split(":"); //Split into pieces of data
                    states.add(strang[0].trim());
                }
            } catch (Exception e){
                System.out.println("It broke: "+e);
            }
        }
        
        // Create the formation and output it
        String output = "The " + number + "th " + states.get(sNum) + " " + forms[type];
        return output;
    }

    // These variables are defualt null of the if statements are easy
    static String[] namesListFirst = null;
    static String[] namesListLast = null;
    static ArrayList<String> officers = null;
    static ArrayList<String> states = null;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Start of the Formation and Unit Classes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Formation{
    // Create a new army formation
    Formation(String name, Soldier commander, LinkedList<Formation> members){
        this.name = name;
        this.commander = commander;
        this.members = members;
    }

    // Recursivly calls fire on all companies
    int fire(){
        int hits = 0;
        for(Formation toFire : members){
            System.out.println(commander.name + " orders " + toFire.commander.name + " (" + toFire.name + ") to fire!");
            hits += toFire.fire();
        }
        return hits;
    }

    // Recursivly calls damage on all companies
    void damage(int casualties){
        if(members.size() == 0){
            System.out.println("FUCKITY FUCK FUCK : " + name);
        }
        int perForm = casualties / members.size(); // Calculates the share of the damage
        for(Formation nextHurt : members){
            nextHurt.damage(perForm);
        }
    }

    // Recursivly calls status on all companies
    int status(){
        int totalAlive = 0;
        for(Formation nextStatus : members){
            totalAlive += nextStatus.status();
        }
        return totalAlive;
    }

    // Recursivly calls promote on all companies
    void promote(){
        for(Formation nextPromote : members){
            nextPromote.status();
        }
    }

    // Prints the names of all the soldiers complete with heirachy
    String chainOfCommand(){
        return "Not yet Implimented";
    }

    String name;
    Soldier commander;
    LinkedList<Formation> members;
}

class Army extends Formation{
    Army(String name, Soldier commander, LinkedList<Formation> members){
        super(name, commander, members);
    }

}

class Division extends Formation{
    Division(String name, Soldier commander, LinkedList<Formation> members){
        super(name, commander, members);
    }
}

class Brigade extends Formation{
    Brigade(String name, Soldier commander, LinkedList<Formation> members){
        super(name, commander, members);
    }
}

class Regiment extends Formation{
    Regiment(String name, Soldier commander, LinkedList<Formation> members){
        super(name, commander, members);
    }
}

class Batallion extends Formation{
    Batallion(String name, Soldier commander, LinkedList<Formation> members){
        super(name, commander, members);
    }
}

// Company is where the functional code for the recursions is called
class Company extends Formation{
    Company(String name, Soldier commander, LinkedList<Unit> members){
        super(name, commander, null);
        this.members = members;
    }

    // Calls shoot on each man
    int fire(){
        System.out.println(commander.name + " orders " + name + " to fire!"); // Debug notice
        System.out.println(name + " fires!"); // Debug notice
        int hits = 0;
        for(Unit man : members){
            hits += man.shoot(commander);
        }
        return hits;
    }

    // Determines how many people in the company die
    void damage(int casualties){
        for(int i = 0; i < casualties; i++){
            int dedIndx = (int)(Math.random() * members.size());
            members.get(dedIndx).kill();
        }
    }

    // Returns the number of people still alive at end of shots
    int status(){
        int dead = 0;
        int totalAlive = 0;
        for(Unit man : members){
            if(!man.alive){
                dead += 1;
            } else {
                totalAlive += 1;
            }
        }
        System.out.println(name + " has " + dead + " dead out of " + members.size());
        return totalAlive;
    }

    // Promote the most experianced soldier
    void promote(){
        if(commander.alive == false){
            Soldier top = new Soldier("Test This Dude", 0);
            for(Unit man : members){
                if((top.exp < man.exp) && man.alive){
                    top = (Soldier) man;
                }
            }
            System.out.println(top.name + " was promoted to replace " + commander.name);
            commander = top;
        }
    }

    LinkedList<Unit> members;
}

// Unit class defines an individual person or cannon
class Unit {
    Unit(String name, double rank){
        this.name = name;
        this.rank = rank;
        this.exp = 0.0;
    }

    // Randomly calculates to determine if the units's shot was succesfull
    int shoot(Unit officer){
        double chance = Math.random() + exp + officer.exp;
        // double chance = Math.random();
        if((chance >= 0.99) && alive){
            exp += 0.01;
            return 1;
        } else {
            return 0;
        }
    }

    void kill(){
        alive = false;
    }

    String name;
    double rank;
    double exp;
    boolean alive = true;
}

// Soldier inherets unit
class Soldier extends Unit{
    Soldier(String name, double rank){
        super(name, rank);
    }

    Soldier(String name, double rank, double exp){
        super(name, rank);
        this.exp = exp;
    }
}

// Cannon inherits unit
class Cannon extends Unit{
    Cannon(String name, double rank){
        super(name, rank);
    }   
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Start of the Battle classes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Battle {
    Battle(Army unionArmy, Army confedrateArmy, String name, int length){
        this.confedrateArmy = confedrateArmy;
        this.unionArmy = unionArmy;
        this.rounds = length;
        this.name = name;
    }

    // Initialize the battle
    void runBattle(){
        System.out.println("\n *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* \n");
        System.out.println(name);
        System.out.println("In which " + this.confedrateArmy.name + ", under the leadership of " + this.confedrateArmy.commander.name);
        System.out.println("Faces off against:");
        System.out.println(this.unionArmy.name + ", under the leadership of " + this.unionArmy.commander.name);
        System.out.println("\n *=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=*=* \n");
    }

    // Calculate the end of the battle
    void endBattle(){
        printDeaths();
        checkWin();
    }

    // Auto complete the battle
    void autoVolley(){
        for(int k = 0; k < rounds +2 ; k++){
            volley();
        }
    }

    // Each side shoots at each other
    void volley(){
        if(count <= rounds){
            System.out.println("========================== Confedrates Fires on the Tyrannical North ==========================");
            int confShots = confedrateArmy.fire();
            // try{Thread.sleep(1000);} catch(InterruptedException ex){Thread.currentThread().interrupt();} // "Sleep for a second"

            System.out.println("========================== Union Fires on the Dirty Rebels ==========================");
            int unionShots = unionArmy.fire();
            // try{Thread.sleep(1000);} catch(InterruptedException ex){Thread.currentThread().interrupt();} // "Sleep for a second"

            System.out.println("========================== Damages are being Done ==========================");
            unionArmy.damage(confShots);
            confedrateArmy.damage(unionShots);

        }else{
            endBattle();
        }
        count++;
    }

    // Confederates shoot on the north
    void confVolley(){
        System.out.println("========================== Confedrates Fires on the Tyrannical North ==========================");
        unionArmy.damage(confedrateArmy.fire());
    }

    // Union shoots on the south
    void unionVolley(){
        System.out.println("========================== Union Fires on the Dirty Rebels ==========================");
        confedrateArmy.damage(unionArmy.fire());
    }

    // PRint the status of each army
    void printDeaths(){
        System.out.println("========================== Union Status Report ==========================");
        unionArmy.status();
        System.out.println("========================== Confed Status Report ==========================");
        confedrateArmy.status();
    }

    // Check who won the battle
    void checkWin(){
        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        if(confedrateArmy.status() >= unionArmy.status()){
            System.out.println("Confederate Army wins the battle!");
        } else {
            System.out.println("Union Army wins the battle!");
        }
        System.out.println("\n+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }

    Army confedrateArmy;
    Army unionArmy;
    int rounds;
    int count = 0;
    String name;
}


////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Start of the Graphical Classes
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class BattleWindow{
    BattleWindow(Battle inputBattle){
        mainWin = new JFrame();
        battleShown = inputBattle;

        //mainWin.setLayout(new GridLayout(1,2));
        mainWin.setLayout(new BorderLayout());
        mainWin.setSize(dimensions[0], dimensions[1]);
        mainWin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create the panels for the GUI
        confPanel = new armyJPanel(battleShown.confedrateArmy);
        confPanel.setBackground(new Color(118,128,133));
        unionPanel = new armyJPanel(battleShown.unionArmy);
        unionPanel.setBackground(new Color(25,72,138));
        fieldPanel = new JPanel();
        fieldPanel.setLayout(new GridLayout(2,1));
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(4,1));
        controlPanel.setBackground(Color.BLACK);
        controlPanel.setMaximumSize(new Dimension(50,100));

        //Add controls to panel
        volleyButton = new JButton("Volley");
        volleyButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                battleShown.volley();
            }});
        autoRunButton = new JButton("AutoRun");
        autoRunButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                battleShown.autoVolley();
            }});
        confVButton = new JButton("Confederate Volley");
        confVButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                battleShown.confVolley();
            }});
        uninVButton = new JButton("Union Volley");
        uninVButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                battleShown.unionVolley();
            }});
        controlPanel.add(volleyButton);
        controlPanel.add(autoRunButton);
        controlPanel.add(confVButton);
        controlPanel.add(uninVButton);

        //Add the panels to each other and the window
        fieldPanel.add(confPanel);
        fieldPanel.add(unionPanel);
        mainWin.add(fieldPanel, BorderLayout.CENTER);
        mainWin.add(controlPanel, BorderLayout.EAST);

        //Make the window visible
        mainWin.setVisible(true);
    }

    class armyJPanel extends JPanel{
        armyJPanel(Army inputArmy){
            super();
            theArmy = inputArmy;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g); //Do the Normal painting Method to allow for painting
            g2d = (Graphics2D) g; //Convert to a Graphics2d component
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(Color.WHITE); //Set the color

            recurseivePaint(theArmy, theArmy.getClass());
            // Reset x and y values
            x = 0;
            y = 0;
            repaint();
        }

        void recurseivePaint(Formation next, Class type){
            // If the class type is company, paint the squares
            if(type == Company.class){
                Company conext = (Company) next;
                for(Unit man : conext.members){
                    if(man.alive){
                        g2d.fillRect(2 * x, 2 * y, 1, 1); //Draw the rectangle to represent the man
                    }

                    // Fancy math to get a nice shape of the army
                    x++;
                    if((x % 25 == 0) && (x > 0)){
                        x -= 25;
                        y++;
                    }
                }

                // Fancy math to get a nice shape of the army
                y++;
                if(y % 20 == 0){
                    y = 0;
                    x += 25;
                }
            } else {
                for(Formation nextPaint : next.members){
                    recurseivePaint(nextPaint, nextPaint.getClass());
                }                
            }
        }

        Army theArmy;

        Graphics2D g2d;
        int x = 0;
        int y = 0;
    }

    JFrame mainWin;
    armyJPanel confPanel;
    armyJPanel unionPanel;

    JPanel fieldPanel;
    JPanel controlPanel;

    JButton volleyButton;
    JButton autoRunButton;
    JButton confVButton;
    JButton uninVButton;
    final int[] dimensions = {1200, 600};

    Battle battleShown;
}
