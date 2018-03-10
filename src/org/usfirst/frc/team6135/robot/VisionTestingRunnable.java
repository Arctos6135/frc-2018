package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.vision.VisionException;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionTestingRunnable implements Runnable {

	@Override
	public void run() {
		while(!Thread.interrupted()) {
			long startTime = System.currentTimeMillis();
			long endTime = 0;
			double angle = 0;
			boolean success;
			try {
				angle = Robot.visionSubsystem.getCubeAngleEx();
				endTime = System.currentTimeMillis();
				SmartDashboard.putString("Error:", "Success");
				success = true;
			} 
			catch (VisionException e) {
				SmartDashboard.putString("Error:", e.getMessage());
				success = false;
			}
			
			SmartDashboard.putNumber("Cube angle", success ? angle : 0);
			SmartDashboard.putNumber("Time elapsed", success ? endTime - startTime : 0);
			
		}
	}

}
