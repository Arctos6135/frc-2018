package org.usfirst.frc.team6135.robot.triggers;

import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *	A Trigger that activates when a controller's POV is at a certain angle.
 */
public class POVTrigger extends Trigger {
	
	GenericHID controller;
	int angle;
	
	public POVTrigger(GenericHID controller, int direction) {
		super();
		this.controller = controller;
		angle = direction;
	}

    public boolean get() {
        return controller.getPOV() == angle;
    }
}
