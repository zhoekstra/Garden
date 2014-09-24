package rules;

import java.util.LinkedList;

import garden.GardenSolver;

public abstract class Rule {
	public void applyRestrictive(GardenSolver gs) {}
	public void applyPositiveRule(GardenSolver gs, LinkedList<Rule> rules) {}
}
