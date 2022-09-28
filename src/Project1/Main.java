package Project1;

// Theory of Computation First Project
// Instructor: Dr. Hosein Rahmani
// Written by: Danial Bazmandeh & Alireza Haqani
// Number of lines: About 1500 (+400)

import javafx.application.Application;

import java.util.*;

public class Main {
    static Map<String, Map<Character, List<String>>> transitionsNFA;
    static Map<String, Map<Character, String>> transitionsDFA;
    static List<String> statesNames;
    static Set<String> finalStates1;
    static boolean hasMultiStates;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("***Welcome to the program***");
        System.out.println("Please enter the states, alphabets and transitions in appropriate format:");

        // Save the states of the automata in states array
        String statesLine = input.nextLine();
        String[] states = statesLine.substring(1, statesLine.length() - 1).split(",");

        // Save the alphabets of the automata in alphabets array
        String alphabetsLine = input.nextLine();
        String[] alphabetsStr = alphabetsLine.substring(1, alphabetsLine.length() - 1).split(",");

        ArrayList<Character> alphabets = new ArrayList<>();
        for (String alphabet: alphabetsStr) {
            alphabets.add(alphabet.charAt(0));
        }
        // Save the number of transitions in numOfTransitions array
        int numOfTransitions = Integer.parseInt(input.nextLine());

        // Save each transition of the automata in 2D array named transitions
        Map<String, Map<Character, List<String>>> transitions = new HashMap<>();

        // Add each transition to the transitions TreeSet as the rule
        // ""state -> (character -> [next states])""
        for (int i = 0; i < numOfTransitions; i++) {
            String[] nextLine = input.nextLine().split(",");
            String from = nextLine[0];
            String to = nextLine[1];
            Character symbol = nextLine.length != 3 ? '~' : nextLine[2].charAt(0);

            if (!transitions.containsKey(from))
                transitions.put(from, new HashMap<Character, List<String>>());

            if (!transitions.get(from).containsKey(symbol))
                transitions.get(from).put(symbol, new ArrayList<String>());

            transitions.get(from).get(symbol).add(to);
        }

        // Save the final states in finalStates array
        String finalStatesLine = input.nextLine();
        String[] finalStates = finalStatesLine.substring(1, finalStatesLine.length() - 1).split(",");

        // Create an NFA by calling NFA Constructor
        NFA nfa = new NFA(states, alphabets, finalStates, transitions);
        nfa.validate();
        DFA equivalentDFA = nfa.convertNFAToDFA();
        ArrayList<List<String>> newStates = new ArrayList<>();
        ArrayList<List<String>> newFinalStates = new ArrayList<>();
        hasMultiStates = false;

        int numOfCommand = 0;
        while (numOfCommand != 8) {
            System.out.println();
            System.out.println("Select the command you want to run: (Just enter the number)");
            System.out.println("1. IsAcceptByNFA");
            System.out.println("2. FindRegex");
            System.out.println("3. CreateEquivalentDFA");
            System.out.println("4. IsAcceptByDFA");
            System.out.println("5. MakeSimpleDFA");
            System.out.println("6. Chematic NFA");
            System.out.println("7. Chematic DFA");
            System.out.println("8. Exit");
            numOfCommand = input.nextInt();
            String word;
            switch (numOfCommand) {
                case 1:
                    System.out.println();
                    System.out.println("1. IsAcceptByNFA");
                    System.out.println("Enter the word you want to check in the NFA: ");
                    word = input.next();
                    String isAccept = nfa.isAcceptByNFA(word) ? "Accepted" : "Rejected";
                    System.out.println(word + ": " + isAccept);
                    break;
                case 2:
                    System.out.println();
                    System.out.println("2. FindRegex");
                    System.out.println("Regular Expression = ");
                    DFA simplifiedDFA = equivalentDFA;
                    simplifiedDFA = simplifiedDFA.MakeSimpleDFA();
                    simplifiedDFA.FindRegex();
                    break;
                case 3:
                    System.out.println();
                    System.out.println("3. CreateEquivalentDFA");
                    System.out.println("\nThe conversion of NFA to DFA has successfully done!\n");

                    newStates = new ArrayList<>();
                    for (String st: equivalentDFA.states) {
                        newStates.add(Arrays.asList(st.split(",")));
                    }

                    newFinalStates = new ArrayList<>();
                    for (String st: equivalentDFA.finalStates) {
                        newFinalStates.add(Arrays.asList(st.split(",")));
                    }

                    if (!equivalentDFA.states.equals(nfa.states))
                        hasMultiStates = true;
                    System.out.println("States: " + newStates);
                    System.out.println("Initial State: " + equivalentDFA.initialState);
                    System.out.println("Final States: " + newFinalStates);
                    System.out.println("Transitions: " + equivalentDFA.transitions);
                    break;
                case 4:
                    System.out.println();
                    System.out.println("4. IsAcceptByDFA");
                    System.out.println("Enter the word you want to check in the equivalent DFA: ");
                    word = input.next();
                    isAccept = nfa.isAcceptByNFA(word) ? "Accepted" : "Rejected";
                    System.out.println(word + ": " + isAccept);
                    break;
                case 5:
                    System.out.println();
                    System.out.println("5. MakeSimpleDFA");
                    ArrayList<String> beforeSimplifyStates = new ArrayList<>(equivalentDFA.states);

                    equivalentDFA = equivalentDFA.MakeSimpleDFA();

                    if (!equivalentDFA.states.equals(beforeSimplifyStates))
                        hasMultiStates = true;
                    if (!hasMultiStates) {
                        System.out.println("\nThe DFA can't be simplified!\n");
                    } else {
                        System.out.println("\nThe DFA has successfully simplified!\n");

                        newStates = new ArrayList<>();
                        for (String st : equivalentDFA.states) {
                            newStates.add(Arrays.asList(st.split(",")));
                        }

                        newFinalStates = new ArrayList<>();
                        for (String st : equivalentDFA.finalStates) {
                            newFinalStates.add(Arrays.asList(st.split(",")));
                        }

                        System.out.println("States: " + newStates);
                        System.out.println("Initial State: " + equivalentDFA.initialState);
                        System.out.println("Final States: " + newFinalStates);
                        System.out.println("Transitions: " + equivalentDFA.transitions);
                    }
                    break;
                case 6:
                    System.out.println();
                    System.out.println("6. Chematic NFA");
                    statesNames = nfa.states;
                    transitionsNFA = nfa.transitions;
                    finalStates1 = nfa.finalStates;
                    Application.launch(ChematicNFA.class, args);
                    break;
                case 7:
                    System.out.println();
                    System.out.println("7. Chematic DFA");

                    if (!equivalentDFA.states.equals(nfa.states))
                        hasMultiStates = true;

                    statesNames = equivalentDFA.states;
                    transitionsDFA = equivalentDFA.transitions;
                    finalStates1 = equivalentDFA.finalStates;
                    Application.launch(ChematicDFA.class, args);
                    break;
                case 8:
                    System.out.println("8. Exit");
                    System.out.println("Have a good day!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("The number that you have entered isn't correct. Try again!");
                    System.out.println();
                    break;
            }
        }
    }
}
