package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.WristAnalogPID;

import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 *
 */
public class WristPIDSubsystem extends PIDSubsystem {
	
	//Tune later
	public static double kP = 0;
	public static double kI = 0;
	public static double kD = 0;
	
	static final double ANGLE_MAX = 1.0;
	static final double ANGLE_MIN = -70.0;
	static final double TOLERANCE = 3;
	
	//The angle at the top and bottom of the wrist
	public static final double ANGLE_TOP = ANGLE_MIN;
	public static final double ANGLE_BOTTOM = ANGLE_MAX;
	
	public static final int DIRECTION_DOWN = 1;
	public static final int DIRECTION_UP = -1;
	
	static int sign(double n) {
		return n > 0 ? 1 : -1;
	}
	//The angle of the wrist when it hits the limit switch
	//Used in calibration via limit switch
	public static final double LIMIT_SWITCH_ANGLE = -65.0;
	
	//This value is added to the angle when it is retrieved
	//Used in calibration via limit switch
	//A workaround since the gyro API can only reset to 0
	double angleBias = LIMIT_SWITCH_ANGLE;
	
	public void setAngleBias(double bias) {
		angleBias = bias;
	}

    // Initialize your subsystem here
    public WristPIDSubsystem() {
        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        // enable() - Enables the PID controller.
    	super("Wrist PID Subsystem", kP, kI, kD);
    	setOutputRange(-1.0, 1.0);
    	setInputRange(ANGLE_MIN, ANGLE_MAX);
    	getPIDController().setContinuous(false);
    	setAbsoluteTolerance(TOLERANCE);
    	//RobotMap.wristGyro.calibrate();
    	setSetpoint(returnPIDInput());
    }
    
    public void setRaw(double speed) {
    	//If the speed is to go down, or there is still room to go up
    	if(notAtTop()) {
    		RobotMap.wristVictor.set(speed);
    	}
    	else {
    		if(sign(speed) == DIRECTION_DOWN)
    			RobotMap.wristVictor.set(speed);
    		else
    			RobotMap.wristVictor.set(0);
    	}
    }
    
    public boolean isEnabled() {
    	return this.getPIDController().isEnabled();
    }
    
    public double getAngle() {
    	//return RobotMap.wristGyro.getAngle() + angleBias;
    	return 1.0;
    }
    
    public Gyro getGyroSensor() {
    	//return RobotMap.wristGyro;
    	return null;
    }
    
    public boolean notAtTop() {
    	return RobotMap.wristSwitch.get();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new WristAnalogPID());
    }

    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;

        //return RobotMap.wristGyro.getAngle();
    	return 1;
    }

    protected void usePIDOutput(double output) {
        // Use output to drive your system, like a motor
        // e.g. yourMotor.set(output);
    	// Since the gyro reading decreases (Gets more negative) when the wrist raises,
    	// And the wrist is reversed, the output does not have to be reversed
    	if(notAtTop()) {
    		RobotMap.wristVictor.set(output);
    	}
    	else {
    		if(sign(output) == DIRECTION_DOWN)
    			RobotMap.wristVictor.set(output);
    		else
    			RobotMap.wristVictor.set(0);
    	}
    }
}
