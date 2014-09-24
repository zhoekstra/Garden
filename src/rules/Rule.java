package rules;

import java.util.LinkedList;

import garden.GardenSolver;

public abstract class Rule {
    /**
     * Optional method Called before solving for each rule. This gives the
     * chance for the Rule to apply any exclusives to the choices made in the
     * GardenSolver.
     * 
     * @param gs
     */
    public void applyRestrictions(GardenSolver gs) {
    }

    /**
     * Optional method The recursive portion of the solver. Each rule should
     * iterate through all valid covers of itself, choosing the nodes,
     * then call myruleset.recurse(). If recurse() returns true, return true yourself.
     * If recurse returns false, iterate over your next cover.
     * 
     * If you cannot find the appropriate solution with the state you were passed, return false;
     * 
     * @param gs
     * @param rules
     */
    public boolean coverRule(GardenSolver gs, Ruleset myruleset) {return true;}

    /**
     * Non-Optional method
     * 
     * @return the logical negation of this Rule, as a new Rule.
     */
    public abstract Rule negative();
}
