package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.Building;

public class Accountant {
	private static boolean isChargeDisplayEnabled = false;

	private static int deliveredItemCount = 0;

	private static double billableActivitySum = 0;
	private static double activityCostSum = 0;
	private static double serviceCostSum = 0;

	private static int lookupSuccessCount = 0;
	private static int lookupFailureCount = 0;

	private static double[] serviceFeeRecords = null;

	public static void enableChargeDisplay() {
		isChargeDisplayEnabled = true;
	}

	public static void recordDelivery(MailItem deliveryItem) throws Exception {
		++deliveredItemCount;

		billableActivitySum += Calculator.calculateActivity(deliveryItem);
		activityCostSum += Calculator.calculateActivityCost(deliveryItem);
		serviceCostSum += serviceFeeRecords[deliveryItem.getDestFloor() - 1];
	}

	public static void reportStatistics() {
		if(!isChargeDisplayEnabled) return;

		System.out.format("Delivered %d items\n", deliveredItemCount);

		System.out.format("Performed %.2f billable activity units\n", billableActivitySum);
		System.out.format("Spent %.2f dollars on activity\n", activityCostSum);
		System.out.format("Spent %.2f dollars on service\n", serviceCostSum);

		int totalLookupCount = lookupSuccessCount + lookupFailureCount;

		double lookupSuccessPercentage = (double)lookupSuccessCount / totalLookupCount;
		double lookupFailurePercentage = 1 - lookupSuccessPercentage;

		System.out.format("Performed %d remote lookups, with " +
				"%d successes (%.2f%%) and %d failures (%.2f%%)\n",
				totalLookupCount,
				lookupSuccessCount, lookupSuccessPercentage,
				lookupFailureCount, lookupFailurePercentage);
	}

	public static String generateItemReport(MailItem deliveryItem) throws Exception {
		if(!isChargeDisplayEnabled) return "";
		
		double charge = Calculator.calculateCharge(deliveryItem);
		// the call to calculateCharge() just updated our service fee records
		double serviceFee = serviceFeeRecords[deliveryItem.getDestFloor() - 1];
		double cost = serviceFee + Calculator.calculateActivityCost(deliveryItem);
		double activity = Calculator.calculateActivity(deliveryItem);
		
		return String.format(" | Charge: %.2f | Cost: %.2f | Fee: %.2f | Activity: %.2f",
				charge, cost, serviceFee, activity);
	}

	public static double lookupServiceFee(MailItem deliveryItem) throws Exception {
		if(serviceFeeRecords == null) {
			initiateServiceFeeRecords();
		}

		int floor = deliveryItem.getDestFloor();
		WifiModem modem = WifiModem.getInstance(deliveryItem.getDestFloor());
		double serviceFee = modem.forwardCallToAPI_LookupPrice(floor);

		boolean wasLookupSuccessful = (serviceFee >= 0);
		recordLookup(wasLookupSuccessful);

		// failure: use the most recent record
		if(serviceFee < 0) return serviceFeeRecords[floor-1]; // -1 to compensate for zero-indexing

		// success: update the records in case of future failure
		serviceFeeRecords[floor-1] = serviceFee; // -1 to compensate for zero-indexing
		return serviceFee;
	}
	
	private static void initiateServiceFeeRecords() {
		serviceFeeRecords = new double[Building.FLOORS];

		for(int i=0; i<Building.FLOORS; i++) {
			serviceFeeRecords[i] = 0; // assume 0 to avoid overcharging
		}
	}

	public static void recordMovement() {
		activityCostSum += Calculator.calculateMovementCost();
	}
	public static void recordLookup(boolean success) {
		if(success) {
			++lookupSuccessCount;
		}
		else {
			++lookupFailureCount;
		}
	}
}

