package rules.ast;

import garden.PieceProperty;

import java.util.List;
import java.util.Set;

import rules.common.Rule;;

public class Not extends Rule {
    private final Rule rule;
    
    public List<List<Rule>> walkAndCreateRulesets() {
        return rule.negative().walkAndCreateRulesets();
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        return !rule.followsRule(board); 
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
