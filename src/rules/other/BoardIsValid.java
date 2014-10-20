package rules.other;

import garden.common.Board;
import garden.common.Choice;
import garden.common.Position;
import garden.solver.GardenSolver;

import java.util.Iterator;
import java.util.LinkedList;

import rules.common.Rule;
import rules.common.RuleType;
import rules.common.Ruleset;

public class BoardIsValid extends Rule {

    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset){
        // Fill out the rest of the graph that other rules didn't use.
        LinkedList<Choice> choicesMade = new LinkedList<Choice>();
        // repeatedly grab the first element from the iterator until there are none left.
        for(Iterator<Choice> i = gs.iterator(); i.hasNext(); i = gs.iterator()){
            Choice choice = i.next();
            choice.choose();
            choicesMade.addFirst(choice);
        }
        
        // check to see if every square contains a valid piece
        for(Position position : gs.getPositions()){
                if(!gs.isCurrentlyValid(position)){
                    // back up! We can't make a valid board out of this
                    for(Choice c : choicesMade) c.open();
                    return false;
                }
        }
        // if everything is valid, this "rule" is covered.
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
