package org.usfirst.frc.team6135.robot.commands.autonomous;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *	Sets the angle of the wrist by calling its {@code setSetpoint()} method.
 */
public class SetWrist extends InstantCommand {
	
	double angle;

    public SetWrist(double angle) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.wristSubsystem);
        this.angle = angle;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.wristSubsystem.setSetpoint(angle);
    }

}
