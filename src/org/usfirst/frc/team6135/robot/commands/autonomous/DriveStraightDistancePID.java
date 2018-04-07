package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.misc.RampedPIDMotorController;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Uses PIDs to drive forward or backwards a straight distance.
 */
public class DriveStraightDistancePID extends Command {
	
	//To be tuned later
	public static double kP = 0.015;
	public static double kI = 0.002;
	public static double kD = 0.4;
	
	public static final double TOLERANCE = 1.0;
	
	PIDController leftPID, rightPID;
	double distance;

    public DriveStraightDistancePID(double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.leftEncoder.reset();
    	RobotMap.rightEncoder.reset();
    	
    	RampedPIDMotorController leftMotor = new RampedPIDMotorController(RobotMap.leftDriveTalon1, 0.1, false);
    	RampedPIDMotorController rightMotor = new RampedPIDMotorController(RobotMap.rightDriveTalon1, 0.1, true);
    	
    	leftPID = new PIDController(kP, kI, kD, RobotMap.leftEncoder, leftMotor);
    	rightPID = new PIDController(kP, kI, kD, RobotMap.rightEncoder, rightMotor);
    	
    	leftPID.setOutputRange(-1.0, 1.0);
    	rightPID.setOutputRange(-1.0, 1.0);
    	
    	leftMotor.setPIDController(leftPID);
    	rightMotor.setPIDController(rightPID);
    	
    	leftPID.setContinuous(false);
    	rightPID.setContinuous(false);
    	leftPID.setAbsoluteTolerance(TOLERANCE);
    	rightPID.setAbsoluteTolerance(TOLERANCE);
    	
    	leftPID.setSetpoint(distance);
    	rightPID.setSetpoint(distance);
    	
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(!leftPID.isEnabled())
    		leftPID.enable();
    	if(!rightPID.isEnabled())
    		rightPID.enable();
    	SmartDashboard.putNumber("Left Encoder", RobotMap.leftEncoder.getDistance());
    	SmartDashboard.putNumber("Right Encoder", RobotMap.rightEncoder.getDistance());
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
