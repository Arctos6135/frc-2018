package org.usfirst.frc.team6135.robot.triggers;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *	A Trigger that activates when a controller's POV is at a certain angle.
 */
public class POVTrigger extends Trigger {
	
	GenericHID controller;
	int angle;
	
	public static final int UP = 0;
	public static final int UPPER_RIGHT = 45;
	public static final int RIGHT = 90;
	public static final int LOWER_RIGHT = 135;
	public static final int DOWN = 180;
	public static final int LOWER_LEFT = 225;
	public static final int LEFT = 270;
	public static final int UPPER_LEFT = 315;
	public static final int CENTER = -1;
	
	public POVTrigger(GenericHID controller, int direction) {
		super();
		this.controller = controller;
		angle = direction;
	}

    public boolean get() {
        return controller.getPOV() == angle;
    }
}
