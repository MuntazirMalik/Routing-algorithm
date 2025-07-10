package dfs;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.fx_viewer.FxViewer;
import org.graphstream.ui.fx_viewer.FxViewPanel;
import org.graphstream.ui.view.Viewer;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.*;


public class Visualizer {
    private final Graph graph;
    private FxViewer viewer;

    public Visualizer() {
        System.setProperty("org.graphstream.ui", "javafx");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.javafx.util.JavaFXRenderer");

        // Suppress only the "SpringBox stopped" message
        Logger.getLogger("org.graphstream.ui.layout.LayoutRunner").setLevel(Level.OFF);
        Logger.getLogger("").setLevel(Level.SEVERE);

        graph = new SingleGraph("DFS Visualizer");
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");
    }


    public void buildGraph(Map<String, List<String>> adjList) {
        int i = 0;
        int total = adjList.size();
        double radius = 150.0;

        for (String node : adjList.keySet()) {
            if (graph.getNode(node) == null) {
                Node n = graph.addNode(node);
                n.setAttribute("ui.label", node);
                n.setAttribute("ui.style", "fill-color: #8BC34A; size: 40px; text-size: 15px; text-color: black;");

                double angle = 2 * Math.PI * i / total;
                double x = radius * Math.cos(angle);
                double y = radius * Math.sin(angle);
                n.setAttribute("xyz", x, y, 0);
                i++;
            }
        }

        Set<String> added = new HashSet<>();
        for (String src : adjList.keySet()) {
            for (String dest : adjList.get(src)) {
                String id1 = src + "-" + dest;
                String id2 = dest + "-" + src;
                if (!added.contains(id1) && !added.contains(id2)) {
                    Edge e = graph.addEdge(id1, src, dest);
                    e.setAttribute("ui.style", "fill-color: #bdbdbd; size: 1px;");
                    added.add(id1);
                }
            }
        }
    }

    public void annotateDistances(Map<String, Integer> distances) {
        for (Map.Entry<String, Integer> entry : distances.entrySet()) {
            Node node = graph.getNode(entry.getKey());
            if (node != null) {
                String label = entry.getKey() + " (" + entry.getValue() + ")";
                node.setAttribute("ui.label", label);
            }
        }
    }

    public void highlightPath(List<String> path) {
        for (int i = 0; i < path.size(); i++) {
            Node node = graph.getNode(path.get(i));
            if (node != null) {
                node.setAttribute("ui.style", "fill-color: #4C1C24; size: 45px; text-size: 15px; text-color: white;");
            }

            if (i < path.size() - 1) {
                String u = path.get(i);
                String v = path.get(i + 1);
                String id1 = u + "-" + v;
                String id2 = v + "-" + u;
                Edge e = graph.getEdge(id1);
                if (e == null) e = graph.getEdge(id2);
                if (e != null) {
                    e.setAttribute("ui.style", "fill-color: #4C1C24; size: 3px;");
                }
            }
        }
    }
    private Stage primaryStage;
    public void rebuildGraph(Map<String, List<String>> adjList,
                             List<String> highlightPath,
                             Map<String, Integer> distances) {
        Platform.runLater(() -> {
            try {
                if (viewer != null) {
                    viewer.close(); // Stop layout to avoid conflicts
                }

                graph.clear();
                buildGraph(adjList);

                if (distances != null && !distances.isEmpty()) {
                    annotateDistances(distances);
                }

                if (highlightPath != null && !highlightPath.isEmpty()) {
                    highlightPath(highlightPath);
                }

                if (primaryStage == null) {
                    show(new Stage());
                } else {
                    show(primaryStage);
                }
            } catch (Exception e) {
                System.err.println("Graph update failed: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public void show(Stage stage) {
        this.primaryStage = stage;  // Save for reuse

        try {
            viewer = new FxViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
            viewer.enableAutoLayout();
            FxViewPanel panel = (FxViewPanel) viewer.addDefaultView(false); // false = no Swing

            StackPane root = new StackPane(panel);
            Scene scene = new Scene(root, 800, 600);
            stage.setTitle("DFS Visualizer");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            System.err.println("Error displaying graph: " + e.getMessage());
            e.printStackTrace();
        }
    }
}