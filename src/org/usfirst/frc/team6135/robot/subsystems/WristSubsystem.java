package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.RobotMap;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.WristAnalog;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class WristSubsystem extends Subsystem {
	
	public static final int DIRECTION_DOWN = 1;
	public static final int DIRECTION_UP = -1;
	
	static int sign(double n) {
		return n > 0 ? 1 : -1;
	}

    // Initialize your subsystem here
    public WristSubsystem() {
    }
    
    public void setRaw(double speed) {
    	//If the speed is to go down, or there is still room to go up
    	if(notAtTop()) {
    		RobotMap.wristVictor.set(speed);
    	}
    	else {
    		if(sign(speed) == DIRECTION_DOWN)
    			RobotMap.wristVictor.set(speed);
    		else
    			RobotMap.wristVictor.set(0);
    	}
    }
    
    public boolean notAtTop() {
    	return RobotMap.wristSwitch.get();
    }

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new WristAnalog());
    }

}
