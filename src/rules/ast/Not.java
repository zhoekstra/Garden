package rules.ast;

import java.util.List;

import rules.common.Rule;;

public class Not extends Rule {
    private final Rule rule;
    
    public List<List<Rule>> walk() {
        return rule.negative().walk();
    }

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
