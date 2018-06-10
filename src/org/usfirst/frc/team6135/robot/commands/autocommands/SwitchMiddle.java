package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistance;
import org.usfirst.frc.team6135.robot.commands.autonomous.FollowTrajectory;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.teleoperated.OperateIntake;
import org.usfirst.frc.team6135.robot.misc.AutoPaths;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class SwitchMiddle extends CommandGroup {

    public SwitchMiddle(int direction) {
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
    	addParallel(new RaiseElevator());
    	addSequential(new FollowTrajectory(direction == Robot.LEFT ? AutoPaths.middle_left : AutoPaths.middle_right));
    	addSequential(new AutoIntake(AutoIntake.Direction.OUT));
    	
    	addParallel(new LowerElevator());
    	addSequential(new FollowTrajectory(direction == Robot.LEFT ? AutoPaths.middle_left2 : AutoPaths.middle_right2));
    	addParallel(new OperateIntake(RobotMap.Speeds.INTAKE_SPEED));
    	addSequential(new FollowTrajectory(AutoPaths.middle_3));
    	addParallel(new OperateIntake(0.2));
    	addSequential(new FollowTrajectory(AutoPaths.middle_4));
    	addParallel(new OperateIntake(0));
    	
    	addParallel(new RaiseElevator());
    	addSequential(new FollowTrajectory(direction == Robot.LEFT ? AutoPaths.middle_left : AutoPaths.middle_right));
    	addSequential(new AutoIntake(AutoIntake.Direction.OUT));
    	
    	addParallel(new LowerElevator());
    	addSequential(new FollowTrajectory(AutoPaths.middle_4));
    }
}
