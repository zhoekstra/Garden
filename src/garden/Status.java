package garden;

//The Choices current status.
public enum Status {

    Open, // This Choice is linked in with the Circular Linked List and can be
          // seen by rules, selected as a Choice, etc.
    Closed, // This Choice has been invalidated by another Choice made, and is
            // currently unlinked.
    Chosen // This piece has been chosen as part of the current solution, and is
           // currently unlinked. Any Choices in this Choice's _exclusiveChoices
           // list will be closed while this is Chosen.
}