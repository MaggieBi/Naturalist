import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class naturalistA5 extends Naturalist {

	// stack for DFS
	private Stack<Node> unvisited = new Stack<Node>();
	// Arraylist implementation of Queue that stores nodes visited
	private ArrayList<Node> visited = new ArrayList<Node>();
	// Arraylist that stores all nodes with animals
	public static ArrayList<Node> nwa = new ArrayList<Node>();
	// the node that is ship
	public static Node ship;

	@Override
	public void run() {

		// traverse through the map and make marks
		DFS(getLocation());
		// compute the distance of each node
		computePath(ship);

		// sort the nodes with animals on them
		Node[] nwa_1 = new Node[nwa.size()];
		for (int c = 0; c < nwa.size(); c++) {
			nwa_1[c] = nwa.get(c);
		}
		Arrays.sort(nwa_1);

		// go through all nodes with animals
		// starting from the last element
		for (int a = nwa_1.length - 1; a >= 0; a--) {
			move(nwa_1[a]);
			// if the naturalist reaches the last node of the path
			if (getLocation() == nwa_1[a]) {
				collectAnimals(nwa_1[a]);
			}
		}
	}
	
	/**
	 * Traverses through the map using depth first search and marks every node
	 * with distance from the ship and its previous node of the shortest path
	 * 
	 * @param n, where the naturalist currently is located
	 */
	private void DFS(Node n) {
		// mark n as visited
		visited.add(n);
		unvisited.push(n);
		// assign ship to the starting point
		ship = n;

		while (!unvisited.isEmpty()) {
			// get the top of the stack
			moveTo(unvisited.peek());
			// add the node if it has animals
			record(getLocation());
			// set the user data to a NodeData object
			NodeData k = new NodeData(getExits());
			getLocation().setUserData(k);
			// if there is an univisited neighbor
			if (helper(getLocation()) != -1) {
				int a = helper(getLocation());
				// mark as visited
				visited.add(getExits()[a]);
				unvisited.push(getExits()[a]);
			} else {
				unvisited.pop();
			}
		}
	}
	
	/**
	 * Dijkstra's Algorithm & Breadth first search Computes the shortest path
	 * and distance to all nodes
	 * 
	 * @param n, the ship
	 */
	public static void computePath(Node n) {
		// set the first node's NodeData's mindistanc to 0
		((NodeData) n.getUserData()).minDistance = 0.0;
		// add it to the queue
		ArrayList<Node> q = new ArrayList<Node>();
		q.add(n);
		int x = 0;

		while (!q.isEmpty()) {
			x = x + 1;
			// get the first element of the queue
			Node u = q.get(0);
			// remove the first element
			q.remove(0);
			NodeData ud = (NodeData) u.getUserData();
			for (Node b : ud.adjacencies) {
				// add one to the distance of the path that goes through u
				double distanceThroughU = ud.minDistance + 1;
				NodeData bd = (NodeData) b.getUserData();
				// if there's no available mindistance originally
				if (bd.minDistance == Double.POSITIVE_INFINITY) {
					bd.minDistance = distanceThroughU;
					// set its the node before it to u
					bd.previous = u;
					q.add(b);
				} else {
					// determine which is smaller and set it to the mindistance
					bd.minDistance = Math.max(bd.minDistance, distanceThroughU);
				}
			}
		}
	}

	/**
	 * Collect animals available on Node n. If n has more than
	 * MAX_ANIMAL_CAPACITY animals collect MAX_ANIMAL_CAPACITY animals and go
	 * back to ship and go back to the node again until it's empty.
	 * 
	 * @param n
	 */
	private void collectAnimals(Node n) {
		while (!listAnimalsPresent().isEmpty()) {
			Collection<String> animals = listAnimalsPresent();
			// collect all animals on the node
			if (listAnimalsPresent().size() <= MAX_ANIMAL_CAPACITY) {
				for (String s : animals) {
					collect(s);
				}
				moveToShip(n);
			}
			// if the node has more than MAX_ANIMAL_CAPACITY
			// repeat going to the node until it's empty
			else {
				for (String s : animals) {
					if (getInventory().size() < MAX_ANIMAL_CAPACITY) {
						collect(s);
					}
				}
				moveToShip(n);
				move(n);
			}
		}
	}

	/**
	 * Move the naturalist to Node n
	 * 
	 * @param n
	 */
	private void move(Node n) {
		List<Node> path = getPath(n);
		// move step by step
		for (int b = 0; b < path.size(); b++) {
			moveTo(path.get(b));
		}
	}

	/**
	 * Move back to ship from Node n and drop animals on ship
	 * 
	 * @param n
	 */
	private void moveToShip(Node n) {
		List<Node> path = getPath(n);
		// move back node by node
		for (int d = path.size() - 1; d >= 0; d--) {
			moveTo(path.get(d));
		}
		// if the naturalist reaches the ship, drop animals
		if (getLocation().isShip() == true) {
			dropAll();
		}
	}

	/**
	 * Adds any nodes with animals on it to nwa field.
	 * 
	 * @param n, where the naturalist currently is located
	 */
	private void record(Node n) {
		// if n has animals on it
		if (!listAnimalsPresent().isEmpty()) {
			// if nwa doesn't already have n
			if (!nwa.contains(n)) {
				nwa.add(n);
			}
		}
	}

	/**
	 * Helper method for DFS. Determines whether node n has an unvisited 
	 * neighbor
	 * 
	 * @param n, where the naturalist currently is
	 * @return the index of the unvisited neighbor if there is any; -1 when
	 *         there is none
	 */
	private int helper(Node n) {
		for (int b = 0; b < getExits().length; b++) {
			if (visited.contains(getExits()[b]) == false) {
				return b;
			}
		}
		// return -1 if there is no unvisited neighbor
		return -1;
	}

	/**
	 * Get the path to n from the ship
	 * 
	 * @param n, the target node
	 * @return a list of nodes that need to be passed
	 */
	private static List<Node> getPath(Node n) {
		List<Node> path = new ArrayList<Node>();
		// get the previous node of every node in the path
		for (Node a = n; a != null; a = ((NodeData) a.getUserData()).previous)
			path.add(a);
		Collections.reverse(path);
		return path;
	}

	/**
	 * Nested class that stores information for later use while the naturalist
	 * traverses the map
	 */
	class NodeData implements Comparable<NodeData> {
		
		// store the Node's neighbor
		private Node[] adjacencies;
		// the minimum distance from the ship to the node
		// initiated to positive infinity
		private double minDistance = Double.POSITIVE_INFINITY;
		// the previous node of this node
		private Node previous;

		/**
		 * Constructor, initializes field adjacencies
		 * 
		 * @param nei
		 */
		public NodeData(Node[] nei) {
			adjacencies = nei;
		}

		/**
		 * Overriding compareTo in Comparable interface according to the
		 * minDistance
		 */
		@Override
		public int compareTo(NodeData n) {
			return Double.compare(minDistance, n.minDistance);
		}

	}
}
