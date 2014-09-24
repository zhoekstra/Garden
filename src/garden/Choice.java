package garden;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import common.Position;



public class Choice {
	private final Attribute _attribute;
	private final Position _position;
	
	private Status _status = Status.Open;
	private Choice _left = this;
	private Choice _right = this;
	
	// A list of other choices that are mutually exclusive with this one. If this choice is chosen, these choices 
	private final List<Choice> _exclusiveChoices = new ArrayList<Choice>(0);
	// A list of the 'basic' exclusions necessitated by the rules of the game (Pieces can't be two colors, an Empty square has no piece info, etc, etc.)
	// Can be used to reset the Choice to a basic state, effectively removing all additional rules placed on the board.
	// starts as null. Once we 'lock' a specific state, this will become a copy of the current _exclusiveChoices, which we can then set _exclusiveChoices back to at any time.
	private List<Choice> _basicExclusiveChoices = null;
	// If a node is chosen, we need to ONLY reopen what we closed. This contains a list of every node we closed when we were chosen, in reverse order of how we closed them.
	// EX: if when we were chosen, we closed A, then B, then D, _chosenClosedList would contain {D,B,A}
	private LinkedList<Choice> _chosenClosedList = new LinkedList<Choice>();
	
	/**
	 * @param attribute
	 * @param position
	 */
	public Choice(Attribute attribute, Position position) {
		this._attribute = attribute;
		this._position = position;
	}
	/**
	 * Root Constructor. Not to be used for any actual Choices
	 */
	public Choice(){
		this(Attribute.Root, new Position(-1,-1));
	}
	/**
	 * Adds a Choice node to the left of this node
	 * @param c the node to add.
	 */
	public void addChoice(Choice c){
		c.setRight(this);
		c.setLeft(this.getLeft());
		this.getLeft().setRight(c);
		this.setLeft(c);
		c._status = Status.Open;
	}
	public boolean close(){
		if(_status != Status.Open) return false;
		
		unlink();
		_status = Status.Closed;
		return true;
	}
	public boolean choose(){
		if(_status != Status.Open) return false;
		// The order of these two lines is important. I must re-link in the reverse order that I unlink.
		unlink();
		closeExclusives();
		//
		_status = Status.Chosen;
		return true;
	}
	public boolean open(){
		if(_status == Status.Open) return false;
		// The order of these two lines is important. I must re-link in the reverse order that I unlink.
		if(_status == Status.Chosen) openExclusives();
		relink();
		//
		_status = Status.Open;
		return true;	
	}
	public boolean addExclusiveChoice(Choice c){
		if(_exclusiveChoices.contains(c)) return false;
		_exclusiveChoices.add(c);
		return true;
	}
	public boolean lockBasicExclusiveChoices(){
		if(_basicExclusiveChoices != null) return false;
		_basicExclusiveChoices = new ArrayList<Choice>(_exclusiveChoices);
		return true;
	}
	public boolean resetToBasicExclusiveChoices(){
		if(_basicExclusiveChoices == null) return false;
		_exclusiveChoices.retainAll(_basicExclusiveChoices);
		return true;
	}
	/**
	 * 
	 * @param a
	 * @param b
	 */
	public static void linkExclusiveChoices(Choice a, Choice b){
		a.addExclusiveChoice(b);
		b.addExclusiveChoice(a);
	}
	/**
	 * unlink this Choice, so that the Choices to the left and right of it point to each other.
	 * This can be relinked, provided that all relinks are done in reverse order of how they were unlinked.
	 * WARNING: If you unlink a Choice and then lose a pointer to it, it will be deleted permanently from the list. make sure a pointer points to this Choice at all times.
	 */
	private void unlink(){
		this.getLeft().setRight(this.getRight());
		this.getRight().setLeft(this.getLeft());
	}
	/**
	 * relink this Choice, so that the Choices to the left and right of it point to this Choice again.
	 * all relinks must be done in reverse order of how they were unlinked.
	 */
	private void relink(){
		this.getRight().setLeft(this);
		this.getLeft().setRight(_right);
	}
	/**
	 * find all Choices that are exclusive to this Choice, and unlink them
	 */
	private void closeExclusives(){
		for(Choice c : _exclusiveChoices){
			if(c.getStatus() == Status.Open){
				c.close();
				// add this to _chosenClosedList in reverse order so that when iterating through,the proper order of relinking is maintained.
				_chosenClosedList.addFirst(c);
			}
		}
	}
	/**
	 * find all Choices that are exclusive to this Choice, and relink them
	 */
	private void openExclusives(){
		// TODO: This is probably wrong. I need to make sure that a node pointed to by two different choices isn't reopened too early. Maybe fixed?
		for(Choice c : _chosenClosedList){
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
	 * @param left the left to set
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
	 * @param right the right to set
	 */
	public void setRight(Choice right) {
		this._right = right;
	}
	/**
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return _attribute;
	}
	/**
	 * @return the position
	 */
	public Position getPosition() {
		return _position;
	}
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return _status;
	}
	
	public boolean isClosed(){
		return _status == Status.Closed;
	}
	
	public boolean isChosen(){
		return _status == Status.Chosen;
	}
	public boolean isOpen(){
		return _status == Status.Open;
	}
}
