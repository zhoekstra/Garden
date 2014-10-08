package rules.core;

import java.util.Set;

import rules.common.Rule;
import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.common.Position;

public class NotAdjacent extends Rule {
    private final Attribute first;
    private final Attribute second;
    
    @Override
    public void applyRestrictions(GardenSolver gs) {
        for(int x = 0; x < gs.getSize(); ++x){
            for(int y = 0; y < gs.getSize(); ++y){
                Choice pos = gs.getChoice(x, y, first);
                int[] arr  = new int[] {-1,0,1};
                for(int xmod : arr){
                    for(int ymod : arr){
                        if(xmod == 0 && ymod == 0) continue;
                        Choice adj = gs.getChoice(x+xmod,  y + ymod, second);
                        if(adj != null) Choice.linkExclusiveChoices(adj, pos);
                    }
                }
                    
            }
        }
    }
    
    private static boolean isAdjacent(Position a, Position b){
        // two choices at the same position do not cover our whole "adjacent" idea, though they might be iterated over.
        if(a.compareTo(b) == 0) return false;
        return (Math.abs(a.x() - b.x()) <= 1 && Math.abs(a.y() - b.y()) <= 1);
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        for(PieceProperty pfirst : board){
            if(pfirst.getAttribute() == first){
                for(PieceProperty psecond : board){
                    if(psecond.getAttribute() == second && isAdjacent(pfirst.getPosition(), psecond.getPosition())) return false;
                }
            }
        }
        return true;
    }
    
    public boolean isCompatableWith(Rule r2){
        if(r2 instanceof Adjacent){
            Adjacent rule = (Adjacent)r2;
            if(rule.getFirst() == first && rule.getSecond() == second) return false;
            else return true;
        }
        return true;
    }

    @Override
    public Rule negative() {
        return new Adjacent(first, second);
    }

    public NotAdjacent(Attribute first, Attribute second) {
        if(first.compareTo(second) <= 0){
            this.first = first;
            this.second = second;
        }
        else{
            this.first = second;
            this.second = first;
        }
    }
    
    public Attribute getFirst() {
        return first;
    }

    public Attribute getSecond() {
        return second;
    }

    public boolean equals(Object o){
        return (o instanceof NotAdjacent) && ((NotAdjacent)o).first == first && ((NotAdjacent)o).second == second;
    }

}
