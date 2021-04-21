package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.Building;


public class Calculator {
	private static final double ACTIVITY_UNITS_PER_MOVEMENT = 5;
	private static final double ACTIVITY_UNITS_PER_LOOKUP = 0.1;

	private static double markupPercentage = 0;
	private static double activityUnitPrice = 0;
	private static double chargeThreshold = 0;

	/**
	 * Predicts the charge for the item and compares it to the charge threshold.
	 * Incurs one lookup cost, which cannot be charged to any customers.
	 * @param deliveryItem
	 * @return whether the item's predicted charge exceeds the charge threshold
	 * @throws Exception from lookupServiceFee()
	 */
	public static boolean isHighPriority(MailItem deliveryItem) throws Exception {
		double chargeEstimate = calculateCharge(deliveryItem);

		return chargeEstimate > chargeThreshold;
	}
	/**
	 * when it comes to charging the customer, always calculate the bill
	 * as if they are the only customer.
	 * @param deliveryItem
	 * @return
	 */
	public static double calculateCharge(MailItem deliveryItem) throws Exception {
		double serviceFee = Accountant.lookupServiceFee(deliveryItem);

		double activityBill = calculateActivityBill(deliveryItem);

		double cost = activityBill + serviceFee;

		return cost * (1 + markupPercentage);
	}
	public static double calculateActivityBill(MailItem deliveryItem) {
		return activityUnitPrice * calculateBillableActivity(deliveryItem);
	}
	public static double calculateBillableActivity(MailItem deliveryItem) {
		// go from mailroom to destination and then come back
		double billableMovementCount = 2 * Math.abs(
				Building.MAILROOM_LOCATION - deliveryItem.getDestFloor());
		// spec says charge for 1 lookup
		double billableLookupCount = 1;
		
		return billableMovementCount * ACTIVITY_UNITS_PER_MOVEMENT +
				billableLookupCount * ACTIVITY_UNITS_PER_LOOKUP;
	}
	
	public static double calculateMovementCost() {
		return ACTIVITY_UNITS_PER_MOVEMENT * activityUnitPrice;
	}
	public static double calculateLookupCost() {
		return ACTIVITY_UNITS_PER_LOOKUP * activityUnitPrice;
	}

	public static double calculateActivityCost(MailItem deliveryItem) {
		return activityUnitPrice * calculateActivity(deliveryItem);
	}
	public static double calculateActivity(MailItem deliveryItem) {
		return deliveryItem.getMovementCount() * ACTIVITY_UNITS_PER_MOVEMENT +
				deliveryItem.getLookupCount() * ACTIVITY_UNITS_PER_LOOKUP;
	}

	public static void setChargeThreshold(double chargeThreshold) {
		Calculator.chargeThreshold = chargeThreshold;
	}
	public static void setActivityUnitPrice(double activityUnitPrice) {
		Calculator.activityUnitPrice = activityUnitPrice;
	}
	public static void setMarkupPercentage(double markupPercentage) {
		Calculator.markupPercentage = markupPercentage;
	}
}
