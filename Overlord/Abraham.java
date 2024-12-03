package Overlord;
import robocode.*;
import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Abraham - a robot by (your name here)
 */

public class Abraham extends AdvancedRobot {
  int gunDirection = 1;

  public void run() {
    // Mere aesthetic changes
    setBodyColor(Color.black);
    setRadarColor(Color.green);
    setGunColor(Color.black);
    setBulletColor(Color.orange);

    // Turns the gun infinitely, looking for enemies
    while (true) {
      turnGunRight(360);
    }
  }

  public void onScannedRobot(ScannedRobotEvent e) {
    // Turn the robot towards the enemy
    setTurnRight(e.getBearing());
    // Shoots always that it's aiming at the enemy
    setFire(3);
    // And move forward
    setAhead(100);
    // Inverts the gun direction on each turn
    gunDirection = -gunDirection;
    // Turn 360 degrees (clockwise or anti clockwise,)
    setTurnGunRight(360 * gunDirection);
    // Execute all the pending actions
    execute();
  }
}