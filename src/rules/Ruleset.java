package rules;

import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.Status;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Ruleset {
    LinkedList<Rule> rules;

    public Ruleset(Rule... rules) {
        this.rules = new LinkedList<Rule>(Arrays.asList(rules));
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
        Rule current = rules.removeFirst();
        boolean succeeded = current.coverRule(gs, this);
        rules.addFirst(current);
        return succeeded;
    }

}
