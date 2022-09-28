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

    //region Helper Functions

    public Set<String> getLambdaClosure(String startState) {
        /*
        Return the lambda closure for the given state.

        The lambda closure of a state q is the set containing q, along with
        every state that can be reached from q by following only lambda
        transitions.
         */
        Stack<String> stack = new Stack<>();
        Set<String> encounteredStates = new TreeSet<>();
        stack.push(startState);

        while (!stack.isEmpty()){
            String state = stack.pop();
            if (!encounteredStates.contains(state)) {
                encounteredStates.add(state);
                if (transitions.containsKey(state)) {
                    if (transitions.get(state).containsKey('~'))
                        stack.addAll(transitions.get(state).get('~'));
                }
            }
        }

        return encounteredStates;
    }

    public Set<String> getNextCurrentStates(Set<String> currentStates, Character inputSymbol){
        //Return the next set of current states given the current set.
        Set<String> nextCurrerntStates = new TreeSet<>();
        for (String currentState: currentStates){
            if (transitions.containsKey(currentState)) {
                if (transitions.get(currentState).containsKey(inputSymbol)) {
                    if (transitions.get(currentState).get(inputSymbol) != null) {
                        List<String> symbolEndStates = transitions.get(currentState).get(inputSymbol);
                        if (!symbolEndStates.isEmpty()) {
                            for (String endState : symbolEndStates) {
                                nextCurrerntStates.addAll(getLambdaClosure(endState));
                            }
                        }
                    }
                }
            }
        }

        return nextCurrerntStates;
    }

    public DFA convertNFAToDFA() {
        //Initialize this NFA as one equivalent to the given DFA.
        return new DFA().CreateEquivalentDFA(this);
    }

    // region Validate functions
    public boolean validateInvalidSymbols(String startState, Map<Character, List<String>> path) {
        for (Character inputSymbol : path.keySet()) {
            if (!alphabets.contains(inputSymbol) && inputSymbol != '~') {
                System.out.printf("State %s has invalid transition symbol %c", startState, inputSymbol);
                return false;
            }
        }
        return true;
    }

    public boolean validateTransitionEndStates(String startState, Map<Character, List<String>> path) {
        //Raise an error if transition end states are invalid.
        for (List<String> endStates : path.values()) {
            for (String endState : endStates) {
                if (!states.contains(endState)) {
                    System.out.printf("End state %s for transition on %s is not valid", endState, startState);
                    return false;
                }
            }
        }
        return true;
    }

    public boolean validateInitialState() {
        //Raise an error if the initial state is invalid.
        if (!this.states.contains(initialState)) {
            System.out.printf("%s is not a valid initial state", this.initialState);
            return false;
        }
        return true;
    }

    public boolean validateInitialStateTransition() {
        //Raise an error if the initial state has no transitions defined.
        if (!this.transitions.containsKey(this.initialState)) {
            System.out.printf("Initial state %s has no transitions defined!", this.initialState);
            return false;
        }
        return true;
    }

    public boolean validateFinalStates() {
        //Raise an error if any final states are invalid.
        Set<String> invalidStates = new HashSet<>(this.finalStates);
        invalidStates.removeAll(this.states);
        if (invalidStates.size() > 0) {
            System.out.println("final states" + invalidStates.toString() + "are not valid");
            return false;
        }
        return true;
    }

    public boolean validate() {
        //Return True if this NFA is internally consistent.
        for (String startState : transitions.keySet()) {
            Map<Character, List<String>> path = transitions.get(startState);
            if (!validateInvalidSymbols(startState, path))
                return false;
            if (!validateTransitionEndStates(startState, path))
                return false;
        }
        if (!validateInitialState()) return false;
        if (!validateInitialStateTransition()) return false;
        return validateFinalStates();
    }
    //endregion

    //endregion
}