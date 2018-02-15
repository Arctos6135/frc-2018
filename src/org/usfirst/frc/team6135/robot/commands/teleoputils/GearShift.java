package org.usfirst.frc.team6135.robot.commands.teleoputils;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class GearShift extends InstantCommand {
	
	Value val;
	
	//Dependent on how the pneumatics are wired and gear box configurations
	public static final Value GEAR_FAST = DoubleSolenoid.Value.kReverse;
	public static final Value GEAR_SLOW = DoubleSolenoid.Value.kForward;
	public static final Value GEAR_STOPSHIFT = DoubleSolenoid.Value.kOff;
	
    public GearShift(DoubleSolenoid.Value value) {
        super();
        requires(Robot.gearShiftSubsystem);
        val = value;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.gearShiftSubsystem.set(val);
    }

}
