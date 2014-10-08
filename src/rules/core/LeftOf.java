package rules.core;

import garden.Attribute;
import garden.Choice;
import garden.GardenSolver;
import garden.PieceProperty;
import garden.common.Position;

import java.util.ArrayList;
import java.util.Set;

import rules.common.Rule;
import rules.common.Ruleset;

public class LeftOf extends Rule {
    private final Attribute left;
    private final Attribute right;
    
    public boolean coverRule(GardenSolver gs, Ruleset myruleset) {
        ArrayList<Choice> leftsAlreadyChosen = new ArrayList<Choice>();
        ArrayList<Choice> rightsAlreadyChosen = new ArrayList<Choice>();

        for(Position position : gs.getPositions()){
                Choice leftChoice = gs.getChoice(position, left);
                Choice rightChoice = gs.getChoice(position, right);
                
                // add leftChoice to our possible list of aboves only if it could have a piece to the right of it.
                if(leftChoice.isChosen() && leftChoice.getPosition().x() != gs.getSize() - 1){
                    leftsAlreadyChosen.add(leftChoice);
                }
                // add rightChoice to our possible list of belows only if it could have a piece to the left of it.
                if(rightChoice.isChosen() && rightChoice.getPosition().x() != 0){
                    rightsAlreadyChosen.add(rightChoice);
                }
        }
        
        // if we've already chosen an A above a B, then this rule is already covered. Recurse. 
        for(Choice leftChoice : leftsAlreadyChosen)
            for(Choice rightChoice : rightsAlreadyChosen){
                if(rightChoice.getPosition().x() > leftChoice.getPosition().x())
                    return myruleset.recurse(gs);
            }
        
        // If it is not, try to find a single Choice we can use to cover this rule. We can only do this if a complementary left or right already exists.
        if(leftsAlreadyChosen.size() != 0 || rightsAlreadyChosen.size() != 0){
            // iterate through any open choices in a random order
            for(Choice tochoose : gs){
                if(tochoose.getAttribute() == left){
                    for(Choice alreadychosen : rightsAlreadyChosen){
                        if(tochoose.getPosition().x() < alreadychosen.getPosition().x()){
                            tochoose.choose();
                            if(myruleset.recurse(gs)) return true;
                            tochoose.open();
                        }
                    }
                }
                else if(tochoose.getAttribute() == right){
                    for(Choice alreadychosen : leftsAlreadyChosen){
                        if(tochoose.getPosition().x() > alreadychosen.getPosition().x()){
                            tochoose.choose();
                            if(myruleset.recurse(gs)) return true;
                            tochoose.open();
                        }
                    }
                }
            }
        }
        
        // if we can't find a single node that covers our problem, we need to try and find both a left and right choice
        for(Choice leftchoice : gs){
            if((leftchoice.getAttribute() == left) && (leftchoice.getPosition().x() != gs.getSize() - 1)){
                leftchoice.choose();
                
                for(Choice rightchoice : gs){
                    if(rightchoice.getAttribute() == right && rightchoice.getPosition().x() > leftchoice.getPosition().x()){
                        rightchoice.choose();
                        if(myruleset.recurse(gs)) return true;
                        System.out.println("right "+rightchoice+" was not valid LeftOf.");
                        rightchoice.open();
                    }
                }
                System.out.println("left "+leftchoice+" was not valid RightOf.");
                leftchoice.open();
            }
        }
        
        // if we can't find anything like this, then we have no possible way to cover this rule. return false.
        return false;
        
    }
    
    public boolean followsRule(Set<PieceProperty> board){
        for(PieceProperty pleft : board){
            if(pleft.getAttribute() == left){
                for(PieceProperty pright : board){
                    if(pright.getAttribute() == right && pright.getPosition().x() > pleft.getPosition().x()) return true;
                }
            }
        }
        return false;
    }
    
    public boolean isCompatableWith(Rule r2){
        if(r2 instanceof NotLeftOf){
            NotLeftOf rule = (NotLeftOf)r2;
            if(rule.getLeft() == left && rule.getRight() == right) return false;
            else return true;
        }
        else if(r2 instanceof Range){
            Range rule = (Range)r2;
            if(left == right && rule.getAttribute() == left) return rule.canBeAtLeast(2);
            else if(rule.getAttribute() == left || rule.getAttribute() == right) return rule.canBeAtLeast(1);
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


    public LeftOf(Attribute a, Attribute b) {
        super();
        this.left = a;
        this.right = b;
    }


    @Override
    public Rule negative() {
        return new NotLeftOf(left,right);
    }

}
