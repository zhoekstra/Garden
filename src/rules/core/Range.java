package rules.core;

import garden.Attribute;
import java.util.BitSet;

import rules.common.InvalidRuleException;
import rules.common.Rule;

public class Range extends Rule {
    private final int maxSize;
    private final BitSet validValues;
    private final Attribute attribute;
    
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

    @Override
    public Rule negative() {
        Range toreturn = new Range(this);
        for(int i = 0; i <= toreturn.maxSize; ++i)
            toreturn.validValues.flip(i);
        return toreturn;
    }

}
