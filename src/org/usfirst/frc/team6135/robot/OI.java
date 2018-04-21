package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.ElevatorAnalog;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.commands.teleoperated.GearShift;
import org.usfirst.frc.team6135.robot.commands.teleoperated.PrecisionToggle;
import org.usfirst.frc.team6135.robot.commands.teleoperated.ScalingPosition;
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
	 * 	<li>Y Button: Auto Power Cube align</li>
	 * 	<li>B Button: Cancel auto align</li>
	 * 	<li>A Button: Start/Stop autonomous recording</li>
	 * 	<li>Back Button: Toggle Training Wheels</li>
	 * 	<li>XBOX Button: Enable rocket booster</li>
	 * </ul>
	 * Attachments:
	 * <ul>
	 * 	<li>Left Analog Stick: Elevator</li>
	 * 	<li>Right Analog Stick: Tilt Wrist</li>
	 * 	<li>Left Trigger: Intake Out (Analog)</li>
	 * 	<li>Right Trigger: Intake In (Analog)</li>
	 * 	<li>D-Pad Up: Raise the elevator to the top</li>
	 * 	<li>D-Pad Down: Lower the elevator to the bottom</li>
	 * 	<li>Y Button: Raise the elevator and wrist to Scale position</li>
	 * 	<li>Left Bumper: Shoots out the cube at full speed for 1s</li>
	 * </ul>
	 */
	public static class Controls {
		public static final int FWD_REV = RobotMap.ControllerMap.LSTICK_Y_AXIS;
		public static final int LEFT_RIGHT = RobotMap.ControllerMap.RSTICK_X_AXIS;
		public static final int SLOW_GEAR = RobotMap.ControllerMap.LBUMPER;
		public static final int FAST_GEAR = RobotMap.ControllerMap.RBUMPER;
		public static final int AUTO_CUBE_ALIGN = RobotMap.ControllerMap.BUTTON_Y;
		public static final int PRECISION_TOGGLE = RobotMap.ControllerMap.BUTTON_X; //Precision toggle
		public static final int CANCEL_ALIGN = RobotMap.ControllerMap.BUTTON_B;
		public static final int RECORD_AUTO = RobotMap.ControllerMap.BUTTON_A;
		
		public static final int ELEVATOR = RobotMap.ControllerMap.LSTICK_Y_AXIS;
		public static final int WRIST = RobotMap.ControllerMap.RSTICK_Y_AXIS;
		public static final int INTAKE_IN = RobotMap.ControllerMap.RTRIGGER;
		public static final int INTAKE_OUT = RobotMap.ControllerMap.LTRIGGER;
		public static final int SCALE_POSITION = RobotMap.ControllerMap.BUTTON_Y;
		public static final int SHOOT_CUBE = RobotMap.ControllerMap.LBUMPER;
		//Note: Some buttons such as the Start button and the D-Pad do not have mappings.
		//Triggers are created for them to read their states and process them.
	}
	
	public static XboxController driveController;
	public static XboxController attachmentsController;
	
	public static JoystickButton gearShiftFast;
	public static JoystickButton gearShiftSlow;
	
	//public static JoystickButton autoCubeAlign;
	//public static JoystickButton cancelAlign;
	
	public static JoystickButton recordAuto;
	
	public static JoystickButton scalePosition;
	
	public static JoystickButton shootCube;
	
	public static JoystickButton precisionToggle;
	
	public OI() {
		driveController = new XboxController(1);
		attachmentsController = new XboxController(0);
		
		//Fast gear = right bumper
		gearShiftFast = new JoystickButton(driveController, Controls.FAST_GEAR);
		gearShiftSlow = new JoystickButton(driveController, Controls.SLOW_GEAR);
		//autoCubeAlign = new JoystickButton(driveController, Controls.AUTO_CUBE_ALIGN);
		//cancelAlign = new JoystickButton(driveController, Controls.CANCEL_ALIGN);
		precisionToggle = new JoystickButton(driveController, Controls.PRECISION_TOGGLE);
		recordAuto = new JoystickButton(driveController, Controls.RECORD_AUTO);
		scalePosition = new JoystickButton(attachmentsController, Controls.SCALE_POSITION);
		shootCube = new JoystickButton(attachmentsController, Controls.SHOOT_CUBE);
		
		recordAuto.whenPressed(new ToggleRecording());
		
		gearShiftFast.whenPressed(new GearShift(GearShift.GEAR_FAST));
		gearShiftSlow.whenPressed(new GearShift(GearShift.GEAR_SLOW));
		
		precisionToggle.whenPressed(new PrecisionToggle());
		
		scalePosition.whenPressed(new ScalingPosition());
		
		shootCube.whenPressed(new AutoIntake(1.5, -1.0));
		
		//Command autoCubeAlignCmd = new AutoCubeAlign(RobotMap.Speeds.AUTO_TURN_SPEED);
		//autoCubeAlign.whenPressed(autoCubeAlignCmd);
		//cancelAlign.whenPressed(new CancelOperation(autoCubeAlignCmd, autoSwitchAlignCmd));
		
		//Trigger for the Back button
		Trigger trainingWheels = new Trigger() {
			@Override
			public boolean get() {
				return driveController.getBackButtonPressed();
			}
		};
		trainingWheels.whenActive(new InstantCommand() {
			@Override
			protected void initialize() {
				TeleopDrive.setRamping(!TeleopDrive.isRamped());
			}
		});
		
		//Triggers for the D-Pad controls
		POVTrigger raiseElevator = new POVTrigger(attachmentsController, 0);
		POVTrigger lowerElevator = new POVTrigger(attachmentsController, 180);
		
		Command elevatorUp = new RaiseElevator(1.0);
		Command elevatorDown = new LowerElevator(1.0);
		
		raiseElevator.whenActive(elevatorUp);
		lowerElevator.whenActive(elevatorDown);
		
		//A trigger that will cancel the one-press elevator movements if the joystick has input
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
		
		//Motor current monitor is currently not working
		//whenActive() is already called by the constructor
		//@SuppressWarnings("unused")
		//Trigger motorCurrentMonitor = new MotorCurrentMonitor();
		
		//For DEBUGGING: Resets left and right encoder readings
		//Assigned to Start button on drive controller
		new Trigger() {
			@Override
			public boolean get() {
				return driveController.getStartButtonPressed();
			}
		}.whenActive(new InstantCommand() {
			@Override
			protected void initialize() {
				RobotMap.leftEncoder.reset();
				RobotMap.rightEncoder.reset();
			}
		});
	}
}
