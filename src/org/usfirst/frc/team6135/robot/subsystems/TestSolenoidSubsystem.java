package org.usfirst.frc.team6135.robot.subsystems;

import edu.wpi.first.wpilibj.command.Subsystem;

import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.DoubleSolenoid;

/**
 *
 */
public class TestSolenoidSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void set(DoubleSolenoid.Value value) {
    	RobotMap.testSolenoid.set(value);
    }
}

