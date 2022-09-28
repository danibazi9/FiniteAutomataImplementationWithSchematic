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

    //region Primary Functions

    public DFA MakeSimpleDFA() {
        /*
        Create a minimal DFA which accepts the same inputs as this DFA.

        First, non-reachable states are removed.
        Then, similar states are merged.
        */
        DFA newDFA = this;
        newDFA.removeUnreachableStates();
        HashMap<Set<String>, Boolean> statesTable = newDFA.createMarkableStatesTable();
        newDFA.markStatesTableFirst(statesTable);
        newDFA.markStatesTableSecond(statesTable);
        newDFA.joinNonMarkedStates(statesTable);

        return newDFA;
    }

    public DFA CreateEquivalentDFA(NFA nfa) {
        //Initialize this DFA as one equivalent to the given NFA.
        Set<String> dfaStates = new HashSet<>();
        ArrayList<Character> dfaSymbols = nfa.alphabets;
        Map<String, Map<Character, String>> dfaTransitions = new HashMap<>();

        //equivalent DFA states
        Set<String> nfaInitialStates = nfa.getLambdaClosure(nfa.initialState);
        String dfaInitialState = this.stringifyStates(nfaInitialStates);
        Set<String> dfaFinalStates = new HashSet<>();

        Queue<Set<String>> stateQueue = new LinkedList<>();
        stateQueue.add(nfaInitialStates);

        while (!stateQueue.isEmpty()) {
            Set<String> currentStates = stateQueue.remove();
            String currentStateName = this.stringifyStates(currentStates);

            if (dfaStates.contains(currentStateName)) {
                //We've been here before and nothing should have changed.
                continue;
            }
            this.addNFAStatesFromQueue(nfa, currentStates, currentStateName, dfaStates, dfaTransitions, dfaFinalStates);
            this.enqueueNextNFACurrentState(nfa, currentStates, currentStateName, stateQueue, dfaTransitions);
        }

        String[] dfaStatesArr = new String[dfaStates.size()];
        String[] finalStatesArr = new String[dfaFinalStates.size()];

        return new DFA(dfaStates.toArray(dfaStatesArr), dfaSymbols, dfaFinalStates.toArray(finalStatesArr), dfaInitialState, dfaTransitions);
    }

    public boolean isAcceptByDFA(String inputString){
        /*
        Check if the given string is accepted by this DFA.
        Yield the current configuration of the DFA at each step.
         */
        String currentState = initialState;

        if (inputString.equals("~"))
            return finalStates.contains(initialState);

        for (Character inputSymbol: inputString.toCharArray()){
            currentState = getNextCurrentState(currentState, inputSymbol);
        }

        return checkForInputRejection(currentState);
    }

    public void FindRegex(){
        //region StateEliminate
        // convert transition format from (start, (symbol, end)) to (start, (end, symbol))
        Map<String, Map<String, List<String>>> transitions = new HashMap<>();
        List<String> states = new ArrayList<>(this.states);

        //fill transitions
        for (String from: this.transitions.keySet()){
            for (Character symbol: this.transitions.get(from).keySet()){
                String to = this.transitions.get(from).get(symbol);
                //(start, (end, symbol)
                if (!transitions.containsKey(from))
                    transitions.put(from, new HashMap<>());
                if (!transitions.get(from).containsKey(to))
                    transitions.get(from).put(to, new ArrayList<>());
                transitions.get(from).get(to).add(symbol.toString());
            }
        }

        // convert multi final state to single final state
        String finalState = "qFinal";
        for (String state: this.finalStates) {
            transitions.get(state).put(finalState, new ArrayList<>());
            transitions.get(state).get(finalState).add("");
        }
        states.add(finalState);

        //create equivalent graph
        Graph dfaGraph = new Graph(states);
        for (String from: transitions.keySet()){
            for (String to: transitions.get(from).keySet()){
                dfaGraph.addEdge(from, to);
            }
        }

        //dfaGraph.computeAllPath("q7", "q6"); //done!
        Map<String[], String> language = new HashMap<>();

        for (String from: states){
            if (!from.equals(finalState)){
                for (String to: states) {
                    String[] key = new String[]{from, to};
                    language.put(key, "");
                    List<String> singleEdgeSymbols = new ArrayList<>();
                    for (Character symbol : this.alphabets) {
                        if (transitions.get(from).containsKey(to)) {
                            if (transitions.get(from).get(to).contains(symbol.toString()))
                                singleEdgeSymbols.add(symbol.toString());
                        }
                    }
                    if (!singleEdgeSymbols.isEmpty())
                        language.replace(key, String.join("+", singleEdgeSymbols));

                    if (!transitions.get(from).containsKey(to))
                        transitions.get(from).put(to, new ArrayList<>());


                    transitions.get(from).get(to).clear();
                    transitions.get(from).get(to).add(language.get(key).contains("+") ?
                            "(" + language.get(key) + ")" : language.get(key));
                }
            }

        }

        computeLanguage(transitions, dfaGraph, initialState, finalState);
    }

    //endregion
}
