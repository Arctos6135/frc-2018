package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

/**
 *	Handles the teleop control of the wrist, and attemps to keep it at the same angle by reading from a gyro.
 */
public class WristAnalogAdjust extends WristAnalog {
	//Multiplier to get the adjust speed of the wrist motor
	protected static final double errorMultiplier = 0.01;
	//Allow up to 4 degrees of imprecision
	protected static final double TOLERANCE = 4.0;
	//The angle to return to
	protected double stationaryAngle;
	//If the robot was stationary the last time
	protected boolean wasStationary;

    public WristAnalogAdjust() {
        super();
    }
    
    static double constrain(double val, double upper, double lower) {
    	return Math.max(lower, Math.min(upper, val));
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	stationaryAngle = Robot.wristSubsystem.getGyro();
    	wasStationary = true;
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(RobotMap.ControllerMap.RSTICK_Y_AXIS);
    	
    	if(Math.abs(joystickVal) > DEADZONE) {
    		wasStationary = false;
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * joystickVal);
    	}
    	//If the joystick does not have an input, and in the last capture the wrist was stationary,
    	//update the angle to return to
    	else if(!wasStationary) {
    		wasStationary = true;
    		stationaryAngle = Robot.wristSubsystem.getGyro();
    		RobotMap.wristVictor.set(0);
    	}
    	//If nothing else, then try to return to the angle last recorded
    	else {
    		double error = Robot.wristSubsystem.getGyro() - stationaryAngle;
    		if(Math.abs(error) < TOLERANCE) {
    			RobotMap.wristVictor.set(0);
    			return;
    		}
    		double adjustment = constrain(error * errorMultiplier, 1.0, -1.0);
    		RobotMap.wristVictor.set(RobotMap.Speeds.WRIST_SPEED * adjustment);
    	}
    }
}
