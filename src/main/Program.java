package main;

import garden.common.Attribute;
import garden.common.Board;
import rules.ast.And;
import rules.common.InvalidRuleException;
import rules.common.RuleTree;
import rules.core.NotLeftOf;
import rules.core.Range;

public class Program {
    public static void main(String[] args) throws InvalidRuleException{
        RuleTree ruleset = new RuleTree(
                new And(
                    new Range(16, Attribute.Water, 4),
                    new Range(16, Attribute.Empty, 12)
                    )
            );
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
        System.out.println((endtime-starttime)/1000000000.0);
    }
}

