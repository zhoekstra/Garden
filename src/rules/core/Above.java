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
import rules.common.RuleType;
import rules.common.Ruleset;

public class Above extends Rule{
    private final Attribute above;
    private final Attribute below;
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset) {
        ArrayList<Choice> abovesAlreadyChosen = new ArrayList<Choice>();
        ArrayList<Choice> belowsAlreadyChosen = new ArrayList<Choice>();

        for(Position position : gs.getPositions()){
                Choice aboveChoice = gs.getChoice(position, above);
                Choice belowChoice = gs.getChoice(position, below);
                
                // add aboveChoice to our possible list of aboves only if it could have a piece below it.
                if(aboveChoice.isChosen() && aboveChoice.getPosition().y() != gs.getSize() - 1){
                    abovesAlreadyChosen.add(aboveChoice);
                }
                // add belowChoice to our possible list of belows only if it could have a piece above it.
                if(belowChoice.isChosen() && belowChoice.getPosition().y() != 0){
                    belowsAlreadyChosen.add(belowChoice);
                }
        }
        
        // if we've already chosen an A above a B, then this rule is already covered. Recurse. 
        for(Choice aboveChoice : abovesAlreadyChosen)
            for(Choice belowChoice : belowsAlreadyChosen){
                if(belowChoice.getPosition().y() > aboveChoice.getPosition().y())
                    return myruleset.recurse(gs);
            }
        
        // If it is not, try to find a single Choice we can use to cover this rule. We can only do this if a complementary above or below already exists.
        if(abovesAlreadyChosen.size() != 0 || belowsAlreadyChosen.size() != 0){
            // iterate through any open choices in a random order
            for(Choice tochoose : gs){
                if(tochoose.getAttribute() == above){
                    for(Choice alreadychosen : belowsAlreadyChosen){
                        if(tochoose.getPosition().y() < alreadychosen.getPosition().y()){
                            tochoose.choose();
                            if(myruleset.recurse(gs)) return true;
                            tochoose.open();
                        }
                    }
                }
                else if(tochoose.getAttribute() == below){
                    for(Choice alreadychosen : abovesAlreadyChosen){
                        if(tochoose.getPosition().y() > alreadychosen.getPosition().y()){
                            tochoose.choose();
                            if(myruleset.recurse(gs)) return true;
                            tochoose.open();
                        }
                    }
                }
            }
        }
        
        // if we can't find a single node that covers our problem, we need to try and find both an above and below choice
        for(Choice abovechoice : gs){
            if((abovechoice.getAttribute() == above) && (abovechoice.getPosition().y() != gs.getSize() - 1)){
                abovechoice.choose();
                
                for(Choice belowchoice : gs){
                    if(belowchoice.getAttribute() == below && belowchoice.getPosition().y() > abovechoice.getPosition().y()){
                        belowchoice.choose();
                        if(myruleset.recurse(gs)) return true;
                        belowchoice.open();
                    }
                }
                
                abovechoice.open();
            }
        }
        
        // if we can't find anything like this, then we have no possible way to cover this rule. return false.
        return false;
        
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        for(PieceProperty pabove : board){
            if(pabove.getAttribute() == above){
                for(PieceProperty pbelow : board){
                    if(pbelow.getAttribute() == below && pbelow.getPosition().y() > pabove.getPosition().y()) return true;
                }
            }
        }
        return false;
    }
    
    public boolean isCompatableWith(Rule r2){
        if(r2.type() == RuleType.NotAbove){
            NotAbove rule = (NotAbove)r2;
            if(rule.getAbove() == above && rule.getBelow() == below) return false;
            else return true;
        }
        else if(r2.type() == RuleType.Range){
            Range rule = (Range)r2;
            if(above == below && rule.getAttribute() == above) return rule.canBeAtLeast(2);
            else if(rule.getAttribute() == above || rule.getAttribute() == below) return rule.canBeAtLeast(1);
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


    public Above(Attribute above, Attribute below) {
        super();
        this.above = above;
        this.below = below;
    }


    @Override
    public Rule negative() {
        return new NotAbove(above, below);
    }
    
    public Rule reduce(Rule r){
        if(r.type() == RuleType.Above){
            Above r2 = (Above)r;
            if(r2.getAbove() == above && r2.getBelow() == below) return this;
        }
        return null;
    }
    
    public void updateMinumumAmountRequired(EnumMap<Attribute,Integer> amountrequired) {
        if(above==below){
            int curramountreq = amountrequired.containsKey(above) ? amountrequired.get(above) : 0;
            amountrequired.put(above, Math.max(curramountreq, 2));
        }
        else{
            int curramountreq = amountrequired.containsKey(above) ? amountrequired.get(above) : 0;
            amountrequired.put(above, Math.max(curramountreq, 1));
            
            curramountreq = amountrequired.containsKey(below) ? amountrequired.get(below) : 0;
            amountrequired.put(below, Math.max(curramountreq, 1));
        }
    }
    
    public String toString(){
        return "["+above+" above "+below+"]";
    }
    
    public RuleType type(){ return RuleType.Above; }
}
