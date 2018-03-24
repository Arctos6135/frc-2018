package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoElevator;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.Delay;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistanceEx;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 * Hard-Coded Command Group that goes around the switch and places a cube on the side.
 * The robot's initial position has to be to the side of the switch
 * Direction indicates what side of the field the robot is on.
 * 
 *	     ----------------------------------------
 *	     |		|						|		|
 *	--->>|		|		Switch			|		|
 *	|    |		|						|		|
 *	|    ----------------------------------------
 *	|
 *	|
 *	|
 * Robot
 */
public class PlaceCubeFromSideOffset extends CommandGroup {

	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;
	
    public PlaceCubeFromSideOffset(int direction) {
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
    	addSequential(new DriveStraightDistanceEx(RobotMap.ArenaDimensions.SWITCH_CENTRE_DISTANCE - RobotMap.ROBOT_LENGTH / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new AutoTurn(-90 * direction, RobotMap.Speeds.AUTO_TURN_SPEED));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistanceEx(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoElevator(RobotMap.AUTO_ELEVATOR_TIME, RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addSequential(new DriveStraightDistanceEx(RobotMap.INTAKE_LENGTH, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoIntake(RobotMap.AUTO_INTAKE_TIME, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
