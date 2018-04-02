package org.usfirst.frc.team6135.robot.commands.teleoperated;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *	Cancels one or multiple commands (if they're running)
 */
public class CancelOperation extends InstantCommand {

	Command[] cmds;
	@SafeVarargs
    public CancelOperation(Command... commands) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        cmds = commands;
    }

    // Called once when the command executes
    protected void initialize() {
    	for(Command c : cmds)
    		if(c.isRunning())
    			c.cancel();
    }

}
