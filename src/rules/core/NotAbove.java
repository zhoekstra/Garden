package rules.core;

import garden.Attribute;
import rules.common.Rule;

public class NotAbove extends Rule {
    private final Attribute above;
    private final Attribute below;
    
    
    public Attribute getAbove() {
        return above;
    }


    public Attribute getBelow() {
        return below;
    }


    public NotAbove(Attribute above, Attribute below) {
        super();
        this.above = above;
        this.below = below;
    }

    @Override
    public Rule negative() {
        return new Above(above,below);
    }

}
