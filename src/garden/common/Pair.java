package garden.common;

public class Pair<K extends Comparable<K>, V extends Comparable<V>> implements
        Comparable<Pair<K, V>> {

    private final K element0;
    private final V element1;

    public static <K extends Comparable<K>, V extends Comparable<V>> Pair<K, V> createPair(
            K element0, V element1) {
        return new Pair<K, V>(element0, element1);
    }

    public Pair(K element0, V element1) {
        this.element0 = element0;
        this.element1 = element1;
    }

    public K getElement0() {
        return element0;
    }

    public V getElement1() {
        return element1;
    }

    @Override
    public int compareTo(Pair<K, V> o) {
        int first = getElement0().compareTo(o.getElement0());
        if (first != 0)
            return first;
        return getElement1().compareTo(o.getElement1());
    }

}
