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
	static final double ANGLE_MIN = -80.0;
	static final double TOLERANCE = 5;
	
	//The angle at the top and bottom of the wrist
	public static final double ANGLE_TOP = ANGLE_MIN;
	public static final double ANGLE_BOTTOM = ANGLE_MAX;

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
    	RobotMap.wristGyro.calibrate();
    	setSetpoint(returnPIDInput());
    }
    
    public void setRaw(double speed) {
    	//If the speed is to go down, or there is still room to go up
    	if(speed < 0 || notAtTop())
    		RobotMap.wristVictor.set(speed);
    }
    
    public boolean isEnabled() {
    	return this.getPIDController().isEnabled();
    }
    
    public double getAngle() {
    	return RobotMap.wristGyro.getAngle();
    }
    
    public Gyro getGyroSensor() {
    	return RobotMap.wristGyro;
    }
    
    public boolean notAtTop() {
    	return RobotMap.elevatorTopSwitch.get();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new WristAnalogPID());
    }

    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;
        return Math.max(-1.0, Math.min(1.0, RobotMap.wristGyro.getAngle()));
    }

    protected void usePIDOutput(double output) {
        // Use output to drive your system, like a motor
        // e.g. yourMotor.set(output);
    	//Might need to be reversed
    	//If the speed is to go down, or there is still room to go up
    	if(-output < 0 || notAtTop())
    		RobotMap.wristVictor.set(-output);
    }
}
