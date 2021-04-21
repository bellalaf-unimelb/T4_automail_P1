package automail;

import automail.Robot.RobotState;
import simulation.IMailDelivery;

public class Automail {
	      
    public Robot[] robots;
    public MailPool mailPool;
    
    public Automail(MailPool mailPool, IMailDelivery delivery, int numRobots) {  	
    	/** Initialize the MailPool */
    	
    	this.mailPool = mailPool;
    	
    	/** Initialize robots */
    	robots = new Robot[numRobots];
    	for (int i = 0; i < numRobots; i++) robots[i] = new Robot(delivery, mailPool, i);
    }
    
    /**
     * @return whether all robots are in the waiting state.
     */
	public boolean isIdle() {
		for(int i=0; i<robots.length; i++) {
			if(robots[i].current_state != RobotState.WAITING) {
				return false;
			}
		}
		return true;
	}
}
