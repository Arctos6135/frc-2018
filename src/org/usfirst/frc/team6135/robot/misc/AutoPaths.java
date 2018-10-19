package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.RobotMap;

import robot.pathfinder.core.RobotSpecs;
import robot.pathfinder.core.TrajectoryParams;
import robot.pathfinder.core.Waypoint;
import robot.pathfinder.core.path.PathType;
import robot.pathfinder.core.trajectory.TankDriveTrajectory;
import robot.pathfinder.core.trajectory.TrajectoryGenerator;

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
		
		baseline_driveForward = TrajectoryGenerator.generateStraightTank(specs, RobotMap.ArenaDimensions.BASELINE_DISTANCE);

		aligned_driveForward = TrajectoryGenerator.generateStraightTank(specs, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH);
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_SIZE / 2, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		params.alpha = 140;
		middle_right = new TankDriveTrajectory(specs, params);
		//Add 12 to X for the offset
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-(RobotMap.ArenaDimensions.SWITCH_SIZE / 2 + 12), RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		middle_left = new TankDriveTrajectory(specs, params);
		//Offset by a bit to compensate for the hardware errors
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_SIZE / 2 + 16, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		middle_right2 = new TankDriveTrajectory(specs, params).retrace();
		middle_left2 = middle_left.retrace();
		middle_3 = TrajectoryGenerator.generateStraightTank(specs, 78);
		middle_4 = middle_3.retrace();
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.ArenaDimensions.SWITCH_CENTER_DISTANCE - RobotMap.ROBOT_LENGTH / 2, 0)
		};
		params.alpha = 200;
		side_left = new TankDriveTrajectory(specs, params);
		side_right = side_left.mirrorLeftRight();
		
		back_up = middle_4;
	}
}
