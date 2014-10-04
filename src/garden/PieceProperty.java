package garden;

import garden.common.Position;

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
}
