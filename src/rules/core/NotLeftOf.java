package rules.core;

import garden.Attribute;
import rules.common.Rule;

public class NotLeftOf extends Rule {
    private final Attribute left;
    private final Attribute right;
    
    
    public Attribute getLeft() {
        return left;
    }


    public Attribute getRight() {
        return right;
    }


    public NotLeftOf(Attribute a, Attribute b) {
        super();
        this.left = a;
        this.right = b;
    }

    @Override
    public Rule negative() {
        return new LeftOf(left,right);
    }

}
