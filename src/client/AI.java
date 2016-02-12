package client;

import client.model.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

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

    public void doTurn(World world) {
        // fill this method, we've presented a stupid AI for example!

        int myID = world.getMyID();
        HashMap<Integer, Integer> center = new HashMap<Integer, Integer>(), border = new HashMap<Integer, Integer>(), inAttack = new HashMap<Integer, Integer>();

        Node[] myNodes = world.getMyNodes();
        for (Node myNode : myNodes) {
            // get neighbours
            Node[] neighbours = myNode.getNeighbours();
            int length = neighbours.length;
            if (length > 0) {


                int minArmy = 0;
                int minIndex = 0;
                boolean isInAttack = false;
                for (int i = 0; i < length; i++) {

                    Node neighbour = neighbours[i];

                    int ownerID = neighbour.getOwner();
                    if (!isInAttack && ownerID != -1 && ownerID != myID) { //attack algorithm

                        isInAttack = true;
                        inAttack.put(myNode.getIndex(), neighbour.getIndex());

//                        //get help from adj nodes
//                        Node[] attackerNeighbours = myNode.getNeighbours();
//                        for (int j = 0; j < attackerNeighbours.length; j++) {
//                            Node attackerNeighbour = attackerNeighbours[j];
//                            if (attackerNeighbour.getOwner() == myID) {
//                                world.moveArmy(attackerNeighbour, myNode, attackerNeighbour.getArmyCount());
//                            }
//                        }
//                        world.moveArmy(myNode, neighbour, myNode.getArmyCount());

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
                    }
                }
                if (!isInAttack) {
                    Node destination = neighbours[minIndex];

                    if (destination.getOwner() == -1) { // border
                        border.put(myNode.getIndex(), destination.getIndex());


//                        Node[] borderNeighbours = myNode.getNeighbours();
//                        for (int j = 0; j < borderNeighbours.length; j++) {
//                            Node borderNeighbour = borderNeighbours[j];
//                            if (borderNeighbour.getOwner() == myID) {
//                                world.moveArmy(borderNeighbour, myNode, borderNeighbour.getArmyCount() / 2);
//                            }
//                        }
//                        world.moveArmy(myNode, destination, myNode.getArmyCount());

                    } else { // center
                        center.put(myNode.getIndex(), destination.getIndex());


//                        if (destination.getArmyCount() <= myNode.getArmyCount()) {
//                            // move half of the node's army to the neighbor node
//                            world.moveArmy(myNode, destination, (myNode.getArmyCount() - destination.getArmyCount()));
//                        } else {
//                            // move half of the node's army to the neighbor node
//                            world.moveArmy(destination, myNode, (destination.getArmyCount() - myNode.getArmyCount()));
//                        }

                    }
                }
            }
        }

        // attackers get help from neighbours
        Set<Integer> inAttackKeys = inAttack.keySet();
        for (int inAttackKey : inAttackKeys) {
            //get help from adj nodes except another attackers
            Node inAttackNode = world.getMap().getNode(inAttackKey);
            Node[] attackerNeighbours = inAttackNode.getNeighbours();
            for (int j = 0; j < attackerNeighbours.length; j++) {
                Node attackerNeighbour = attackerNeighbours[j];
                if(!inAttack.containsKey(attackerNeighbour.getIndex())){
                    if(border.containsKey(attackerNeighbour.getIndex()) || center.containsKey(attackerNeighbour.getIndex()))
                    border.remove(attackerNeighbour.getIndex());
                    center.remove(attackerNeighbour.getIndex());
                    if (attackerNeighbour.getOwner() == myID) {
                        world.moveArmy(attackerNeighbour, inAttackNode, attackerNeighbour.getArmyCount());
                    }
                }
            }
            world.moveArmy(inAttackKey, inAttack.get(inAttackKey), inAttackNode.getArmyCount());
        }

        // borders get help from centers
        Set<Integer> borderKeys = border.keySet();
        for (int borderKey : borderKeys) {
            //get help from adj nodes
            Node borderNode = world.getMap().getNode(borderKey);
            Node[] borderNeighbours = borderNode.getNeighbours();
            for (int j = 0; j < borderNeighbours.length; j++) {
                Node borderNeighbour = borderNeighbours[j];
                if (center.containsKey(borderNeighbour.getIndex())) {
                    center.remove(borderNeighbour.getIndex());
                    world.moveArmy(borderNeighbour, borderNode, borderNeighbour.getArmyCount());
                }
            }
            world.moveArmy(borderKey, border.get(borderKey), borderNode.getArmyCount());
        }

        // centers share their strength
        Set<Integer> centerKeys = center.keySet();
        for (int centerKey : centerKeys) {
            //get help from adj nodes
            Node centerNode = world.getMap().getNode(centerKey);
            Integer dst = center.get(centerKey);
            Node dstNode = world.getMap().getNode(dst);
            world.moveArmy(centerKey, dst, centerNode.getArmyCount());
        }
    }

}
