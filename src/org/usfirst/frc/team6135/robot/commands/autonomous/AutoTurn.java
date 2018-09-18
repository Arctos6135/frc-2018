package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.misc.TrapezoidalMotionProfile;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	A command that uses a trapezoidal motion profile to turn a set number of degrees.
 */
public class AutoTurn extends Command {
	
	public static final double ROBOT_DIAM = 23.25; //For turning, INCHES
	public static final double ROBOT_RADIUS = ROBOT_DIAM/2;
	public static final double DISTANCE_PER_DEGREE = (ROBOT_DIAM*Math.PI)/360;

	public static double kP = FollowTrajectory.kP;
	public static double kD = FollowTrajectory.kD;
	public static double kV = FollowTrajectory.kV;
	public static double kA = FollowTrajectory.kA;
	
	public static double maxVel = RobotMap.specs.getMaxVelocity();
	public static double maxAccel = RobotMap.specs.getMaxAcceleration();
	
	TrapezoidalMotionProfile leftProfile, rightProfile;
	double lLastErr, rLastErr, lastTime;

	/**
	 * Creates a new instance of the command that turns the Robot a number of degrees <b>counterclockwise</b>.<br>
	 * The degrees follow the unit circle, so a positive value means turning left.
	 * @param degrees - The amount of degrees to turn
	 */
    public AutoTurn(double degrees) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	
    	leftProfile = new TrapezoidalMotionProfile(-DISTANCE_PER_DEGREE * degrees, maxVel, maxAccel);
    	rightProfile = new TrapezoidalMotionProfile(DISTANCE_PER_DEGREE * degrees, maxVel, maxAccel);
    }
    public static final int LEFT = 1;
    public static final int RIGHT = -1;
    
    public AutoTurn(double degrees, int direction) {
    	this(degrees * direction);
    }
    

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.drive.resetEncoders();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	if(timeSinceInitialized() > leftProfile.totalTime() || timeSinceInitialized() > rightProfile.totalTime()) {
    		return;
    	}
    	
    	//Calculate current t and time difference from last iteration
		double t = timeSinceInitialized();
		double dt = t - lastTime;
		
		//Calculate left and right errors
		double leftErr = Robot.drive.getLeftDistance() - leftProfile.distAt(t);
		double rightErr = Robot.drive.getRightDistance() - rightProfile.distAt(t);
		//Get the derivative of the errors
		//Subtract away the desired velocity to get the true error
		double leftDeriv = (leftErr - lLastErr) / dt 
    			- leftProfile.veloAt(t);
    	double rightDeriv = (rightErr - rLastErr) / dt
    			- rightProfile.veloAt(t);

    	//Calculate outputs
    	double leftOutput = kA * leftProfile.accelAt(t) + kV * leftProfile.veloAt(t)
				+ kP * leftErr + kD * leftDeriv;
		double rightOutput = kA * rightProfile.accelAt(t) + kV * rightProfile.accelAt(t)
				+ kP * rightErr + kD * rightDeriv;
		//Constrain
    	leftOutput = Math.max(-1, Math.min(1, leftOutput));
    	rightOutput = Math.max(-1, Math.min(1, rightOutput));
    	
    	Robot.drive.setMotorsVBus(leftOutput, rightOutput);
    	
    	lastTime = t;
    	lLastErr = leftErr;
    	rLastErr = rightErr;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	//Return if the time since initialized is more than the total time of the trajectory
    	return timeSinceInitialized() > leftProfile.totalTime();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drive.setMotorsVBus(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
