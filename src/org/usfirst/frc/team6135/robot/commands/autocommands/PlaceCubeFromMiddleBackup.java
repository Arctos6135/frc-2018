package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistanceEx;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *	Hard-Coded Command Group that places a cube on the switch when the robot is in the middle.
 *	Fail-safe alternative to vision
 *
 *	   ------------------------------------------
 *	   |		|						|		|
 *	   |		|		Switch			|		|
 *	   |		|						|		|
 *	   ------------------------------------------
 *	 		^								^
 *			|-------------------------------|
 *		   				   |
 *		 				 Robot
 *						   |
 */
public class PlaceCubeFromMiddleBackup extends CommandGroup {
	
	static double DISTANCE_Y = RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH / 2;
	
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;
	
    public PlaceCubeFromMiddleBackup(int direction, double distanceRan) {
    	DISTANCE_Y -= distanceRan;
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    	addSequential(new DriveStraightDistanceEx(DISTANCE_Y / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoTurn(90 * direction, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new DriveStraightDistanceEx(RobotMap.ArenaDimensions.SWITCH_SIZE / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoTurn(-90 * direction, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new DriveStraightDistanceEx(DISTANCE_Y / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoIntake(1.5, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
