package rules.core;

import java.util.Set;

import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import rules.common.Rule;

public class NotLeftOf extends Rule {
    private final Attribute left;
    private final Attribute right;
    
    @Override
    public void applyRestrictions(GardenSolver gs) {
        for(int x = 0; x < gs.getSize(); ++x){
            for(int y = 0; y < gs.getSize(); ++y){
                
                Choice aboveChoice = gs.getChoice(x,y,left);
                
                for(int x2 = x+1; x2 < gs.getSize(); ++x2){
                    for(int y2 = 0; y2 < gs.getSize(); ++y2){
                        Choice belowChoice = gs.getChoice(x2, y2, right);
                        if(belowChoice != null) Choice.linkExclusiveChoices(aboveChoice,  belowChoice);
                    }
                }
            }
        }
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        for(PieceProperty pleft : board){
            if(pleft.getAttribute() == left){
                for(PieceProperty pright : board){
                    if(pright.getAttribute() == right && pright.getPosition().x() > pleft.getPosition().x()) return false;
                }
            }
        }
        return true;
    }
    
    public boolean isCompatableWith(Rule r2){
        if(r2 instanceof LeftOf){
            LeftOf rule = (LeftOf)r2;
            if(rule.getLeft() == left && rule.getRight() == right) return false;
            else return true;
        }
        return true;
    }
    
    public Attribute getLeft() {
        return left;
    }


    public Attribute getRight() {
        return right;
    }


    public NotLeftOf(Attribute a, Attribute b) {
        super();
        this.left = a;
        this.right = b;
    }

    @Override
    public Rule negative() {
        return new LeftOf(left,right);
    }

}
