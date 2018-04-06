package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurnPID;
import org.usfirst.frc.team6135.robot.commands.autonomous.Delay;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.SetWrist;
import org.usfirst.frc.team6135.robot.subsystems.WristPIDSubsystem;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *	Hard-Coded Command Group that places a cube on the switch when the robot is in the middle.
 *	Direction indicates the side of the field the alliance's switch colour is on.
 *
 *	   ------------------------------------------
 *	   |		|						|		|
 *	   |		|		Switch			|		|
 *	   |		|						|		|
 *	   ------------------------------------------
 *	 		^								^
 *			|-------------------------------|
 *		   				   |
 *		   				   |
 *		 				 Robot
 */
public class PlaceCubeFromMiddle extends CommandGroup {
	
	static final double DISTANCE_Y = RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH;
	
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;
	
    public PlaceCubeFromMiddle(int direction) {
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
    	addSequential(new DriveStraightDistancePID(DISTANCE_Y / 2));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new AutoTurnPID(90 * direction));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_SIZE / 2 - (1.0 * 12)));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new AutoTurnPID(-90 * direction));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistancePID(DISTANCE_Y / 2));
    	addSequential(new RaiseElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addSequential(new DriveStraightDistancePID(RobotMap.INTAKE_LENGTH));
    	addSequential(new AutoIntake(RobotMap.AUTO_INTAKE_TIME, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
