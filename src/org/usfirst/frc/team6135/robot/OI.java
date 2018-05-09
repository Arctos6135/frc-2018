package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.autonomous.AutoIntake;
import org.usfirst.frc.team6135.robot.commands.autonomous.LowerElevator;
import org.usfirst.frc.team6135.robot.commands.autonomous.RaiseElevator;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.ElevatorAnalog;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.commands.teleoperated.AutoCubeAlign;
import org.usfirst.frc.team6135.robot.commands.teleoperated.CancelOperation;
import org.usfirst.frc.team6135.robot.commands.teleoperated.GearShift;
import org.usfirst.frc.team6135.robot.commands.teleoperated.OperateIntake;
import org.usfirst.frc.team6135.robot.commands.teleoperated.PrecisionToggle;
import org.usfirst.frc.team6135.robot.commands.teleoperated.ScalingPosition;
import org.usfirst.frc.team6135.robot.commands.teleoperated.ToggleRecording;
import org.usfirst.frc.team6135.robot.triggers.POVTrigger;

import edu.wpi.first.wpilibj.GenericHID;
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
	 * <b>SCIENCE RENDEZVOUS CONTROLS</b><br><br>
	 * 
	 * Drive:
	 * <ul>
	 * 	<li>Left Analog Stick: Forwards/Backwards</li>
	 * 	<li>Right Analog Stick: Left/Right</li>
	 * 	<li>Left Bumper: Shift gear to slower configuration</li>
	 * 	<li>Right Bumper: Shift gear to faster configuration</li>
	 * 	<li>Y Button: Auto Power Cube align</li>
	 * 	<li>X Button: Toggle Precision Mode</li>
	 * 	<li>B Button: Cancel auto align</li>
	 * 	<li>A Button: Start/Stop autonomous recording</li>
	 * 	<li>Back Button: Toggle Training Wheels</li>
	 * 	<li>Start Button: Enter <em>Demo Mode</em>
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
	 * <br>
	 * <br>
	 * <b>DEMO MODE CONTROLS</b><br><br>
	 * 
	 * Drive:
	 * <ul>
	 * 	<li>Left Analog Stick: Forwards/Backwards</li>
	 * 	<li>Right Analog Stick: Left/Right</li>
	 * 	<li>Left Bumper: Shift gear to slower configuration</li>
	 * 	<li>Right Bumper: Shift gear to faster configuration</li>
	 * 	<li>X Button: Toggle Precision Mode</li>
	 * 	<li>B Button: Block/Unblock Attachments Controller</li>
	 * 	<li>Start Button: Exit <em>Demo Mode</em>
	 * 	<li>Back Button: Toggle Training Wheels</li>
	 * 	<li>XBOX Button: Enable rocket booster</li>
	 * </ul>
	 * Attachments:
	 * <ul>
	 * 	<li>D-Pad Up: Raise the elevator to the top</li>
	 * 	<li>D-Pad Down: Lower the elevator to the bottom</li>
	 * 	<li>Left Bumper: Intake Out</li>
	 * 	<li>Right Bumper: Intake In</li>
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
		
		public static final int DEMO_BLOCK_ATTACHMENTS = RobotMap.ControllerMap.BUTTON_B;
		
		public static final int DEMO_INTAKE_IN = RobotMap.ControllerMap.RBUMPER;
		public static final int DEMO_INTAKE_OUT = RobotMap.ControllerMap.LBUMPER;
	}
	
	public static boolean isInDemoMode = false;
	public static boolean attachmentsControllerBlocked = false;
	
	/**
	 * A class that represents a {@code JoystickButton} that is only used in non-demo mode.<br>
	 * <br>
	 * If you wish to create a button that is for both demo and non-demo modes, use {@code JoystickButton} instead.
	 * @see DemoButton
	 * @author Tyler
	 *
	 */
	static class NonDemoButton extends JoystickButton {

		public NonDemoButton(GenericHID joystick, int buttonNumber) {
			super(joystick, buttonNumber);
		}
		
		@Override
		public void whenPressed(Command cmd) {
			//Wrap the command inside another command that only executes if not in demo mode
			super.whenPressed(new InstantCommand() {
				@Override
				protected void initialize() {
					if(!OI.isInDemoMode)
						cmd.start();
				}
			});
		}
		
		@Override
		public void whenReleased(Command cmd) {
			super.whenReleased(new InstantCommand() {
				@Override
				protected void initialize() {
					if(!OI.isInDemoMode)
						cmd.start();
				}
			});
		}
	}
	
	/**
	 * A class that represents a {@code JoystickButton} that is used only in demo mode.
	 * @see NonDemoButton
	 * @author Tyler
	 *
	 */
	static class DemoButton extends JoystickButton {
		
		boolean isAttachment;
		
		public DemoButton(GenericHID joystick, int buttonNumber, boolean isAttachment) {
			super(joystick, buttonNumber);
			this.isAttachment = isAttachment;
		}
		
		@Override
		public void whenPressed(Command cmd) {
			//Wrap the command inside an InstantCommand that only runs if the OI is in demo mode,
			//Or if the button is on the attachments controller, only run if the OI is in demo mode and attachments controller is not disabled.
			super.whenPressed(new InstantCommand() {
				@Override
				protected void initialize() {
					if(isAttachment) {
						if(OI.isInDemoMode && !OI.attachmentsControllerBlocked)
							cmd.start();
					}
					else {
						if(OI.isInDemoMode)
							cmd.start();
					}
				}
			});
		}
	}
	
	public static XboxController driveController;
	public static XboxController attachmentsController;
	
	public static JoystickButton gearShiftFast;
	public static JoystickButton gearShiftSlow;
	
	public static NonDemoButton autoCubeAlign;
	public static NonDemoButton cancelAlign;
	
	public static NonDemoButton recordAuto;
	
	public static JoystickButton scalePosition;
	
	public static JoystickButton shootCube;
	
	public static JoystickButton precisionToggle;
	
	
	public static DemoButton demo_intake;
	public static DemoButton demo_outtake;
	public static DemoButton demo_blockAttachments;
	
	public OI() {
		driveController = new XboxController(0);
		attachmentsController = new XboxController(1);
		
		//Fast gear = right bumper
		gearShiftFast = new JoystickButton(driveController, Controls.FAST_GEAR);
		gearShiftSlow = new JoystickButton(driveController, Controls.SLOW_GEAR);
		autoCubeAlign = new NonDemoButton(driveController, Controls.AUTO_CUBE_ALIGN);
		cancelAlign = new NonDemoButton(driveController, Controls.CANCEL_ALIGN);
		precisionToggle = new JoystickButton(driveController, Controls.PRECISION_TOGGLE);
		recordAuto = new NonDemoButton(driveController, Controls.RECORD_AUTO);
		scalePosition = new JoystickButton(attachmentsController, Controls.SCALE_POSITION);
		shootCube = new JoystickButton(attachmentsController, Controls.SHOOT_CUBE);
		
		demo_blockAttachments = new DemoButton(driveController, Controls.DEMO_BLOCK_ATTACHMENTS, false);
		demo_intake = new DemoButton(attachmentsController, Controls.DEMO_INTAKE_IN, true);
		demo_outtake = new DemoButton(attachmentsController, Controls.DEMO_INTAKE_OUT, true);
		
		recordAuto.whenPressed(new ToggleRecording());
		
		gearShiftFast.whenPressed(new GearShift(GearShift.GEAR_FAST));
		gearShiftSlow.whenPressed(new GearShift(GearShift.GEAR_SLOW));
		
		precisionToggle.whenPressed(new PrecisionToggle());
		
		scalePosition.whenPressed(new ScalingPosition());
		
		shootCube.whenPressed(new AutoIntake(1.5, -1.0));
		
		Command autoCubeAlignCmd = new AutoCubeAlign(RobotMap.Speeds.AUTO_TURN_SPEED);
		autoCubeAlign.whenPressed(autoCubeAlignCmd);
		cancelAlign.whenPressed(new CancelOperation(autoCubeAlignCmd));
		
		demo_intake.whenPressed(new OperateIntake(0.7));
		demo_outtake.whenPressed(new OperateIntake(-0.7));
		demo_intake.whenReleased(new OperateIntake(0.0));
		demo_outtake.whenReleased(new OperateIntake(0.0));
		demo_blockAttachments.whenPressed(new InstantCommand() {
			@Override
			protected void initialize() {
				OI.attachmentsControllerBlocked = !OI.attachmentsControllerBlocked;
			}
		});
		Trigger toggleDemo = new Trigger() {
			@Override
			public boolean get() {
				return driveController.getStartButtonPressed();
			}
		};
		toggleDemo.whenActive(new InstantCommand() {
			@Override
			protected void initialize() {
				OI.isInDemoMode = !OI.isInDemoMode;
				OI.attachmentsControllerBlocked = false;
			}
		});
		
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
		//Make an anonymous class that extends POVTrigger
		POVTrigger raiseElevator = new POVTrigger(attachmentsController, POVTrigger.UP) {
			@Override
			public boolean get() {
				//Return the result from the POVTrigger's get() and take into consideration if the robot is in demo mode or not
				return super.get() && !OI.isInDemoMode;
			}
		};
		POVTrigger lowerElevator = new POVTrigger(attachmentsController, POVTrigger.DOWN) {
			@Override
			public boolean get() {
				return super.get() && !OI.isInDemoMode;
			}
		};
		Command elevatorUp = new RaiseElevator(1.0);
		Command elevatorDown = new LowerElevator(1.0);
		raiseElevator.whenActive(elevatorUp);
		lowerElevator.whenActive(elevatorDown);
		
		POVTrigger demo_raiseElevator = new POVTrigger(attachmentsController, POVTrigger.UP) {
			@Override
			public boolean get() {
				return super.get() && OI.isInDemoMode;
			}
		};
		POVTrigger demo_lowerElevator = new POVTrigger(attachmentsController, POVTrigger.DOWN) {
			@Override
			public boolean get() {
				return super.get() && OI.isInDemoMode;
			}
		};
		Command demo_elevatorUp = new RaiseElevator(0.6);
		Command demo_elevatorDown = new LowerElevator(0.6);
		demo_raiseElevator.whenActive(demo_elevatorUp);
		demo_lowerElevator.whenActive(demo_elevatorDown);
		
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
				
				if(demo_elevatorUp.isRunning() && !demo_elevatorUp.isCanceled())
					demo_elevatorUp.cancel();
				else if(demo_elevatorDown.isRunning() && !demo_elevatorDown.isCanceled())
					demo_elevatorDown.cancel();
			}
		});
		
		
	}
}
