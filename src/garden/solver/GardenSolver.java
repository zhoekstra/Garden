package garden.solver;

import garden.common.Attribute;
import garden.common.Choice;
import garden.common.PieceProperty;
import garden.common.Position;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public class GardenSolver implements Iterable<Choice>{
    /**
     * the root of our circular linked list. This is the only Choice that is
     * allowed to have Root as it's attribute, and is never returned for
     * iterations.
     */
    private final Choice _root = new Choice();
    /**
     * A map of all of our choices. We can use this to reset the list at any
     * time, and Rules can use this to point directly to the choices they need
     * to modify
     */
    private final HashMap<Position, EnumMap<Attribute, Choice>> _choiceTable = new HashMap<Position, EnumMap<Attribute, Choice>>();
    /**
     * The size of this Garden
     */
    private final int size;

    /**
     * Create a default 4x4 garden with approximately 2/3 of the free spaces
     * after generation being empty.
     */
    public GardenSolver() {
        this(4, 0.15);
    }

    /**
     * 
     * @param height
     * @param width
     * @param empty_prevalence_perc
     */
    public GardenSolver(int size, double empty_prevalence_perc) {
        if (size < 1)
            size = 1;
        this.size = size;
        for (int y = 0; y < size; ++y) {
            for (int x = 0; x < size; ++x) {
                Position pos = new Position(x, y);
                // we use this temp array so we can easily link things together
                // later
                Choice[] choices = new Choice[9];
                /*
                 * Piece definition. A piece is one of the following:
                 * 
                 * One Type, one Color, and one Size. (must have exactly one of
                 * each) Water (this precludes the piece having any other
                 * attribute) Empty (the "lack" of a piece, which is in itself a
                 * type of piece. this precludes the "piece" having any other
                 * attribute)
                 */

                // The type of the piece at this position.
                choices[0] = new Choice(Attribute.Stone, pos);
                choices[1] = new Choice(Attribute.Plant, pos);
                choices[2] = new Choice(Attribute.Statue, pos);
                // by choosing one type, we cannot choose any other type at the
                // same position.
                Choice.linkExclusiveChoices(choices[0], choices[1], choices[2]);

                // The color of the piece.
                choices[3] = new Choice(Attribute.Black, pos);
                choices[4] = new Choice(Attribute.Gray, pos);
                choices[5] = new Choice(Attribute.White, pos);
                // by choosing one color, we cannot choose any other color at
                // the same position
                Choice.linkExclusiveChoices(choices[3], choices[4], choices[5]);

                // The size of the piece
                choices[6] = new Choice(Attribute.Small, pos);
                choices[7] = new Choice(Attribute.Large, pos);
                // by choosing one size, we cannot choose any other size at the
                // same position
                Choice.linkExclusiveChoices(choices[6], choices[7]);

                // Whether the piece is water
                choices[8] = new Choice(Attribute.Water, pos);
                // If we choose water, the piece cannot have any Sizes, Colors,
                // or Types.
                for (int i = 0; i < choices.length - 1; ++i) {
                    // link water to all other choices
                    Choice.linkExclusiveChoices(choices[8], choices[i]);
                }

                // Whether there is no piece at all.
                Choice empty = new Choice(Attribute.Empty, pos);
                // If we choose empty, we can choose no other attributes for
                // this location.
                for (Choice c : choices)
                    Choice.linkExclusiveChoices(empty, c); // link empty to all
                                                           // other choices

                // add all of our Choices to the relevant table, so we can look
                // things up by position and attribute at any point.
                // We also need all our nodes to be in the HashTable for
                // shuffle() to work
                EnumMap<Attribute, Choice> thispos = new EnumMap<Attribute, Choice>(Attribute.class);
                for (Choice c : choices)
                    thispos.put(c.getAttribute(), c);
                thispos.put(empty.getAttribute(), empty);
                _choiceTable.put(pos, thispos);
            }
        }

        // shuffle the elements in the tables we added. Coincidentally, this
        // also adds them to _root.
        shuffle(empty_prevalence_perc);

        // lock our choices. If we ever need to reset our graph, we can avoid
        // having to create everything again.
        for (Choice c = _root.getRight(); c != _root; c = c.getRight())
            c.lockBasicExclusiveChoices();
    }
    public List<Choice> basicSolve(){
        LinkedList<Choice> solution = new LinkedList<Choice>();
        while(_root.getRight() != _root){
            solution.add(_root.getRight());
            _root.getRight().choose();
        }
        return solution;
    }
    public Set<Choice> choicesMade(){
        Set<Choice> toreturn = new TreeSet<Choice>();
        // if the garden is not completely solved, return an empty set.
        if(_root.getRight() != _root) return toreturn;
        for (Map<Attribute, Choice> posmap : _choiceTable.values()) {
            for (Choice choice : posmap.values()) {
                if(choice.isChosen()) toreturn.add(choice);
            }
        }
        return toreturn;
        
    }
    public void reset(double empty_prevalence_perc) {
        // remove any additional choice exclusions rules may have applied
        for (Map<Attribute, Choice> posmap : _choiceTable.values()) {
            for (Choice choice : posmap.values()) {
                choice.resetToBasicExclusiveChoices();
            }
        }
        // reshuffle and reset our circular linked list
        shuffle(empty_prevalence_perc);
    }
    public boolean fullyCovered(){
        return _root.getRight() == _root;
    }
    public Choice getChoice(int x, int y, Attribute a) {
        return getChoice(new Position(x, y), a);
    }

    public Choice getChoice(Position pos, Attribute a) {
        if(!_choiceTable.containsKey(pos)) return null;
        return _choiceTable.get(pos).get(a);
    }
    
    public Set<Position> getPositions(){
        return _choiceTable.keySet();
    }
    public Set<PieceProperty> getAllChosenProperties(){
        Set<PieceProperty> toreturn = new TreeSet<PieceProperty>();
        for(EnumMap<Attribute, Choice> map : _choiceTable.values()){
            for(Choice c : map.values()){
                if(c.isChosen()) toreturn.add(new PieceProperty(c.getPosition(), c.getAttribute()));
            }
        }
        return toreturn;
    }

    private void shuffle(double empty_prevalence_perc) {
        // as 10% of our values will always be Empty's, empty_prevalence_perc
        // can't go below %10.
        if (empty_prevalence_perc < .1)
            empty_prevalence_perc = .1;
        else if (empty_prevalence_perc > 1)
            empty_prevalence_perc = 1;

        // temporary lists that hold our choices. Used so we can call
        // Collections.shuffle on our Choices.
        // Choices listed as Empty need to be shuffled into the first part of
        // our list, so we need to separate them now.
        LinkedList<Choice> temp_emptys = new LinkedList<Choice>();
        LinkedList<Choice> temp_choices = new LinkedList<Choice>();

        for (Map<Attribute, Choice> posmap : _choiceTable.values()) {
            for (Choice choice : posmap.values()) {
                // disconnect all choices from the list to avoid any weird
                // adding problems, just in case.
                choice.close();
                // all Attribute.Empty's will get shuffled into the front part
                // of the list, so set them aside for now.
                if (choice.getAttribute() == Attribute.Empty)
                    temp_emptys.add(choice);
                // everything else gets added to a general piece pool.
                else
                    temp_choices.add(choice);
            }
        }

        // randomly choose enough items from the choices list so that when
        // shuffled together with the emptys, they form the first
        // %empty_prevalence_perc of our list
        Collections.shuffle(temp_choices);
        LinkedList<Choice> firstNitems = new LinkedList<Choice>();
        // (total_number_of_choices * %of_list_emptys_will_be_shuffled_into) -
        // number_of_emptys = the number of non-empty choices we need to
        // randomly grab.
        int num_choices_to_grab = (int) ((temp_emptys.size() + temp_choices.size()) * empty_prevalence_perc) - temp_emptys.size();
        // grab 'em, merge them with the emptys, and shuffle the whole thing.
        for (int i = 0; i < num_choices_to_grab; ++i)
            firstNitems.add(temp_choices.removeFirst());
        firstNitems.addAll(temp_emptys);
        Collections.shuffle(firstNitems);

        // then merge our two lists - the one with the empty's first and add
        // them back into _root.
        for (Choice c : firstNitems)
            _root.addChoice(c);
        for (Choice c : temp_choices)
            _root.addChoice(c);
    }

    public boolean CanStillBeValid(int x, int y) {
        return CanStillBeValid(new Position(x, y));
    }

    public boolean CanStillBeValid(Position p) {
        EnumMap<Attribute, Choice> pos = _choiceTable.get(p);

        // if either the Empty or Water choices are still available (either Open
        // or Chosen), this spot if still valid.
        if (!(pos.get(Attribute.Empty).isClosed() || pos.get(Attribute.Water).isClosed()))
            return true;

        // If both are closed, then we need to choose one Type, one Color, and
        // one Size.
        // If all of a certain category is closed, we don't have a valid Space
        // and whatever current Choice set we currently have is invalid.
        boolean typevalid = !(pos.get(Attribute.Stone).isClosed() && pos.get(Attribute.Statue).isClosed() && pos.get(Attribute.Plant).isClosed());
        boolean colorvalid = !(pos.get(Attribute.Black).isClosed() && pos.get(Attribute.Gray).isClosed() && pos.get(Attribute.White).isClosed());
        boolean sizevalid = !(pos.get(Attribute.Small).isClosed() && pos.get(Attribute.Large).isClosed());

        if (typevalid && colorvalid && sizevalid)
            return true;
        else
            return false;
    }
    public boolean isCurrentlyValid(int x, int y) {
        return CanStillBeValid(new Position(x, y));
    }
    public boolean isCurrentlyValid(Position p){
        EnumMap<Attribute, Choice> pos = _choiceTable.get(p);
        
        if(pos.get(Attribute.Empty).isChosen() || pos.get(Attribute.Water).isChosen())
            return true;
        
        boolean typevalid = (pos.get(Attribute.Stone).isChosen() || pos.get(Attribute.Statue).isChosen() || pos.get(Attribute.Plant).isChosen());
        boolean colorvalid = (pos.get(Attribute.Black).isChosen() || pos.get(Attribute.Gray).isChosen() || pos.get(Attribute.White).isChosen());
        boolean sizevalid = (pos.get(Attribute.Small).isChosen() || pos.get(Attribute.Large).isChosen());
        
        if(typevalid && colorvalid && sizevalid)
            return true;
        
        else return false;
    }

    @Override
    public Iterator<Choice> iterator() {
        return new ChoiceIterator();
    }

    public class ChoiceIterator implements Iterator<Choice> {
        private Choice current = _root.getRight();

        @Override
        public boolean hasNext() {
            return current != _root;
        }

        @Override
        public Choice next() {
            if (!hasNext())
                throw new NoSuchElementException();
            Choice toreturn = current;
            current = current.getRight();
            return toreturn;
        }
    }

    /**
     * 
     * @return
     */
    public int getSize() {
        return size;
    }
}
