package rules.ast;

import rules.common.Rule;;

public class Not extends Rule {
    private final Rule rule;

    public Rule getRule() {
        return rule;
    }

    public Not(Rule rule) {
        super();
        this.rule = rule;
    }
    
    @Override
    public Rule negative() {
        // !!A == A
        return rule;
    }


}
