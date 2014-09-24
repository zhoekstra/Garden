package garden;

public enum Attribute {
    // this piece has no Attributes. Mutually exclusive with every other
    // attribute
    Empty, 

    // This piece is Water. Mutually exclusive with every other attribute
    Water, 

    // This piece's color. Mutually exclusive with each other, Empty, and Water.
    White, Black, Gray,

    // This piece's type. Mutually exclusive with each other, Empty, and Water.
    Stone, Plant, Statue,

    // This piece's size. Mutually exclusive with each other, Empty, and Water.
    Small, Large,

    // only to be used for the Circular Linked List's Root element. Any actual
    // Choice using this is invalid
    Root
}
