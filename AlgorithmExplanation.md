RULES AND HOW TO GENERATE THEM
======

The thing about Rules in Karensansui is that there are three things I need to do for the Master AI to work correctly:

Given a rule (R) as an AST, I need to do the following:
* Generate Boards that fit the rule
* Generate Boards that don't fit the rule
* Given another rule (S), check to see if (S) is equivalent to (R), and if it isn't, generate a Board that shows the disparity between the rules.

Provided that all Rules I define for the board are Nottable (ie. given a base Rule, I can generate a logical Not.) and I can use standard boolean logic gates, I can do all three of these requirements by generating positive Boards for a Rule
* I can generate positive Rules for a Board by solving for (R)
* I can generate negative rules by solving a board for (not R)
* I can generate disparate boards by attempting to solve for (R xor S)

Because I check to see if two rules are equivalent by checking to see if there is NO solution, I need the search for impossible boards to be relatively fast. I can't spin for a minute checking boards left and right while the player is waiting to see if their guess is right.

So this problem is a classic NP-Complete search with a dash of Boolean Satisfiability. While at first glance this looks like the perfect use for Donald Knuth's Algorithm X, in practice the amount of columns and rows make solving it much more intensive than it should be. 
This is largely because I wanted the rule to be able to use ranges of counts, and Algorithm X doesn't handle large ranges of options well. It much prefers concrete restrictions.

That being said, Algorithm X's quick elimination of options combined with Dancing Link's quick searches mean that it is still a tempting choice. So I decided to develop a weird in-between algorithm that's kind-of Algorithm X and kind of not.

Note: undertanding what I'm doing here will require some knowledge of Algorithm X, so read up on it here:

http://en.wikipedia.org/wiki/Knuth's_Algorithm_X 

Garden Solver Structure
====
A Garden Solver represents the entire space of possible boards (~6.55 x 10^20 possible boards).

This space is represented by a circularly linked list of nodes, each with information about a choice made about a Position on the board

Each Position has 10 choices associated with it: Small, Large, Black, White, Grey, Stone, Statue, Plant, Empty, and Water.

For easy access to specific Choices, we will also keep a Map of pointers into the Circular Linked List so we don't need to do any O(n) searching for everything.

As with the Dancing Links Algorithm, we can remove a node with the following code:
```
this.left.right = right;
this.right.left = left;
```
And, provided we do so in the _reverse_ order we removed them, we can restore it in it's place in the list with
```
this.left.right = this;
this.right.left = this;
```

In addition to this search space, we have a set of rules that we want all to apply (In order for this algorithm, I need to flatten an AST into a series of flat <Rule AND Rule ND Rule AND not Rule AND Rule>. That's easy enough to do by walking the AST.)

As we make choices related to these rules, the space will shrink as options are removed.

Some of the ways in which this space shrinks are standard across all rules (You can't have a Feature be both Large and Small, you can't have a space both Empty and full, etc etc)

Each rule, however, may impose negative restrictions on the space as well, in the form of YOU MAY NOT DO X (no two Ponds may be adjacent, for example)

In both of these cases, you can represent a negative restriction as a non-directional edge between two nodes. If one choice is chosen as part of our rulesset, all nodes that this is linked to this way are closed - they are removed from the list of choosable nodes, but are not chosen as a part of the solution.

ex: If I choose the Small choice at Position 0,0, then I can't choose the following choices:
* Large at 0,0 
* Water at 0,0
* Empty at 0,0

ex: If I'm working on a ruleset with the rule (not Small leftof Large), then if I choose a Large at 1,0, I cannot choose the following:
* Small at 1,0 (basic board rules)
* Water at 1,0 (basic board rules)
* Empty at 1,0 (basic board rules)
* Small at 0,0 (rule restriction)
* Small at 0,1 (rule restriction)
* Small at 0,2 (rule restriction)
* Small at 0,3 (rule restriction)

These links are known in the Code as Exclusive Links. Basic ones are set up at GardenSolver creation, and furthur exclusions can be added by Rules.


Some rules, however, impose a Positive restriction in YOU MUST HAVE X. For these rules, we need to iterate through them and allow them to choose a set of available choices that satisfy them. If they cannot find a set that satisfys 

So the high level abstract for solving a set of Rules is thus:
* Create our entire search space
* Apply the basic restrictions of the board
* Apply any specific negative restrictions of any rules that apply to the space, so that as choices are chosen, other choices will be come unavailable.
* For each positive restriction, allow it to choose a set of available choices that cover that positive restriction. Eliminate any choices that are restricted by negative restrictions or basic board restrictions.
* If a positive restriction cannot cover itself, backtrack and request the last positive restriction to find another way to cover itself.
* If no solution can be found, there is no board that covers this ruleset.
* If we find a solution, great! build it and return it.

Turns out that Algorithm X can search through a search space REALLY fast. It quickly trims branches and tries only branches that could lead to a positive solution.

But if there's no possible solution, it can take a while to find that. And large, unweildly rulesets can make the search space even longer.

Reducing Rulesets
===
A Rule is originally treated as a Abstract Syntax Tree, but solving for the Rule becomes easier if I can break it down into a list of flat Rulesets, where I can look at all Rules that will simultaneously apply to a board. I also need to be able to recognize when Rules can combine or be represented differently, to simplify searches


. So, for example, the Rule (atleast 3 Small xor (atmost 4 Small or exactly 5 Small)) can not only be flattened into the following different situations:
* (not atleast 3 Small and atmost 4 Small)
* (not atleast 3 Small and exactly 5 Small)
* (not atleast 3 Small and atmost 4 Small and exactly 5 Small)
* (atleast 3 Small and not atmost 4 Small and not exactly 5 Small)

These Rulesets can be merged into simpler rules or discounted, as there would be no Garden capable of supporting them.

For example, considering the rulesets above 
* (not atleast 3 Small and atmost 4 Small) can be simplified to (atmost 2 Small)
* (not atleast 3 Small and exactly 5 Small) is impossible, and can be discounted
* (not atleast 3 Small and atmost 4 Small and exactly 5 Small) is impossible, and can be discounted
* (atleast 3 Small and not atmost 4 Small and not exactly 5 Small) can be simplified to (atleast 6 Small)

In addition, some Rulesets may require that there be more Properties than can exist in a 4x4 board. For example, the rule (atleast 8 Small and atleast 9 Large) cannot be generated, as that would require that there be 17 spaces in a 4x4 board. Rulesets that violate this principle can also be discounted

So in order to solve a Rule AST (R), we do the following:
* Walk (R) and flatten it into a series of rulesets
* For each Ruleset, check to make sure that ruleset can generate a valid board. If it cannot, discount it.
* If no Rulesets are left after discounting invalid rulesets, no valid board exists for this Rule.
* If at least 1 exists, for each ruleset, attempt to find a valid board using the Garden Solver.
* Return the first valid board you can find.

Making Pretty Boards
===

In order to create a random board instead of a deterministic board, all we have to do is make sure that all of our positive rules select Choices in the order they appear in our circularly linked list, and then shuffle that list before we do our search. However, if we just randomly shuffle our space, we end up with a situation where a Feature (Size, Type, or Color) is chosen three times as much as an Empty space. This results in a bunch of very ugly, cluttered boards where there are pieces everywhere. A human couldn't easily find a pattern in all that mess. On the other hand, we don't want to choose only the choices we need to cover the rule and choose Empty for every other space - that would make it too easy to figure out the rule.

The solution I ended up using was, instead of doing a completely random shuffle, to concentrate the Empty Choices towards the front of the list where they would be more likely to be picked first by any Rules that choose to do so. So when I shuffle the order of the Choices, I make sure the 16 Empty Choices are shuffled into the front 15% of the list. So of the first 24 Choices, 16 are Empty, and 8 will lead to a full on Feature being there. This means that after I choose my minumum number of Features needed, on average a remaining third of the spaces that still need to be assigned will be "dummy" Features, with the other squares assigned an Empty Choice.
