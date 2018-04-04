package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurnPID;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.teleoperated.IntakingPosition;

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
    	//Back up a bit so we can turn and put the intake down
    	addSequential(new DriveStraightDistancePID(-RobotMap.INTAKE_LENGTH - 6.0));
    	addParallel(new IntakingPosition());
    	addSequential(new AutoTurnPID(90 * side));
    	//Get behind the cube
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SIDE_CUBE_PICKUP_DIST + RobotMap.ArenaDimensions.HALF_SWITCH_DEPTH));
    	
    }
}
