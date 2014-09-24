package rules;

import java.util.LinkedList;

import garden.GardenSolver;

public abstract class Rule {
	/**
	 * Optional method
	 * Called before solving for each rule.
	 * This gives the chance for the Rule to apply any exclusives to the choices made in the GardenSolver.
	 * @param gs
	 */
	public void applyRestrictiveRules(GardenSolver gs) {}
	/**
	 * Optional method
	 * The recursive portion of the solver.
	 * Each rule should attempt to find a valid solution for itself, then recursively call this function on the next Rule in the list.
	 * This creates a pile of boilerplate recursive code, but I'm not sure there's a good way to do this and make the rules generic.
	 * @param gs
	 * @param rules
	 */
	public void applyPositiveRules(GardenSolver gs, LinkedList<Rule> rules) {}
	/**
	 * Non-Optional method
	 * @return the logical negation of this Rule, as a new Rule. 
	 */
	public abstract Rule negative();
}
