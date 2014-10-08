package rules.ast;

import garden.PieceProperty;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import rules.common.Rule;

public class Or extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walkAndCreateRulesets() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        
        toreturn.addAll(left.walkAndCreateRulesets());
        toreturn.addAll(right.walkAndCreateRulesets());
        
        return toreturn;
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        return left.followsRule(board) || right.followsRule(board); 
    }

    public Rule getLeft() {
        return left;
    }

    public Rule getRight() {
        return right;
    }

    public Or(Rule left, Rule right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public Rule negative() {
        // !(A || B) == !A && !B
        return new And(left.negative(), right.negative());
    }

}
