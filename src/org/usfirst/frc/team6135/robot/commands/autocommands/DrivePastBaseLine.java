package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistance;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *	CommandGroup that drives past the base line. 
 *	Note even though only 1 command is needed a CommandGroup is used 
 *	so that any extra steps can be implemented later.
 */
public class DrivePastBaseLine extends CommandGroup {

    public DrivePastBaseLine() {
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
    	addSequential(new DriveStraightDistance(RobotMap.ArenaDimensions.BASELINE_DISTANCE, RobotMap.Speeds.AUTO_SPEED));
    }
}
