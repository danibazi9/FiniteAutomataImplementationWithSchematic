package Project1;

import java.util.*;

public class NFA {
    //region Constructor
    String initialState;
    ArrayList<Character> alphabets;
    Set<String> finalStates;
    List<String> states;

    // state -> (character -> [next states])
    // The difference from DFA: can have multiple different transitions from a state for each character
    Map<String, Map<Character, List<String>>> transitions;

    // Constructs the NFA from the arrays, as specified in the overall header
    NFA(String[] states, ArrayList<Character> alphabets, String[] finalStates, Map<String, Map<Character, List<String>>> transitions) {
        this.alphabets = alphabets;
        this.transitions = transitions;
        this.states = new ArrayList<String>(Arrays.asList(states));

        this.finalStates = new TreeSet<String>();
        // Add initial state and each final states of the NFA to finalStates Treeset
        this.initialState = states[0];
        this.finalStates.addAll(Arrays.asList(finalStates));

        if (validate()) {
            System.out.println("\nThe NFA has successfully created!\n");

            //Sort the name of states and alphabets and final states
            Collections.sort(this.states);
            Collections.sort(this.alphabets);
            ArrayList<String> newFinalStates = new ArrayList<>(this.finalStates);
            Collections.sort(newFinalStates);
            System.out.println("States: " + this.states);
            System.out.println("Initial State: " + this.initialState);
            System.out.println("Final States: " + newFinalStates);
            System.out.println("Transitions: " + this.transitions);
        }
    }
    //endregion

    //region Primary Functions

    public boolean isAcceptByNFA(String inputString){
        DFA equivalentDFA = new DFA().CreateEquivalentDFA(this);
        return equivalentDFA.isAcceptByDFA(inputString);
    }

    //endregion
}