package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurnPID;
import org.usfirst.frc.team6135.robot.commands.autonomous.Delay;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerWrist;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.teleoperated.OperateIntake;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class MultiCubeFromMiddle extends CommandGroup {
	
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;

    public MultiCubeFromMiddle(int direction) {
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
    	addSequential(new PlaceCubeFromMiddleDiagonal(direction));
    	addParallel(new LowerElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addParallel(new LowerWrist(RobotMap.AUTO_WRIST_TIME));
    	addSequential(new DriveStraightDistancePID(-(RobotMap.ArenaDimensions.ALIGNED_CUBE_PICKUP_BACK - RobotMap.ROBOT_LENGTH / 2)));
    	addSequential(new AutoTurnPID(-45 * direction));
    	addParallel(new OperateIntake(0.9));
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.ALIGNED_CUBE_PICKUP_DIST));
    	//Run it at a low speed here to prevent the cube from falling out
    	addParallel(new OperateIntake(0.4));
    	addSequential(new DriveStraightDistancePID(-RobotMap.ArenaDimensions.ALIGNED_CUBE_PICKUP_DIST));
    	addSequential(new AutoTurnPID(45 * direction));
    	addParallel(new OperateIntake(0.0));
    	addSequential(new DriveStraightDistancePID(RobotMap.ArenaDimensions.ALIGNED_CUBE_PICKUP_BACK - RobotMap.ROBOT_LENGTH / 2));
    	addSequential(new RaiseElevator(RobotMap.Speeds.AUTO_ELEVATOR_SPEED));
    	addSequential(new DriveStraightDistancePID(RobotMap.INTAKE_LENGTH));
    	addSequential(new AutoIntake(RobotMap.AUTO_INTAKE_TIME, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
