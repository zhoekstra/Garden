package main;

import garden.common.Attribute;
import garden.common.PieceProperty;
import garden.common.Position;

import java.util.LinkedList;
import java.util.Set;

import rules.ast.And;
import rules.common.InvalidRuleException;
import rules.common.RuleTree;
import rules.core.Above;
import rules.core.LeftOf;
import rules.core.Range;
import rules.generator.RuleGenerator;

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
                    new Range(16, Attribute.Water, 8,9,10),
                    new Range(16, Attribute.White, 7,8,9)
                    ),
                new And(
                    new Range(16, Attribute.Water, 10, 11, 12),
                    new Range(16, Attribute.Gray, 1,2,3)
                    )
                )
            );
        LinkedList<Set<PieceProperty>> boards = new LinkedList<Set<PieceProperty>>();
        long starttime = System.nanoTime();
        for(int i = 0; i < 2000; ++i){
            //System.out.println(i + " =====================================================");
            Set<PieceProperty> board = ruleset.solve();
            if(ruleset.isValidForBoard(board)) boards.add(board);
        }
        long endtime = System.nanoTime();
        for(Set<PieceProperty> i : boards){
            printBoard(i);
        }
        System.out.println(endtime-starttime);
    }
}
