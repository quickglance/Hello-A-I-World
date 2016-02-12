package client;

import client.model.Node;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.lang.reflect.Array;
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
    private Map<Node, Node> currentMoveList = new HashMap<Node, Node>(); //<source,destination>
    private List<Node> expandingCondidate = new ArrayList<Node>();
    private Boolean isNodeHasFreeNeighbour = false;

    private int countNeighboursO1;
    private int countNeighboursO2;

    public void doTurn(World world) {
//        initialize values
        currentMoveList.clear();
        if (world.getTurnNumber() == 0) {
            Node[] nodes = world.getMyNodes();
            for (Node node : nodes) {
                expandingCondidate.add(node);
            }
        }

//        iteration
        for (Node source : expandingCondidate) {
            Node[] neighbours = source.getNeighbours();

            isNodeHasFreeNeighbour = false;
            for (Node node : neighbours) {
                if (node.getOwner() == -1)
                    isNodeHasFreeNeighbour = true;
            }
            if (!isNodeHasFreeNeighbour) {
                expandingCondidate.remove(source);
                continue;
            }
//
////            sorting
            Arrays.sort(neighbours, (o1, o2) -> {
                if (o1.getOwner() == -1 && o2.getOwner() == -1) {

//                    barrasi inke kase dige nakhad biad rooye in node

                    for (Node node : currentMoveList.keySet()) {
                        if (o1.getIndex() == currentMoveList.get(node).getIndex()) {
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
                if (o1.getOwner() == -1 && o2.getOwner() != -1) {
                    return -1;
                }
                return 1;
            });

            Node destination = neighbours[0];
            if (neighbours.length > 0) {
                world.moveArmy(source, destination, source.getArmyCount() / 2);
                currentMoveList.put(source, destination);
                expandingCondidate.add(destination);
            }
        }
    }


}
