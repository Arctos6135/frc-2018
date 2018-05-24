package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Subsystem for the driving of the robot
 */
public class DriveTrain extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	double leftLastRate = 0, rightLastRate = 0;
	double lastTime;
	
	//Default: 30% Max (For demonstration purposes)
	double speedMultiplier = 0.3;
	
	public DriveTrain() {
		lastTime = Timer.getFPGATimestamp();
	}
	
	/**
	 * Gets the speed multiplier.<br>
	 * <br>
	 * Every time the speed of the motors are set, the speed is first multiplied by the multiplier.
	 * Setting this value to be between 0 and 1 effectively constrains the top speed of the drive.
	 * @return The speed multiplier
	 */
	public double getSpeedMultiplier() {
		return speedMultiplier;
	}
	/**
	 * Sets the speed multiplier.<br>
	 * <br>
	 * Every time the speed of the motors are set, the speed is first multiplied by the multiplier.
	 * Setting this value to be between 0 and 1 effectively constrains the top speed of the drive.
	 * @param multiplier - The speed multiplier
	 */
	public void setSpeedMultiplier(double multiplier) {
		this.speedMultiplier = multiplier;
	}

	/**
	 * Sets the speed of the drive motors for both sides of the robot.
	 * The motor speeds are first scaled by the scaling constant, and then constrained to [-1, 1].
	 * @param leftMotorVBus - The left output percentage
	 * @param rightMotorVBus - The right ouput percentage
	 */
	public void setMotorsVBus(double leftMotorVBus, double rightMotorVBus) {
		//Constrain to [-1, 1]
		RobotMap.leftDriveTalon1.set(ControlMode.PercentOutput, Math.max(-1, Math.min(1, leftMotorVBus)));
		RobotMap.rightDriveTalon1.set(ControlMode.PercentOutput, Math.max(-1, Math.min(1, rightMotorVBus)));
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TeleopDrive());
    }
    
    /**
     * Resets the internal left and right encoders.
     */
    public void resetEncoders() {
    	RobotMap.leftEncoder.reset();
    	RobotMap.rightEncoder.reset();
    }
    /**
     * Retrieves the left side encoder's distance reading, as determined by the
     * {@link edu.wpi.first.wpilibj.Encoder#getDistance() getDistance()} method of the encoder.
     * @return The distance of the left encoder
     */
    public double getLeftDistance() {
    	return RobotMap.leftEncoder.getDistance();
    }
    /**
     * Retrieves the right side encoder's distance reading, as determined by the
     * {@link edu.wpi.first.wpilibj.Encoder#getDistance() getDistance()} method of the encoder.
     * @return The distance of the right encoder
     */
    public double getRightDistance() {
    	return RobotMap.rightEncoder.getDistance();
    }
    /**
     * Retrieves the left side encoder's speed reading, as determined by the 
     * {@link edu.wpi.first.wpilibj.Encoder#getRate() getRate()} method of the encoder.
     * @return The left encoder's speed reading
     */
    public double getLeftSpeed() {
    	return RobotMap.leftEncoder.getRate();
    }
    /**
     * Retrieves the right side encoder's speed reading, as determined by the 
     * {@link edu.wpi.first.wpilibj.Encoder#getRate() getRate()} method of the encoder.
     * @return The right encoder's speed reading
     */
    public double getRightSpeed() {
    	return RobotMap.rightEncoder.getRate();
    }
    /**
     * Retrieves the acceleration of both sides.<br>
     * <br>
     * Instead of being read directly from the encoder, the acceleration is calculated by deriving the result
     * of {@link #getLeftSpeed()} and {@link #getRightSpeed()}. The derivation is done only when this method
     * is called, therefore the result will be more accurate if this method was called a short time ago.
     * However, please note that two subsequent calls right after each other may yield a result of 0, as the
     * last known encoder rate from {@link edu.wpi.first.wpilibj.Encoder#getRate() getRate()} may not have time
     * to update.
     * @return An array containing the acceleration of both sides, with element 0 being the left and element 1 
     * being the right
     */
    public double[] getAccelerations() {
    	double dt = Timer.getFPGATimestamp() - lastTime;
    	double leftRate = RobotMap.leftEncoder.getRate();
    	double rightRate = RobotMap.rightEncoder.getRate();
    	double leftAccel = (leftRate - leftLastRate) / dt;
    	double rightAccel = (rightRate - rightLastRate) / dt;
    	leftLastRate = leftRate;
    	rightLastRate = rightRate;
    	lastTime = Timer.getFPGATimestamp();
    	return new double[] { leftAccel, rightAccel };
    }
    
    /**
     * Retrieves the left {@code Encoder} object.
     * @return The left encoder
     */
    public Encoder getLeftEncoder() {
    	return RobotMap.leftEncoder;
    }
    /**
     * Retrieves the right {@code Encoder} object.
     * @return The right encoder
     */
    public Encoder getRightEncoder() {
    	return RobotMap.rightEncoder;
    }
}

