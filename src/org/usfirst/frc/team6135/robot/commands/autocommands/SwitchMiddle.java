package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.FollowTrajectory;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerWrist;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.teleoperated.OperateIntake;
import org.usfirst.frc.team6135.robot.misc.AutoPaths;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class SwitchMiddle extends CommandGroup {

    public SwitchMiddle(Robot.GenericLocation direction) {
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
    	//Place first cube
    	addParallel(new RaiseElevator());
    	addSequential(new FollowTrajectory(direction == Robot.GenericLocation.LEFT ? AutoPaths.middle_left : AutoPaths.middle_right));
    	addSequential(new OperateIntake(OperateIntake.OPEN));
    	addSequential(new AutoIntake(AutoIntake.Direction.OUT));
  
    	addParallel(new LowerElevator());
    	addParallel(new LowerWrist()); //Lower elevator and wrist and drive back at the same time
    	addSequential(new FollowTrajectory(direction == Robot.GenericLocation.LEFT ? AutoPaths.middle_left2 : AutoPaths.middle_right2));
    	addSequential(new FollowTrajectory(AutoPaths.middle_4)); //Drive forward
    	addSequential(new OperateIntake(OperateIntake.CLOSE)); //Close the intake

    	addSequential(new AutoIntake(AutoIntake.Direction.IN, 0.8)); //Run the intake for a bit to pull the cube in
    	addParallel(new AutoIntake(AutoIntake.Direction.IN, 0.7));
    	addSequential(new FollowTrajectory(AutoPaths.middle_5)); //Drive back
    	
    	addParallel(new RaiseElevator());
    	addSequential(new FollowTrajectory(direction == Robot.GenericLocation.LEFT ? AutoPaths.middle_left3 : AutoPaths.middle_right3));
    	addSequential(new OperateIntake(OperateIntake.OPEN));
    	addSequential(new AutoIntake(AutoIntake.Direction.OUT));
    	
    	addParallel(new LowerElevator());
    	addSequential(new FollowTrajectory(AutoPaths.back_up));
    }
}
