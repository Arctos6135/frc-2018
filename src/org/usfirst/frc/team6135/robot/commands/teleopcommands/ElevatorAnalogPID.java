package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.PIDSubsystem;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *	Moves the elevator with a joystick. This command is the default for ElevatorPIDSubsystem since
 *	it not only moves the elevator, but also sets the PID target as well.
 */
public class ElevatorAnalogPID extends Command {

	static final double DEADZONE = 0.15;

	public static final double SPEED = 0.005;
	
	final PIDSubsystem elevatorSubsystem;
	
    public ElevatorAnalogPID() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.elevatorSubsystem);
    	//Ugly cast
    	//Avoids compilation error if elevatorSubsystem is not an instance of PIDSubsystem
    	//Only added to allow compilation when using a regular, non-PID elevator
    	elevatorSubsystem = (PIDSubsystem) ((Subsystem) Robot.elevatorSubsystem);
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.ELEVATOR);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		//Check if we are at the boundary
    		if((joystickVal < 0 && Robot.elevatorSubsystem.notAtTop()) || joystickVal > 0) {
    			elevatorSubsystem.setSetpointRelative(SPEED * joystickVal);
    		}
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	//No need to set motor movement to 0 here - the PID will take care of that
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    }
}
