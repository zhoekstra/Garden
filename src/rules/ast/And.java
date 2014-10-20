package rules.ast;

import garden.PieceProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rules.common.Rule;

public class And extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walkAndCreateRulesets() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        for(List<Rule> leftruleset : left.walkAndCreateRulesets()){
            for(List<Rule> rightruleset : right.walkAndCreateRulesets()){
                LinkedList<Rule> newList = new LinkedList<Rule>(leftruleset);
                newList.addAll(rightruleset);
                toreturn.add(newList);
            }
        }
        return toreturn;
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        return left.followsRule(board) && right.followsRule(board); 
    }

    public Rule getLeft() {
        return left;
    }

    public Rule getRight() {
        return right;
    }

    public And(Rule left, Rule right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public Rule negative() {
        // !(A && B) == !A || !B
        return new Or(left.negative(), right.negative());
    }
    
    public String toString(){
        return "[ "+left.toString()+" AND "+right.toString()+" ]";
    }
}
