package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.ElevatorAnalog;
import org.usfirst.frc.team6135.robot.commands.teleoperated.AutoCubeAlign;
import org.usfirst.frc.team6135.robot.commands.teleoperated.AutoSwitchAlign;
import org.usfirst.frc.team6135.robot.commands.teleoperated.CancelOperation;
import org.usfirst.frc.team6135.robot.commands.teleoperated.GearShift;
import org.usfirst.frc.team6135.robot.commands.teleoperated.ResetGyro;
import org.usfirst.frc.team6135.robot.commands.teleoperated.ToggleRecording;
import org.usfirst.frc.team6135.robot.triggers.POVTrigger;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.InstantCommand;

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
	
	/**
	 * <b>CONTROLS</b><br><br>
	 * 
	 * Drive:
	 * <ul>
	 * 	<li>Left Analog Stick: Forwards/Backwards</li>
	 * 	<li>Right Analog Stick: Left/Right</li>
	 * 	<li>Left Bumper: Shift gear to slower configuration</li>
	 * 	<li>Right Bumper: Shift gear to faster configuration</li>
	 * 	<li>X Button: Auto Switch align
	 * 		- Note: The robot will first attempt to locate the alliance colour, but if
	 * 		the team switch cannot be found, it will ignore the colour and try to find any switch at all.</li>
	 * 	<li>Y Button: Auto Power Cube align</li>
	 * 	<li>B Button: Cancel auto align (in case something goes terribly wrong)</li>
	 * 	<li>A Button: Start/Stop autonomous recording</li>
	 * 	<li>XBOX Button: Enable rocket booster</li>
	 * </ul>
	 * Attachments:
	 * <ul>
	 * 	<li>Left Analog Stick: Elevator</li>
	 * 	<li>Right Analog Stick: Tilt Wrist</li>
	 * 	<li>Left Trigger: Intake Out (Analog)</li>
	 * 	<li>Right Trigger: Intake In (Analog)</li>
	 * 	<li>B Button: The Emergency Button (Hold 2s, changes behavior of the wrist, in case of gyro issues)</li>
	 * 	<li>Start Button: Gyro reset (Use only if gyro drift gets too much, and wrist is flat.)</li>
	 * 	<li>D-Pad Up: Raise the elevator to the top</li>
	 * 	<li>D-Pad Down: Lower the elevator to the bottom</li>
	 * </ul>
	 */
	public static class Controls {
		public static final int FWD_REV = RobotMap.ControllerMap.LSTICK_Y_AXIS;
		public static final int LEFT_RIGHT = RobotMap.ControllerMap.RSTICK_X_AXIS;
		public static final int SLOW_GEAR = RobotMap.ControllerMap.LBUMPER;
		public static final int FAST_GEAR = RobotMap.ControllerMap.RBUMPER;
		public static final int AUTO_CUBE_ALIGN = RobotMap.ControllerMap.BUTTON_Y;
		public static final int AUTO_SWITCH_ALIGN = RobotMap.ControllerMap.BUTTON_X;
		public static final int CANCEL_ALIGN = RobotMap.ControllerMap.BUTTON_B;
		public static final int RECORD_AUTO = RobotMap.ControllerMap.BUTTON_A;
		
		public static final int ELEVATOR = RobotMap.ControllerMap.LSTICK_Y_AXIS;
		public static final int WRIST = RobotMap.ControllerMap.RSTICK_Y_AXIS;
		public static final int INTAKE_IN = RobotMap.ControllerMap.RTRIGGER;
		public static final int INTAKE_OUT = RobotMap.ControllerMap.LTRIGGER;
		public static final int EMERGENCY = RobotMap.ControllerMap.BUTTON_B;
		//Note: Some buttons such as the Start button and the D-Pad do not have mappings.
		//Triggers are created for them to read their states and process them.
	}
	
	public static XboxController driveController;
	public static XboxController attachmentsController;
	
	public static JoystickButton gearShiftFast;
	public static JoystickButton gearShiftSlow;
	
	public static JoystickButton autoCubeAlign;
	public static JoystickButton autoSwitchAlign;
	public static JoystickButton cancelAlign;
	
	public static JoystickButton recordAuto;
	
	//public static JoystickButton emergencyButton;
	
	//public static Trigger calibrateGyroTrigger;
	
	public OI() {
		driveController = new XboxController(0);
		attachmentsController = new XboxController(1);
		
		//Fast gear = right bumper
		gearShiftFast = new JoystickButton(driveController, Controls.FAST_GEAR);
		gearShiftSlow = new JoystickButton(driveController, Controls.SLOW_GEAR);
		autoCubeAlign = new JoystickButton(driveController, Controls.AUTO_CUBE_ALIGN);
		autoSwitchAlign = new JoystickButton(driveController, Controls.AUTO_SWITCH_ALIGN);
		cancelAlign = new JoystickButton(driveController, Controls.CANCEL_ALIGN);
		recordAuto = new JoystickButton(driveController, Controls.RECORD_AUTO);
		//emergencyButton = new JoystickButton(attachmentsController, Controls.EMERGENCY);
		
		recordAuto.whenPressed(new ToggleRecording());
		
		gearShiftFast.whenPressed(new GearShift(GearShift.GEAR_FAST));
		gearShiftSlow.whenPressed(new GearShift(GearShift.GEAR_SLOW));
		
		Command autoCubeAlignCmd = new AutoCubeAlign(RobotMap.Speeds.AUTO_TURN_SPEED); 
		Command autoSwitchAlignCmd = new AutoSwitchAlign(RobotMap.Speeds.AUTO_TURN_SPEED);
		
		autoCubeAlign.whenPressed(autoCubeAlignCmd);
		autoSwitchAlign.whenPressed(autoSwitchAlignCmd);
		cancelAlign.whenPressed(new CancelOperation(autoCubeAlignCmd, autoSwitchAlignCmd));
		
		//Command emergencyCmd = new EmergencySwitch();
		
		//emergencyButton.whenPressed(emergencyCmd);
		//emergencyButton.whenReleased(new CancelOperation(emergencyCmd));
		
		//A trigger has to be used instead since there's no mapping for the start and back buttons
		Trigger resetGyro = new Trigger() {
			@Override
			public boolean get() {
				return attachmentsController.getStartButtonPressed();
			}
		};
		resetGyro.whenActive(new ResetGyro());
		
		//Triggers for the D-Pad controls
		POVTrigger raiseElevator = new POVTrigger(attachmentsController, 0);
		POVTrigger lowerElevator = new POVTrigger(attachmentsController, 180);
		
		Command elevatorUp = new RaiseElevator(1.0);
		Command elevatorDown = new LowerElevator(1.0);
		
		raiseElevator.whenActive(elevatorUp);
		lowerElevator.whenActive(elevatorDown);
		
		Trigger cancelElevatorMovement = new Trigger() {
			@Override
			public boolean get() {
				return Math.abs(attachmentsController.getRawAxis(Controls.ELEVATOR)) > ElevatorAnalog.DEADZONE;
			}
		};
		
		cancelElevatorMovement.whenActive(new InstantCommand() {
			@Override
			protected void initialize() {
				if(elevatorUp.isRunning() && !elevatorUp.isCanceled())
					elevatorUp.cancel();
				else if(elevatorDown.isRunning() && !elevatorDown.isCanceled())
					elevatorDown.cancel();
			}
		});
		
		//whenActive() is already called by the constructor
		//@SuppressWarnings("unused")
		//Trigger motorCurrentMonitor = new MotorCurrentMonitor();
		
		//calibrateGyroTrigger = new GyroLimitSwitch();
		//calibrateGyroTrigger.whenActive(new ResetGyro(WristPIDSubsystem.LIMIT_SWITCH_ANGLE));
	}
}
