package org.usfirst.frc.team6135.robot.misc;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;

import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;

/**
 * Since CTRE Phoenix's TalonSRXes and VictorSPXes don't implement PIDOutput, a wrapper class is created.<br>
 * This class also provides ramping for motors so when the PID first starts there is no sudden acceleration.
 * @author Tyler
 *
 */
public class RampedPIDMotorController implements PIDOutput {
	
	IMotorController motorController;
	boolean reversed;
	double rampBand;
	PIDController pidController;
	
	public RampedPIDMotorController(IMotorController controller, double rampBand, boolean reversed) {
		this.motorController = controller;
		this.rampBand = rampBand;
		this.reversed = reversed;
	}
	public RampedPIDMotorController(IMotorController controller, double rampBand, boolean reversed, PIDController pidController) {
		this.motorController = controller;
		this.rampBand = rampBand;
		this.reversed = reversed;
		setPIDController(pidController);
	}
	public RampedPIDMotorController(PIDMotorController pmc, double rampBand) {
		this.motorController = pmc.controller;
		this.reversed = pmc.reversed;
		this.rampBand = rampBand;
	}
	public RampedPIDMotorController(PIDMotorController pmc, double rampBand, PIDController pidController) {
		this.motorController = pmc.controller;
		this.reversed = pmc.reversed;
		this.rampBand = rampBand;
		setPIDController(pidController);
	}
	
	/**
	 * Sets the {@code PIDController} that uses this {@code RampedPIDMotorController}.
	 * The output range of the controller is constantly adjusted to provide ramping for the motors.
	 * @param pc - The {@code PIDController} that uses this instace of {@code RampedPIDMotorController}
	 * @return The controller, for easy chaining.
	 */
	public RampedPIDMotorController setPIDController(PIDController pc) {
		pidController = pc;
		pidController.setOutputRange(Math.max(-1, 0 - rampBand), Math.min(1, 0 + rampBand));
		return this;
	}

	@Override
	public void pidWrite(double output) {
		pidController.setOutputRange(Math.max(-1, output - rampBand), Math.min(1, output + rampBand));
		if(reversed)
			output = -output;
		this.motorController.set(ControlMode.PercentOutput, output);
	}

}
