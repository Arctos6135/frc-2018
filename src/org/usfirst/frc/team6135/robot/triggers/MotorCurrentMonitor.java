package org.usfirst.frc.team6135.robot.triggers;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.InstantCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Constantly checks the PDP for the current pulled by the motors.
 *	If the current pulled gets too high {@code get()} will return true.
 */
public class MotorCurrentMonitor extends Trigger {

	boolean exceeded = false;
	long exceededAt = System.currentTimeMillis();
	
	public static final int[] PORTS = new int[] {
			1, 2, 3, 12, 13, 14,
	};
	
	public MotorCurrentMonitor() {
		super();
		this.whenActive(new InstantCommand() {
			@Override
			protected void initialize() {
				Robot.drive.getCurrentCommand().cancel();
			}
		});
	}
	
    public boolean get() {
    	SmartDashboard.putNumber("PDP Channel 1 Current", RobotMap.PDP.getCurrent(1));
    	SmartDashboard.putNumber("PDP Channel 12 Current", RobotMap.PDP.getCurrent(12));
    	SmartDashboard.putNumber("PDP Channel 2 Current", RobotMap.PDP.getCurrent(2));
    	SmartDashboard.putNumber("PDP Channel 13 Current", RobotMap.PDP.getCurrent(13));
    	if(!DriverStation.getInstance().isAutonomous())
    		return false;
    	
    	boolean result = false;
        for(int i = 0; i < PORTS.length; i ++) {
        	double current = RobotMap.PDP.getCurrent(PORTS[i]);
        	
        	if(current >= 30.0 && !exceeded) {
        		exceeded = true;
        		exceededAt = System.currentTimeMillis();
        	}
        	
        	if(current >= 40.0 && exceeded) {
        		if(System.currentTimeMillis() - exceededAt > 500) {
        			result = true;
        		}
        	}
        	else if(current >= 35.0 && exceeded) {
        		if(System.currentTimeMillis() - exceededAt > 1500) {
        			result = true;
        		}
        	}
        	else if(current >= 30.0 && exceeded) {
        		if(System.currentTimeMillis() - exceededAt > 3000) {
        			result = true;
        		}
        	}
        	else {
        		exceeded = false;
        		result = false;
        	}
        }
        
        return result;
    }
}
