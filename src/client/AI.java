package client;

import client.model.Node;

import java.util.ArrayList;

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
        Node[] myNodes = world.getMyNodes();
        ArrayList<Integer> warNodes = new ArrayList<Integer>();
        ArrayList<Integer> peaceNodes = new ArrayList<Integer>();
        
        //getting free nodes 
        for (Node node : myNodes) {
			Node[] neigboures = node.getNeighbours();
			int neigboursCount = neigboures.length;
			boolean peachfullness = true;
			if(neigboursCount >0)
			{
				//help in war Nodes
				if(warNodes.size() >0)
				{
					//Under Development
					
//					for (int i = 0; i < warNodes.size(); i++) {
//						
//						if(warNodes.get(i).equals(node.getIndex()))
//						{
//							for (Node node2 : neigboures) {
//								
//								if(node2.getOwner()==myID)
//									world.moveArmy(node2, node, node2.getArmyCount()/neigboursCount);
//							}
//						}
//						
//					}
				}

				boolean ArmyMoved = false;
				for (Node node2 : neigboures) {
					
					if(node2.getOwner() == -1 && !ArmyMoved)
					{
						world.moveArmy(node, node2, node.getArmyCount()/neigboursCount);
						ArmyMoved = true;
					}
					if(node2.getOwner() != -1 && node2.getOwner() != myID)
					{
						warNodes.add(node.getIndex());
						peachfullness = false;
					}
				}
				
				if(peachfullness)
					peaceNodes.add(node.getIndex());
					
			}

		}
        
        
        
        
    }

}
