package automail;

import simulation.Clock;

public class Accountant {
	private static boolean isChargeDisplayEnabled = false;
	
	private static int deliveredItemCount = 0;

	// ignoring service costs? unit is in activity units or dollars?
	private static double billableActivitySum = 0;
	private static double activityCostSum = 0;
	private static double serviceCostSum = 0;
	
	private static int lookupSuccessCount = 0;
	private static int lookupFailureCount = 0;
	
	public static void enableChargeDisplay() {
		isChargeDisplayEnabled = true;
	}
	
	public static void recordDelivery(MailItem deliveryItem) {
		++deliveredItemCount;
		
		billableActivitySum += Calculator.calculateActivity();
		activityCostSum += activityCost;
		serviceCostSum += Calculator.calculateServiceFee(deliveryItem.destination_floor);
		
		System.out.printf("T: %3d > Delivered(%4d) [%s]%n",
				Clock.Time(), deliveredItemCount, deliveryItem.toString());
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
		
		System.out.format("Delivered %d items", deliveredItemCount);
		
		System.out.format("Billed %lf dollars for activity\n", billableActivitySum);
		System.out.format("Spent %lf dollars on activity\n", activityCostSum);
		System.out.format("Spent %lf dollars on service\n", serviceCostSum);
		
		int totalLookupCount = lookupSuccessCount + lookupFailureCount;
		
		double lookupSuccessPercentage = (double)lookupSuccessCount / totalLookupCount;
		double lookupFailurePercentage = 1 - lookupSuccessPercentage;
		
		System.out.format("Performed %d remote lookups, with " +
							"%d successes (%lf%%) and %d failures (%lf%%)\n",
				totalLookupCount,
				lookupSuccessCount, lookupSuccessPercentage,
				lookupFailureCount, lookupFailurePercentage);
	}
}

