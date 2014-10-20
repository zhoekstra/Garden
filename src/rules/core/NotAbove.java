package rules.core;

import garden.common.Attribute;
import garden.common.Board;
import garden.common.Choice;
import garden.common.PieceProperty;
import garden.solver.GardenSolver;
import rules.common.Rule;
import rules.common.RuleType;

public class NotAbove extends Rule {
    private final Attribute above;
    private final Attribute below;
    
    @Override
    public void applyRestrictions(GardenSolver gs) {
        for(int y = 0; y < gs.getSize(); ++y){
            for(int x = 0; x < gs.getSize(); ++x){
                
                Choice aboveChoice = gs.getChoice(x,y,above);
                
                for(int y2 = y+1; y2 < gs.getSize(); ++y2){
                    for(int x2 = 0; x2 < gs.getSize(); ++x2){
                        Choice belowChoice = gs.getChoice(x2, y2, below);
                        if(belowChoice != null) Choice.linkExclusiveChoices(aboveChoice,  belowChoice);
                    }
                }
            }
        }
    }
    
    public boolean followsRule(Board board){
        for(PieceProperty pabove : board){
            if(pabove.getAttribute() == above){
                for(PieceProperty pbelow : board){
                    if(pbelow.getAttribute() == below && pbelow.getPosition().y() > pabove.getPosition().y()) return false;
                }
            }
        }
        return true;
    }
    
    public boolean isCompatableWith(Rule r2){
        if(r2.type() == RuleType.Above){
            Above rule = (Above)r2;
            if(rule.getAbove() == above && rule.getBelow() == below) return false;
            else return true;
        }
        return true;
    }
    
    
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

    public Rule reduce(Rule r){
        if(r.type() == RuleType.NotAbove){
            NotAbove r2 = (NotAbove)r;
            if(r2.getAbove() == above && r2.getBelow() == below) return this;
        }
        return null;
    }
    
    public String toString(){
        return "!["+above+" above "+below+"]";
    }
    
    public RuleType type(){ return RuleType.NotAbove; }
}
