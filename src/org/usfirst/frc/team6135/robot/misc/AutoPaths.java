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
	
	public static TankDriveTrajectory middle_left1, middle_right1;
	public static TankDriveTrajectory middle_left2, middle_right2;
	public static TankDriveTrajectory middle_left3, middle_right3;
	public static TankDriveTrajectory middle_4, middle_5;
	
	public static TankDriveTrajectory side_left, side_right;
	
	public static TankDriveTrajectory back_up;
	
	public static void generateAll(RobotSpecs specs) {
		TrajectoryParams params = new TrajectoryParams();
		params.isTank = true;
		params.pathType = PathType.QUINTIC_HERMITE;
		params.segmentCount = 500;
		
		
		baseline_driveForward = TrajectoryGenerator.generateStraightTank(specs, RobotMap.ArenaDimensions.BASELINE_DISTANCE);

		
		aligned_driveForward = TrajectoryGenerator.generateStraightTank(specs, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH);
		
		//No offsets for going from starting to switch
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-(RobotMap.ArenaDimensions.SWITCH_SIZE / 2), RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		params.alpha = 140;
		middle_left1 = new TankDriveTrajectory(specs, params);
		//Offset by 16 when going from switch back to start and back again
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(-(RobotMap.ArenaDimensions.SWITCH_SIZE / 2 + 16), RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		middle_left3 = new TankDriveTrajectory(specs, params);
		middle_left2 = middle_left3.retrace();
		
		//Offset by 12 when going from starting to switch
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_SIZE / 2 + 12, RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH, Math.PI / 2),
		};
		middle_right1 = new TankDriveTrajectory(specs, params);
		middle_right2 = middle_right1.retrace();
		middle_right3 = middle_right1;
		
		middle_4 = TrajectoryGenerator.generateStraightTank(specs, 78);
		middle_5 = middle_4.retrace();
		
		
		params.waypoints = new Waypoint[] {
				new Waypoint(0, 0, Math.PI / 2),
				new Waypoint(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.ArenaDimensions.SWITCH_CENTER_DISTANCE - RobotMap.ROBOT_LENGTH / 2, 0)
		};
		params.alpha = 200;
		side_left = new TankDriveTrajectory(specs, params);
		side_right = side_left.mirrorLeftRight();
		
		
		back_up = middle_5;
	}
}
