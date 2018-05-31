package org.usfirst.frc.team6135.robot.commands.debug;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class SetMotors extends InstantCommand {

	double l, r;
	
    public SetMotors(double l, double r) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.drive);
        this.l = l;
        this.r = r;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.drive.setMotorsVBus(l, r);
    }

}
