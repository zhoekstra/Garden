package garden.common;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import properties.Properties;

public class Board implements Iterable<PieceProperty> {
    public static final Board NoSolutionFound = new Board();
    
    private final Set<PieceProperty> choices;
    
    public Board(Collection<? extends PieceProperty> choices){
        this.choices = new TreeSet<PieceProperty>(choices);
    }
    public Board(PieceProperty... choices){
        this.choices = new TreeSet<PieceProperty>(Arrays.asList(choices));
    }
    
    public boolean equals(Object o){
        if(o instanceof Board){
            Board other = (Board)o;
            if(other.choices.size() == this.choices.size()){
                boolean toreturn1 = other.choices.containsAll(this.choices);
                boolean toreturn2 = this.choices.containsAll(other.choices);
                return toreturn1 && toreturn2;
            }
            else return false;
        }
        else return false;
    }
    
    public String toString(){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        printBoard(new PrintStream(os));
        try {
            return os.toString("UTF8");
        } catch (UnsupportedEncodingException e) {
            return "ERROR";
        }
    }
    public void printBoard(){
        printBoard(System.out);
    }
    public void printBoard(PrintStream stream){
        String[][][] printboard = new String[Properties.GARDENSIZE][Properties.GARDENSIZE][3];
        
        for(PieceProperty p : choices){
            Position pos = p.getPosition();
            switch(p.getAttribute()){
            case Empty:
                printboard[pos.x()][pos.y()][0] = "     ";
                printboard[pos.x()][pos.y()][1] = "     ";
                printboard[pos.x()][pos.y()][2] = "     ";
                break;
            case Water:
                printboard[pos.x()][pos.y()][0] = "     ";
                printboard[pos.x()][pos.y()][1] = "Water";
                printboard[pos.x()][pos.y()][2] = "     ";
                break;
            case Small:
                printboard[pos.x()][pos.y()][0] = "Small";
                break;
            case Large:
                printboard[pos.x()][pos.y()][0] = "Large";
                break;
            case White:
                printboard[pos.x()][pos.y()][1] = "White";
                break;
            case Gray:
                printboard[pos.x()][pos.y()][1] = "Gray ";
                break;
            case Black:
                printboard[pos.x()][pos.y()][1] = "Black";
                break;
            case Plant:
                printboard[pos.x()][pos.y()][2] = "Plant";
                break;
            case Stone:
                printboard[pos.x()][pos.y()][2] = "Stone";
                break;
            case Statue:
                printboard[pos.x()][pos.y()][2] = "Budha";
                break;
            default:
                break;
            }
        }
        
        for(int y = 0; y < Properties.GARDENSIZE; ++y){
            stream.println(dividerString());
            for(int subsquare = 0; subsquare < 3; ++subsquare){
                for(int x = 0; x < Properties.GARDENSIZE; ++x){
                    stream.print("|"+printboard[x][y][subsquare]);
                }
                stream.println("|");
            }
        }
        stream.println(dividerString());
    }
    private String dividerString(){
        StringBuilder toreturn = new StringBuilder();
        for(int i = 0; i < Properties.GARDENSIZE; ++i)
            toreturn.append("+-----");
        toreturn.append("+");
        return toreturn.toString();
    }
    @Override
    public Iterator<PieceProperty> iterator() {
        return choices.iterator();
    }
}
