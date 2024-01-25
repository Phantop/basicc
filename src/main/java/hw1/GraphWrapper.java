package hw1;
import java.util.Iterator;
public class GraphWrapper extends Graph {
    private Graph<String,String> g;

    /**
     * @effects Constructs a new graph with no nodes or edges.
     * @modifies g
     * @returns none
     * @requires true
     * @throws none
     */
    public GraphWrapper() {
        g = new Graph<String,String>();
    }

    /**
     * Adds a node to this graph
     *
     * @param nodeData the data in the node to be added
     * @modifies g
     * @effects a node with data of nodeData is added if it doesn't exist, otherwise no change occurs
     * @requires true
     * @returns none
     * @throws none
     *          
     */
    public void addNode(String nodeData) { g.addNode(nodeData); }

    /**
     * Adds an edge to this graph
     *
     * @param parentNode the data of the parent node
     * @param childNode the data of the child node
     * @param edgeLabel the data of the edge to connect them
     * @requires both parentNode and childNode exist in the graph
     * @modifies g
     * @effects adds an edge from parentNode to childNode with edge edgeLabel
     * @throws none
     */
    public void addEdge(String parentNode, String childNode, String edgeLabel) {
        g.addEdge(parentNode, childNode, edgeLabel);
    }

    /**
     * Implements an alphabetical Iterator of all nodeData
     * @return an ordered Iterator over the nodes of this graph
     * @requires true
     * @modifies none
     * @effects none
     * @throws none
     */
    public Iterator<String> listNodes() { return g.listNodes(); }

    /**
     * Implements an alphabetical Iterator of the children and edges of a parent node
     * @param parentNode the data of the parent node
     * @requires parentNode exists in the graph
     * @modifies none
     * @throws none
     * @return an ordered Iterator over the children and edges of this node
     *         in the format of childNode(edgeLabel)
     */
    public Iterator<String> listChildren(String parentNode) {
        return g.listChildren(parentNode);
    }
}
