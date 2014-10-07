package garden;

import garden.common.Position;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Choice implements Comparable<Choice> {
    private final PieceProperty _property;

    private Status _status = Status.Open;
    private Choice _left = this;
    private Choice _right = this;

    // A list of other choices that are mutually exclusive with this one. If
    // this choice is chosen, these choices will automatically be closed.
    private final List<Choice> _exclusiveChoices = new ArrayList<Choice>(0);
    // A list of the 'basic' exclusions necessitated by the rules of the game
    // (Pieces can't be two colors, an Empty square has no piece info, etc,
    // etc.)
    // Can be used to reset the Choice to a basic state, effectively removing
    // all additional rules placed on the board.
    // starts as null. Once we 'lock' a specific state, this will become a copy
    // of the current _exclusiveChoices, which we can then set _exclusiveChoices
    // back to at any time.
    private List<Choice> _basicExclusiveChoices = null;
    // If a node is chosen, we need to ONLY reopen what we closed. This contains
    // a list of every node we closed when we were chosen, in reverse order of
    // how we closed them.
    // EX: if when we were chosen, we closed A, then B, then D,
    // _chosenClosedList would contain {D,B,A}
    private LinkedList<Choice> _chosenClosedList = new LinkedList<Choice>();

    /**
     * @param attribute
     * @param position
     */
    public Choice(Attribute attribute, Position position) {
        _property = new PieceProperty(position, attribute);
    }

    /**
     * Root Constructor. Not to be used for any actual Choices
     */
    public Choice() {
        this(Attribute.Root, new Position(-1, -1));
    }

    /**
     * Adds a Choice node to the left of this node
     * 
     * @param c
     *            the node to add.
     */
    public void addChoice(Choice c) {
        c.setRight(this);
        c.setLeft(this.getLeft());
        this.getLeft().setRight(c);
        this.setLeft(c);
        c._status = Status.Open;
    }

    /**
     * Close this Choice, rendering it incapable of being chosen for the final
     * solution
     * 
     * @return
     */
    public boolean close() {
        if (_status != Status.Open)
            return false;

        unlink();
        _status = Status.Closed;
        return true;
    }

    /**
     * Choose this Choice, claiming it as part of the final solution and closing
     * all exclusive choices
     * 
     * @return
     */
    public boolean choose() {
        if (_status != Status.Open)
            return false;
        // The order of these two lines is important. I must re-link in the
        // reverse order that I unlink.
        unlink();
        closeExclusives();
        //
        _status = Status.Chosen;
        return true;
    }

    /**
     * Re-open this Choice after it has previously been closed or chosen.
     * 
     * @return
     */
    public boolean open() {
        if (_status == Status.Open)
            return false;
        // The order of these two lines is important. I must re-link in the
        // reverse order that I unlink.
        if (_status == Status.Chosen)
            openExclusives();
        relink();
        //
        _status = Status.Open;
        return true;
    }

    /**
     * Add a one-way exclusive link between this Choice and another choice.
     * Choice c will be closed if this choice is chosen
     * 
     * @param c
     * @return
     */
    public boolean addExclusiveChoice(Choice c) {
        if (_exclusiveChoices.contains(c))
            return false;
        else if(c == this)
            return false;
        _exclusiveChoices.add(c);
        return true;
    }

    /**
     * Declare the current set of exclusive choices to be the basic rules for
     * this Board. From here on out, any call to resetToBasicExclusiveChoices()
     * will reset to the exclusive set defined at this point in time.
     * 
     * @return
     */
    public boolean lockBasicExclusiveChoices() {
        if (_basicExclusiveChoices != null)
            return false;
        _basicExclusiveChoices = new ArrayList<Choice>(_exclusiveChoices);
        return true;
    }

    /**
     * reset to the exclusive choices locked in by lockBasicExclusiveChoices()
     * 
     * @return
     */
    public boolean resetToBasicExclusiveChoices() {
        if (_basicExclusiveChoices == null)
            return false;
        _exclusiveChoices.retainAll(_basicExclusiveChoices);
        return true;
    }

    /**
     * Exclusively link two Choices together, so that they are mutually
     * exclusive to each other.
     * 
     * @param a
     * @param b
     */
    public static void linkExclusiveChoices(Choice a, Choice b) {
        a.addExclusiveChoice(b);
        b.addExclusiveChoice(a);
    }

    /**
     * Exclusively link multiple Choices together, so that they are mutually
     * exclusive to each other.
     * 
     * @param choices
     */
    public static void linkExclusiveChoices(Choice... choices) {
        for (int firstiter = 0; firstiter < choices.length - 1; ++firstiter)
            for (int seconditer = firstiter + 1; seconditer < choices.length; ++seconditer) {
                linkExclusiveChoices(choices[firstiter], choices[seconditer]);
            }
    }

    /**
     * unlink this Choice, so that the Choices to the left and right of it point
     * to each other. This can be relinked, provided that all relinks are done
     * in reverse order of how they were unlinked. WARNING: If you unlink a
     * Choice and then lose a pointer to it, it will be deleted permanently from
     * the list. make sure a pointer points to this Choice at all times.
     */
    private void unlink() {
        this.getLeft().setRight(this.getRight());
        this.getRight().setLeft(this.getLeft());
    }

    /**
     * relink this Choice, so that the Choices to the left and right of it point
     * to this Choice again. all relinks must be done in reverse order of how
     * they were unlinked.
     */
    private void relink() {
        this.getRight().setLeft(this);
        this.getLeft().setRight(_right);
    }

    /**
     * find all Choices that are exclusive to this Choice, and unlink them
     */
    private void closeExclusives() {
        for (Choice c : _exclusiveChoices) {
            if (c.getStatus() == Status.Open) {
                c.close();
                // add this to _chosenClosedList in reverse order so that when
                // iterating through,the proper order of relinking is
                // maintained.
                _chosenClosedList.addFirst(c);
            }
        }
    }

    /**
     * find all Choices that are exclusive to this Choice, and relink them
     */
    private void openExclusives() {
        // TODO: This is probably wrong. I need to make sure that a node pointed
        // to by two different choices isn't reopened too early. Maybe fixed?
        for (Choice c : _chosenClosedList) {
            c.open();
        }
        _chosenClosedList.clear();
    }

    /**
     * @return the left
     */
    public Choice getLeft() {
        return _left;
    }

    /**
     * @param left
     *            the left to set
     */
    public void setLeft(Choice left) {
        this._left = left;
    }

    /**
     * @return the right
     */
    public Choice getRight() {
        return _right;
    }

    /**
     * @param right
     *            the right to set
     */
    public void setRight(Choice right) {
        this._right = right;
    }

    /**
     * @return the attribute
     */
    public Attribute getAttribute() {
        return _property.getAttribute();
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return _property.getPosition();
    }

    /**
     * @return the status
     */
    public Status getStatus() {
        return _status;
    }

    public boolean isClosed() {
        return _status == Status.Closed;
    }

    public boolean isChosen() {
        return _status == Status.Chosen;
    }

    public boolean isOpen() {
        return _status == Status.Open;
    }
    public boolean isClosedOrChosen(){
        return _status == Status.Closed || _status == Status.Chosen;
    }
    
    @Override
    public String toString(){
        return String.format("[%s at %s]", getAttribute().toString(), getPosition().toString());
    }

    public PieceProperty getProperty() {
        return _property;
    }

    @Override
    public int compareTo(Choice o) {
        if(getAttribute() == Attribute.Empty && o.getAttribute() != Attribute.Empty) return -1;
        if(getAttribute() != Attribute.Empty && o.getAttribute() == Attribute.Empty) return 1;
        
        int poscompare = getPosition().compareTo(o.getPosition());
        if (poscompare != 0) return poscompare;
        
        else return getAttribute().compareTo(o.getAttribute());
    }
    
    @Override
    public boolean equals(Object o){
        if (o instanceof Choice)
            return compareTo((Choice)o) == 0;
        else return false;
    }
}
