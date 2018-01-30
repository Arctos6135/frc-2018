package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.ClampAnalog;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ClampSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        setDefaultCommand(new ClampAnalog());
    }
    
    public void setSpeed(double speed) {
    	RobotMap.clampLeft.set(speed);
    	RobotMap.clampRight.set(speed);
    }
}

