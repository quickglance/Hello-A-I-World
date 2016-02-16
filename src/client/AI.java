package client;

import client.model.Node;

import java.util.*;

/**
 * AI class.
 * You should fill body of the method {@link #doTurn}.
 * Do not change name or modifiers of the methods or fields
 * and do not add constructor for this class.
 * You can add as many methods or fields as you want!
 * Use world parameter to access and modify game's
 * world!
 * See World interface for more details.
 */
public class AI {
    private Map<Integer, Integer> currentMoveList = new HashMap<Integer, Integer>(); //<source,destination>

    private HashMap<Integer, Node> supporter = new HashMap<Integer, Node>();
    private HashMap<Integer, Node> underAttack = new HashMap<Integer, Node>();
    private List<Node> expandingCondidate = new ArrayList<Node>();
    private Map<Node, Boolean> changeCondidate = new HashMap<Node, Boolean>();
    private Boolean isNodeHasFreeNeighbour = false;
    private Boolean isNodeHasEnemyNeighbour = false;
    private Node[] myNode;
    private List<Integer> enemyCoreNode = new ArrayList<Integer>();

    private int countNeighboursO1;
    private int countNeighboursO2;

    private int myID;
    private int enemyID;

    public void doTurn(World world) {

        initial(world);

        expand(world);

        categorizeMyNodes(world);

        if(underAttack.size() > 0) {
            // attackers get help from neighbours
            HashMap<Node, Node> moveList = new HashMap<Node, Node>();
            getHelp(underAttack, moveList);

            move(world, moveList);

            attack(world);
        }
        else {
            HashMap<Node, Node> moveList = new HashMap<Node, Node>();

            HashMap<Integer, Node> expanding = new HashMap<Integer, Node>();
            for(Node node : expandingCondidate) {
                expanding.put(node.getIndex(), node);
            }
            getHelp(expanding, moveList);

            move(world, moveList);
        }

        System.out.println(world.getTurnTimePassed());

    }

    private void initial(World world) {
//        initialize values
        currentMoveList.clear();
        supporter.clear();
//        changeCondidate.clear();
        myNode = world.getMyNodes();

        if (world.getTurnNumber() == 0) {
            Node[] nodes = world.getMyNodes();
            for (Node node : nodes) {
                expandingCondidate.add(node);
            }
            Node[] nodes1 = world.getOpponentNodes();
            for (Node node1 : nodes) {
                enemyCoreNode.add(node1.getIndex());
            }

//          set enemy ID
            myID = world.getMyID();

            enemyID = (myID == 1) ? 0 : 1;
        }
    }

    private void expand(World world) {
        for (Node node : changeCondidate.keySet()) {
            if (changeCondidate.get(node)) {
                System.out.println("##");
                if (node.getOwner() == world.getMyID())
                    expandingCondidate.add(node);
            } else {
                expandingCondidate.remove(node);
            }
        }
        changeCondidate.clear();
        for (Node source : expandingCondidate) {
            Node[] neighbours = source.getNeighbours();
            isNodeHasFreeNeighbour = false;
            isNodeHasEnemyNeighbour = false;
            for (Node node : neighbours) {
                if (node.getOwner() == -1)
                    isNodeHasFreeNeighbour = true;
                else if (node.getOwner() != world.getMyID())
                    isNodeHasEnemyNeighbour = true;
            }
            if (!isNodeHasFreeNeighbour) {
                changeCondidate.put(source, false);
                continue;
            }

            Arrays.sort(neighbours, (o1, o2) -> {
                if (o1.getOwner() == -1 && o2.getOwner() == -1) {

//                    barrasi inke kase dige nakhad biad rooye in node

                    for (int node : currentMoveList.keySet()) {
                        if (o1.getIndex() == currentMoveList.get(node)) {
                            return 1;
                        }
                    }
//                       paida kardan nodi ke kamtarin hamsaye khodi ra darad
                    countNeighboursO1 = 0;
                    for (Node node : o1.getNeighbours()) {
                        if (node.getOwner() == world.getMyID() || node.getOwner() == world.getMyID())
                            countNeighboursO1++;
                    }
                    countNeighboursO2 = 0;
                    for (Node node : o2.getNeighbours()) {
                        if (node.getOwner() == world.getMyID() || node.getOwner() == world.getMyID())
                            countNeighboursO2++;
                    }
                    if (countNeighboursO1 < countNeighboursO2) {
                        return -1;
                    }

                    return 1;
                }
                if (o1.getOwner() == -1) {
                    return -1;
                }
                return 1;
            });

            Node destination = neighbours[0];
            if (neighbours.length > 0) {
                world.moveArmy(source, destination, source.getArmyCount());
                currentMoveList.put(source.getIndex(), destination.getIndex());
                changeCondidate.put(destination, true);
            }
        }
    }

    private void categorizeMyNodes(World world) {
        Node[] myNodes = world.getMyNodes();

        underAttack.clear();

        for (Node myNode : myNodes) {

            // get neighbours
            Node[] neighbours = myNode.getNeighbours();

            int length = neighbours.length;
            if (length > 0) {

                boolean isUnderAttack = false, isSupporter = true;
                for (int i = 0; i < length && !isUnderAttack; i++) {

                    Node neighbour = neighbours[i];

                    int ownerID = neighbour.getOwner();
                    if (ownerID == enemyID) { //attacker

                        isUnderAttack = true;
                        underAttack.put(myNode.getIndex(), myNode);

                    } else if (ownerID == -1) {
                        isSupporter = false;
                    }

                }
                if (isSupporter) {
                    supporter.put(myNode.getIndex(), myNode);
                }
            }
        }
    }

    private void getHelp(HashMap<Integer, Node> nodes, HashMap<Node, Node> moveList) {
        moveList.clear();

        HashMap<Integer, Node> supporterList = new HashMap<Integer, Node>();
        Set<Map.Entry<Integer, Node>> entries = supporter.entrySet();
        Iterator<Map.Entry<Integer, Node>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Node> next = iterator.next();
            supporterList.put(next.getKey(), next.getValue());
        }

        getHelpRecursive(supporterList, nodes, moveList);
    }

    private void getHelpRecursive(HashMap<Integer, Node> supporter, HashMap<Integer, Node> nodes, HashMap<Node, Node> moveList) {
        // final condition
        if (supporter.isEmpty() || nodes.isEmpty()) {
            return;
        }

        // recursive step
        HashMap<Integer, Node> supporterNodes = new HashMap<Integer, Node>();

        Set<Integer> keySet = nodes.keySet();
        for (Integer key : keySet) {
            Node node = nodes.get(key);

            Node[] neighbours = node.getNeighbours();

            for (Node neighbour : neighbours) {

                if (neighbour.getOwner() == myID) {
                    if (supporter.containsKey(neighbour.getIndex())) {
                        if (moveList.containsKey(node) && moveList.get(node).getIndex() == neighbour.getIndex()) {
                            System.out.println(neighbour.getIndex() + " -> " + node.getIndex());
                            System.out.println("*****Move List*****");
                            Set<Map.Entry<Node, Node>> entries = moveList.entrySet();
                            Iterator<Map.Entry<Node, Node>> iterator = entries.iterator();
                            while (iterator.hasNext()) {
                                Map.Entry<Node, Node> next = iterator.next();
                                System.out.println(next.getKey().getIndex() + " -> " + next.getValue().getIndex());
                            }
                            System.out.println("*****Move List*****");
                        } else {
                            supporter.remove(neighbour.getIndex());

                            supporterNodes.put(neighbour.getIndex(), neighbour);

                            moveList.put(neighbour, node);

                        }
                    }
                }

            }
        }

        getHelpRecursive(supporter, supporterNodes, moveList);
    }

    private void move(World world, HashMap<Node, Node> moveList) {
        Set<Map.Entry<Node, Node>> entries = moveList.entrySet();
        Iterator<Map.Entry<Node, Node>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Node, Node> next = iterator.next();
            world.moveArmy(next.getKey(), next.getValue(), next.getKey().getArmyCount());
        }
    }

    private void attack(World world) {
        Set<Map.Entry<Integer, Node>> underAttacks = underAttack.entrySet();
        Iterator<Map.Entry<Integer, Node>> iterator1 = underAttacks.iterator();
        while (iterator1.hasNext()) {
            Map.Entry<Integer, Node> next = iterator1.next();
            Node node = next.getValue();

            Node[] neighbours = node.getNeighbours();

            for (Node neighbour : neighbours) {
                if (neighbour.getOwner() == enemyID) {
                    world.moveArmy(node, neighbour, node.getArmyCount());
                    break;
                }
            }
        }
    }

}
