package elements;

import java.awt.*;

//Material is a type of generatable that defines an element, like dirt, stone, grass etc.
public class Material implements Generatable {
    //Constructor
    public Material(char symbol, String name, int temp){
        setData(symbol, name, temp);
    }

    //Sets the data of the material type
    public void setData(char symbol, String name, int temp){
        this.symbol = symbol;
        this.name = name;
        this.temp = temp;
    }

    public String toString(){
        return name+symbol+temp;
    } //Returns the string

    public char getSymbol(){
        return symbol;
    } //Returns the symbol of the material

    public String getName(){
        return name;
    } //Returns the name of the material

    public int getTemp(){
        return temp;
    } //Returns the range of the temperaure of the material

    //The data of the object
    char symbol;
    String name;
    int temp;
    public Color colour = Color.blue;
}
