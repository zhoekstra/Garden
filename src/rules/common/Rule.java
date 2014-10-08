package rules.common;

import garden.GardenSolver;
import garden.PieceProperty;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class Rule {
    /**
     * Optional method Called before solving for each rule. This gives the
     * chance for the Rule to apply any exclusives to the choices made in the
     * GardenSolver.
     * 
     * @param gs
     */
    public void applyRestrictions(GardenSolver gs) {}

    /**
     * Optional method The recursive portion of the solver. Each rule should
     * iterate through all valid covers of itself, choosing the nodes,
     * then call myruleset.recurse(). If recurse() returns true, a valid solution has been found for the rules underneath you.
     * If recurse returns false, iterate over your next cover.
     * 
     * If you cannot find the appropriate solution with the state you were passed, return false;
     * 
     * @param gs
     * @param rules
     */
    public boolean coverRule(GardenSolver gs, Ruleset myruleset) {return myruleset.recurse(gs);}
    
    /**
     * 
     * @param generatedBoards
     * @return
     */
    public final List<Ruleset> walkRulesets(List<Set<PieceProperty>> generatedBoards){
        List<Ruleset> toreturn = new LinkedList<Ruleset>();
        List<List<Rule>> result = this.walkAndCreateRulesets();
        Rule[] toarrayidentifier = new Rule[0];
        for(List<Rule> ruleset : result){
            toreturn.add( new Ruleset(generatedBoards, ruleset.toArray(toarrayidentifier)));
        }
        return toreturn;
    }
    
    /**
     * return a list of rulesets that represent you and the rules underneath you. This is used to form a set of Ruleset objects later.
     * by default, this just returns itself as a single list of list with one element. non-basic rules will need to do more than this
     * @return
     */
    public List<List<Rule>> walkAndCreateRulesets() {
        return Arrays.asList( Arrays.asList(this));
    }
    
    public abstract boolean followsRule(Set<PieceProperty> board);

    /**
     * Non-Optional method
     * 
     * @return the logical negation of this Rule, as a new Rule.
     */
    public abstract Rule negative();

    public boolean isCompatableWith(Rule r2) {
        return true;
    }
    
    
}
