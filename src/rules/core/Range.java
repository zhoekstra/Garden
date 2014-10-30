package rules.core;

import garden.common.Attribute;
import garden.common.Board;
import garden.common.PieceProperty;
import garden.solver.Choice;
import garden.solver.GardenSolver;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.EnumMap;
import java.util.LinkedList;

import properties.Properties;
import rules.common.InvalidRuleException;
import rules.common.Rule;
import rules.common.RuleType;
import rules.common.Ruleset;

public class Range extends Rule {
    private final int maxSize;
    private final BitSet validValues;
    private final Attribute attribute;
    
    public BitSet getValidValues() {
        return validValues;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public boolean coverRule(GardenSolver gs, Ruleset myruleset){
        ArrayList<Choice> chosenAlready = new ArrayList<Choice>();
        ArrayList<Choice> open = new ArrayList<Choice>();
        
        for(Choice choice : gs){
            if(choice.getAttribute() == attribute) open.add(choice);
        }
        for(int x = 0; x < Properties.GARDENSIZE; ++x){
            for(int y = 0; y< Properties.GARDENSIZE; ++y){
                Choice c = gs.getChoice(x, y, attribute);
                if(c.isChosen())
                    chosenAlready.add(c);
            }
        }
        
        // if we have a valid number of chosen attributes and all other rules down the line are happy, we've found a solution
        if(validValues.get(chosenAlready.size())){
            // We can't have any more of this attribute chosen, since this fits, so close any remaining attributes.
            LinkedList<Choice> closedChoices = new LinkedList<Choice>();
            for(Choice c : gs){
                if(c.getAttribute() == attribute){
                    // add in reverse order so they open in the correct order.
                    closedChoices.addFirst(c);
                    c.close();
                }
            }
            if(myruleset.recurse(gs)) return true;
            else{
                for(Choice c : closedChoices){
                    c.open();
                }
            }
        }
        
        // else go through the list and grab the appropriate number of elements.
        for(int numElementsToPick = chosenAlready.size() + 1; numElementsToPick <= Properties.GARDENSIZE * Properties.GARDENSIZE; ++numElementsToPick){
            if(validValues.get(numElementsToPick)){
                int elementsRemainingToPick = numElementsToPick - chosenAlready.size();
                
                if(pickNElementsFromListAndRecurse(elementsRemainingToPick, gs, myruleset)) return true;
            }
        }
        return false;
    }
    
    private boolean pickNElementsFromListAndRecurse(int elementsRemaining, GardenSolver gs, Ruleset myruleset){
        if(elementsRemaining == 0){
         // We can't have any more of this attribute chosen, since this fits, so close any remaining attributes.
            LinkedList<Choice> closedChoices = new LinkedList<Choice>();
            for(Choice c : gs){
                if(c.getAttribute() == attribute){
                    // add in reverse order so they open in the correct order.
                    closedChoices.addFirst(c);
                    c.close();
                }
            }
            if(myruleset.recurse(gs)) return true;
            // if the next set of rules cannot find a solution, open up my nodes and return false.
            else{
                for(Choice c : closedChoices){
                    c.open();
                }
                return false;
            }
        }
        LinkedList<Choice> alreadyTried = new LinkedList<Choice>();
        for(Choice c : gs){
            if(c.getAttribute() == attribute){
                c.choose();
                if(pickNElementsFromListAndRecurse(elementsRemaining-1, gs, myruleset)) return true;
                c.open();
                // we already tried this node, so just close it and prevent others from trying it as well.
                c.close();
                alreadyTried.addFirst(c);
            }
        }
        for(Choice c : alreadyTried) c.open();
        return false;
    }
    
    public boolean followsRule(Board board){
        int count = 0;
        for(PieceProperty p : board){
            if(p.getAttribute() == attribute) ++count;
        }
        return validValues.get(count);
    }
    
    public boolean isCompatableWith(Rule r2){
        // if no amount of this attribute is valid, this Range, and thus thw whole ruleset, is invalid
        if(this.validValues.cardinality() == 0) return false;
        
        if(r2.type() == RuleType.Range){
            Range rule = (Range)r2;
            if(rule.getAttribute() == attribute){
                try {
                    BitSet copy = (BitSet)this.clone();
                    copy.and(rule.getValidValues());
                    return copy.cardinality() > 0;
                } catch (CloneNotSupportedException e) {
                    // I KNOW this is going to be a BitSet. What do I do here? Don't want to throw an Exception
                    e.printStackTrace();
                    return false;
                }
            }
            else return true;
        }
        else return true;
    }
    
    public Range(int boardSize, Attribute attribute, int... validvalues) throws InvalidRuleException{
        if(validvalues.length == 0)
            throw new InvalidRuleException("This rule cannot fit any board");
        
        this.maxSize = boardSize * boardSize;
        this.validValues = new BitSet(maxSize+1);
        this.attribute = attribute;    
        
        for(Integer i : validvalues){
            this.validValues.set(i);
        }
    }
    private Range(int boardSize, Attribute attribute, BitSet validValues){
        this.maxSize = boardSize * boardSize;
        this.validValues = validValues;
        this.attribute = attribute;
       
    }
    private Range(Range r){
        this.maxSize = r.maxSize;
        this.attribute = r.attribute;
        this.validValues = (BitSet)r.validValues.clone();
    }
    public static Range atLeast(int boardSize, Attribute attribute, int atLeast) throws InvalidRuleException{
        if(atLeast < 0)
            atLeast = 0;
        else if(atLeast > boardSize*boardSize)
            throw new InvalidRuleException("This rule cannot fit any board");
        
        BitSet validValues = new BitSet((boardSize*boardSize)+1);
        for(int i = atLeast; i <= boardSize*boardSize; ++i)
            validValues.set(i);
        return new Range(boardSize, attribute, validValues);
    }
    public static Range atMost(int boardSize, Attribute attribute, int atMost) throws InvalidRuleException{
        if(atMost > boardSize*boardSize)
            atMost = boardSize*boardSize;
        if(atMost < 0)
            throw new InvalidRuleException("This rule cannot fit any board");
        
        BitSet validValues = new BitSet((boardSize*boardSize)+1);
        for(int i = atMost; i >= 0; --i)
            validValues.set(i);
        return new Range(boardSize, attribute, validValues);
    }
    public static Range exactly(int boardSize, Attribute attribute, int exactly) throws InvalidRuleException{
        if(exactly > boardSize*boardSize || exactly < 0)
            throw new InvalidRuleException("This rule cannot fit any board");
        
        BitSet validValues = new BitSet((boardSize*boardSize)+1);
        validValues.set(exactly);
        
        return new Range(boardSize, attribute, validValues);
    }
    
    public boolean canBeAtLeast(int amount){
        for(int i = amount; i <= maxSize; ++i){
            if(validValues.get(i)) return true;
        }
        return false;
    }

    @Override
    public Rule negative() {
        Range toreturn = new Range(this);
        for(int i = 0; i <= toreturn.maxSize; ++i)
            toreturn.validValues.flip(i);
        return toreturn;
    }

    public Rule reduce(Rule r){
        if(r.type() == RuleType.Range){
            Range r2 = (Range)r;
            if(r2.getAttribute() == attribute){
                BitSet toreturn = (BitSet)this.validValues.clone();
                toreturn.and(r2.getValidValues());
                return new Range(maxSize, attribute, toreturn);
            }
        }
        return null;
    }
    
    public void updateMinumumAmountRequired(EnumMap<Attribute,Integer> amountrequired) {
        int rangeamountrequired = 0;
        for(; rangeamountrequired < maxSize; rangeamountrequired++)
            if(validValues.get(rangeamountrequired)) break;
        
        int curramountreq = amountrequired.containsKey(attribute) ? amountrequired.get(attribute) : 0;
        amountrequired.put(attribute, Math.max(rangeamountrequired,curramountreq));
    }
    
    public String toString(){
        String toreturn = "[ "+attribute+" range ";
        for(int i = 0; i < maxSize; ++i)
            if(validValues.get(i)) toreturn+=i+" ";
        return toreturn+"]";
    }
    
    public RuleType type(){ return RuleType.Range; }
}
