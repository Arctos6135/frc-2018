package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.Clamp;
import org.usfirst.frc.team6135.robot.commands.ResetTestEncoder;
import org.usfirst.frc.team6135.robot.commands.TestMotor;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;

/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
	//// CREATING BUTTONS
	// One type of button is a joystick button which is any button on a
	//// joystick.
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
	
	public static Joystick xboxController;
	public static Joystick attachmentsController;
	public static JoystickButton testMotor;
	public static JoystickButton resetEncoder;
	public static JoystickButton runClamp;
	public static JoystickButton runClampOpposite;
	
	public OI() {
		//Port 0 is on the right of the programming laptop and port 1 is on the left.
		xboxController = new Joystick(0);
		attachmentsController = new Joystick(1);
		
		runClamp = new JoystickButton(attachmentsController, 3);
		runClamp.whenPressed(new Clamp(0.70));
		runClamp.whenReleased(new Clamp(0));
		runClampOpposite = new JoystickButton(attachmentsController, 4);
		runClampOpposite.whenPressed(new Clamp(-0.70));
		runClampOpposite.whenReleased(new Clamp(0));
	}
}
