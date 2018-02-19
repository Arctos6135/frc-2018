package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import static org.usfirst.frc.team6135.robot.RobotMap.*;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Turns the robot a certain degrees with a certain speed, with encoders
 *	AutoTurn is the recommended version. All constructors are compatible.
 *	For more information see the AutoTurn command
 */
@Deprecated
public class AutoTurnWithEncoders extends Command {
	
	public static final double ROBOT_DIAM = 23.25; //For turning, INCHES
	public static final double ROBOT_RADIUS = ROBOT_DIAM/2;
	public static final double DISTANCE_PER_DEGREE = (ROBOT_DIAM*Math.PI)/360;
	
	public int degrees;
	public double leftDistance;
	public double rightDistance;
	public double speed;
	
	//Degrees follow the unit circle
	//i.e. Positive means counter-clockwise and negative means clockwise
    public AutoTurnWithEncoders(int degrees, double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.degrees = degrees;
    	this.speed = speed;
    	leftDistance = -DISTANCE_PER_DEGREE*degrees;
    	rightDistance = DISTANCE_PER_DEGREE*degrees;
    }
    
    public AutoTurnWithEncoders(double leftDistance, double rightDistance, double speed){
    	//Assume that leftDistance is negative
    	requires(Robot.drive);
    	this.leftDistance = leftDistance;
    	this.rightDistance = rightDistance;
    	this.speed = speed;
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    	leftEncoder.reset();
    	rightEncoder.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drive.setMotorsVBus(speed, speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return (Math.abs(leftEncoder.getDistance()) >= Math.abs(leftDistance) 
        		|| Math.abs(rightEncoder.getDistance()) >= Math.abs(rightDistance));
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
