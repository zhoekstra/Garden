package common;

public class Position implements Comparable<Position> {
    private final int x;
    private final int y;
    
    public Position(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    @Override
    public int compareTo(Position arg0) {
        if(this.x() != arg0.x()) return this.x() - arg0.x();
        else return this.y() - arg0.y();
    }
    
    @Override
    public String toString(){
        return String.format("{%d,%d}", x,y);
    }
    /**
     *  we'll use the cantor pairing function to generate a hashcode for this position. http://en.wikipedia.org/wiki/Pairing_function#Cantor_pairing_function
     *  This should work for the positive integers I need.
     */
    @Override
    public int hashCode(){
        return (int) (.5 * (x+y) * (x + y + 1) + y);
    }
    @Override
    public boolean equals(Object o){
        return o instanceof Position && ((Position)o).x() == x && ((Position)o).y() == y;
    }
}