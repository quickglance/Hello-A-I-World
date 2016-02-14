package client;

import client.model.Node;
import com.sun.org.apache.xpath.internal.operations.Bool;

import javax.swing.text.StyledEditorKit;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.BooleanSupplier;

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
    private Map<Integer, Integer> previousMoveList = new HashMap<Integer, Integer>();
    private Map<Integer, Integer> currentMoveList = new HashMap<Integer, Integer>(); //<source,destination>

    private List<Node> supporter = new ArrayList<Node>();
    private List<Node> underAttack = new ArrayList<Node>();
    private List<Node> expandingCondidate = new ArrayList<Node>();
    private Map<Node, Boolean> changeCondidate = new HashMap<Node, Boolean>();
    private Boolean isNodeHasFreeNeighbour = false;
    private Boolean isNodeHasEnemyNeighbour = false;
    private Node[] myNode;
    private List<Integer> enemyCoreNode = new ArrayList<Integer>();

    private int countNeighboursO1;
    private int countNeighboursO2;

    private int enemyID;

    public void doTurn(World world) {
//        initialize values
        currentMoveList.clear();
        changeCondidate.clear();
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
//            set enemy ID
            if (world.getMyID() == 1) {
                enemyID = 0;
            } else {
                enemyID = 1;
            }
        }

/*
#################################
---------Expanding Part----------
---------------------------------
#################################
 */
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
                if (isNodeHasEnemyNeighbour) {
                    underAttack.add(source);
                } else {
                    supporter.add(source);
                }
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
//routing to enemy core
                if (o1.getOwner() == world.getMyID() && o2.getOwner() == world.getMyID()) {
                    for (int node : previousMoveList.keySet()) {
                        if (node == o1.getIndex() && previousMoveList.get(node) == source.getIndex()) {
                            return 1;
                        }
                    }
                    if (Math.abs(o1.getIndex() - enemyCoreNode.get(0)) < Math.abs(o2.getIndex() - enemyCoreNode.get(0))) {
                        return -1;
                    } else {
                        return 1;
                    }
                }

                if (o1.getOwner() == enemyID) {
                    return -1;
                }
                return 1;
            });

            Node destination = neighbours[0];
            if (neighbours.length > 0) {
                world.moveArmy(source, destination, source.getArmyCount() / 2);
                currentMoveList.put(source.getIndex(), destination.getIndex());
                changeCondidate.put(destination, true);
            }
        }
//        update change candidate
        for (Node node : changeCondidate.keySet()) {
            if (changeCondidate.get(node)) {
                expandingCondidate.add(node);
            } else {
                expandingCondidate.remove(node);
            }
        }
        changeCondidate.clear();
/*
#################################
---------Supporting Part----------
---------------------------------
#################################
*/
//        check under attack nodes
//      brarye har nodi ke taht hamle ast , man khode node ra barraye inke tahte hamle hast ye na barrasi mikonam
//      tamam node haye hamsaye'e ke tahte hamle hastand niz check khahad shod


//        System.out.println("###################################");
//        for (Node node : underAttack) {
//            System.out.println(node.getIndex());
//
//            if (node.getOwner() == enemyID) {
//                changeCondidate.put(node, false);
//                continue;
//            }
//            isNodeHasEnemyNeighbour = false;
//            for (Node node1 : node.getNeighbours()) {
//                if (node1.getOwner() == enemyID) {
//                    continue;
//                }
//                if (node1.getOwner() == world.getMyID()) {
//                    for (Node node2 : node1.getNeighbours()) {
//                        if (node2.getOwner() == enemyID) {
//                            if (!underAttack.contains(node1)) {
//                                changeCondidate.put(node1, true);
//                            }
//                            break;
//                        }
//                    }
//                } else if (node1.getOwner() == enemyID) {
//                    isNodeHasEnemyNeighbour = true;
//                }
//            }
//            if (!isNodeHasEnemyNeighbour) {
//                changeCondidate.put(node, false);
//            }
//        }
//
////        update list
//        for (Node node : changeCondidate.keySet()) {
//            if (changeCondidate.get(node)) {
//                underAttack.add(node);
//            } else {
//                underAttack.remove(node);
//            }
//        }
//        changeCondidate.clear();

        underAttack.clear();
        supporter.clear();
        Boolean isUnderattack = false;
        Boolean hasFreeNeighbours = false;
        for (Node node : myNode) {
            Node[] neighbour = node.getNeighbours();
            isUnderattack = false;
            hasFreeNeighbours = false;
            for (Node node1 : neighbour) {
                if (node1.getOwner() == -1) {
                    hasFreeNeighbours = true;
                    break;
                } else if (node1.getOwner() == enemyID) {
                    isUnderattack = true;
                    break;
                }
            }
            if (isUnderattack) {
                underAttack.add(node);
            } else if (!hasFreeNeighbours) {
                supporter.add(node);
            }
        }


        Map<Node, Node> moving = new HashMap<Node, Node>();
        Map<Node, Integer> helping = new HashMap<Node, Integer>();//node that help send to it & power sended
        System.out.println("#######################");
        System.out.println(supporter.size());
        for (int i = 0; i < 2; i++) {
            for (Node node : supporter) {

                Node[] neighbours = node.getNeighbours();
                for (Node node1 : neighbours) {
                    int force = 0;
                    if (moving.containsValue(node))
                        continue;
                    if (underAttack.contains(node1)) {
                        if (helping.containsKey(node)) {
                            force = node.getArmyCount() + helping.get(node);
                        } else {
                            force = node.getArmyCount();
                        }
                        world.moveArmy(node, node1, force - 4);
                        moving.put(node, node1);
                        if (helping.containsKey(node1)) {
                            helping.put(node1, helping.get(node1) + force);
                        } else {
                            helping.put(node1, force);
                        }
                        break;
                    }
                    if (moving.containsKey(node1) && moving.get(node1).getIndex() != node.getIndex()) {
                        if (helping.containsKey(node)) {
                            force = node.getArmyCount() + helping.get(node);
                        } else {
                            force = node.getArmyCount();
                        }
                        world.moveArmy(node, node1, force - 4);
                        moving.put(node, node1);
                        if (helping.containsKey(node1)) {
                            helping.put(node1, helping.get(node1) + force);
                        } else {
                            helping.put(node1, force);
                        }
                    } else if (moving.containsKey(node1) && moving.get(node1).getIndex() == node.getIndex()) {
                        continue;
                    }
                }
            }
        }

/*
#################################
---------Attack Part----------
---------------------------------
#################################
*/


    }


}
