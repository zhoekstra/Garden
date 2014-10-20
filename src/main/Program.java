package main;

import garden.common.Attribute;
import garden.common.Board;
import garden.common.PieceProperty;
import garden.common.Position;

import java.util.Set;

import rules.ast.And;
import rules.common.InvalidRuleException;
import rules.common.RuleTree;
import rules.core.NotLeftOf;
import rules.core.Range;

public class Program {
    
    public static void printBoard(Set<PieceProperty> solvedset){
        String[][][] printboard = new String[4][4][3];
        
        for(PieceProperty p : solvedset){
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
        
        for(int y = 0; y < 4; ++y){
            System.out.println("+-----+-----+-----+-----+");
            for(int subsquare = 0; subsquare < 3; ++subsquare){
                for(int x = 0; x < 4; ++x){
                    System.out.print("|"+printboard[x][y][subsquare]);
                }
                System.out.println("|");
            }
        }
        System.out.println("+-----+-----+-----+-----+");
    }
    public static void main(String[] args) throws InvalidRuleException{
        
        RuleTree ruleset = new RuleTree(
            
            new And(
                new And(
                    new Range(16, Attribute.Water, 4),
                    new Range(16, Attribute.Empty, 12)
                    ),
                    new NotLeftOf(Attribute.Water, Attribute.Water)
                )
            );
        //LinkedList<Set<PieceProperty>> boards = new LinkedList<Set<PieceProperty>>();
        long starttime = System.nanoTime();
        for(int i = 0; i < 2000; ++i){
            System.out.println(i + " =====================================================");
            Board board = ruleset.solve();
            if(ruleset.isValidForBoard(board))  board.printBoard();
            else{
                System.out.println("no more boards found");
                break;
            }
        }
        long endtime = System.nanoTime();
        System.out.println(endtime-starttime);
    }
}
