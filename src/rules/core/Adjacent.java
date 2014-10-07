package rules.core;

import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.common.Position;

import java.util.ArrayList;

import rules.common.Rule;
import rules.common.Ruleset;

public class Adjacent extends Rule {
    private final Attribute first;
    private final Attribute second;
    
    private static boolean isAdjacent(Choice a, Choice b){
        Position pa = a.getPosition();
        Position pb = b.getPosition();
        // two choices at the same position do not cover our whole "adjacent" idea, though they might be iterated over.
        if(pa.compareTo(pb) == 0) return false;
        return (Math.abs(pa.x() - pb.x()) <= 1 && Math.abs(pa.y() - pb.y()) <= 1);
    }
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset) {
        ArrayList<Choice> firstsAlreadyChosen = new ArrayList<Choice>();
        ArrayList<Choice> secondsAlreadyChosen = new ArrayList<Choice>();

        for(Position position : gs.getPositions()){
                Choice firstChoice = gs.getChoice(position, first);
                Choice secondChoice = gs.getChoice(position, second);
                
                // add firstChoice to our possible list of firsts
                if(firstChoice.isChosen()){
                    firstsAlreadyChosen.add(firstChoice);
                }
                // add secondChoice to our possible list of seconds 
                if(secondChoice.isChosen()){
                    secondsAlreadyChosen.add(secondChoice);
                }
        }
        
        // if any of our already chosen A's and B's are already adjacent, we're already covered - just keep recursing.
        for(Choice a : firstsAlreadyChosen){
            for(Choice b : secondsAlreadyChosen){
                if(isAdjacent(a,b)) return myruleset.recurse(gs);
            }
        }
        
        //try and find a single added Choice that will fulfill our adjacency rule.
        for(Choice tochoose : gs){
            if(tochoose.getAttribute() == first){
                for(Choice alreadyChosen : secondsAlreadyChosen){
                    if(isAdjacent(tochoose, alreadyChosen)){
                        tochoose.choose();
                        if(myruleset.recurse(gs)) return true;
                        tochoose.open();
                    }
                }
            }
            else if(tochoose.getAttribute() == second){
                for(Choice alreadyChosen : firstsAlreadyChosen){
                    if(isAdjacent(tochoose, alreadyChosen)){
                        tochoose.choose();
                        if(myruleset.recurse(gs)) return true;
                        tochoose.open();
                    }
                }
            }
        }
        
        // if we can't find a single Choice, we have to choose two choices.
        for(Choice firsttochoose : gs){
            if(firsttochoose.getAttribute() == first){
                firsttochoose.choose();
                
                for(Choice secondtochoose : gs){
                    if(secondtochoose.getAttribute() == second && isAdjacent(firsttochoose, secondtochoose)){
                        secondtochoose.choose();
                        if(myruleset.recurse(gs)) return true;
                        secondtochoose.open();
                    }
                }
                
                firsttochoose.open();
            }
        }
        
        // if we can't find any of the above, this rule is impossible
        return false;
    }
    
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
