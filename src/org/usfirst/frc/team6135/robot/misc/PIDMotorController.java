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
	
	public PIDMotorController(IMotorController c) {
		controller = c;
	}
	
	@Override
	public void pidWrite(double output) {
		controller.set(ControlMode.PercentOutput, output);
	}

}
