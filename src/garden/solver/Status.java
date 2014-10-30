package garden.solver;

/**
 * Status
 * @author hoekstrz
 * the status of a choice 
 */
public enum Status {

    /**
     * This Choice is linked in with the Circular Linked List and can be seen by rules, selected as a Choice, etc.
     */
    Open, 
    /**
     * This Choice has been invalidated by another Choice made, and is currently unlinked.
     */
    Closed,
    /**
     * This piece has been chosen as part of the current solution, and is currently unlinked.
     * Any Choices in this Choice's _exclusiveChoices list will be closed while this is Chosen.
     */
    Chosen 
}