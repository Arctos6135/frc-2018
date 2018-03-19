package org.usfirst.frc.team6135.robot.commands.autocommands;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.Delay;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistanceEx;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;
import org.usfirst.frc.team6135.robot.vision.VisionException;

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
        requires(Robot.visionSubsystem);
        requires(Robot.drive);
        direction = switchDirection;
    }
    
    void execCmds(Command... commands) {
    	for(Command cmd : commands) {
    		cmd.start();
    		while(!cmd.isCompleted());
    	}
    }

    // Called once when the command executes
    protected void initialize() {
    	double theta1, theta2, d;
    	try {
			theta1 = Robot.visionSubsystem.getSwitchAngle(Robot.color);
		} catch (VisionException e) {
			(new PlaceCubeFromMiddle(direction)).start();
			return;
		}
    	Command moveForwardCommand = new DriveStraightDistanceEx(RobotMap.ArenaDimensions.VISION_SAMPLING_DISTANCE, RobotMap.Speeds.AUTO_SPEED);
    	d = RobotMap.ArenaDimensions.VISION_SAMPLING_DISTANCE;
    	execCmds(moveForwardCommand);
    	try {
			theta2 = Robot.visionSubsystem.getSwitchAngle(Robot.color);
		} catch (VisionException e) {
			(new PlaceCubeFromMiddleBackup(direction, d)).start();
			return;
		}
    	
    	double s = (d * Math.sin(theta1)) / Math.sin(theta2 - theta1);
    	double xDist = Math.abs(Math.sin(theta2) * s);
    	double yDist = Math.abs(Math.cos(theta2) * s);
    	
    	Command delay = new Delay(RobotMap.AUTO_DELAY);
    	execCmds(new AutoTurn(90 * direction, RobotMap.Speeds.AUTO_SPEED), delay,
    			new DriveStraightDistanceEx(xDist, RobotMap.Speeds.AUTO_SPEED), delay,
    			new AutoTurn(-90 * direction, RobotMap.Speeds.AUTO_SPEED), delay,
    			new DriveStraightDistanceEx(yDist, RobotMap.Speeds.AUTO_SPEED),
    			new AutoIntake(1.5, -RobotMap.Speeds.AUTO_INTAKE_SPEED));
    }
}
