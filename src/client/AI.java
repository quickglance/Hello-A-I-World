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

        initial(world);
        expand(world);

    }

    private void initial(World world) {
//        initialize values
        currentMoveList.clear();
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
//            set enemy ID
            if (world.getMyID() == 1) {
                enemyID = 0;
            } else {
                enemyID = 1;
            }
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
                return 1;
            });

            Node destination = neighbours[0];
            if (neighbours.length > 0) {
                world.moveArmy(source, destination, source.getArmyCount() / 2);
                currentMoveList.put(source.getIndex(), destination.getIndex());
                changeCondidate.put(destination, true);
            }
        }
    }


}
