package rules.common;

import garden.Attribute;
import garden.GardenSolver;
import garden.PieceProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import rules.core.BoardIsUnique;
import rules.core.BoardIsValid;

public class Ruleset {
    private final LinkedList<Rule> rules = new LinkedList<Rule>();

    public Ruleset(Rule... rules) {
        LinkedList<Rule> temprules = new LinkedList<Rule>(Arrays.asList(rules));
        
        // reduce the rules, so that the final list of rules is the minimum number needed to cover the rule.
        while(temprules.size() > 0){
            Rule first = temprules.removeFirst();
            for(Iterator<Rule> i = temprules.iterator(); i.hasNext();){
                Rule second = i.next();
                Rule reduced = first.reduce(second);
                if(reduced != null){
                    i.remove();
                    first = reduced;
                }
            }
            this.rules.add(first);
        }
        
        Collections.shuffle(this.rules);
        // tack on the base rules to the end... boards must be complete.
        this.rules.add(new BoardIsValid());
    }
    
    public Ruleset(Collection<Set<PieceProperty>> existingBoards, Rule... rules) {
        this(rules);
        // tack on an additional rule- boards must be unique
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
        // check all of our Rules that require pieces. If the number of pieces could not fit inside our board, we have a problem
        EnumMap<Attribute, Integer> amountrequired = new EnumMap<Attribute, Integer>(Attribute.class); 
        for(Rule r : rules){
            r.updateMinumumAmountRequired(amountrequired);
        }
        Attribute[] toiterate = {Attribute.Small, Attribute.Large,Attribute.White, Attribute.Black, Attribute.Gray,Attribute.Stone, Attribute.Plant, Attribute.Statue};
        int piecespacesused = 0;
        for(Attribute attr : toiterate){
            if(amountrequired.containsKey(attr))
                piecespacesused = Math.max(piecespacesused, amountrequired.get(attr));
        }
        int emptyspacesused = amountrequired.containsKey(Attribute.Empty) ? amountrequired.get(Attribute.Empty) : 0;
        int waterspacesused = amountrequired.containsKey(Attribute.Water) ? amountrequired.get(Attribute.Water) : 0;
        if(piecespacesused + emptyspacesused + waterspacesused > RuleTree.GARDENSIZE * RuleTree.GARDENSIZE)
            return false;
        else
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
