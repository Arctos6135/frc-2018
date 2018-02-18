package org.usfirst.frc.team6135.robot.commands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;

import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *
 */
public class AutoCubeAlign extends InstantCommand {

	double speed;
    public AutoCubeAlign(double speed) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        requires(Robot.drive);
        this.speed = speed;
    }

    // Called once when the command executes
    protected void initialize() {
    	Robot.camera.setBrightness(100);
    	Robot.camera.setExposureManual(20);
    	try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
    	
    	double angleRaw;
    	try {
    		angleRaw = Robot.getCubeAngle();
    	}
    	catch(Robot.VisionException e) {
    		return;
    	}
    	int angle = (int) (-Math.round(angleRaw));
    	(new AutoTurn(angle, speed)).start();
    	
    	Robot.camera.setBrightness(Robot.cameraInitBrightness);
		Robot.camera.setExposureAuto();
    }

}
