RULES AND HOW TO GENERATE THEM
======

The thing about Rules in Karensansui is that there are three things I need to do for the Master AI to work correctly:

Given a rule (R) as an AST,
1. Generate Boards that fit the rule
2. Generate Boards that don't fit the rule
3. Given another rule (S), check to see if (S) is equivalent to (R), and if it isn't, generate a Board that shows the disparity between the rules.

Provided that all Rules I define for the board are Nottable (ie. given a base Rule, I can generate a logical Not.) and I can use standard boolean logic gates, I can do all three of these requirements by generating positive Boards for a Rule
1. I can generate positive Rules for a Board by solving for (R)
2. I can generate negative rules by solving a board for (not R)
3. I can generate disparate boards by attempting to solve for (R xor S)

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

Some rules, however, impose a Positive restriction in YOU MUST HAVE X. For these rules, we need to iterate through them and allow them to choose a set of available choices that satisfy them. If they cannot find a set that satisfys 

So the high level abstract is thus:
1. Create our entire search space
2. Apply the basic restrictions of the board
3. Apply any specific negative restrictions of any rules that apply to the space, so that as choices are chosen, other choices will be come unavailable.
4. For each positive restriction, allow it to choose a set of available choices that cover that positive restriction. Eliminate any choices that are restricted by negative restrictions or basic board restrictions.
5. If a positive restriction cannot cover itself, backtrack and request the last positive restriction to find another way to cover itself.
6. If no solution can be found, there is no board that covers this ruleset.
7. If we find a solution, great! build it and return it.

Turns out that Algorithm X can search through a search space REALLY fast. It quickly trims branches and tries only branches that could lead to a positive solution.

But if there's no possible solution, it can take a while to find that. And large, unweildly rulesets can make the search space even longer.

Reducing Rulesets
===
TODO: keep talking about reducing rulesets
