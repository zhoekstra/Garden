package rules.ast;

import garden.common.Board;

import java.util.List;

import rules.common.Rule;
import rules.common.RuleType;

public class Not extends Rule {
    private final Rule rule;
    
    public List<List<Rule>> walkAndCreateRulesets() {
        return rule.negative().walkAndCreateRulesets();
    }
    
    public boolean followsRule(Board board){
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

    public String toString(){
        return "![ "+rule.toString()+" ]";
    }
    
    public RuleType type(){ return RuleType.Not; }
}
