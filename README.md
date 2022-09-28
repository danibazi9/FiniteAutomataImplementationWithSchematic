# FiniteAutomataImplementationWithSchematic

Implementation of fully-functioned finite automata (DFA/NFA), also include schematically implementation

**"Theory of Languages & Automata"** course project, IUST, Dr. Hossein Rahmani, Spring 2019

We've implemented a complete and comprehensive system to work with finite automata. This project meets your needs in the following areas:
1. Check the acceptance of a string with an NFA with usage of 'IsAcceptByNFA' function
2. Find the regular expression for the input NFA with usage of `FindRegex` function
3. Convert an NFA to equivalent DFA and return the DFA object with usage of 'CreateEquivalentDFA' function
4. Check the acceptance of a string with an DFA with usage of 'IsAcceptByDFA' function
5. Convert a DFA to the simplest form with usage of 'MakeSimpleDFA' function
6. Show the scematic version of an NFA or equivalent DFA with the usage of `ShowSchematicNFA` & `ShowSchematicDFA` functions

Input format:
```
{q0,q1,q2,q3}
{a,b}
6
q0,q1,a
q1,q2,b
q1,q3,
q3,q4,b
q2,q3,a
q4,q2,a
{q1,q3}
```

**Notes:**
* The project can be run in both terminal or GUI version by choosing the proper option at the first.
* We use `~` as the notation used for lambda transition.

![image](https://s6.uupload.ir/files/q1_41nl.png)

Project contributors:

* Danial Bazmandeh, BSc, Computer Engineering
* Alireza Haghani, BSc, Computer Engineering
