package dfs;

import java.util.*;

public class DFSRouter {
    private final Map<String, List<String>> graph;
    private final Map<String, String> parents = new HashMap<>();
    private final Map<String, Integer> depths = new HashMap<>();
    private final Set<String> visited = new HashSet<>();

    public DFSRouter(Map<String, List<String>> graph) {
        this.graph = graph;
    }

    public void dfs(String start) {
        visited.clear();
        parents.clear();
        depths.clear();
        dfsUtil(start, null, 0);
    }

    private void dfsUtil(String node, String parent, int depth) {
        visited.add(node);
        parents.put(node, parent);
        depths.put(node, depth);

        for (String neighbor : graph.getOrDefault(node, Collections.emptyList())) {
            if (!visited.contains(neighbor)) {
                dfsUtil(neighbor, node, depth + 1);
            }
        }
    }

    public Map<String, Integer> getDepths() {
        return depths;
    }

    public List<String> findPath(String src, String dest) {
        dfs(src);
        List<String> path = new ArrayList<>();
        if (!parents.containsKey(dest)) return path;

        for (String at = dest; at != null; at = parents.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }
}