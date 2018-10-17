package org.usfirst.frc.team6135.robot.misc;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

@FunctionalInterface
public interface Autonomous {
	public Command getCommand(Robot.GenericLocation location, PowerUpGameData gameData);
}
