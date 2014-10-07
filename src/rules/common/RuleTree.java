package rules.common;

import garden.GardenSolver;
import garden.PieceProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import rules.ast.Xor;

public class RuleTree {
    public static final int GARDENSIZE = 4;
    public static final double EMPTYPREVALENCE = 0.15;
    
    private static final GardenSolver gardensolver = new GardenSolver(GARDENSIZE,EMPTYPREVALENCE);
    private final Rule root;
    private final List<Ruleset> representedRulesets;
    private List<Set<PieceProperty>> generatedBoards = new ArrayList<Set<PieceProperty>>();
    
    public RuleTree(Rule rootrule){
        root = rootrule;
        representedRulesets = root.walkRulesets(generatedBoards);
        for(Ruleset r : representedRulesets){
            if(!r.isValidRuleset()) representedRulesets.remove(r);
        }
    }
    
    private RuleTree(List<Set<PieceProperty>> generatedboards, Rule rootrule){
        this(rootrule);
        this.generatedBoards = generatedboards;
    }
    
    public RuleTree contrastingRuleTree(Rule other){
        return new RuleTree(generatedBoards, new Xor(root, other));
    }
    
    public boolean isSolvable() {
        return representedRulesets.size() > 0;
    }
    
    public Set<PieceProperty> solve(){
        Collections.shuffle(representedRulesets);
        for(Ruleset rset : representedRulesets){
            gardensolver.reset(EMPTYPREVALENCE);
            Set<PieceProperty> result = rset.solveRuleset(gardensolver);
            if(result.size() > 0){
                generatedBoards.add(result);
                return result;
            }
        }
        return new TreeSet<PieceProperty>();
    }
}
