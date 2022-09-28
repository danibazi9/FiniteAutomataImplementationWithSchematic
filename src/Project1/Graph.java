package Project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private int vertices;
    List<String> states;
    private Map<String, ArrayList<String>> adjacencyList;

    public Graph(List<String> states) {
        this.vertices = states.size();
        this.states = new ArrayList<>(states);
        initializeAdjacencyList();
    }

    private void initializeAdjacencyList() {
        adjacencyList = new HashMap<>();

        for (String state : states) {
            adjacencyList.put(state, new ArrayList<>());
        }
    }

    public void addEdge(String from, String to) {
        if (!adjacencyList.get(from).contains(to))
            adjacencyList.get(from).add(to);
    }

    public void removeEdge(String from, String to) {
        adjacencyList.remove(from, to);
    }

    public void removeState(String state) {
        adjacencyList.remove(state);
        for (String node : adjacencyList.keySet())
            adjacencyList.get(node).remove(state);
        states.remove(state);
    }

    public ArrayList<ArrayList<String>> computeAllPath(String src, String dest) {
        Map<String, Boolean> isVisited = new HashMap<>();
        for (String state : states)
            isVisited.put(state, false);
        ArrayList<String> path = new ArrayList();
        ArrayList<ArrayList<String>> paths = new ArrayList<>();

        path.add(src);
        computeAllPathUtil(src, dest, isVisited, path, paths);

        return paths;
    }

    public void computeAllPathUtil(String from, String to, Map<String, Boolean> isVisited, ArrayList<String> path, ArrayList<ArrayList<String>> paths) {
        isVisited.replace(from, true);

        if (from.equals(to)) {
            ArrayList<String> pathCopy = new ArrayList(path);
            paths.add(pathCopy);
            isVisited.replace(from, false);
            return;
        }

        for (String adjanceState : adjacencyList.get(from)) {
            if (!isVisited.get(adjanceState)) {
                path.add(adjanceState);
                computeAllPathUtil(adjanceState, to, isVisited, path, paths);
                path.remove(adjanceState);
            }
        }

        isVisited.replace(from, false);
    }

    public HashMap<String, ArrayList<ArrayList<String>>> computeAllCycle() {
        HashMap<String, ArrayList<ArrayList<String>>> allCycles = new HashMap<>();
        for (String state : states) {
            allCycles.put(state, computeAllCycleUtil(state));
        }
        return allCycles;
    }

    public ArrayList<ArrayList<String>> computeAllCycleUtil(String state) {
        ArrayList<ArrayList<String>> cycles = new ArrayList<>();

        //TODO
        // find cycle and add it to cycles
        for (String adjance : adjacencyList.get(state)) {
            cycles.addAll(computeAllPath(adjance, state));
        }

        for (ArrayList<String> cycle : cycles) {
            cycle.add(0, state);
        }

        return cycles;
    }
}


