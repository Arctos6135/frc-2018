package org.usfirst.frc.team6135.robot.commands.teleoputils;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;
import org.usfirst.frc.team6135.robot.vision.VisionException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Uses a VisionSubsystem to find the Alliance Switch's angle offset from the robot,
 *	then starts a new command to automatically turn towards it and thus aligning the two.
 *
 *	This is an InstantCommand
 */
public class AutoSwitchAlign extends InstantCommand {

	protected final double speed;
    public AutoSwitchAlign(double speed) {
        super();
        requires(Robot.drive);
        requires(Robot.visionSubsystem);
        this.speed = speed;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.visionSubsystem.setMode(VisionSubsystem.Mode.VISION);
    	try {
			Thread.sleep(1000);
		}
    	catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	double angleRaw;
    	try {
    		//First try to look for the team colour
    		angleRaw = Robot.visionSubsystem.getSwitchAngle(Robot.color);
    	}
    	catch(VisionException e) {
    		try {
    			//If team colour cannot be found, look for the other colour
    			angleRaw = Robot.visionSubsystem.getSwitchAngle(
    					Robot.color.equals(DriverStation.Alliance.Red) ? DriverStation.Alliance.Blue : DriverStation.Alliance.Red);
    		}
    		catch(VisionException e1) {
    			//If nothing can be found, give up.
    			return;
    		}
    	}
    	catch(Exception e) {
    		SmartDashboard.putString("Error:", "Unexpected Exception in Vision: " + e.toString());
    		return;
    	}
    	int angle = (int) (-Math.round(Math.toDegrees(angleRaw)));
    	SmartDashboard.putNumber("Angle: ", angle);
    	(new AutoTurn(angle, speed)).start();
    	
    	Robot.visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO);
    }

}
