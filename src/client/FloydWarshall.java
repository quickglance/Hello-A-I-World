package client;

import client.model.Node;

import java.util.*;

public class FloydWarshall {

    public static HashMap<Integer, HashMap<Integer, Integer>> getAdj(Node[] nodes) {
        int n = nodes.length;

        int owner = nodes[0].getOwner();

        HashMap<Integer, HashMap<Integer, Integer>> adj = new HashMap<Integer, HashMap<Integer, Integer>>(n);


        for (int i = 0; i < n; i++) {
            Node node = nodes[i];

            int index = node.getIndex();

            HashMap<Integer, Integer> indexAdj = adj.get(index);
            indexAdj = new HashMap<Integer, Integer>(n);

            Node[] neighbours = node.getNeighbours();

            for (int j = 0; j < neighbours.length; j++) {
                Node neighbour = neighbours[j];

                if (neighbour.getOwner() == owner) {
                    indexAdj.put(neighbour.getIndex(), 1);
                }
            }
        }

        return adj;
    }

    public static void shortestpath(HashMap<Integer, HashMap<Integer, Integer>> adj, HashMap<Integer, HashMap<Integer, Integer>> path) {

        int n = adj.size();

        // Compute successively better paths through vertex k.
        for (int k = 0; k < n; k++) {

            Set<Integer> keySet = adj.keySet();
            Iterator<Integer> iIterator = keySet.iterator();

            // Do so between each possible pair of points.
            while (iIterator.hasNext()) {
                int i = iIterator.next();
                Iterator<Integer> jIterator = keySet.iterator();

                while (jIterator.hasNext()) {
                    int j = jIterator.next();

                    int adj_ik = adj.get(i).getOrDefault(k, 0);
                    int adj_kj = adj.get(k).getOrDefault(j, 0);
                    int adj_ij = adj.get(i).getOrDefault(j, 0);
                    int path_kj = path.get(k).getOrDefault(j, k);

                    if (adj_ik + adj_kj < adj_ij) {
                        adj.get(i).put(j, adj_ik + adj_kj);
                        path.get(i).put(j, path_kj);
                    }
                }
            }
        }
    }
    
    public static List<Integer> getPath(HashMap<Integer, HashMap<Integer, Integer>> paths, int start, int end) {
        System.out.println("From where to where do you want to find the shortest path?(0 to 4)");

        LinkedList<Integer> path = new LinkedList<>();

        // The path will always end at end.
        path.push(end);
        String myPath = end + "";

        // Loop through each previous vertex until you get back to start.
        do {
            end = paths.get(start).get(end);

            path.push(end);

            myPath = paths.get(start).get(end) + " -> " + myPath;

        } while (end != start);

        // Just add start to our string and print.
        path.push(start);
        myPath = start + " -> " + myPath;
        System.out.println("Here's the path " + myPath);

        return path;
    }

    public static HashMap<Integer, HashMap<Integer, Integer>> con(int[][] a) {
        HashMap<Integer, HashMap<Integer, Integer>> b = new HashMap<Integer, HashMap<Integer, Integer>>();

        for (int i = 0; i < a.length; i++) {
            int[] aa = a[i];
            HashMap<Integer, Integer> bb = new HashMap<>();
            for (int j = 0; j < aa.length; j++) {
                bb.put(j, aa[j]);
            }
            b.put(i, bb);
        }

        return b;
    }

    public static void main(String[] args) {

        Scanner stdin = new Scanner(System.in);


        // Tests out algorithm with graph shown in class.
        int[][] m = new int[5][5];
        m[0][0] = 0;
        m[0][1] = 3;
        m[0][2] = 8;
        m[0][3] = 10000;
        m[0][4] = -4;
        m[1][0] = 10000;
        m[1][1] = 0;
        m[1][2] = 10000;
        m[1][3] = 1;
        m[1][4] = 7;
        m[2][0] = 10000;
        m[2][1] = 4;
        m[2][2] = 0;
        m[2][3] = 10000;
        m[2][4] = 10000;
        m[3][0] = 2;
        m[3][1] = 10000;
        m[3][2] = -5;
        m[3][3] = 0;
        m[3][4] = 10000;
        m[4][0] = 10000;
        m[4][1] = 10000;
        m[4][2] = 10000;
        m[4][3] = 6;
        m[4][4] = 0;


        int[][] path = new int[5][5];

        // Initialize with the previous vertex for each edge. -1 indicates
        // no such vertex.
        for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++)
                if (m[i][j] == 10000)
                    path[i][j] = -1;
                else
                    path[i][j] = i;

        // This means that we don't have to go anywhere to go from i to i.
        for (int i = 0; i < 5; i++)
            path[i][i] = i;


        HashMap<Integer, HashMap<Integer, Integer>> shortpath = con(m);
        HashMap<Integer, HashMap<Integer, Integer>> mpath = con(path);
        shortestpath(shortpath, mpath);

        // Prints out shortest distances.
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++)
                System.out.print(shortpath.get(i).get(j) + " ");
            System.out.println();
        }

        System.out.println("From where to where do you want to find the shortest path?(0 to 4)");
        int start = stdin.nextInt();
        int end = stdin.nextInt();

        // The path will always end at end.
        String myPath = end + "";

        // Loop through each previous vertex until you get back to start.
        int tmp = mpath.get(start).get(end);
        while (tmp != start) {
            myPath = mpath.get(start).get(end) + " -> " + myPath;
            end = mpath.get(start).get(end);
            tmp = mpath.get(start).get(end);
        }

        // Just add start to our string and print.
        myPath = start + " -> " + myPath;
        System.out.println("Here's the path " + myPath);

    }

}
