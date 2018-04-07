package org.usfirst.frc.team6135.robot.commands.teleoperated;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.interfaces.Gyro;

/**
 *	Resets the gyroscope on the wrist of the Robot and sets the bias.
 */
public class ResetGyro extends InstantCommand {

	double bias;
	
	public ResetGyro() {
		this(0);
	}
	
    public ResetGyro(double bias) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.wristSubsystem);
        this.bias = bias;
    }

    // Called once when the command executes
    protected void initialize() {
    	Gyro gyro = Robot.wristSubsystem.getGyroSensor();
    	if(gyro != null){
    		gyro.reset();
    		Robot.wristSubsystem.setAngleBias(bias);
    	}
    }

}
