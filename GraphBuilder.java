package dfs;

import java.util.*;

public class GraphBuilder {
    private final Map<String, List<String>> adjacencyList = new HashMap<>();

    // Remove any constructor parameters if they exist
    public GraphBuilder() {
    }

    public void addNode(String node) {
        adjacencyList.putIfAbsent(node, new ArrayList<>());
    }

    public void addEdge(String src, String dest) {
        addNode(src);
        addNode(dest);
        adjacencyList.get(src).add(dest);
        adjacencyList.get(dest).add(src); // Undirected graph
    }

    public Map<String, List<String>> getAdjacencyList() {
        return adjacencyList;
    }
}