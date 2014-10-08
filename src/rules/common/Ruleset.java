package rules.common;

import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.Status;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
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
    
    public Ruleset(Collection<Set<PieceProperty>> existingBoards, Rule... rules) {
        this(rules);
        this.rules.add(new BoardIsUnique(existingBoards));
    }
    
    /**
     * return whether you think this set of rules can return a valid board. If it absolutely could not, return false.
     * This is an attempt to prevent long waits by checking for obviously invalid rulesets beforehand.
     * @return
     */
    public boolean isValidRuleset(){
        for(Rule r1 : rules){
            for(Rule r2 : rules){
                if(r1 != r2)
                    if(!r1.isCompatableWith(r2)) return false;
            }
        }
        return true;
    }

    public Set<PieceProperty> solveRuleset(GardenSolver gs){
        for(Rule rule : rules){
            rule.applyRestrictions(gs);
        }
        if(!recurse(gs)) return new HashSet<PieceProperty>();
        else return gs.getAllChosenProperties();
    }

    public boolean recurse(GardenSolver gs) {
        if(rules.size() == 0) return true;
        
        Rule current = rules.removeFirst();
        boolean succeeded = current.coverRule(gs, this);
        rules.addFirst(current);
        return succeeded;
    }

}
