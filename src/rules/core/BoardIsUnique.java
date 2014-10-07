package rules.core;

import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;

import java.util.Collection;
import java.util.Set;

import rules.common.Rule;
import rules.common.Ruleset;

public class BoardIsUnique extends Rule {
    private final Collection<Set<PieceProperty>> existingBoards;
    
    public BoardIsUnique(Collection<Set<PieceProperty>> existingBoards){
        this.existingBoards = existingBoards;
    }
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset){
        Set<Choice> currentBoard = gs.choicesMade();
        if(currentBoard.size() == 0) return false;
        
        for(Set<PieceProperty> existingBoard : existingBoards){
            if(!currentBoard.equals(existingBoard)) return false;
        }
        // if this board does not match up with any existing boards, this rule is covered.
        return myruleset.recurse(gs);
    }

    @Override
    public Rule negative() {
        // TODO Auto-generated method stub
        return null;
    }
}
