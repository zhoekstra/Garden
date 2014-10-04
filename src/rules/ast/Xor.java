package rules.ast;

import rules.common.Rule;

public class Xor extends Rule {
    private final Rule left;
    private final Rule right;

    public Rule getLeft() {
        return left;
    }

    public Rule getRight() {
        return right;
    }

    public Xor(Rule left, Rule right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public Rule negative() {
        // !(A ^^ B) == (!A && !B) || (A && B)
        return new Or(
                new And(left.negative(), right.negative()),
                new And(left, right)
                );
    }

}
