package main;

import garden.common.Attribute;
import garden.common.Board;
import rules.ast.And;
import rules.common.InvalidRuleException;
import rules.common.RuleTree;
import rules.core.Range;

public class Program {
    public static void main(String[] args) throws InvalidRuleException{
        // this is an example restrictive ruleset.
        RuleTree ruleset = new RuleTree(
                new And(
                    new Range(Attribute.Water, 4),
                    new Range(Attribute.Empty, 12)
                    )
            );
        long starttime = System.nanoTime();
        // Solve for all instances of the board. According to combinatorics, there should be exactly 1820 possible boards.
        // this for loop should print out all 1820 boards in a random order, then report no other possible boards.
        // this generally takes about 3 seconds on my home machine
        for(int i = 0; i < 2000; ++i){
            System.out.println(i + " =====================================================");
            Board board = ruleset.solve();// Every time we call this it should create a new unique board
            if(ruleset.isValidForBoard(board))  board.printBoard();
            else{
                System.out.println("no more boards found");
                break;
            }
        }
        long endtime = System.nanoTime();
        System.out.println((endtime-starttime)/1000000000.0);
    }
}

