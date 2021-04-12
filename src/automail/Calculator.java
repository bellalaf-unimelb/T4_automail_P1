package automail;

import com.unimelb.swen30006.wifimodem.WifiModem;

import simulation.Building;

import exceptions.ReinitiateCalculatorException;
import exceptions.UninitiatedCalculatorException;

public class Calculator {
	private static final double movementCost = 5;
	private static final double lookupCost = 0.1;
	
	private static double[] serviceFees = null;
	
	private static boolean isInitiated = false;
	
	private static double markupPercentage;
	private static double activityUnitCost;
	private static double chargeThreshold;
	
	
	public static double calculateServiceFee(int floor) throws Exception {
		if(serviceFees == null) {
			serviceFees = new double[Building.FLOORS];
			for(int i=0; i<Building.FLOORS; i++) {
				serviceFees[i] = 0;
			}
		}
		
		double lookupResult;
		try {
			lookupResult = WifiModem.getInstance(floor).forwardCallToAPI_LookupPrice(floor);
		}
		catch(Exception e) {
			throw e;
		}
		
		if(lookupResult < 0) {
			Accountant.recordLookup(false);
			return serviceFees[floor];
		}
		else {
			for(int i=0; i<Building.FLOORS; i++) {
				if(fee == 0) {
					fee = lookupResult;
				}
			}
			Accountant.recordLookup(true);
			return lookupResult;
		}
	}
	
	public static double charge(double serviceFee,
								int movementCount,
								int lookupCount)
					throws UninitiatedCalculatorException {
		if(!isInitiated) {
			throw new UninitiatedCalculatorException();
		}
		
		double activityUnits = (movementCount * movementCost) +
								(lookupCount * lookupCost);
		
		double activityCost = activityUnits * activityUnitCost;
		
		double cost = serviceFee + activityCost;
		
		return cost + (1 + markupPercentage);
	}
	
	public static double getChargeThreshold() throws UninitiatedCalculatorException {
		if(!isInitiated) {
			throw new UninitiatedCalculatorException();
		}
		return chargeThreshold;
	}
}

