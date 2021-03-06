package Graph;

import java.util.ArrayList;
import java.util.List;

public class UndirectedGraph implements Graph {

	private List<Node> nodes;

	public UndirectedGraph() {
		nodes = new ArrayList<Node>();
	}

	@Override
	public int size() {

		return nodes.size();
	}

	@Override
	public boolean isEmpty() {

		return nodes.isEmpty();
	}

	@Override
	public List<Node> getNodes() {

		return nodes;
	}

	@Override
	public void addNode(Node n) {

		nodes.add(n);
	}

	@Override
	public void removeNode(Node n) {

		for (Node node : nodes) {
			node.removeNeighbour(n);
		}
		nodes.remove(n);
	}

	@Override
	public void addEdge(Node source, Node destination) {

		source.addNeighbour(destination);
		destination.addNeighbour(source);
	}

	/**
	 * NO.1 FIND HOW MANY PAIRS OF NODES ARE APART AFTER A NODE IS DELETED Idea:
	 * Attempt to remove u from g, which will possibly create some isolated
	 * graph components Then run DFS on each components, for each DFS, record
	 * how many nodes are there in this component Finally do some
	 * calculation....
	 * */
	public void disruptingPowerOf(Graph g, UndirectedGraphNode u) {
		g.removeNode(u);
		List<Node> record = new ArrayList<Node>();
		for (Node node : g.getNodes()) {
			record.add(node);
		}

		int num = 0;
		int totalVertex = 0;
		List<Integer> treeSize = new ArrayList<Integer>();
		List<Node> visited;
		List<Node> toVisit;

		while (!record.isEmpty()) {
			visited = new ArrayList<Node>();
			toVisit = new ArrayList<Node>();

			Node node = record.get(0);
			visited.add(node);
			toVisit.add(node);

			while (!toVisit.isEmpty()) {
				Node current = toVisit.remove(toVisit.size() - 1);
				record.remove(current);
				for (Node child : current.getNeighbours()) {
					if (!visited.contains(child)) {
						toVisit.add(child);
						visited.add(child);
					}

				}
				++num; // record how many nodes are found in this sub graph
			}
			totalVertex += num;
			treeSize.add(num);
			num = 0;
		}

		int power = 0;
		for (Integer i : treeSize) {
			power += i * (totalVertex - i);
		}

		System.out.println(treeSize.toString());
		System.out.println("How many pairs are aparted: " + power / 2);

	}

	/**
	 * NO.2 TESTING IF A GRAPH IS BIPARTITE Idea: A graph is bipartite iff its
	 * BFS tree contains no odd cycle The algorithm will be to prove this
	 * property
	 * 
	 * --- NOT YET FINISHED
	 * */

	public void isBipartite(Graph g) {
		// A different implementation of BFS.
		List<Node> layers = new ArrayList<Node>();
		List<Node> currentLayer = new ArrayList<Node>();
		List<Node> nextLayer = new ArrayList<Node>();
		Node start = g.getNodes().get(0);
		currentLayer.add(start);
		boolean seen[] = new boolean[g.getNodes().size()];

		int counter = 0;
		for (Node node : g.getNodes()) {
			if (!node.equals(start))
				seen[counter] = false;
			else
				seen[counter] = true;
			++counter;
		}

		while (!currentLayer.isEmpty()) {
			layers.addAll(currentLayer);
			for (Node u : currentLayer) {
				for (Node v : u.getNeighbours()) {
					nextLayer.add(v);

				}
			}
		}
	}

	/**
	 * NO.3 Leaf-constrained MST problem Given G=(V, E) and A as a subset of V
	 * find MST such that all vertices in A are leaves
	 */
	public Graph findLCMST(UndirectedGraph g, List<Node> A) {
		/*
		 *     Detach all u∈A from G
		 *     dfsNodes = DFS(G)
		 *     if |dfsNodes| != |G-A|
		 *       	report the problem is infeasible and return
		 *     T = The MST generated by running Kruskal's alogirthm on G-A
		 *     Reattach all u∈A to restore G
		 *     for each u∈A
		 *        v = the vertex in G-A that is nearest to u
		 *        T = T∪{(u,v)} 
		 *        return T
		 */

		return null;
	}

	/** NO.4 BFS AND DFS */
	public List<Node> BFS(Node startNode) {

		List<Node> visited = new ArrayList<Node>();
		List<Node> toVisit = new ArrayList<Node>();
		List<Node> visitedOrder = new ArrayList<Node>(); /*- which we will add nodes to in the order that we 
															expand them, and return at the end  */
		visited.add(startNode);
		toVisit.add(startNode);

		while (!toVisit.isEmpty()) {
			Node current = toVisit.remove(0);
			for (Node child : current.getNeighbours()) {
				if (!visited.contains(child)) {
					toVisit.add(child);
					visited.add(child);
				}
			}
			visitedOrder.add(current);
		}

		return visitedOrder;
	}

	public List<Node> DFS(Node startNode) {

		List<Node> visited = new ArrayList<Node>();
		List<Node> toVisit = new ArrayList<Node>();
		List<Node> visitedOrder = new ArrayList<Node>(); /*- which we will add nodes to in the order that we 
															expand them, and return at the end  */
		visited.add(startNode);
		toVisit.add(startNode);

		while (!toVisit.isEmpty()) {
			Node current = toVisit.remove(toVisit.size() - 1);
			for (Node child : current.getNeighbours()) {
				if (!visited.contains(child)) {
					toVisit.add(child);
					visited.add(child);
				}
			}
			visitedOrder.add(current);
		}

		return visitedOrder;
	}

	/**NO 5. check if is cut vertex*/
	public boolean isCutVertex (UndirectedGraph g, List<Node> nodes){
		
		return false;
	}
	
	
	public String toString(List<Node> nodes) {

		String str = new String();
		for (Node node : nodes) {
			str += node.getValue();
			str += " , ";
		}
		return str;
	}

	public static void main(String[] agrs) {

		UndirectedGraph dg = new UndirectedGraph();
		UndirectedGraphNode A = new UndirectedGraphNode("A");
		UndirectedGraphNode B = new UndirectedGraphNode("B");
		UndirectedGraphNode C = new UndirectedGraphNode("C");
		UndirectedGraphNode D = new UndirectedGraphNode("D");
		UndirectedGraphNode E = new UndirectedGraphNode("E");
		UndirectedGraphNode F = new UndirectedGraphNode("F");
		UndirectedGraphNode G = new UndirectedGraphNode("G");
		UndirectedGraphNode H = new UndirectedGraphNode("H");
		UndirectedGraphNode I = new UndirectedGraphNode("I");
		UndirectedGraphNode J = new UndirectedGraphNode("J");
		UndirectedGraphNode K = new UndirectedGraphNode("K");
		UndirectedGraphNode L = new UndirectedGraphNode("L");
		UndirectedGraphNode M = new UndirectedGraphNode("M");
		UndirectedGraphNode N = new UndirectedGraphNode("N");
		UndirectedGraphNode O = new UndirectedGraphNode("O");

		dg.addNode(A);
		dg.addNode(H);
		dg.addNode(B);
		dg.addNode(I);
		dg.addNode(C);
		dg.addNode(J);
		dg.addNode(D);
		dg.addNode(K);
		dg.addNode(G);
		dg.addNode(L);
		dg.addNode(E);
		dg.addNode(M);
		dg.addNode(F);
		dg.addNode(N);
		dg.addNode(G);
		dg.addNode(O);

		dg.addEdge(A, B);
		dg.addEdge(E, J);
		dg.addEdge(N, M);
		dg.addEdge(A, D);
		dg.addEdge(G, H);
		dg.addEdge(N, O);
		dg.addEdge(C, D);
		dg.addEdge(G, I);
		dg.addEdge(C, B);
		dg.addEdge(C, K);
		dg.addEdge(B, E);
		dg.addEdge(K, L);
		dg.addEdge(E, F);
		dg.addEdge(B, L);
		dg.addEdge(E, G);
		dg.addEdge(L, M);

		System.out.println(dg.toString(dg.DFS(A)));
		// dg.isBipartite(dg);
		// dg.disruptingPowerOf(dg, L);
	}
}
