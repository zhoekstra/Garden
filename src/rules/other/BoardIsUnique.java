package rules.other;

import garden.common.Board;
import garden.common.Choice;
import garden.common.PieceProperty;
import garden.solver.GardenSolver;

import java.util.Collection;
import java.util.Set;

import rules.common.Rule;
import rules.common.RuleType;
import rules.common.Ruleset;

public class BoardIsUnique extends Rule {
    private final Collection<Board> existingBoards;
    
    public BoardIsUnique(Collection<Board> existingBoards){
        this.existingBoards = existingBoards;
    }
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset){
        Board currentBoard = new Board(gs.getAllChosenProperties());
        if(currentBoard.equals(Board.NoSolutionFound)) return false;
        
        for(Board existingBoard : existingBoards){
            if(currentBoard.equals(existingBoard)){
                return false;
            }
        }
        // if this board does not match up with any existing boards, this rule is covered.
        return myruleset.recurse(gs);
    }
    
    public boolean followsRule(Board board){
        return false;
    }

    @Override
    public Rule negative() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public RuleType type(){ return RuleType.Other; }
}
