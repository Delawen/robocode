package fusewarhw2019;

import robocode.*;
import static robocode.util.Utils.normalRelativeAngleDegrees;

import java.awt.*;


/**
 * PointyHairedBoss - your office's worst nightmare!
 * 
 * Started from Tracker sample and added some dumb stuff I came up with :-)
 * https://github.com/robo-code/robocode/blob/master/robocode.samples/src/main/java/sample/Tracker.java
 */
public class PointyHairedBoss extends AdvancedRobot {
	int count = 0; // Keeps track of how long we've
	// been searching for our target
	double gunTurnAmt; // How much to turn our gun when searching
	String trackName; // Name of the robot we're currently tracking

	public void run() {
		this.setColors(Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK, Color.BLACK);

		// Prepare gun
		trackName = null; // Initialize to not tracking anyone
		setAdjustGunForRobotTurn(true); // Keep the gun still when we turn
		gunTurnAmt = 10; // Initialize gunTurn to 10

		// Loop forever
		while (true) {
			// turn the Gun (looks for enemy)
			setTurnGunRight(gunTurnAmt);
			// Keep track of how long we've been looking
			count++;
			// If we've haven't seen our target for 2 turns, look left
			if (count > 2) {
				gunTurnAmt = -10;
			}
			// If we still haven't seen our target for 5 turns, look right
			if (count > 5) {
				gunTurnAmt = 10;
			}
			// If we *still* haven't seen our target after 10 turns, find another target
			if (count > 11) {
				trackName = null;
			}
			scan();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {

		// If we have a target, and this isn't it, return immediately
		// so we can get more ScannedRobotEvents.
		if (trackName != null && !e.getName().equals(trackName)) {
			return;
		}

		// If we don't have a target, well, now we do!
		if (trackName == null) {
			trackName = e.getName();
		}
		// This is our target.  Reset count (see the run method)
		count = 0;
		// If our target is too far away, turn and move toward it.
		if (e.getDistance() > 150) {
			gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));

			setTurnGunRight(gunTurnAmt);
			setTurnRight(e.getBearing());
			setAhead(e.getDistance() - 140);
			return;
		}

		// Our target is close.
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		setTurnGunRight(gunTurnAmt);
        setFire(3);

		// Our target is too close!  Back up.
		if (e.getDistance() < 100) {
			if (e.getBearing() > -90 && e.getBearing() <= 90) {
				setBack(40);
			} else {
				setAhead(40);
			}
		}
	}

	public void onHitRobot(HitRobotEvent e) {
		// Set the target
		trackName = e.getName();
		// Back up a bit.
		gunTurnAmt = normalRelativeAngleDegrees(e.getBearing() + (getHeading() - getRadarHeading()));
		setTurnGunRight(gunTurnAmt);
		setFire(3);
		setBack(50);
		execute();
	}

	@Override
	public void onRobotDeath(RobotDeathEvent robotDeathEvent) {		
		final String deadRobotName = robotDeathEvent.getName();

		if (trackName != null && trackName.equals(deadRobotName)) {
			trackName = null;
			count = 0;
		}
    }

}

