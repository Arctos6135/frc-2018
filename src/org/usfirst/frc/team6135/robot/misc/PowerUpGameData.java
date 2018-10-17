package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.DriverStation;

public class PowerUpGameData {
	String data;
	public PowerUpGameData(String data) {
		this.data = data;
	}
	
	public static PowerUpGameData getGameDataFromDS() {
		return new PowerUpGameData(DriverStation.getInstance().getGameSpecificMessage().toUpperCase());
	}
	
	public Robot.GenericLocation getOurSwitchLocation() {
		return data.charAt(0) == 'L' ? Robot.GenericLocation.LEFT : Robot.GenericLocation.RIGHT;
	}
	public Robot.GenericLocation getScaleLocation() {
		return data.charAt(1) == 'L' ? Robot.GenericLocation.LEFT : Robot.GenericLocation.RIGHT;
	}
	public Robot.GenericLocation getTheirSwitchLocation() {
		return data.charAt(2) == 'L' ? Robot.GenericLocation.LEFT : Robot.GenericLocation.RIGHT;
	}
	
	public boolean isValid() {
		return data.length() == 3;
	}
}
