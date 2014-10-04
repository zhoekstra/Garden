package rules.core;

import garden.Attribute;
import rules.common.Rule;

public class LeftOf extends Rule {
    private final Attribute left;
    private final Attribute right;
    
    
    public Attribute getLeft() {
        return left;
    }


    public Attribute getRight() {
        return right;
    }


    public LeftOf(Attribute a, Attribute b) {
        super();
        this.left = a;
        this.right = b;
    }


    @Override
    public Rule negative() {
        // TODO Auto-generated method stub
        return null;
    }

}
