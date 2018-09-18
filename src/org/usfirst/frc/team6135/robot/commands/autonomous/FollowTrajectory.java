package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

import robot.pathfinder.core.trajectory.TankDriveTrajectory;
import robot.pathfinder.follower.Follower;
import robot.pathfinder.follower.TankFollower;

/**
 * 	Makes the Robot follow the trajectory of a {@link robot.pathfinder.core.trajectory.TankDriveTrajectory TankDriveTrajectory}.<br>
 * 	<br>
 * 	Note: For this command to function correctly, the unit of length used in the trajectory must be <em>inches</em>,
 * 	and the unit for time must be <em>seconds</em>.
 *	@author Tyler
 */
public class FollowTrajectory extends Command {

	//Acceleration feedforward term, velocity feedforward term, proportional gain, derivative gain
	//Must tune later by trial and error
	//These are kept as non-constant to allow easy tuning from the SmartDashboard
	public static double kA = 0.00215, kV = 0.01, kP = 0.02225, kD = 0.001;
	
	static final Follower.TimestampSource TIMER = () -> {
		return Timer.getFPGATimestamp();
	};
	static final Follower.Motor MOTOR_LEFT = (s) -> {
		Robot.drive.setLeftMotor(s);
	};
	static final Follower.Motor MOTOR_RIGHT = (s) -> {
		Robot.drive.setRightMotor(s);
	};
	static final Follower.DistanceSource ENCODER_LEFT = () -> {
		return Robot.drive.getLeftDistance();
	};
	static final Follower.DistanceSource ENCODER_RIGHT = () -> {
		return Robot.drive.getRightDistance();
	};
	
	Follower follower;
	
    public FollowTrajectory(TankDriveTrajectory trajectory) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	follower = new TankFollower(trajectory, MOTOR_LEFT, MOTOR_RIGHT, ENCODER_LEFT, ENCODER_RIGHT, TIMER,
    			kV, kA, kP, kD);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	follower.initialize();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	follower.run();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return !follower.isRunning();
    }

    // Called once after isFinished returns true
    protected void end() {
    	follower.stop();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	follower.stop();
    }
}
