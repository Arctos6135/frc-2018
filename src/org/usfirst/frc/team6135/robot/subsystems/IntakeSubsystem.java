package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.IntakeAnalog;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Power Cube Intake subsystem
 */
public class IntakeSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	DoubleSolenoid.Value currentState;
	public IntakeSubsystem() {
		super();
		close();
	}
    public void initDefaultCommand() {
        setDefaultCommand(new IntakeAnalog());
    }
    
    public void setSpeed(double speed) {
    	RobotMap.intakeLeft.set(speed);
    	RobotMap.intakeRight.set(speed);
    }
    
    public void setLeft(double speed) {
    	RobotMap.intakeLeft.set(speed);
    }
    public void setRight(double speed) {
    	RobotMap.intakeRight.set(speed);
    }
    
    public void open() {
    	setPneumatics(DoubleSolenoid.Value.kForward);
    }
    public void close() {
    	setPneumatics(DoubleSolenoid.Value.kReverse);
    }
    public void setPneumatics(DoubleSolenoid.Value value) {
    	if(value == currentState) {
    		return;
    	}
    	RobotMap.intakeSolenoid.set(value);
    	currentState = value;
    }
}

