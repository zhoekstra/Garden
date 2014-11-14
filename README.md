Karensansui
======

Karensansui is an inductive logic game for one human player, known as the "Student" and an AI known as the "Master".

The following is an overview of the rules of the game. For anyone interested in understanding the algorithms behind the game, read the quick synopsis below
and then open the file "AlgorithmExplanations.md"

OVERVIEW
======
In Karensansui, the Students job is to find out by what rule the Master judges the rock gardens created for the temple. To do this, the Student creates
rock gardens filled with different features. He then submits it to the Master along with a guess as to what the Master will mark it as. The Master judges
the garden by her secret rule and marks the new rock garden with a black or white flag, with the white flag designating that the garden follows her rule,
and the black flag designating that the garden does not follow her rule.

To kickstart the process and assure the student that there is a way to figure out the rule, the Master must generate two 
boards before the Student starts playing - one that follows her rule, and one that doesn't.

Once the student has correctly guessed 5 of the gardens he created, he may make a guess about the rule. The Master must create a garden that contradicts his rule;
Either a garden that his guess says should be marked white but is marked black, or vice versa.

If the Master cannot create a garden that contradicts the Students rule, he has won the game and guessed the Master's rule.


GARDEN MAKEUP
======
A rock garden is represented by a square 4x4 board. Each square must be populated by exactly one of the following:

* A Feature with 3 properties;
  * A type that may be a Buddha Statue, a Plant, or a Rock
  * A size that may be Large or Small
  * A color that may be Black, White, or Grey
* A Pond with the Water property.
* An Empty Square with the Empty property.

Some example boards in ASCII format are below:
```
+-----+-----+-----+-----+
|Large|     |Small|Small|
|White|     |Gray |White|
|Budha|     |Budha|Plant|
+-----+-----+-----+-----+
|Large|Small|     |     |
|White|White|Water|     |
|Stone|Plant|     |     |
+-----+-----+-----+-----+
|     |Small|Small|Small|
|     |White|White|Black|
|     |Stone|Stone|Stone|
+-----+-----+-----+-----+
|     |     |Large|     |
|     |     |Black|     |
|     |     |Plant|     |
+-----+-----+-----+-----+

+-----+-----+-----+-----+
|     |     |Large|Large|
|     |     |Black|Black|
|     |     |Stone|Budha|
+-----+-----+-----+-----+
|Small|     |Small|     |
|Gray |     |White|     |
|Budha|     |Plant|     |
+-----+-----+-----+-----+
|Small|     |Large|Small|
|Black|     |White|Gray |
|Plant|     |Plant|Plant|
+-----+-----+-----+-----+
|     |Small|Large|     |
|Water|White|Black|     |
|     |Plant|Budha|     |
+-----+-----+-----+-----+
```

RULES
======
A Property is defined as one of the properties carried by a square in a rock garden. (Buddha, Plant, Stone, Small, Large, White, Black, Grey, Pond, Empty)

The following are rules that may be used by the Master and guessed by the student:

* Property leftof Property
    > this rule returns true if and only if there exists a square with the first Property to the left of a square with the second property.
    > Squares in the same column do not count as being to the left
    
* Property rightof Property
    > this rule returns true if and only if there exists a square with the first Property to the right of a square with the second property.
    > Squares in the same column do not count as being to the right
    
* Property above Property
    > this rule returns true if and only if there exists a square with the first Property above a square with the second property.
    > Squares in the same row do not count as being above each other
    
* Property below Property
    > this rule returns true if and only if there exists a square with the first Property below a square with the second property.
    > Squares in the same row do not count as being below each other
    
* Property adjacent Property
    > this rule returns true if and only if there exists a square with the first Property adjacent to a square with the second property.
    > Diagonal squares count as adjacent.
    
* atleast Integer Property
    > this rule returns true if and only if the number of squares with the Property equals or exceeds the number specified.
    
* atmost Integer Property
    > this rule returns true if and only if the number of squares with the Property is less than or equals the number specified.
    
* exactly Integer Property
    > this rule returns true if and only if the number of squares with the Property is equal the number specified
    
* not Rule
    > this rule returns true if and only if the specified Rule returns false.
    
* Rule and Rule
    > this rule returns true if and only if both of the specified Rules return true

* Rule and Rule
    > this rule returns true if and only if at least one of the specified Rules return true
    
* Rule xor Rule
    > this rule returns true if and only one of the Rules specified returns true and one of the Rules specified returns false (it doesn't matter which)
    

EXAMPLE RULES
======
The following is a list of example rules and a board that follows the specified rule.

(exactly 1 Water) and (Black leftof Grey)
```
+-----+-----+-----+-----+
|     |     |     |Large|
|     |     |     |Gray |
|     |     |     |Plant|
+-----+-----+-----+-----+
|     |     |     |     |
|     |     |     |     |
|     |     |     |     |
+-----+-----+-----+-----+
|     |Small|Small|     |
|Water|Black|Gray |     |
|     |Plant|Stone|     |
+-----+-----+-----+-----+
|Large|     |     |     |
|White|     |     |     |
|Plant|     |     |     |
+-----+-----+-----+-----+
```
(Black leftof Grey) xor (Water above Grey)
```
+-----+-----+-----+-----+
|     |Large|Small|     |
|     |Gray |Black|     |
|     |Budha|Stone|     |
+-----+-----+-----+-----+
|     |     |Large|     |
|     |     |White|     |
|     |     |Stone|     |
+-----+-----+-----+-----+
|     |     |     |     |
|Water|     |     |     |
|     |     |     |     |
+-----+-----+-----+-----+
|     |     |Small|     |
|     |     |Gray |     |
|     |     |Budha|     |
+-----+-----+-----+-----+
```

PROGRESS AND CHECKLIST
======
API DONE
* Given a rule, the Master can generate a valid rock garden
* Given a rule, the master can generate an invalid Rock garden
* Given two rules (the Masters rule and the Student's guess), the master can generate a garden that is valid for one, but not the other.
* Gardens generated are unique and non-deterministic (When asked to create gardens, the Master will never create a garden that already exists and the garden will be random)
* Gardens generated are pretty (The master will make sure there aren't too many complicated features and leaves most spaces Empty)

API TODO
* The Master needs to be able to generate a random Rule that roughly matches a designated difficulty

GUI AND GAME LOGIC TODO
* Basically everything (Algorithms are easy - first time game coding is hard)
* Port rule generation code from Java to Android.
* Create GUI interface to create and judge rock gardens and scroll through judged gardens.
* Create GUI interface to create Rules so the Student can guess the Master's rule.
