package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.RobotMap;

import robot.pathfinder.core.RobotSpecs;
import robot.pathfinder.core.TrajectoryParams;
import robot.pathfinder.core.Waypoint;
import robot.pathfinder.core.path.PathType;
import robot.pathfinder.core.trajectory.BasicTrajectory;
import robot.pathfinder.core.trajectory.TankDriveTrajectory;

public class AutoPaths {
	
	public static TankDriveTrajectory baseline_driveForward;
	
	public static TankDriveTrajectory aligned_driveForward;
	
	public static TankDriveTrajectory middle_left, middle_right;
	public static TankDriveTrajectory middle_left2, middle_right2;
	public static TankDriveTrajectory middle_3, middle_4;
	
	public static TankDriveTrajectory side_left, side_right;
	
	public static TankDriveTrajectory back_up;
	
	public static void generateAll(RobotSpecs specs) {
		TrajectoryParams params = new TrajectoryParams();
		params.isTank = true;
		params.pathType = PathType.QUINTIC_HERMITE;
		params.segmentCount = 500;
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.BASELINE_DISTANCE, Math.PI / 2)
		};
		params.alpha = 1;
		baseline_driveForward = new TankDriveTrajectory(new BasicTrajectory(specs, params));
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2)
		};
		params.alpha = 1;
		aligned_driveForward = new TankDriveTrajectory(new BasicTrajectory(specs, params));
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-RobotMap.ArenaDimensions.SWITCH_SIZE / 2, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		params.alpha = 125;
		middle_left = new TankDriveTrajectory(new BasicTrajectory(specs, params));
		middle_right = middle_left.mirrorLeftRight();
		middle_left2 = middle_left.retrace();
		middle_right2 = middle_right.retrace();
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(0, 60, Math.PI / 2),
		};
		params.alpha = 1;
		middle_3 = new TankDriveTrajectory(new BasicTrajectory(specs, params));
		middle_4 = middle_3.retrace();
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.ArenaDimensions.SWITCH_CENTER_DISTANCE - RobotMap.ROBOT_LENGTH / 2, 0)
		};
		params.alpha = 100;
		side_left = new TankDriveTrajectory(new BasicTrajectory(specs, params));
		side_right = side_left.mirrorLeftRight();
		
		back_up = middle_4;
	}
}
