package rules.ast;

import java.util.LinkedList;
import java.util.List;

import rules.common.Rule;

public class Or extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walk() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        
        toreturn.addAll(left.walk());
        toreturn.addAll(right.walk());
        
        return toreturn;
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
