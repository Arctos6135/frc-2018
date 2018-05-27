package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.RobotMap;

import robot.pathfinder.RobotSpecs;
import robot.pathfinder.TankDriveTrajectory;
import robot.pathfinder.Waypoint;

public class AutoPaths {
	public static TankDriveTrajectory aligned_driveForward;
	
	public static TankDriveTrajectory middle_left, middle_right;
	
	public static void generateAll(RobotSpecs specs) {
		aligned_driveForward = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2)
		}, specs, 100, 1000);
		
		middle_left = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-RobotMap.ArenaDimensions.SWITCH_SIZE / 2, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		}, specs, 250, 3000);
		middle_right = middle_left.mirror();
	}
}
