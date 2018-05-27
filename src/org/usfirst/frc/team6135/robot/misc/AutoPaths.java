package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.RobotMap;

import robot.pathfinder.RobotSpecs;
import robot.pathfinder.TankDriveTrajectory;
import robot.pathfinder.Waypoint;

public class AutoPaths {
	public static TankDriveTrajectory aligned_driveForward;
	
	static boolean initialized = false;
	
	public static void generateAll(RobotSpecs specs) {
		aligned_driveForward = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2)
		}, specs, 100, 1000);
		initialized = true;
	}
	
	public static void check() {
		if(!initialized)
			throw new RuntimeException("Not initialized");
	}
}
