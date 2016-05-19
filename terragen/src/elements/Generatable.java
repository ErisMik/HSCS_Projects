package elements;

//This interface specifies methods for any object that can be put in the world
public interface Generatable {
    void setData(char symbol, String name, int temp); //This method sets the data in the object

    String toString(); //Converts object to string

    //This is the data
    char getSymbol();
    String getName();
    int getTemp();
}
