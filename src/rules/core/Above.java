package rules.core;

import rules.common.Rule;
import garden.Attribute;

public class Above extends Rule{
    private final Attribute above;
    private final Attribute below;
    
    
    public Attribute getAbove() {
        return above;
    }


    public Attribute getBelow() {
        return below;
    }


    public Above(Attribute above, Attribute below) {
        super();
        this.above = above;
        this.below = below;
    }


    @Override
    public Rule negative() {
        return new NotAbove(above, below);
    }

}
