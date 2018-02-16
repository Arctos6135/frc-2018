package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistance;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class DrivePastBaseLineOffset extends CommandGroup {
	
	static final double DISTANCE_Y = RobotMap.ArenaDimensions.BASELINE_DISTANCE;
	
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;

    public DrivePastBaseLineOffset(int direction) {
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
    	
    	addSequential(new DriveStraightDistance(DISTANCE_Y / 2, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoTurn(90 * direction, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new DriveStraightDistance(RobotMap.ArenaDimensions.BASELINE_OFFSET, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new AutoTurn(-90 * direction, RobotMap.Speeds.AUTO_SPEED));
    	addSequential(new DriveStraightDistance(DISTANCE_Y / 2, RobotMap.Speeds.AUTO_SPEED));
    }
}
