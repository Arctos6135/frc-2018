package org.usfirst.frc.team6135.robot.commands.teleopcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	DO NOT USE UNLESS NECESSARY. THIS COMMAND IS UNTESTED AND BUG-PRONE.
 *	Handles the teleop control of the elevator, as well as release the wrist rope by a set amount to keep it from snapping.
 *	This is a backup only. If the gyro on the intake is functional, this class should not be used and instead WristAnalogAdjust
 *	should be used as the default command to the wrist.
 *	IMPORTANT: For this command to function properly, the default command for the wrist should be set to WristAnalogBackup
 */
public class ElevatorAnalogBackup extends Command {
	
	static final double DEADZONE = 0.15;
	static final double SPEED_MULTIPLIER = 1.0; //Subject to adjustments
	//Set to true if you wish to have the wrist rope TIGHTEN (to avoid slack) when the elevator is rising
	//Otherwise the wrist rope can only loosen whenever the elevator is going down.
	//Caution must be taken: if set to true there is a chance of the wrist rope snapping due to tension.
	static final boolean ALLOW_TIGHTENING = false;

    public ElevatorAnalogBackup() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.elevatorSubsystem);
		//Do not requires(Robot.wristSubsystem) here
		//Doing this will cause conflicts between this command and the wrist teleop command, and as they're both default commands, it will have unknown results
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	final double joystickVal = OI.attachmentsController.getRawAxis(OI.Controls.ELEVATOR);
    	if(Math.abs(joystickVal) > DEADZONE) {
    		//Check if we are at the boundary
			//Super messed up conditional. Reason unknown but it works.
    		if((joystickVal < 0 && Robot.elevatorSubsystem.topReached()) || joystickVal > 0) {
    			RobotMap.elevatorVictor.set(joystickVal * RobotMap.Speeds.ELEVATOR_SPEED);
				//Subject to change
				if(!ALLOW_TIGHTENING && joystickVal > 0)
					return;
				RobotMap.wristVictor.set(joystickVal * RobotMap.Speeds.ELEVATOR_SPEED * SPEED_MULTIPLIER);
    		}
    		else {
    			RobotMap.elevatorVictor.set(0);
    		}
    	}
    	else {
    		RobotMap.elevatorVictor.set(0);
    	}
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	RobotMap.elevatorVictor.set(0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	RobotMap.elevatorVictor.set(0);
    }
}
