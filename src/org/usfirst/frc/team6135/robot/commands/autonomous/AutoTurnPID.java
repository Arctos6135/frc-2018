package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;

/**
 *	A command that uses PID to turn a set number of degrees.
 */
public class AutoTurnPID extends Command {
	
	public static final double ROBOT_DIAM = 23.25; //For turning, INCHES
	public static final double ROBOT_RADIUS = ROBOT_DIAM/2;
	public static final double DISTANCE_PER_DEGREE = (ROBOT_DIAM*Math.PI)/360;

	//To be tuned later
	public static double kP = 0.02;
	public static double kI = 0.001;
	public static double kD = 0.016;
	
	public static final double TOLERANCE = 1.0;
	
	PIDController leftPID, rightPID;
	double leftDistance, rightDistance;

	/**
	 * Creates a new instance of the command that turns the Robot a number of degrees <b>counterclockwise</b>.<br>
	 * The degrees follow the unit circle, so a positive value means turning left.
	 * @param degrees - The amount of degrees to turn
	 */
    public AutoTurnPID(double degrees) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	
    	leftDistance = -DISTANCE_PER_DEGREE * degrees;
    	rightDistance = DISTANCE_PER_DEGREE * degrees;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.leftEncoder.reset();
    	RobotMap.rightEncoder.reset();
    	
    	leftPID = new PIDController(kP, kI, kD, RobotMap.leftEncoder, RobotMap.leftDrivePIDMotor);
    	rightPID = new PIDController(kP, kI, kD, RobotMap.rightEncoder, RobotMap.rightDrivePIDMotor);
    	
    	leftPID.setOutputRange(-1.0, 1.0);
    	rightPID.setOutputRange(-1.0, 1.0);
    	leftPID.setContinuous(false);
    	rightPID.setContinuous(false);
    	leftPID.setAbsoluteTolerance(TOLERANCE);
    	rightPID.setAbsoluteTolerance(TOLERANCE);
    	
    	leftPID.setSetpoint(leftDistance);
    	rightPID.setSetpoint(rightDistance);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(!leftPID.isEnabled())
    		leftPID.enable();
    	if(!rightPID.isEnabled())
    		rightPID.enable();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//Return true only if left and right PID are on target
        return leftPID.onTarget() && rightPID.onTarget();
    }

    // Called once after isFinished returns true
    protected void end() {
    	leftPID.disable();
    	rightPID.disable();
    	leftPID.free();
    	rightPID.free();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	leftPID.disable();
    	rightPID.disable();
    	leftPID.free();
    	rightPID.free();
    }
}
