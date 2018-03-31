package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Backup default command for DriveTrain if the motors' brake config does not work.
 *	This command constantly checks if the Robot has shifted more than an amount, and if yes
 *	it runs the motor with a speed proportional to the error.
 */
public class Brake extends Command {

	public static final double TOLERANCE = 0.5;
	public static final double SPEED_MULTIPLIER = 0.03;
	public static final double MIN_SPEED = 0.0;
	
    public Brake() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.leftEncoder.reset();
    	RobotMap.rightEncoder.reset();
    }
    
    static double constrain(double val, double upper, double lower) {
    	return Math.max(lower, Math.min(upper, val));
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double left = RobotMap.leftEncoder.getDistance();
    	double right = RobotMap.rightEncoder.getDistance();
    	double leftSpeed = 0, rightSpeed = 0;
    	if(Math.abs(left) > TOLERANCE) {
    		double error = Math.abs(left);
    		int sign = left < 0 ? -1 : 1;
    		leftSpeed = -error * sign * SPEED_MULTIPLIER;
    		leftSpeed = Math.abs(leftSpeed) > MIN_SPEED ? leftSpeed : MIN_SPEED*sign;
    	}
    	if(Math.abs(right) > TOLERANCE) {
    		double error = Math.abs(right);
    		int sign = right < 0 ? -1 : 1;
    		rightSpeed = -error * sign * SPEED_MULTIPLIER;
    		rightSpeed = Math.abs(rightSpeed) > MIN_SPEED ? rightSpeed : MIN_SPEED*sign;
    	}
    	leftSpeed = constrain(leftSpeed, RobotMap.Speeds.DRIVE_SPEED, -RobotMap.Speeds.DRIVE_SPEED);
    	rightSpeed = constrain(rightSpeed, RobotMap.Speeds.DRIVE_SPEED, -RobotMap.Speeds.DRIVE_SPEED);
    	
    	Robot.drive.setMotorsVBus(leftSpeed, rightSpeed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drive.setMotorsVBus(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.drive.setMotorsVBus(0, 0);
    }
}
