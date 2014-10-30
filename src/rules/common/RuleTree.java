package rules.common;

import garden.common.Board;
import garden.solver.GardenSolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import properties.Properties;
import rules.ast.Not;
import rules.ast.Xor;

public class RuleTree {
    private static final GardenSolver gardensolver = new GardenSolver();
    private final Rule root;
    private final List<Ruleset> representedRulesets;
    private List<Board> generatedBoards = new ArrayList<Board>();
    
    public RuleTree(Rule rootrule){
        root = rootrule;
        representedRulesets = root.walkRulesets(generatedBoards);
        for(Iterator<Ruleset> i = representedRulesets.iterator(); i.hasNext();){
            if(!i.next().isValidRuleset()) i.remove();
        }
    }
    
    private RuleTree(List<Board> generatedboards, Rule rootrule){
        this(rootrule);
        this.generatedBoards = generatedboards;
    }
    
    public RuleTree contrastingRuleTree(Rule other){
        return new RuleTree(generatedBoards, new Xor(root, other));
    }
    
    public RuleTree negativeRuleTree(){
        return new RuleTree(generatedBoards, new Not(root));
    }
    
    public boolean isSolvable() {
        return representedRulesets.size() > 0;
    }
    
    public Board solve(){
        Collections.shuffle(representedRulesets);
        for(Ruleset rset : representedRulesets){
            gardensolver.reset(Properties.EMPTYPREVALENCE);
            Board result = rset.solveRuleset(gardensolver);
            if(!result.equals(Board.NoSolutionFound)){
                generatedBoards.add(result);
                return result;
            }
        }
        return Board.NoSolutionFound;
    }
    
    public boolean isValidForBoard(Board board){
        return root.followsRule(board);
    }
}
