package rules.core;

import rules.common.Rule;
import garden.Attribute;

public class Adjacent extends Rule {
    private final Attribute first;
    private final Attribute second;
    
    
    public Attribute getFirst() {
        return first;
    }


    public Attribute getSecond() {
        return second;
    }


    public Adjacent(Attribute first, Attribute second) {
        super();
        if(first.compareTo(second) <= 0){
            this.first = first;
            this.second = second;
        }
        else{
            this.first = second;
            this.second = first;
        }
    }


    @Override
    public Rule negative() {
        return new NotAdjacent(first,second);
    }

}
