package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.RobotMap;

import robot.pathfinder.RobotSpecs;
import robot.pathfinder.TankDriveTrajectory;
import robot.pathfinder.Waypoint;

public class AutoPaths {
	
	public static TankDriveTrajectory baseline_driveForward;
	
	public static TankDriveTrajectory aligned_driveForward;
	
	public static TankDriveTrajectory middle_left, middle_right;
	public static TankDriveTrajectory middle_left2, middle_right2;
	public static TankDriveTrajectory middle_3, middle_4;
	
	public static TankDriveTrajectory side_left, side_right;
	
	public static TankDriveTrajectory back_up;
	
	public static void generateAll(RobotSpecs specs) {
		baseline_driveForward = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.BASELINE_DISTANCE),
		}, specs, 100, 1000);
		
		aligned_driveForward = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2)
		}, specs, 100, 1000);
		
		middle_left = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-RobotMap.ArenaDimensions.SWITCH_SIZE / 2, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		}, specs, 250, 3000);
		middle_right = middle_left.mirrorLeftRight();
		middle_left2 = middle_left.retrace();
		middle_right2 = middle_right.retrace();
		middle_3 = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, 60, Math.PI / 2),
		}, specs, 50, 1000);
		middle_4 = middle_3.retrace();
		
		side_left = new TankDriveTrajectory(new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.ArenaDimensions.SWITCH_CENTER_DISTANCE - RobotMap.ROBOT_LENGTH / 2, 0)
		}, specs, 100, 3000);
		side_right = side_left.mirrorLeftRight();
		
		back_up = middle_4;
	}
}
