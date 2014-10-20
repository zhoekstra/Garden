package rules.ast;

import garden.common.PieceProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rules.common.Rule;
import rules.common.RuleType;

public class Xor extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walkAndCreateRulesets() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        
        // add !left && right
        for(List<Rule> leftruleset : left.negative().walkAndCreateRulesets()){
            for(List<Rule> rightruleset : right.walkAndCreateRulesets()){
                LinkedList<Rule> newList = new LinkedList<Rule>(leftruleset);
                newList.addAll(rightruleset);
                toreturn.add(newList);
            }
        }
        
        // add left && !right
        for(List<Rule> leftruleset : left.walkAndCreateRulesets()){
            for(List<Rule> rightruleset : right.negative().walkAndCreateRulesets()){
                LinkedList<Rule> newList = new LinkedList<Rule>(leftruleset);
                newList.addAll(rightruleset);
                toreturn.add(newList);
            }
        }
        
        return toreturn;
    }

    public Rule getLeft() {
        return left;
    }

    public Rule getRight() {
        return right;
    }

    public Xor(Rule left, Rule right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public Rule negative() {
        // !(A ^^ B) == (!A && !B) || (A && B)
        return new Or(
                new And(left.negative(), right.negative()),
                new And(left, right)
                );
    }

    @Override
    public boolean followsRule(Set<PieceProperty> board) {
        return left.followsRule(board) ^ right.followsRule(board);
    }

    public String toString(){
        return "[ "+left.toString()+" XOR "+right.toString()+" ]";
    }
    
    public RuleType type(){ return RuleType.Xor; }
}
