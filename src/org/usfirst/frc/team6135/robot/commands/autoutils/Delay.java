package org.usfirst.frc.team6135.robot.commands.autoutils;

import edu.wpi.first.wpilibj.command.TimedCommand;

/**
 *	Simple command that delays an amount of time
 *	Used in autonomous command groups so that there is no sudden switch between different actions
 *	NOTE: Unlike other TimedCommands, the value passed in is in MILLISECONDS
 */
public class Delay extends TimedCommand {

    public Delay(double millis) {
        super(millis / 1000.0);
    }
}
