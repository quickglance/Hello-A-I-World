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

    private HashMap<Integer, Integer> wave = new HashMap<Integer, Integer>();
    private int waveLevel = 0;

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

<<<<<<< HEAD
//        ArrayList<HashMap<Integer, HashMap<Integer, Integer>>> hashMaps = FloydWarshall.getAdj(myNodes);
//        HashMap<Integer, HashMap<Integer, Integer>> adj = hashMaps.get(0);
//        HashMap<Integer, HashMap<Integer, Integer>> path = hashMaps.get(1);
//        FloydWarshall.shortestpath(adj, path);

        System.out.println("time passed after routing: " + world.getTurnTimePassed());

        for (Node myNode : myNodes) {
            if (wave.getOrDefault(myNode.getIndex(), -1) == -1)
                wave.put(myNode.getIndex(), waveLevel);

            // calculate shortest paths

=======
        underAttack.clear();

        for (Node myNode : myNodes) {
>>>>>>> refs/remotes/origin/Mohammad-Amin

            // get neighbours
            Node[] neighbours = myNode.getNeighbours();

            int length = neighbours.length;
            if (length > 0) {

                boolean isUnderAttack = false, isSupporter = true;
                for (int i = 0; i < length && !isUnderAttack; i++) {

                    Node neighbour = neighbours[i];

                    int ownerID = neighbour.getOwner();
<<<<<<< HEAD
                    if (!isInAttack && ownerID != -1 && ownerID != myID) { //attacker

                        isInAttack = true;
                        inAttack.put(myNode.getIndex(), neighbour.getIndex());

                    } else if (!isInAttack) {
                        if (i == 0) {
                            minArmy = neighbour.getArmyCount();
                        }
                        if (neighbour.getArmyCount() < minArmy) {
                            minArmy = neighbour.getArmyCount();
                            minIndex = i;
                        }
                        if (minArmy == 0)
                            break;
=======
                    if (ownerID == enemyID) { //attacker

                        isUnderAttack = true;
                        underAttack.put(myNode.getIndex(), myNode);

                    } else if (ownerID == -1) {
                        isSupporter = false;
>>>>>>> refs/remotes/origin/Mohammad-Amin
                    }

                }
                if (isSupporter) {
                    supporter.put(myNode.getIndex(), myNode);
                }
            }
        }
    }

<<<<<<< HEAD
                    if (destination.getOwner() == -1) { // border
                        border.put(myNode.getIndex(), destination.getIndex());
                    } else { // center
                        center.put(myNode.getIndex(), destination.getIndex());
=======
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
>>>>>>> refs/remotes/origin/Mohammad-Amin
                    }
                }

            }
        }

<<<<<<< HEAD
        // attackers get help from neighbours
        Set<Integer> inAttackKeys = inAttack.keySet();
        for (int inAttackKey : inAttackKeys) {
            //get help from adj nodes except another attackers
            Node inAttackNode = world.getMap().getNode(inAttackKey);
            Node[] attackerNeighbours = inAttackNode.getNeighbours();
            for (int j = 0; j < attackerNeighbours.length; j++) {
                Node attackerNeighbour = attackerNeighbours[j];
                if (!inAttack.containsKey(attackerNeighbour.getIndex())) {
                    if (border.containsKey(attackerNeighbour.getIndex()) || center.containsKey(attackerNeighbour.getIndex()))
                        border.remove(attackerNeighbour.getIndex());
                    center.remove(attackerNeighbour.getIndex());
                    if (attackerNeighbour.getOwner() == myID) {
                        world.moveArmy(attackerNeighbour, inAttackNode, attackerNeighbour.getArmyCount());
                    }
                }
            }
            world.moveArmy(inAttackKey, inAttack.get(inAttackKey), inAttackNode.getArmyCount());
=======
        getHelpRecursive(supporter, supporterNodes, moveList);
    }

    private void move(World world, HashMap<Node, Node> moveList) {
        Set<Map.Entry<Node, Node>> entries = moveList.entrySet();
        Iterator<Map.Entry<Node, Node>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<Node, Node> next = iterator.next();
            world.moveArmy(next.getKey(), next.getValue(), next.getKey().getArmyCount());
>>>>>>> refs/remotes/origin/Mohammad-Amin
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
<<<<<<< HEAD
            world.moveArmy(borderKey, border.get(borderKey), borderNode.getArmyCount());
        }

        // centers share their strength
        Set<Integer> centerKeys = center.keySet();
        for (int centerKey : centerKeys) {
            // send help to attacker nodes
            Node centerNode = world.getMap().getNode(centerKey);

//            int nextNode = FloydWarshall.getNextNode(path, centerKey, inAttackKeys.iterator().next());

//            if (nextNode != -1) {
//                world.moveArmy(centerKey, nextNode, centerNode.getArmyCount());
//            } else {

//                Integer centerLevel = wave.get(centerKey);
//
//                int dstLevel = -1;
//                Node[] neighbours = centerNode.getNeighbours();
////            Node dstNode = world.getMap().getNode(center.get(centerKey));
//                Node dstNode = null;
//                for (Node neighbour : neighbours) {
//                    Integer neighbourLevel = wave.getOrDefault(neighbour.getIndex(), -1);
//                    if (neighbourLevel != -1 && neighbourLevel > centerLevel) {
//                        if (dstLevel == -1) {
//                            dstLevel = neighbourLevel;
//                            dstNode = neighbour;
//                        }
//                        if (dstLevel < neighbourLevel) {
//                            dstLevel = neighbourLevel;
//                            dstNode = neighbour;
//                        }
//                    }
//                }
//
//                if (dstNode != null)
//                    world.moveArmy(centerNode, dstNode, centerNode.getArmyCount());

//            }
=======
>>>>>>> refs/remotes/origin/Mohammad-Amin
        }

        waveLevel++;

        System.out.println("time passed after all: " + world.getTurnTimePassed());
    }

}
