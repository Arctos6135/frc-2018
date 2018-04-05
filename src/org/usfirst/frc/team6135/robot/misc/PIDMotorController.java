package org.usfirst.frc.team6135.robot.misc;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.IMotorController;

import edu.wpi.first.wpilibj.PIDOutput;

/**
 * Since CTRE Phoenix's TalonSRXes and VictorSPXes don't implement PIDOutput, a wrapper class is created.
 * @author Tyler
 *
 */
public class PIDMotorController implements PIDOutput {

	IMotorController controller;
	
	boolean reversed;
	
	public PIDMotorController(IMotorController c, boolean reverse) {
		controller = c;
		reversed = reverse;
	}
	
	@Override
	public void pidWrite(double output) {
		double val = reversed ? -output : output;
		controller.set(ControlMode.PercentOutput, val);
	}

}
