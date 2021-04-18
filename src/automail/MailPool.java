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
	private Queue<Item> priorityPool;
	private LinkedList<Robot> robots;
	private boolean sortByPriority;

	public MailPool(int nrobots){
		// Start empty
		regPool = new LinkedList<Item>();
		robots = new LinkedList<Robot>();
		if (Calculator.getChargeThreshold() > 0) {
			sortByPriority = true;
			priorityPool = new LinkedList<Item>();
		}
	}

	// Adds item to the regular pool
	//called by addItemToPools
	public void addToRegPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		regPool.add(item);
		regPool.sort(new ItemComparator());
	}

	// Adds item to the priority pool
	// If it's high priority, it just gets added to the HP queue - no distinction between classes
	//called by addItemToPools
	public void addToPriorityPool(MailItem mailItem) {
		Item item = new Item(mailItem);
		priorityPool.add(item);
	}


	/**
	 * Adds an item to the mail pool
	 * @param mailItem the mail item being added.
	 */
	public void addItemToPools(MailItem mailItem) throws Exception {
		// Accounts for charge threshold
		if (sortByPriority) {
			// Estimate the charge for the new item
			// Assuming that the robot travels from ground -> destination
			double estimatedCost = Calculator.calculateCharge(mailItem);
			if (estimatedCost > Calculator.getChargeThreshold()) {
				addToPriorityPool(mailItem);
			}
			else {
				addToRegPool(mailItem);
			}
		}
		else {
			addToRegPool(mailItem);
		}
	}

	/**
	 * load up any waiting robots with mailItems, if any.
	 */
	public void loadItemsToRobot() throws ItemTooHeavyException {
		//List available robots
		ListIterator<Robot> i = robots.listIterator();
		while (i.hasNext()) loadItem(i);
	}

	/**
	 * adds item to a robot originating from a given pool
	 * @param robot
	 */

	private void addItemsFromPool(Robot robot, Iterator<Item> j) throws ItemTooHeavyException {
		if (j.hasNext()) {
			try {
				robot.addToHand(j.next().mailItem); // hand first as we want higher priority delivered first
				j.remove();
				// if there are still items deliverable from the given pool
				if (j.hasNext()) {
					robot.addToTube(j.next().mailItem);
					j.remove();
				}
			}
			catch (Exception e) {
				throw e;
			}
		}
	}


	//Load items onto the robot, from the priorityPool and the
	private void loadItem(ListIterator<Robot> i) throws ItemTooHeavyException{
		// gets next available robot
		Robot robot = i.next();
		assert(robot.isEmpty());
		// System.out.printf("P: %3d%n", pool.size());
		// loads the current items
		ListIterator<Item> j = regPool.listIterator();

		// Load the high priority items first
		if ((priorityPool != null) && (priorityPool.size() > 0)) {
			Iterator<Item> k = priorityPool.iterator();
			addItemsFromPool(robot, k);
		}

		// Check the other pool
		if (robot.isEmpty() || robot.getTube() == null) {
			// then we can start adding from the other pool.
			addItemsFromPool(robot, j);
		}

		// send the robot off if it has any items to deliver
		if (!robot.isEmpty()) {
			robot.dispatch();

		}

		i.remove();
	}



	/**
	 * @param robot refers to a robot which has arrived back ready for more mailItems to deliver
	 */
	public void registerWaiting(Robot robot) { // assumes won't be there already
		robots.add(robot);
	}

}
