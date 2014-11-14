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
    /**
     * The gardensolver that represents the garden space. This is used by all RuleTrees to solve, and is reset every time a solve is called.
     * Not multi-threaded capable. Should probably change that in the future, but 
     * board generation is quick and rare enough that I don't think it'll be an issue.
     */
    private static final GardenSolver gardensolver = new GardenSolver();
    /**
     * the root of the Rule, represented as an AST. This is flattened on creation to the rulesets.
     */
    private final Rule root;
    /**
     * The rulesets that the Root represents. These are flattened from the root and simplified so that no extra rules exist and generation is as quick as possible.
     */
    private final List<Ruleset> representedRulesets;
    /**
     * A pointer to a list of boards that have been generated from this Rule. This can also be connected to other RuleTrees that have the same list of boards.
     * All RuleTrees that share the same generatedBoards list will generate boards that have not been seen in that list yet.
     */
    private List<Board> generatedBoards = new ArrayList<Board>();
    
    public RuleTree(Rule rootrule){
        root = rootrule;
        representedRulesets = root.walkRulesets(generatedBoards);
        for(Iterator<Ruleset> i = representedRulesets.iterator(); i.hasNext();){
            if(!i.next().isValidRuleset()) i.remove();
        }
    }
    
    public RuleTree(List<Board> generatedboards, Rule rootrule){
        this(rootrule);
        this.generatedBoards = generatedboards;
    }
    /**
     * Given a rule, produce a contrasting Ruletree. Boards produced from this RuleTree will
     * follow one Rule, but not the other.
     * 
     * This will be used to generate contrasting boards when the Student guesses a Rule.
     * @param other the Rule to contrast this Rule against.
     * @return a contrasting Ruleset.
     */
    public RuleTree contrastingRuleTree(Rule other){
        return new RuleTree(generatedBoards, new Xor(root, other));
    }
    /**
     * Produce a Negative Ruletree, that produces boards for which this Rule returns False.
     * @return the Negative Ruletree
     */
    public RuleTree negativeRuleTree(){
        return new RuleTree(generatedBoards, new Not(root));
    }
    /**
     * Make sure that, after simplifying and checking for invalid Rulesets, that we can generate at least one positive board.
     * @return
     */
    public boolean isSolvable() {
        return representedRulesets.size() > 0;
    }
    /**
     * Create a new unique board that follows this ruleset. 
     * @return
     */
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
    /**
     * Given a board, check to make sure that the board follows this Rule.
     * @param board
     * @return
     */
    public boolean isValidForBoard(Board board){
        return root.followsRule(board);
    }
}
