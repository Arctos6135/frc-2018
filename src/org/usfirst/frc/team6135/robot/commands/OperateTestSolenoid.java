package org.usfirst.frc.team6135.robot.commands;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class OperateTestSolenoid extends InstantCommand {
	
	Value val;
    public OperateTestSolenoid(DoubleSolenoid.Value value) {
        super();
        requires(Robot.testSolenoidSubsystem);
        val = value;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.testSolenoidSubsystem.set(val);
    }

}
