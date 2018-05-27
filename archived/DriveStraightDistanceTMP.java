package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.misc.Setpoint;
import org.usfirst.frc.team6135.robot.misc.TrapezoidalMotionProfile;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;

/**
 *	Drives a straight distance forward with a trapezoidal motion profile.
 */
public class DriveStraightDistanceTMP extends Command {
	
	TrapezoidalMotionProfile leftTrajectory, rightTrajectory;
	
	//Acceleration feedforward term, velocity feedforward term, proportional gain, derivative gain
	//Must tune later by trial and error
	//These are kept as non-constant to allow easy tuning from the SmartDashboard
	public static double kA = 0, kV = 0, kP = 0, kD = 0;
	//Must tune later by looking at graphs
	public static final double maxAccel = 0, maxVelo = 0;
	
	double distance;
	
	double lastTime;
	double leftLastErr, rightLastErr;

    public DriveStraightDistanceTMP(double distance) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	this.distance = distance;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Create our trajectories to follow
    	leftTrajectory = new TrapezoidalMotionProfile(distance, maxAccel, maxVelo);
    	rightTrajectory = new TrapezoidalMotionProfile(distance, maxAccel, maxVelo);
    	//Reset encoders
    	Robot.drive.resetEncoders();
    	//Initialize values for differentiation
    	lastTime = Timer.getFPGATimestamp();
    	//Left and right position errors are always going to be 0
    	leftLastErr = rightLastErr = 0;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	//Skip if both left and right trajectories are finished
    	if(leftTrajectory.ended() && rightTrajectory.ended())
    		return;
    	//Time diff
    	double dt = Timer.getFPGATimestamp() - lastTime;
    	//Advance left and right paths
    	leftTrajectory.advanceTime(dt);
    	rightTrajectory.advanceTime(dt);
    	//Get setpoints
    	Setpoint leftSetpoint = leftTrajectory.getSetpoint();
    	Setpoint rightSetpoint = rightTrajectory.getSetpoint();
    	//Calculate left and right position errors
    	double leftErr = leftSetpoint.getPos() - Robot.drive.getLeftDistance();
    	double rightErr = rightSetpoint.getPos() - Robot.drive.getRightDistance();
    	//Calculate left and right position derivatives
    	//The desired velocity is subtracted to get the difference
    	double leftDeriv = (leftErr - leftLastErr) / dt 
    			- leftSetpoint.getVel();
    	double rightDeriv = (rightErr - rightLastErr) / dt
    			- rightSetpoint.getVel();
    	//Calculate speeds
    	double leftSpeed = leftSetpoint.getAcl() * kA + leftSetpoint.getVel() * kV 
    			+ leftErr * kP + leftDeriv * kD;
    	double rightSpeed = rightSetpoint.getAcl() * kA + rightSetpoint.getVel() * kV
    			+ rightErr * kP + rightDeriv * kD;
    	//Constrain
    	leftSpeed = Math.max(-1, Math.min(1, leftSpeed));
    	rightSpeed = Math.max(-1, Math.min(1, rightSpeed));
    	//Set motors
    	Robot.drive.setMotorsVBus(leftSpeed, rightSpeed);
    	//Reset stuff
    	leftLastErr = leftErr;
    	rightLastErr = rightErr;
    	lastTime = Timer.getFPGATimestamp();
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return leftTrajectory.ended() && rightTrajectory.ended();
    }

    // Called once after isFinished returns true
    protected void end() {
    	//Reset motors to make sure everything stops
    	Robot.drive.setMotorsVBus(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
