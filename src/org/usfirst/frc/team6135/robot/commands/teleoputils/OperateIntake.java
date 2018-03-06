package org.usfirst.frc.team6135.robot.commands.teleoputils;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *	Sets the speed of the Robot's intake. A positive speed means to intake and a negative means to shoot out.
 *	The speed of the intake will not change until another command sets it
 */
public class OperateIntake extends InstantCommand {

	final double speed;
    public OperateIntake(double speed) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.intakeSubsystem);
        this.speed = speed;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.intakeSubsystem.setSpeed(speed);
    }

}
