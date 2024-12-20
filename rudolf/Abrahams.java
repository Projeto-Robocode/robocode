package rudolf;
import robocode.*;
//import java.awt.Color;

// API help : https://robocode.sourceforge.io/docs/robocode/robocode/Robot.html

/**
 * Abrahams - a robot by (your name here)
 */
public class Abrahams extends AdvancedRobot
{
	/**
	 * run: Abrahams's default behavior
	 */
	public void run() {
		// Initialization of the robot should be put here

		// After trying out your robot, try uncommenting the import at the top,
		// and the next line:

		// setColors(Color.red,Color.blue,Color.green); // body,gun,radar

		// Robot main loop
		

      double v = 5;
      double c = Math.PI*2;
      double a = .1;
      double b = .0053468;
      setMaxVelocity(v);
      setAhead(100*999);
      setTurnRight(360*999);	  

      while(true) {
			// Replace the next 4 lines with any behavior you would like
			
     

      
      while(true)
      {
          double t = getTime();
          double f = a*Math.pow(Math.E,b*t);
          double w = v/(c*f);       

          setMaxTurnRate(w);
          execute();
          System.out.println(t+"\t"+w);
      }
    }
		}
	

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Replace the next line with any behavior you would like
		fire(1);
	}

	/**
	 * onHitByBullet: What to do when you're hit by a bullet
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		// Replace the next line with any behavior you would like
		back(10);
	}
	
	/**
	 * onHitWall: What to do when you hit a wall
	 */
	public void onHitWall(HitWallEvent e) {
		// Replace the next line with any behavior you would like
		back(20);
	}	
}
