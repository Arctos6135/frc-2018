package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistance;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

/**
 *	Auto code that places a cube on the switch from the middle
 *	uses vision to calculate distance
 *     ------------------------------------------
 *	   |		|						|		|
 *	   |		|		Switch			|		|
 *	   |		|						|		|
 *	   ------------------------------------------
 *	 		^			 				^
 *			|-----------<o>-------------|
 *		   				 |
 *		   				 |  
 *		 			   Robot
 *					    <o>
 */
public class VisionAuto extends InstantCommand {

	int direction;
	
	public static final int DIRECTION_LEFT = 1;
	public static final int DIRECTION_RIGHT = -1;
	
    public VisionAuto(int switchDirection) {
        super();
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
        direction = switchDirection;
    }

    // Called once when the command executes
    protected void initialize() {
    	double theta1, theta2, d;
    	try {
			theta1 = Robot.getSwitchAngle();
		} catch (Robot.ImageException e) {
			(new PlaceCubeFromMiddle(direction)).start();
			return;
		}
    	Command moveForwardCommand = new DriveStraightDistance(RobotMap.ArenaDimensions.VISION_SAMPLING_DISTANCE, RobotMap.Speeds.AUTO_SPEED);
    	d = RobotMap.ArenaDimensions.VISION_SAMPLING_DISTANCE;
    	moveForwardCommand.start();
    	while(!moveForwardCommand.isCompleted());
    	try {
			theta2 = Robot.getSwitchAngle();
		} catch (Robot.ImageException e) {
			(new PlaceCubeFromMiddleBackup(direction, d)).start();
			return;
		}
    	
    	double s = (d * Math.sin(theta1)) / Math.sin(theta2 - theta1);
    	double xDist = Math.abs(Math.sin(theta2) * s);
    	double yDist = Math.cos(theta2) * s;
    	
    	Command turnCommand = new AutoTurn(90 * direction, RobotMap.Speeds.AUTO_SPEED);
    	turnCommand.start();
    	while(!turnCommand.isCompleted());
    	moveForwardCommand = new DriveStraightDistance(xDist, RobotMap.Speeds.AUTO_SPEED);
    	moveForwardCommand.start();
    	while(!moveForwardCommand.isCompleted());
    	turnCommand = new AutoTurn(-90 * direction, RobotMap.Speeds.AUTO_SPEED);
    	turnCommand.start();
    	while(!turnCommand.isCompleted());    
    	moveForwardCommand = new DriveStraightDistance(yDist, RobotMap.Speeds.AUTO_SPEED);
    	moveForwardCommand.start();
    	while(!moveForwardCommand.isCompleted());
    	Command dropCommand = new AutoIntake(1.5, -RobotMap.Speeds.AUTO_SPEED);
    	dropCommand.start();
    	while(!dropCommand.isCompleted());
    }
}
