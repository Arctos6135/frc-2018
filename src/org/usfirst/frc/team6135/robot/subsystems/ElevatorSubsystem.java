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
	
	public static final int DIRECTION_UP = -1;
	public static final int DIRECTION_DOWN = 1;
	
	static int sign(double n) {
		return n > 0 ? 1 : -1;
	}
	
	public void setSpeed(double speed) {
		//Check if we are at the edges
		if(sign(speed) == DIRECTION_UP && notAtTop())
			RobotMap.elevatorVictor.set(speed);
		else if(sign(speed) == DIRECTION_DOWN && notAtBottom())
			RobotMap.elevatorVictor.set(speed);
		else
			RobotMap.elevatorVictor.set(0);
	}
	/**
	 * Returns true if <b>not at top</b> (???)<br>
	 * Don't question if it works.
	 */
	public boolean notAtTop() {
		return RobotMap.elevatorTopSwitch.get();
	}
	public boolean notAtBottom() {
		return RobotMap.elevatorBottomSwitch.get();
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    	setDefaultCommand(new ElevatorAnalog());
    }
}

