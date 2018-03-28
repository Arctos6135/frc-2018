package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.PIDSubsystem;

/**
 *
 */
public class ElevatorPIDSubsystem extends PIDSubsystem {
	
	public static final double kP = 0;
	public static final double kI = 0;
	public static final double kD = 0;
	
	//The encoder reading at the top & bottom
	//These values are used to calculate the ratio
	//The encoder reading is first reduced by MIN_ENCODER_READING, then scaled down by
	//the difference between MAX_ENCODER_READING and MIN_ENCODER_READING
	public static final double MAX_ENCODER_READING = 0;
	public static final double MIN_ENCODER_READING = 0;

    // Initialize your subsystem here
    public ElevatorPIDSubsystem() {
        // Use these to get going:
        // setSetpoint() -  Sets where the PID controller should move the system
        //                  to
        // enable() - Enables the PID controller.
    	super("Elevator PID Subsystem", kP, kI, kD);
    	//Allow up to 7% of imprecision
    	setAbsoluteTolerance(0.07);
    }

    public void initDefaultCommand() {
    }

    //Note: Instead of returning a raw value, this will return a ratio
    protected double returnPIDInput() {
        // Return your input value for the PID loop
        // e.g. a sensor, like a potentiometer:
        // yourPot.getAverageVoltage() / kYourMaxVoltage;
        return (RobotMap.elevatorEncoder.getDistance() - MIN_ENCODER_READING) / (MAX_ENCODER_READING - MIN_ENCODER_READING);
    }

    protected void usePIDOutput(double output) {
        // Use output to drive your system, like a motor
        // e.g. yourMotor.set(output);
    	RobotMap.elevatorVictor.set(Math.max(-1.0, Math.min(1.0, output)));
    }
}
