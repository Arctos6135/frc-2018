package org.usfirst.frc.team6135.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class CancelOperation extends InstantCommand {

	Command cmd;
    public CancelOperation(Command command) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        cmd = command;
    }

    // Called once when the command executes
    protected void initialize() {
    	cmd.cancel();
    }

}
