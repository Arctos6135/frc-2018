package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurnPID;
import org.usfirst.frc.team6135.robot.commands.autonomous.Delay;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.teleoperated.IntakingPosition;
import org.usfirst.frc.team6135.robot.commands.teleoperated.OperateIntake;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class MultiCubeSameSide extends CommandGroup {
	
	public static final int SIDE_LEFT = 1;
	public static final int SIDE_RIGHT = -1;

    public MultiCubeSameSide(int side) {
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
    	addSequential(new PlaceCubeFromSide(side));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	//Back up a bit so we can turn and put the intake down
    	addSequential(new DriveStraightDistancePID(-RobotMap.INTAKE_LENGTH + -6.0));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addParallel(new IntakingPosition());
    	addSequential(new AutoTurnPID(90 * side));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	//Get behind the cube
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SIDE_CUBE_PICKUP_DIST + RobotMap.ArenaDimensions.HALF_SWITCH_DEPTH));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new AutoTurnPID(-90 * side));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new DriveStraightDistancePID(RobotMap.INTAKE_LENGTH + 6.0 + RobotMap.ROBOT_LENGTH));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new AutoTurnPID(-90 * side));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	//Pick up the cube
    	addParallel(new OperateIntake(0.9));
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SIDE_CUBE_PICKUP_DIST));
    	addSequential(new Delay(RobotMap.AUTO_DELAY));
    	addSequential(new RaiseElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addSequential(new DriveStraightDistancePID(RobotMap.INTAKE_LENGTH));
    	addSequential(new AutoIntake(RobotMap.AUTO_INTAKE_TIME, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
