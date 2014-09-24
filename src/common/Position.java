package common;

public class Position extends Pair<Integer,Integer> {

	public Position(final int x, final int y) {
		super(x, y);
	}
	
	public int x(){
		return this.getElement0();
	}
	
	public int y(){
		return this.getElement1();
	}
}