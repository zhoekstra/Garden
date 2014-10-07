package rules.ast;

import java.util.LinkedList;
import java.util.List;

import rules.common.Rule;

public class Xor extends Rule {
    private final Rule left;
    private final Rule right;
    
    public List<List<Rule>> walk() {
        List<List<Rule>> toreturn =new LinkedList<List<Rule>>();
        
        // add !left && right
        for(List<Rule> leftruleset : left.negative().walk()){
            for(List<Rule> rightruleset : right.walk()){
                LinkedList<Rule> newList = new LinkedList<Rule>(leftruleset);
                newList.addAll(rightruleset);
                toreturn.add(newList);
            }
        }
        
        // add left && !right
        for(List<Rule> leftruleset : left.walk()){
            for(List<Rule> rightruleset : right.negative().walk()){
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

}
