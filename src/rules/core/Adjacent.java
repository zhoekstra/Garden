package rules.core;

import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.common.Position;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Set;

import rules.common.Rule;
import rules.common.Ruleset;

public class Adjacent extends Rule {
    private final Attribute first;
    private final Attribute second;
    
    private static boolean isAdjacent(Position a, Position b){
        // two choices at the same position do not cover our whole "adjacent" idea, though they might be iterated over.
        if(a.compareTo(b) == 0) return false;
        return (Math.abs(a.x() - b.x()) <= 1 && Math.abs(a.y() - b.y()) <= 1);
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
                if(isAdjacent(a.getPosition(),b.getPosition())) return myruleset.recurse(gs);
            }
        }
        
        //try and find a single added Choice that will fulfill our adjacency rule.
        for(Choice tochoose : gs){
            if(tochoose.getAttribute() == first){
                for(Choice alreadyChosen : secondsAlreadyChosen){
                    if(isAdjacent(tochoose.getPosition(), alreadyChosen.getPosition())){
                        tochoose.choose();
                        if(myruleset.recurse(gs)) return true;
                        tochoose.open();
                    }
                }
            }
            else if(tochoose.getAttribute() == second){
                for(Choice alreadyChosen : firstsAlreadyChosen){
                    if(isAdjacent(tochoose.getPosition(), alreadyChosen.getPosition())){
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
                    if(secondtochoose.getAttribute() == second && isAdjacent(firsttochoose.getPosition(), secondtochoose.getPosition())){
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
    
    public boolean followsRule(Set<PieceProperty> board){
        for(PieceProperty pfirst : board){
            if(pfirst.getAttribute() == first){
                for(PieceProperty psecond : board){
                    if(psecond.getAttribute() == second && isAdjacent(pfirst.getPosition(), psecond.getPosition())) return true;
                }
            }
        }
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
    
    public boolean isCompatableWith(Rule r2){
        if(r2 instanceof NotAdjacent){
            NotAdjacent rule = (NotAdjacent)r2;
            if(rule.getFirst() == first && rule.getSecond() == second) return false;
            else return true;
        }
        else if(r2 instanceof Range){
            Range rule = (Range)r2;
            if(first == second && rule.getAttribute() == first) return rule.canBeAtLeast(2);
            else if(rule.getAttribute() == first || rule.getAttribute() == second) return rule.canBeAtLeast(1);
            else return true;
        }
        return true;
    }


    @Override
    public Rule negative() {
        return new NotAdjacent(first,second);
    }
    
    public Rule reduce(Rule r){
        if(r instanceof Adjacent){
            Adjacent r2 = (Adjacent)r;
            if(r2.getFirst() == first && r2.getSecond() == second) return this;
        }
        return null;
    }
    
    public void updateMinumumAmountRequired(EnumMap<Attribute,Integer> amountrequired) {
        if(first==second){
            int curramountreq = amountrequired.containsKey(first) ? amountrequired.get(first) : 0;
            amountrequired.put(first, Math.max(curramountreq, 2));
        }
        else{
            int curramountreq = amountrequired.containsKey(first) ? amountrequired.get(first) : 0;
            amountrequired.put(first, Math.max(curramountreq, 1));
            
            curramountreq = amountrequired.containsKey(second) ? amountrequired.get(second) : 0;
            amountrequired.put(second, Math.max(curramountreq, 1));
        }
    }
    
    public String toString(){
        return "["+first+" adjacent "+second+"]";
    }

}
