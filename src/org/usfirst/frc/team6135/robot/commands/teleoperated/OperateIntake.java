package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *	Sets the speed of the Robot's intake. A positive speed means to intake and a negative means to shoot out.
 *	The speed of the intake will not change until another command sets it
 */
public class OperateIntake extends InstantCommand {

	double speed;
	DoubleSolenoid.Value direction = null;
	
	public static final DoubleSolenoid.Value OPEN = DoubleSolenoid.Value.kForward;
	public static final DoubleSolenoid.Value CLOSE = DoubleSolenoid.Value.kReverse;
	
    public OperateIntake(double speed) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.intakeSubsystem);
        this.speed = speed;
    }
    public OperateIntake(DoubleSolenoid.Value direction) {
    	super();
    	requires(Robot.intakeSubsystem);
    	this.direction = direction;
    }

    // Called once when the command executes
    protected void initialize() {
    	if(direction == null) {
    		Robot.intakeSubsystem.setSpeed(speed);
    	}
    	else {
    		Robot.intakeSubsystem.setPneumatics(direction);
    	}
    }

}
