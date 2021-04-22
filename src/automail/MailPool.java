package automail;

import java.util.*;
import java.util.ListIterator;
import exceptions.*;

/**
 * addToPool is called when there are mail items newly arrived at the building to add to the MailPool or
 * if a robot returns with some undelivered items - these are added back to the MailPool.
 * The data structure and algorithms used in the MailPool is your choice.
 *
 */

public class MailPool {

	private class Item {
		int destination;
		MailItem mailItem;
		// Use stable sort to keep arrival time relative positions

		public Item(MailItem mailItem) {
			destination = mailItem.getDestFloor();
			this.mailItem = mailItem;
		}
	}

	public class ItemComparator implements Comparator<Item> {
		@Override
		public int compare(Item i1, Item i2) {
			int order = 0;
			if (i1.destination < i2.destination) {
				order = 1;
			} else if (i1.destination > i2.destination) {
				order = -1;
			}
			return order;
		}
	}

	private LinkedList<Item> regPool;
	private LinkedList<Item> priorityPool;
	private LinkedList<Robot> robots;

	public MailPool(int nrobots){
		// Start empty
		//if Charge is 0, only priority pool will be used
		
		priorityPool = new LinkedList<Item>();
		regPool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
	}

	// Adds item to the priority pool
	// If it's high priority, it just gets added to the HP queue - no distinction between classes
	// called by addItemToPools
	// if not using ChargeThreshold, all items are considered priority
	private void addToPriorityPool(MailItem mailItem) {
		
		Item item = new Item(mailItem);
		priorityPool.add(item);
		priorityPool.sort(new ItemComparator());
		//DEBUG
		//System.out.println("Priority pool");
	}

	// Adds item to the regular pool
	// called by addItemToPools
	private void addToRegPool(MailItem mailItem) {
		
		Item item = new Item(mailItem);
		regPool.add(item);
		regPool.sort(new ItemComparator());
		//DEBUG
		//System.out.println("Regular pool");
	}

	/**
	 * Adds an item to the mail pool
	 * @param mailItem the mail item being added.
	 */
	public void addItemToPools(MailItem mailItem) throws Exception {
		// Assuming that the robot travels from ground -> destination
		
		if (Calculator.isHighPriority(mailItem)) {
			addToPriorityPool(mailItem);
		}
		else {
			addToRegPool(mailItem);
		}
	}

	/**
	 * load up any waiting robots with mailItems, if any.
	 */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		// List available robots
		ListIterator<Robot> i = robots.listIterator();
		while (i.hasNext()) loadItem(i);
	}

	/**
	 * adds item to a robot originating from a given pool
	 * @param robot
	 */

	private void addItemsFromPool(Robot robot, boolean priority) throws ItemTooHeavyException {

		LinkedList<Item> pool = priority? priorityPool: regPool;

		/* if regPool isn't being used (as in chargeThreshold == 0),
		 * it will come up as empty here and the function will return
		 */
		if (!pool.isEmpty()) {
			
			if(robot.isEmpty()) {
				// hand first as we want higher priority delivered first
				robot.addToHand(pool.getFirst().mailItem);
				pool.removeFirst();
			}
			if(!pool.isEmpty()) {
				robot.addToTube(pool.getFirst().mailItem);
				pool.removeFirst();
			}
		}
	}


	//Load items onto the robot, from the priorityPool and the regPool
	private void loadItem(ListIterator<Robot> i) throws ItemTooHeavyException{
		
		// gets next available robot
		Robot robot = i.next();
		assert(robot.isEmpty());
		
		boolean priority = true; 

		// Load the high priority items first if any are there
		if (priorityPool.size() > 0) {

			addItemsFromPool(robot, priority);
		}

		priority = false;

		// Check if the robot still has room for item(s) 
		if (robot.isEmpty() || (robot.getTube() == null)) {
			//if the hand or tube is empty, we also check the other pool

			addItemsFromPool(robot, priority);
		}

		// send the robot off if it has any items to deliver
		if (!robot.isEmpty()) {

			robot.dispatch();
			i.remove();
		}
	}

	/**
	 * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
	 */
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
