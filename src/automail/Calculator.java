package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.Building;

public class Calculator {
	private static final double movementCost = 5;
	private static final double lookupCost = 0.1;

	private static double[] serviceFees = null;

	private static double markupPercentage = 0;
	private static double activityUnitPrice = 0;
	private static double chargeThreshold = 0;


	public static double calculateCharge(MailItem item) throws Exception {

		double cost = calculateActivityCost(item) + calculateServiceFee(item);

		return cost * (1 + markupPercentage);
	}

	public static double calculateServiceFee(MailItem item) throws Exception {
		if(serviceFees == null) {
			serviceFees = new double[Building.FLOORS + 1];
			for(int i=0; i<Building.FLOORS; i++) {
				serviceFees[i] = 0;
			}
		}

		double lookupResult;
		try {
			lookupResult = WifiModem.getInstance(item.getDestFloor()).forwardCallToAPI_LookupPrice(item.getDestFloor());
		}
		catch(Exception e) {
			throw e;
		}

		// record-keeping
		item.recordLookup();
		Accountant.recordLookup(lookupResult >= 0); // negative result means failure

		// actually giving an answer
		if(lookupResult < 0) {
			// failure: use the most recent record
			return serviceFees[item.getDestFloor()];
		}
		else {
			// success: update the records in case of future failure
			serviceFees[item.getDestFloor()] = lookupResult;
			return lookupResult;
		}
	}

	public static double calculateActivityCost(MailItem item) {
		return calculateActivityUnits(item) * activityUnitPrice;
	}
	public static double calculateActivityUnits(MailItem item) {
		return (movementCost * item.getMovementCount()) +
				(lookupCost * item.getLookupCount());
	}

	public static double getChargeThreshold() {
		return chargeThreshold;
	}
	public static void setChargeThreshold(double chargeThreshold) {
		Calculator.chargeThreshold = chargeThreshold;
	}

	public static double getActivityUnitPrice() {
		return activityUnitPrice;
	}
	public static void setActivityUnitPrice(double activityUnitPrice) {
		Calculator.activityUnitPrice = activityUnitPrice;
	}

	public static double getMarkupPercentage() {
		return markupPercentage;
	}
	public static void setMarkupPercentage(double markupPercentage) {
		Calculator.markupPercentage = markupPercentage;
	}
}
