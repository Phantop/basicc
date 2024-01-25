package hw1;
import java.util.*;
/**
 * <b>Graph</b> represents a mutable directed graph
 */
public class Graph<N extends Comparable<N>, E extends Comparable<E>> {
    // Map of Nodes to their map of <childNode, edgeLabel>
    private Map<N, ArrayList<Map.Entry<N, E>>> wholeGraph;

    // Abstraction Function:
    // Graph, g, represents a graph such that:
    // The nodes of the graph are the keys of wholeGraph
    // The edges from a node are stored at index 1 of the relevant value of wholeGraph,
    // and the child node each edge points to is at index 0
    //
    // For example, where wholeGraph is comprised of a single entry like:
    //    "a" : { "b", "1" }
    // Then it represents a graph comprised of two nodes "a" and "b" with an edge
    // labeled as "1" from "a" to "b"
    //
    //
    // Representation Invariant for every Graph g:
    // - wholeGraph, and the values enclosed by it, should not be null
    // - All child nodes should also be keys of wholeGraph
    //   (effectively, also parent nodes, even if they have no children)

    /**
     * @effects Constructs a new Graph with no nodes or edges.
     * @modifies wholeGraph
     * @returns none
     * @requires true
     * @throws none
     */
    public Graph() {
        wholeGraph = new HashMap<N, ArrayList<Map.Entry<N, E>>>();
    }

    /**
     * @effects Constructs a new Graph with the inputGraph data inputted
     * @modifies wholeGraph
     * @returns none
     * @requires inputGraph follows the rep invariant for wholeGraph
     * @throws none
     */
    public Graph(Map<N, ArrayList<Map.Entry<N, E>>> inputGraph) {
        wholeGraph = new HashMap<N, ArrayList<Map.Entry<N, E>>>(inputGraph);
        checkRep();
    }


    /**
     * Adds a node to This Graph
     *
     * @param nodeData the data in the node to be added
     * @requires true
     * @modifies wholeGraph
     * @effects a node with data of nodeData is added if it doesn't exist, otherwise no change occurs
     * @throws none
     * @return none
     */
    public void addNode(N nodeData) { 
        if (!wholeGraph.containsKey(nodeData))
            wholeGraph.put(nodeData, new ArrayList<Map.Entry<N, E>>());
        checkRep();
    }

    /**
     * Adds an edge to This Graph
     *
     * @param parentNode the data of the parent node
     * @param childNode the data of the child node
     * @param edgeLabel the data of the edge to connect them
     * @requires both parentNode and childNode exist in the graph
     * @modifies wholeGraph
     * @effects adds an edge from parentNode to childNode with edge edgeLabel
     * @throws none
     * @return none
     */
    public void addEdge(N parentNode, N childNode, E edgeLabel) {
        wholeGraph.get(parentNode).add(new AbstractMap.SimpleEntry<N, E>(childNode, edgeLabel));
        checkRep();
    }

    /**
     * Indicates whether a provided node is in the graph
     * @param node the data of the node to check
     * @requires true
     * @return true of false
     * @throws none
     * @modifies none
     * @effects none
     */
    public boolean has(String node) {
        return (wholeGraph.containsKey(node));
    }


    /**
     * Implements an alphabetical Iterator of all nodeData
     * @requires true
     * @return an ordered Iterator over the nodes of this graph
     * @throws none
     * @modifies none
     * @effects none
     */
    public Iterator<N> listNodes() {
        Map<N, ArrayList<Map.Entry<N, E>>> treeGraph = new HashMap<N, ArrayList<Map.Entry<N, E>>>(wholeGraph);
        return java.util.Collections.unmodifiableCollection(wholeGraph.keySet()).iterator();
    }

    /**
     * Implements an alphabetical Iterator of the children and edges of a parent node
     * @param parentNode the data of the parent node
     * @requires parentNode exists in the graph
     * @return an ordered Iterator over the children and edges of this node
     *         in the format of childNode(edgeLabel)
     * @throws none
     * @effects none
     * @modifies none
     */
    public Iterator<String> listChildren(N parentNode) {
        TreeSet<String> outSet = new TreeSet<String>();
        for (Map.Entry<N, E> i : wholeGraph.get(parentNode)) {
            outSet.add(i.getKey().toString() + "(" + i.getValue().toString() + ")");
        }
        return java.util.Collections.unmodifiableCollection(outSet).iterator();
    }

    /**
     * Implements a list of the children and edges of a parent node
     * @param parentNode the data of the parent node
     * @requires parentNode exists in the graph
     * @return an ordered list of the children and edges of this node
     *         in the format of Pair<childNode, edgeLabel>
     * @throws none
     * @effects none
     * @modifies none
     */
    public List<Map.Entry<N, E>> listEdges(N parentNode) {
        List<Map.Entry<N, E>> copy = new ArrayList<Map.Entry<N, E>>(wholeGraph.get(parentNode));
        copy.sort(Map.Entry.comparingByValue());
        copy.sort(Map.Entry.comparingByKey());
        return copy;
    }

    /**
     * Checks that the representation invariant holds (if any).
     * @requires true
     * @return none
     * @modifies none
     * @effects none
     * @throws RuntimeException if invariant broken
     **/
    private void checkRep() throws RuntimeException {
        for (ArrayList<Map.Entry<N, E>> i : wholeGraph.values())
            for (Map.Entry<N, E> j : i)
                if (wholeGraph.get(j.getKey()) == null)
                    throw new RuntimeException("Child node not in Graph");
    }
}
