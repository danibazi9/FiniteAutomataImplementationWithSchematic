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

    //region Helper Functions

    public String getNextCurrentState(String currentState, Character inputSymbol){
        /*Follow the transition for the given input symbol on the current state.
        print error if the transition does not exist.*/
        if (transitions.get(currentState).containsKey(inputSymbol)){
            return transitions.get(currentState).get(inputSymbol);
        }else{
            System.out.printf("%c is not a valid input symbol", inputSymbol);
            return "";
        }
    }

    public boolean checkForInputRejection(String currentState){
        //Print an error if the given config indicates rejected input.
        if (!finalStates.contains(currentState)) {
            System.out.printf("The DFA stopped on a non-final state %s", currentState);
            return false;
        }
        return true;
    }

    //region Compute (Un)ReachableStates Functions

    public void removeUnreachableStates(){
        //Remove states which are not reachable from the initial state.
        Set<String> reachableState = this.computeReachableStates();
        Set<String> unreachableState = new HashSet<>(states);
        unreachableState.removeAll(reachableState);

        for (String state: unreachableState){
            states.remove(state);
            transitions.remove(state);
        }
    }

    public Set<String> computeReachableStates() {
        //Compute the states which are reachable from the initial state.
        Set<String> reachableStates = new HashSet<>();
        Queue<String> statesToCheck = new LinkedList<>();
        Set<String> statesChecked = new HashSet<>();
        statesToCheck.add(initialState);
        while (!statesToCheck.isEmpty()) {
            String state = statesToCheck.remove();
            reachableStates.add(state);
            for (Character symbol: transitions.get(state).keySet()) {
                String destState = transitions.get(state).get(symbol);
                if (!statesChecked.contains(destState)) {
                    statesToCheck.add(destState);
                }
            }
            statesChecked.add(state);
        }

        return reachableStates;
    }

    //endregion

    //region MarkState Functions

    public HashMap<Set<String>, Boolean> createMarkableStatesTable(){
        /*Create a "markable table" with all combinations of two states.
        This is a dict with sets of states as keys and `False` as value.*/
        HashMap<Set<String>, Boolean> table = new HashMap<>();
        for (String i: states) {
            for (String j: states) {
                if (!i.equals(j)) {
                    Set<String> set2 = new HashSet<>();
                    set2.add(i);
                    set2.add(j);
                    if (!table.containsKey(set2)) {
                        table.put(set2, false);
                    }
                }
            }
        }

        return table;
    }

    public void markStatesTableFirst(HashMap<Set<String>, Boolean> table) {
        //Mark pairs of states if one is final and one is not.
        for (Set<String> s: table.keySet()) {
            boolean flag1 = false;
            for (String x: s) {
                if (finalStates.contains(x)) {
                    flag1 = true;
                    break;
                }
            }

            boolean flag2 = false;
            if (flag1) {
                for (String x: s) {
                    if (!finalStates.contains(x)) {
                        flag1 = false;
                        flag2 = true;
                        break;
                    }
                }
            }

            if (flag2) {
                table.replace(s, true);
            }
        }
    }

    public void markStatesTableSecond(HashMap<Set<String>, Boolean> table) {
        /*Mark additional state pairs.

        A non-marked pair of two states q, q_ will be marked
        if there is an input_symbol a for which the pair
        transition(q, a), transition(q_, a) is marked.*/
        boolean changed = true;
        while (changed) {
            changed = false;
            ArrayList<Set<String>> sets = new ArrayList<>();
            for (Set<String> s: table.keySet()) {
                if (!table.get(s)) {
                    sets.add(s);
                }
            }

            for (Set<String> s: sets) {
                List<String> s1 = new ArrayList<>(s);
                for (Character a : alphabets) {
                    Set<String> s2 = new HashSet<>();
                    s2.add(getNextCurrentState(s1.get(0), a));
                    s2.add(getNextCurrentState(s1.get(1), a));

                    if (table.containsKey(s2) && table.get(s2)) {
                        table.replace(s, true);
                        changed = true;
                        break;
                    }
                }
            }
        }
    }

    public void joinNonMarkedStates(HashMap<Set<String>, Boolean> table) {
        //Join all overlapping non-marked pairs of states to a new state.
        Set<Set<String>> nonMarkedStates = new HashSet<>();
        for (Set<String> s: table.keySet()) {
            if (!table.get(s)) {
                nonMarkedStates.add(s);
            }
        }

        boolean changed = true;
        while (changed) {
            changed = false;

            Set<Set<Set<String>>> allSetPairs = new HashSet<>();
            for (Set<String> s : nonMarkedStates) {
                for (Set<String> s2 : nonMarkedStates) {
                    if (s.equals(s2)) {
                        continue;
                    } else {
                        Set<Set<String>> set = new HashSet<>();
                        set.add(s);
                        set.add(s2);
                        allSetPairs.add(set);
                    }
                }
            }

            for (Set<Set<String>> pair: allSetPairs) {
                List<Set<String>> pairList = new ArrayList<>(pair);
                if (areDisjoint(pairList.get(0), pairList.get(1))) {
                    continue;
                }

                //merge them!
                Set<String> unionSet = pairList.get(0);
                unionSet.addAll(pairList.get(1));

                //remove the old ones
                nonMarkedStates.remove(pair);

                //add the new one
                nonMarkedStates.add(unionSet);

                //set the changed flag
                changed = true;
                break;
            }
        }

        //finally adjust the DFA
        for (Set<String> states: nonMarkedStates) {
            String stringified = this.stringifyStates(states);
            //add the new state
            this.states.add(stringified);

            //copy the transitions from one of the states
            ArrayList<String> stateList = new ArrayList<>(states);
            transitions.put(stringified, transitions.get(stateList.get(0)));

            //replace all occurrences of the old states
            for (String state: states) {
                this.states.remove(state);
                transitions.remove(state);
                for (String srcState: transitions.keySet()) {
                    Map<Character, String> transition = transitions.get(srcState);
                    for (Character symbol: transition.keySet()) {
                        if (transition.get(symbol).equals(state)) {
                            transition.replace(symbol, stringified);
                        }
                    }
                }

                if (this.finalStates.contains(state)) {
                    this.finalStates.add(stringified);
                    this.finalStates.remove(state);
                }

                if (state.equals(this.initialState)) {
                    this.initialState = stringified;
                }
            }
        }
    }

    //endregion

    public String stringifyStates(Set<String> states) {
        //Stringify the given set of states as a single state name.
        List<String> list = new ArrayList<>(states);
        Collections.sort(list);

        String str = "";
        int index = 0;
        while (index < list.size()) {
            if (index != list.size() - 1) {
                str += list.get(index) + ",";
            } else {
                str += list.get(index);
            }
            index++;
        }

        return str;
    }

    //region NFA Queue Functions

    public void addNFAStatesFromQueue(NFA nfa, Set<String> currentStates, String currentStateName, Set<String> dfaStates, Map<String, Map<Character, String>> dfaTransitions, Set<String> dfaFinalStates){
        //Add NFA states to DFA as it is constructed from NFA.
        dfaStates.add(currentStateName);
        dfaTransitions.put(currentStateName, new HashMap<>());

        Set<String> nfaFinalStates =  new HashSet<>(nfa.finalStates);
        nfaFinalStates.retainAll(currentStates);
        if (!nfaFinalStates.isEmpty())
            dfaFinalStates.add(currentStateName);
    }

    public void enqueueNextNFACurrentState(NFA nfa, Set<String> currentStates, String currentStateName, Queue<Set<String>> stateQueue, Map<String, Map<Character, String>> dfaTransitions){
        //Enqueue the next set of current states for the generated DFA.
        for (Character inputSymbol: nfa.alphabets){
            Set<String> nextCurrentState = nfa.getNextCurrentStates(currentStates, inputSymbol);
            dfaTransitions.get(currentStateName).put(inputSymbol, this.stringifyStates(nextCurrentState));
            stateQueue.add(nextCurrentState);
        }
    }

    //endregion

    //region FindRegExp Helper Functions
    private void computeLanguage(Map<String, Map<String, List<String>>> transitions, Graph dfaGraph, String from, String to) {
        ArrayList<ArrayList<String>> paths;
        paths = dfaGraph.computeAllPath(from, to);

        HashMap<String, ArrayList<ArrayList<String>>> cycles;
        cycles = dfaGraph.computeAllCycle();

        System.out.println(stringifyPath(transitions, paths, cycles));
    }

    private String stringifyPath(Map<String, Map<String, List<String>>> transitions, ArrayList<ArrayList<String>> paths, HashMap<String, ArrayList<ArrayList<String>>> cycles) {
        String[] stringifyPaths = new String[paths.size()];
        int pathsIndex = 0;
        for (ArrayList<String> path: paths) {
            String transition = "";
            for (int index = 0; index < path.size() - 1; index++) {
                String current = path.get(index);
                String next = path.get(index+1);
                if (!transitions.get(current).get(current).get(0).equals(""))
                    transition += "(" + String.join("+", transitions.get(current).get(current)) + ")*";
                // if (cycles.get(current) exist)
                //     transition += foreach cycle: (stringifyPath(cycle))* and (String.join("+", cycles))
                if (cycles.get(current).size() != 0)
                    transition += "(" + stringifyCycle(transitions, cycles.get(current)) + ")*";

                if (!transitions.get(current).get(next).get(0).equals("") && !current.equals(next)) {
                    if (transitions.get(current).get(next).size() > 1)
                        transition += "(" + String.join("+", transitions.get(current).get(next)) + ")";
                    else
                        transition +=  transitions.get(current).get(next).get(0);
                }
            }
            String last = path.get(path.size()-1);
            if (!last.equals("qFinal"))
                if (!transitions.get(last).get(last).get(0).equals(""))
                    transition += "(" + String.join("+", transitions.get(last).get(last)) + ")*";

            stringifyPaths[pathsIndex++] = transition;
        }
        if (paths.size() == 1)
            return stringifyPaths[0];
        return "(" + String.join("+", stringifyPaths) + ")";
    }

    private String stringifyCycle(Map<String, Map<String, List<String>>> transitions, ArrayList<ArrayList<String>> paths){
        String[] stringifyPaths = new String[paths.size()];
        int pathsIndex = 0;
        for (ArrayList<String> path: paths) {
            String transition = "";
            for (int index = 0; index < path.size() - 1; index++) {
                String current = path.get(index);
                String next = path.get(index+1);

                if (!transitions.get(current).get(next).get(0).equals("")) {
                    if (transitions.get(current).get(next).size() > 1)
                        transition += "(" + String.join("+", transitions.get(current).get(next)) + ")";
                    else
                        transition +=  transitions.get(current).get(next).get(0);
                }
            }

            stringifyPaths[pathsIndex++] = transition;
        }
        if (paths.size() == 1)
            return stringifyPaths[0];
        return "(" + String.join("+", stringifyPaths) + ")";
    }
    //endregion

    // Returns true if set1 and set2 are disjoint, else false
    boolean areDisjoint(Set<String> set1, Set<String> set2) {
        // Take every element of set1 and
        // search it in set2
        for (String i: set1) {
            for (String j: set2) {
                if (i.equals(j)) {
                    return false;
                }
            }
        }
        // If no element of set1 is present in set2
        return true;
    }

    // region Validate functions
    public boolean validateTransitionMissingSymbols(String startState, Map<Character, String> path){
        //Raise an error if the transition input_symbols are missing.
        for (Character symbol: this.alphabets) {
            if (!path.containsKey(symbol)) {
                System.out.printf("state %s is missing transitions for symbol %s", startState, symbol);
                return false;
            }
        }
        return true;
    }

    public boolean validateTransitionInvalidSymbols(String startState, Map<Character, String> path){
        //Raise an error if transition input symbols are invalid.
        for (Character symbol: path.keySet()) {
            if (!this.alphabets.contains(symbol)) {
                System.out.printf("state %s has invalid transition symbol %s", startState, symbol);
                return false;
            }
        }
        return true;
    }

    public boolean validateTransitionStartStates(){
        //Raise an error if transition start states are missing.
        for (String state: this.states) {
            if (!this.transitions.containsKey(state)) {
                System.out.printf("transition start state %s is missing", state);
                return false;
            }
        }
        return true;
    }

    public boolean validateTransitionEndStates(String startState, Map<Character, String> path){
        //Raise an error if transition end states are invalid.
        for (String endState: path.values()) {
            if (!this.states.contains(endState)) {
                System.out.printf("end state %s for transition on %s is not valid", endState, startState);
                return false;
            }
        }
        return true;
    }

    public boolean validateTransitions(String startState, Map<Character, String> path){
        //Raise an error if transitions are missing or invalid.
        if (!validateTransitionMissingSymbols(startState, path)) return false;
        if (!validateTransitionInvalidSymbols(startState, path)) return false;
        return validateTransitionEndStates(startState, path);
    }

    public boolean validateInitialState() {
        //Raise an error if the initial state is invalid.
        if (!this.states.contains(initialState)) {
            System.out.printf("%s is not a valid initial state", this.initialState);
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
        //Return True if this DFA is internally consistent.
        if (!validateTransitionStartStates()) return false;
        for (String startState : transitions.keySet()) {
            Map<Character, String> paths = transitions.get(startState);
            if (!validateTransitions(startState, paths)) return false;
        }
        if (!validateInitialState()) return false;
        return !validateFinalStates();
    }

    //endregion

    //endregion
}
