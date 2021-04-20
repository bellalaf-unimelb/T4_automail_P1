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
		int movementCountEstimate = Math.abs(Building.MAILROOM_LOCATION - deliveryItem.getDestFloor());
		int billableLookupCount = 1;

		double activityEstimate =
				(ACTIVITY_UNITS_PER_MOVEMENT * movementCountEstimate) +
				(ACTIVITY_UNITS_PER_LOOKUP * billableLookupCount);

		double activityCostEstimate = activityUnitPrice * activityEstimate;

		double serviceFeeEstimate = Accountant.lookupServiceFee(deliveryItem, false);

		double costEstimate = activityCostEstimate + serviceFeeEstimate;

		double chargeEstimate = costEstimate * (1 + markupPercentage);

		return chargeEstimate > chargeThreshold;
	}

	public static double calculateCharge(MailItem deliveryItem) throws Exception {
		double serviceFee = Accountant.lookupServiceFee(deliveryItem, false);

		double activityCost = calculateActivityCost(deliveryItem);

		double cost = activityCost + serviceFee;

		return cost * (1 + markupPercentage);
	}

	public static double calculateLookupCost() {
		return ACTIVITY_UNITS_PER_LOOKUP * activityUnitPrice;
	}

	public static double calculateActivityCost(MailItem item) {
		return calculateActivityUnits(item) * activityUnitPrice;
	}
	public static double calculateActivityUnits(MailItem deliveryItem) {
		return (deliveryItem.getMovementCount() * ACTIVITY_UNITS_PER_MOVEMENT) +
				(deliveryItem.getLookupCount() * ACTIVITY_UNITS_PER_LOOKUP);
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
