package rules.core;

import java.util.Collection;
import java.util.Set;

import garden.Choice;
import garden.GardenSolver;
import rules.common.Rule;
import rules.common.Ruleset;

public class BoardIsUnique extends Rule {
    private final Collection<Set<Choice>> existingBoards;
    
    public BoardIsUnique(Collection<Set<Choice>> existingBoards){
        this.existingBoards = existingBoards;
    }
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset){
        Set<Choice> currentBoard = gs.choicesMade();
        if(currentBoard.size() == 0) return false;
        
        for(Set<Choice> existingBoard : existingBoards){
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
