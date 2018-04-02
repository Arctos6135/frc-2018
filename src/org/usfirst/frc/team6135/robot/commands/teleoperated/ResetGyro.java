package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 *	Resets the gyroscope on the wrist of the Robot. 
 *	It is very important that when performing the reset, the wrist is at its lowest position.
 *	This command is for use if the gyro gains significant drift only.
 */
public class ResetGyro extends InstantCommand {

    public ResetGyro() {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.wristSubsystem);
    }

    // Called once when the command executes
    protected void initialize() {
    	Gyro gyro = Robot.wristSubsystem.getGyroSensor();
    	gyro.reset();
    }

}
