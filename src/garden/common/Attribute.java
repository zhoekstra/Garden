package garden.common;
/**
 * Attribute
 * @author hoekstrz
 * This enum represents the various attributes that pieces on a board can have.
 * Some attributes are mutually exclusive with others. 
 */
public enum Attribute {
    // this piece has no Attributes. Mutually exclusive with every other
    // attribute
    Empty, 

    // This piece is Water. Mutually exclusive with every other attribute
    Water,
    
    // This piece's size. Mutually exclusive with each other, Empty, and Water.
    Small, Large,

    // This piece's color. Mutually exclusive with each other, Empty, and Water.
    White, Black, Gray,

    // This piece's type. Mutually exclusive with each other, Empty, and Water.
    Stone, Plant, Statue,

    // only to be used for the Circular Linked List's Root element. Any actual
    // Choice using this is invalid
    Root
}
