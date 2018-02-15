package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.teleopcommands.TeleopDrive;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	public void setMotorsVBus(double leftMotorVBus, double rightMotorVBus) {
		RobotMap.leftFrontDriveMotor.set(ControlMode.PercentOutput, leftMotorVBus);
		RobotMap.rightFrontDriveMotor.set(ControlMode.PercentOutput, -rightMotorVBus);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new TeleopDrive());
    }
}

