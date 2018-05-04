package org.usfirst.frc.team6135.robot.commands.defaultcommands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.command.Command;

/**
 *	A command that uses PIDControllers to try to restore the robot's encoder readings to 0 and
 *	keep them there.
 */
public class BrakePID extends Command {
	
	//To be tuned later
	public static double kP = 0;
	public static double kI = 0;
	public static double kD = 0;
	
	public static final double TOLERANCE = 0.5;
	
	PIDController leftPID, rightPID;

    public BrakePID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drive.resetEncoders();
    	
    	leftPID = new PIDController(kP, kI, kD, Robot.drive.getLeftEncoder(), RobotMap.leftDrivePIDMotor);
    	rightPID = new PIDController(kP, kI, kD, Robot.drive.getRightEncoder(), RobotMap.rightDrivePIDMotor);
    	
    	leftPID.setOutputRange(-1.0, 1.0);
    	rightPID.setOutputRange(-1.0, 1.0);
    	leftPID.setContinuous(false);
    	rightPID.setContinuous(false);
    	leftPID.setAbsoluteTolerance(TOLERANCE);
    	rightPID.setAbsoluteTolerance(TOLERANCE);
    	
    	leftPID.setSetpoint(0);
    	rightPID.setSetpoint(0);
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
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	leftPID.disable();
    	rightPID.disable();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	leftPID.disable();
    	rightPID.disable();
    }
}
