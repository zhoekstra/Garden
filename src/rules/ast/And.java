package rules.ast;

import java.util.LinkedList;
import java.util.List;

import rules.common.Rule;

public class And extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walk() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        for(List<Rule> leftruleset : left.walk()){
            for(List<Rule> rightruleset : right.walk()){
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

}
