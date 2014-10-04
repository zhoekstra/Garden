package rules.core;

import rules.common.Rule;
import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;

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

    @Override
    public Rule negative() {
        return null;
        //TODO return new Adjacent(first, second);
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
    
    public boolean equals(Object o){
        return (o instanceof NotAdjacent) && ((NotAdjacent)o).first == first && ((NotAdjacent)o).second == second;
    }

}
