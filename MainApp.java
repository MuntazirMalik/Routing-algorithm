package dfs;

import javafx.stage.Stage;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import javafx.application.Application;
import java.util.logging.*;

public class MainApp extends Application {
    public static void main(String[] args) {
        // Suppress GraphStream layout logs
        LogManager.getLogManager().reset();
        Logger layoutLogger = Logger.getLogger("org.graphstream.ui.layout.LayoutRunner");
        layoutLogger.setLevel(Level.OFF);
        layoutLogger.setUseParentHandlers(false);
        Logger.getLogger("").setLevel(Level.SEVERE);

        // GraphStream configuration
        System.setProperty("org.graphstream.ui", "javafx");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.javafx.util.JavaFXRenderer");

        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Scanner scanner = new Scanner(System.in);
        GraphBuilder builder = new GraphBuilder();
        Map<String, List<String>> graph = builder.getAdjacencyList();
        Visualizer visualizer = new Visualizer();

        // the default graph
        builder.addEdge("A", "B");
        builder.addEdge("A", "C");
        builder.addEdge("B", "D");
        builder.addEdge("B", "E");
        builder.addEdge("C", "F");
        builder.addEdge("C", "H");
        builder.addEdge("H", "I");
        builder.addEdge("H", "J");
        builder.addEdge("H", "K");
        builder.addEdge("K", "L");
         //Using Default graph inputs
        System.out.print("Enter DFS source node: ");
        String root = scanner.nextLine().trim();
        AtomicReference<String> rootRef = new AtomicReference<>(root);

        DFSRouter router = new DFSRouter(graph);
        router.dfs(rootRef.get());

        visualizer.buildGraph(graph);
        visualizer.annotateDistances(router.getDepths());

        System.out.print("Enter destination node to find path: ");
        String firstDestination = scanner.nextLine().trim();
        List<String> path = router.findPath(rootRef.get(), firstDestination);
         //if wrong path enters warns no path find
        if (path.isEmpty()) {
            System.out.println("[WARN] No path found.");
        } else {
            System.out.println("[PATH] " + String.join(" -> ", path));
            System.out.println("[DEPTH] " + (path.size() - 1));
            visualizer.highlightPath(path);
        }

        visualizer.show(primaryStage);

        // CLI thread for graph operations
        new Thread(() -> {
            while (true) {
                System.out.println("\nChoose an option:");
                System.out.println("[0] Add node");
                System.out.println("[1] Add edge");
                System.out.println("[2] Remove node");
                System.out.println("[3] Remove edge");
                System.out.println("[4] Re-run DFS");
                System.out.println("[5] Find path");
                System.out.println("[6] Exit");
                System.out.print("Enter choice: ");
                String choice = scanner.nextLine().trim();
                             //entering users choice to enter node or remove node enter edge or remove edge
                switch (choice) {
                    case "0":
                        System.out.print("Enter node name: ");
                        String node = scanner.nextLine().trim();
                        builder.addNode(node);
                        visualizer.rebuildGraph(graph, Collections.emptyList(), Collections.emptyMap());
                        break;
                    case "1":
                        System.out.print("Enter edge (A B): ");
                        String[] parts = scanner.nextLine().trim().split("\\s+");
                        if (parts.length == 2) {
                            builder.addEdge(parts[0], parts[1]);
                            visualizer.rebuildGraph(graph, Collections.emptyList(), Collections.emptyMap());
                        } else {
                            System.out.println("[ERROR] Format: A B");
                        }
                        break;
                    case "2":
                        System.out.print("Enter node to remove: ");
                        String nodeToRemove = scanner.nextLine().trim();
                        graph.remove(nodeToRemove);
                        for (List<String> edges : graph.values()) {
                            edges.remove(nodeToRemove);
                        }
                        visualizer.rebuildGraph(graph, Collections.emptyList(), Collections.emptyMap());
                        break;
                    case "3":
                        System.out.print("Enter edge to remove (A B): ");
                        String[] edge = scanner.nextLine().trim().split("\\s+");
                        if (edge.length == 2) {
                            graph.getOrDefault(edge[0], new ArrayList<>()).remove(edge[1]);
                            graph.getOrDefault(edge[1], new ArrayList<>()).remove(edge[0]);
                            visualizer.rebuildGraph(graph, Collections.emptyList(), Collections.emptyMap());
                        } else {
                            System.out.println("[ERROR] Format: A B");
                        }
                        break;
                    case "4":
                        System.out.print("Enter new DFS source: ");
                        rootRef.set(scanner.nextLine().trim());
                        router.dfs(rootRef.get());
                        visualizer.rebuildGraph(graph, Collections.emptyList(), router.getDepths());
                        break;
                    case "5":
                        System.out.print("Enter source: ");
                        String src = scanner.nextLine().trim();
                        System.out.print("Enter destination: ");
                        String dst = scanner.nextLine().trim();
                        List<String> dfsPath = router.findPath(src, dst);
                        if (dfsPath.isEmpty()) {
                            System.out.println("[WARN] No path found.");
                        } else {
                            System.out.println("[PATH] " + String.join(" -> ", dfsPath));
                            System.out.println("[DEPTH] " + (dfsPath.size() - 1));
                            visualizer.rebuildGraph(graph, dfsPath, Collections.emptyMap());
                        }
                        break;
                    case "6":
                        System.out.println("[EXIT] Exiting...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("[ERROR] Invalid choice.");
                }
            }
        }).start();
    }
}
