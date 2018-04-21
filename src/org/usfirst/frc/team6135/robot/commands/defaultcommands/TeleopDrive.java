package org.usfirst.frc.team6135.robot.commands.defaultcommands;

import org.usfirst.frc.team6135.robot.OI;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Handles driving
 *	Called as a default command by DriveTrain
 */
public class TeleopDrive extends Command {
	
	private static final int X_AXIS = OI.Controls.LEFT_RIGHT;
    private static final int Y_AXIS = OI.Controls.FWD_REV;
    
    private static final double DEADZONE = 0.15;
    
    public static double rampBand = 0.05;
    boolean rampingOn = false;
    double prevLeft, prevRight;

    public TeleopDrive() {
        // Use requires() here to declare subsystem dependencies
        requires(Robot.drive);
    }
    
    public boolean isRamped() {
    	return rampingOn;
    }
    public void setRamping(boolean r) {
    	rampingOn = r;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	prevLeft = RobotMap.leftDriveTalon1.getMotorOutputPercent();
    	prevRight = RobotMap.rightDriveTalon1.getMotorOutputPercent();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	double x = Math.abs(OI.driveController.getRawAxis(X_AXIS))>DEADZONE?OI.driveController.getRawAxis(X_AXIS):0;
        double y = Math.abs(OI.driveController.getRawAxis(Y_AXIS))>DEADZONE?-OI.driveController.getRawAxis(Y_AXIS):0;
        double l = Math.max(-RobotMap.Speeds.DRIVE_SPEED, Math.min(RobotMap.Speeds.DRIVE_SPEED, y + x));//constrain to [-1,1]
        double r = Math.max(-RobotMap.Speeds.DRIVE_SPEED, Math.min(RobotMap.Speeds.DRIVE_SPEED, y - x));
        
        //Square the final values to smooth out driving
        //Math.copySign ensures the direction is not lost
        l = Math.copySign(l * l, l);
        r = Math.copySign(r * r, r);
        
        if(rampingOn) {
        	l = Math.max(prevLeft - rampBand, Math.min(prevLeft + rampBand, l));
        	r = Math.max(prevRight - rampBand, Math.min(prevRight + rampBand, r));
        }
        
        Robot.drive.setMotorsVBus(l, r);
        
        prevLeft = l;
        prevRight = r;
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
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
