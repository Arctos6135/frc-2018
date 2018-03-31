package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.teleopcommands.WristAnalogAdjust;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Intake tilt (wrist) subsystem
 */
@SuppressWarnings("unused")
public class WristSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	public static final double DRIFT_DEG_PER_SECOND = 2.0 / 43000.0;
	public long START_TIME;

	public void setSpeed(double speed) {
		RobotMap.wristVictor.set(Math.max(Math.min(speed, 1.0), -1.0));
	}
	public double getGyro() {
		//SmartDashboard.putNumber("Adjustment", (System.currentTimeMillis() - START_TIME) * DRIFT_DEG_PER_SECOND);
		//return RobotMap.wristGyro.getAngle() - (System.currentTimeMillis() - START_TIME) * DRIFT_DEG_PER_SECOND;
		return RobotMap.wristGyro.getAngle();
	}
	
    public void initDefaultCommand() {
    	setDefaultCommand(new WristAnalogAdjust());
    }
}

