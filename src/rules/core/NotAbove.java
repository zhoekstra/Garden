package rules.core;

import java.util.Set;

import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import rules.common.Rule;

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
    
    public boolean followsRule(Set<PieceProperty> board){
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
        if(r2 instanceof Above){
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

}
