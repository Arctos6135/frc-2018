package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.vision.VisionException;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class {@code TestingThread} is a Runnable that loops forever and prints testing/debug info.
 * @author Tyler
 *
 */
public class TestingThread implements Runnable {

	@Override
	public void run() {
		/*
		 * Comment out individual sections to test them
		 */
		while(!Thread.interrupted()) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			double angle = 0;
			boolean success;
			
			
			try {
				angle = Math.toDegrees(Robot.visionSubsystem.getCubeAngleOpenCV());
				endTime = System.currentTimeMillis();
				SmartDashboard.putString("Cube Vision Error:", "Success");
				success = true;
			} 
			catch (VisionException e) {
				SmartDashboard.putString("Cube Vision Error:", e.getMessage());
				success = false;
			}
			SmartDashboard.putNumber("Cube angle", success ? angle : 0);
			SmartDashboard.putNumber("Cube calculation time", success ? endTime - startTime : 0);
			
			
			
			/*startTime = System.currentTimeMillis();
			try {
				angle = Robot.visionSubsystem.getSwitchAngleEx(Robot.color);
				endTime = System.currentTimeMillis();
				SmartDashboard.putString("Switch Vision Error:", "Success");
				success = true;
			}
			catch(VisionException e) {
				SmartDashboard.putString("Switch Vision Error:", e.getMessage());
				success = false;
			}
			SmartDashboard.putNumber("Switch angle", success ? angle : 0);
			SmartDashboard.putNumber("Switch calculation time", success ? endTime - startTime : 0);*/
			
			
			
			//SmartDashboard.putNumber("Gyro reading", Robot.wristSubsystem.getGyro());
		}
	}

}
