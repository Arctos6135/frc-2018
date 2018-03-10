package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.teleopcommands.WristAnalog;
import org.usfirst.frc.team6135.robot.commands.teleopcommands.WristAnalogAdjust;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Intake tilt (wrist) subsystem
 */
@SuppressWarnings("unused")
public class WristSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

	public void setSpeed(double speed) {
		RobotMap.wristVictor.set(speed);
	}
	public double getGyro() {
		return RobotMap.wristGyro.getAngle();
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new WristAnalog());
    	//setDefaultCommand(new WristAnalogAdjust());
    }
}

