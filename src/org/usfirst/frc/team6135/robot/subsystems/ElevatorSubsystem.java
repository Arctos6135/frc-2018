package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.ElevatorAnalog;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Elevator subsystem
 */
public class ElevatorSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public void setSpeed(double speed) {
		RobotMap.elevatorVictor.set(speed);
	}
	/**
	 * Returns true if <b>not at top</b> (???)<br>
	 * Don't question if it works.
	 */
	public boolean notAtTop() {
		return RobotMap.elevatorTopSwitch.get();
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new ElevatorAnalog());
    }
}

