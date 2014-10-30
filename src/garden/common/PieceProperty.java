package garden.common;

/**
 * PieceProperty
 * @author hoekstrz
 * One particular property of a pieceon the board. This PieceProperty may represent only a part of the actual "piece"
 * for example, a single piece could be represented by three pieceproperties - one that defines it's size, one that defines it's 
 * shape,a dn one that defines it's colors.
 */
public class PieceProperty implements Comparable<PieceProperty> {
    public final Position position;
    public final Attribute attribute;
    
    public Position getPosition() {
        return position;
    }
    public Attribute getAttribute() {
        return attribute;
    }
    public PieceProperty(Position position, Attribute attribute) {
        this.position = position;
        this.attribute = attribute;
    }
    @Override
    public int compareTo(PieceProperty o) {
        int comppos = position.compareTo(o.getPosition());
        return comppos == 0 ? attribute.compareTo(o.attribute) : comppos;
    }
    
    public boolean equals(Object o){
        if(o instanceof PieceProperty){
            PieceProperty p = (PieceProperty)o;
            return (p.position == position && p.attribute == attribute);
        }
        else return false;
    }
    
    @Override
    public String toString(){
        return String.format("[%s at %s]", getAttribute().toString(), getPosition().toString());
    }
}
