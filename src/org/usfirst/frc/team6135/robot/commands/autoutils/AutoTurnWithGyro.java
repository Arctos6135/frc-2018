package org.usfirst.frc.team6135.robot.commands.autoutils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;

/**
 *	Turns the robot a certain amount with a certain speed
 *	A positive value means counter-clockwise (left) turning. This is done to keep consistency with AutoTurnWithEncoders.
 *	Note that the brake/coast mechanics of the motor controllers may reduce precision.
 */
@Deprecated
public class AutoTurnWithGyro extends Command {

	final int degrees;
	final double speed;
	final int multiplier;
	
	//How close our gyro reading must get to our desired value before stopping
	//Note since that because the motors don't stop immediately on coast, even after this command ends,
	//the robot would still turn a few more degrees. Adjust this value if necessary for brake/coast modes.
	static final double THRESHOLD = 2.5;
	
    public AutoTurnWithGyro(final int degrees, final double speed) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.drive);
    	//Reverse the input to make positive mean counter-clockwise
    	//Maintains consistency with AutoTurnWithEncoders command
    	this.degrees = -degrees;
    	this.speed = speed;
    	this.multiplier = degrees > 0 ? -1 : 1;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	RobotMap.gyro.reset();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.drive.setMotorsVBus(multiplier * speed, -multiplier * speed);
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return Math.abs(Math.abs(degrees) - Math.abs(RobotMap.gyro.getAngle())) <= THRESHOLD;
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.drive.setMotorsVBus(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	Robot.drive.setMotorsVBus(0, 0);
    }
}
