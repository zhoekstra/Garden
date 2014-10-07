package rules.common;

import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.Status;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rules.core.BoardIsUnique;
import rules.core.BoardIsValid;

public class Ruleset {
    private final LinkedList<Rule> rules;

    public Ruleset(Rule... rules) {
        this.rules = new LinkedList<Rule>(Arrays.asList(rules));
        // tack on the two base rules to the end... boards must be complete and unique.
        this.rules.add(new BoardIsValid());
    }
    
    public Ruleset(Collection<Set<Choice>> existingBoards, Rule... rules) {
        this.rules = new LinkedList<Rule>(Arrays.asList(rules));
        // tack on the two base rules to the end... boards must be complete and unique.
        this.rules.add(new BoardIsValid());
        this.rules.add(new BoardIsUnique(existingBoards));
    }

    public List<PieceProperty> solveRuleset(GardenSolver gs){
        for(Rule rule : rules){
            rule.applyRestrictions(gs);
        }
        List<PieceProperty> toreturn = new LinkedList<PieceProperty>();
        if(!recurse(gs)) return toreturn;
        for(Choice c : gs){
            if(c.getStatus() == Status.Chosen) toreturn.add(c.getProperty());
        }
        return toreturn;
    }

    public boolean recurse(GardenSolver gs) {
        if(rules.size() == 0) return true;
        
        Rule current = rules.removeFirst();
        boolean succeeded = current.coverRule(gs, this);
        rules.addFirst(current);
        return succeeded;
    }

}
