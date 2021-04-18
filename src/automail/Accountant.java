package automail;

import simulation.Clock;

public class Accountant {
	private static boolean isChargeDisplayEnabled = false;

	private static int deliveredItemCount = 0;

	private static double billableActivitySum = 0;
	private static double activityCostSum = 0;
	private static double serviceCostSum = 0;

	private static int lookupSuccessCount = 0;
	private static int lookupFailureCount = 0;

	public static void enableChargeDisplay() {
		isChargeDisplayEnabled = true;
	}

	public static void recordDelivery(MailItem item) throws Exception {
		++deliveredItemCount;

		billableActivitySum += Calculator.calculateActivityUnits(item);
		activityCostSum += Calculator.calculateActivityCost(item);
		serviceCostSum += Calculator.calculateServiceFee(item);

		System.out.printf("T: %3d > Delivered(%4d) [%s]%n",
				Clock.Time(), deliveredItemCount, item.toString());
	}

	public static void recordLookup(boolean wasSuccessful) {
		if(wasSuccessful) {
			++lookupSuccessCount;
		}
		else {
			++lookupFailureCount;
		}
	}

	public static void reportStatistics() {
		if(!isChargeDisplayEnabled) {
			return;
		}

		System.out.format("Delivered %d items\n", deliveredItemCount);

		System.out.format("Billed %.2f dollars for activity\n", billableActivitySum);
		System.out.format("Spent %.2f dollars on activity\n", activityCostSum);
		System.out.format("Spent %.2f dollars on service\n", serviceCostSum);

		int totalLookupCount = lookupSuccessCount + lookupFailureCount;

		double lookupSuccessPercentage = (double)lookupSuccessCount / totalLookupCount;
		double lookupFailurePercentage = 1 - lookupSuccessPercentage;

		System.out.format("Performed %d remote lookups, with " +
							"%d successes (%f%%) and %d failures (%f%%)\n",
				totalLookupCount,
				lookupSuccessCount, lookupSuccessPercentage,
				lookupFailureCount, lookupFailurePercentage);
	}
}

