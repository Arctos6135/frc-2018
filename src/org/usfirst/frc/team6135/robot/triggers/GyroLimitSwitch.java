package org.usfirst.frc.team6135.robot.triggers;

import org.usfirst.frc.team6135.robot.Robot;

import edu.wpi.first.wpilibj.buttons.Trigger;

/**
 *	A trigger that activates when the wrist activates the limit switch.
 */
public class GyroLimitSwitch extends Trigger {

    public boolean get() {
        return !Robot.wristSubsystem.notAtTop();
    }
}
