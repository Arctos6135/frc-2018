package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autonomous.Delay;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistanceEx;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.SetWrist;
import org.usfirst.frc.team6135.robot.subsystems.WristPIDSubsystem;

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
public class PlaceCubeFromSide extends CommandGroup {

	public static final int SIDE_LEFT = 1;
	public static final int SIDE_RIGHT = -1;
	
    public PlaceCubeFromSide(int side) {
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
    	addParallel(new SetWrist(WristPIDSubsystem.ANGLE_BOTTOM));
    	addSequential(new DriveStraightDistanceEx(RobotMap.ArenaDimensions.SWITCH_CENTER_DISTANCE - RobotMap.ROBOT_LENGTH / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	
    	addSequential(new AutoTurn(-85 * side, RobotMap.Speeds.AUTO_TURN_SPEED));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistanceEx(RobotMap.ArenaDimensions.SWITCH_EDGE_OFFSET, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new RaiseElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistanceEx(RobotMap.INTAKE_LENGTH, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoIntake(RobotMap.AUTO_INTAKE_TIME, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
