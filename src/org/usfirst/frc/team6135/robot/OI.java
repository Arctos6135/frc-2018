package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.AutoCubeAlign;
import org.usfirst.frc.team6135.robot.commands.CancelOperation;
import org.usfirst.frc.team6135.robot.commands.teleoputils.GearShift;

//import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	// joystick.
	// You create one by telling it which joystick it's on and which button
	// number it is.
	// Joystick stick = new Joystick(port);
	// Button button = new JoystickButton(stick, buttonNumber);

	// There are a few additional built in buttons you can use. Additionally,
	// by subclassing Button you can create custom triggers and bind those to
	// commands the same as any other Button.

	//// TRIGGERING COMMANDS WITH BUTTONS
	// Once you have a button, it's trivial to bind it to a button in one of
	// three ways:

	// Start the command when the button is pressed and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenPressed(new ExampleCommand());

	// Run the command while the button is being held down and interrupt it once
	// the button is released.
	// button.whileHeld(new ExampleCommand());

	// Start the command when the button is released and let it run the command
	// until it is finished as determined by it's isFinished method.
	// button.whenReleased(new ExampleCommand());
	
	/*
	 * CONTROLS
	 * Drive:
	 * 	Left Analog Stick: Forwards/Backwards
	 * 	Right Analog Stick: Left/Right
	 * 	Left Bumper: Shift gear to slower configuration
	 * 	Right Bumper: Shift gear to faster configuration
	 * 	Y Button: Auto Power Cube align
	 * 	B Button: Cancel auto align (when processing takes too long)
	 * 	XBOX Button: Enable rocket booster
	 * Attachments:
	 * 	Left Analog Stick: Elevator (Max. speed = 50%)
	 * 	Right Analog Stick: Tilt Wrist (Max. speed = 30%, up/down)
	 * 	Left Trigger: Intake Out (Analog)
	 * 	Right Trigger: Intake In (Analog)
	 */
	
	public static Joystick driveController;
	public static Joystick attachmentsController;
	
	public static JoystickButton gearShiftFast;
	public static JoystickButton gearShiftSlow;
	
	public static JoystickButton autoCubeAlign;
	public static JoystickButton cancelAlign;
	
	public OI() {
		//Port 0 is on the right of the programming laptop and port 1 is on the left.
		driveController = new Joystick(0);
		attachmentsController = new Joystick(1);
		
		//Fast gear = right bumper
		gearShiftFast = new JoystickButton(driveController, RobotMap.ControllerMap.RBUMPER);
		gearShiftSlow = new JoystickButton(driveController, RobotMap.ControllerMap.LBUMPER);
		gearShiftFast.whenPressed(new GearShift(GearShift.GEAR_FAST));
		gearShiftSlow.whenPressed(new GearShift(GearShift.GEAR_SLOW));
		gearShiftFast.whenReleased(new GearShift(GearShift.GEAR_STOPSHIFT));
		gearShiftSlow.whenReleased(new GearShift(GearShift.GEAR_STOPSHIFT));
		
		autoCubeAlign = new JoystickButton(driveController, RobotMap.ControllerMap.BUTTON_Y);
		Command autoAlignCmd = new AutoCubeAlign(RobotMap.Speeds.AUTO_SPEED); 
		autoCubeAlign.whenPressed(autoAlignCmd);
		cancelAlign = new JoystickButton(driveController, RobotMap.ControllerMap.BUTTON_B);
		cancelAlign.whenPressed(new CancelOperation(autoAlignCmd));
	}
}
