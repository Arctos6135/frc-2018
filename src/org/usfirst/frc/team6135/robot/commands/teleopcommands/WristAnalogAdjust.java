package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Handles the teleop control of the wrist, and attempts to keep it at the same angle by reading from a gyro
 *	and applying corrections if necessary. Correction speed is proportional to the error (actual - desired angle)
 *	This command also applies a soft limit to the wrist angle, and attempting to turn while it's over that limit
 *	will not result in any movement. The angle to correct to is also constrained, and if the physical angle is
 *	over the limit, it will be corrected (when there's no input on the controller).
 */
public class WristAnalogAdjust extends WristAnalog {
	//Multiplier to get the adjust speed of the wrist motor
	protected static final double errorMultiplier = 0.01;
	//Allow up to 4 degrees of imprecision
	//IMPORTANT: MUST BE GREATER THAN 5 IF USING LOGARITHMIC ADJUSTMENTS.
	//See graph at https://www.desmos.com/calculator/4x4qkmrf62
	protected static final double TOLERANCE = 6.0;
	//The angle to return to
	protected double stationaryAngle;
	//If the robot was stationary the last time
	protected boolean wasStationary;
	//Soft limits for the wrist angle. If past this angle, the wrist will not turn (in code) and angle will not be recorded
	//This is to prevent slacking due to wrist reaching the bottom
	protected static final double WRIST_SOFT_LIMIT_HIGH = 3.0; //Not constrained - to be set w/ later testing
	protected static final double WRIST_SOFT_LIMIT_LOW = Double.MIN_VALUE; //Not constrained - to be set w/ later testing

    public WristAnalogAdjust() {
        super();
    }
    
    static double constrain(double val, double upper, double lower) {
    	return Math.max(lower, Math.min(upper, val));
    }
    static boolean inRange(double val, double max, double min) {
    	return val <= max && val >= min;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	//Initialize the desired angle to the current angle
    	stationaryAngle = constrain(Robot.wristSubsystem.getGyro(), WRIST_SOFT_LIMIT_HIGH, WRIST_SOFT_LIMIT_LOW);
    	wasStationary = true;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.WRIST);
    	final double gyroReading = Robot.wristSubsystem.getGyro();
    	
    	SmartDashboard.putNumber("Stationary Angle", stationaryAngle);
    	SmartDashboard.putNumber("Current Angle", gyroReading);
    	SmartDashboard.putNumber("Joystick Value", joystickVal);
    	SmartDashboard.putBoolean("stationary", wasStationary);
    	
    	if(Math.abs(joystickVal) > DEADZONE) {
    		//Only move the wrist if the gyro shows that the angle is in the constraints
    		/*if(inRange(gyroReading, WRIST_SOFT_LIMIT_HIGH, WRIST_SOFT_LIMIT_LOW)) {
	    		wasStationary = false;
	    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * joystickVal);
    		}
    		else {
    			wasStationary = true;
    			RobotMap.wristVictor.set(0);
    		}*/
    		
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * joystickVal);
    		wasStationary = false;
    	}
    	//If the joystick does not have an input, and in the last capture the wrist was not stationary,
    	//(i.e. the wrist just stopped moving) update the desired angle
    	else if(!wasStationary) {
    		wasStationary = true;
    		//Constrain the angle and then assign it
    		stationaryAngle = constrain(gyroReading, WRIST_SOFT_LIMIT_HIGH, WRIST_SOFT_LIMIT_LOW);
    		RobotMap.wristVictor.set(0);
    	}
    	//If no input and the wrist is already "stationary", then try to return to the angle last recorded
    	else {
    		//Do not constrain the gyro reading here since we want to apply the correction even if
    		//the current physical angle is outside the limits.
    		
    		double error = gyroReading - stationaryAngle;
    		SmartDashboard.putNumber("Error", error);
    		//Do not do anything if the error is small
    		if(Math.abs(error) < TOLERANCE) {
    			RobotMap.wristVictor.set(0);
    			return;
    		}
    		//Constrain the speed and apply correction
    		double adjustment = constrain(error * errorMultiplier, 1.0, -1.0);
    		SmartDashboard.putNumber("Adjustment", adjustment);
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * adjustment);
    		//Logarithmic adjustment
    		/*double adjustment = constrain(Math.copySign(
    				Math.log(
    						(Math.abs(error) - 2) / 4)
    				, error), 1.0, -1.0);
    		SmartDashboard.putNumber("Adjustment Value", adjustment);
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * adjustment);*/
    	}
    }
}
