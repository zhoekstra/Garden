package rules.core;

import garden.Choice;
import garden.GardenSolver;

import java.util.Iterator;
import java.util.LinkedList;

import rules.common.Rule;
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
        for(int x = 0; x < gs.getSize(); ++x){
            for(int y = 0; y < gs.getSize(); ++y){
                if(!gs.isValid(x,y)){
                    // back up! We can't make a valid board out of this
                    for(Choice c : choicesMade) c.open();
                    return false;
                }
            }
        }
        // if everything is valid, this "rule" is covered.
        return myruleset.recurse(gs);
    }
    @Override
    public Rule negative() {
        // TODO Auto-generated method stub
        return null;
    }

}
