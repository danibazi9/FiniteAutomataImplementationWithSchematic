package Project1;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.lang.Object.*;

public class DFA {
    //region Constructor
    String initialState;
    ArrayList<Character> alphabets;
    Set<String> finalStates;
    List<String> states;

    // state -> (character -> [next states])
    // The difference from DFA: can have multiple different transitions from a state for each character
    Map<String, Map<Character, String>> transitions;

    DFA(){ }

    DFA(String[] states, ArrayList<Character> alphabets, String[] finalStates, String initialState, Map<String ,Map<Character, String>> transitions) {
        this.alphabets = alphabets;
        this.transitions = transitions;
        this.states = new ArrayList<String>(Arrays.asList(states));

        this.finalStates = new TreeSet<String>();
        // Add initial state and each final states of the NFA to finalStates Treeset
        this.initialState = initialState;
        this.finalStates.addAll(Arrays.asList(finalStates));

        if (validate())
            System.out.println("\nThe DFA has successfully created!\n");
        //Sort the name of states and alphabets
        Collections.sort(this.states);
        Collections.sort(this.alphabets);
    }
    //endregion
}
