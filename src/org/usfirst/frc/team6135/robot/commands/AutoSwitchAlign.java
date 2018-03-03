package org.usfirst.frc.team6135.robot.commands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;

import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
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
    		angleRaw = Robot.visionSubsystem.getSwitchAngle(Robot.color);
    	}
    	catch(VisionSubsystem.VisionException e) {
    		return;
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
