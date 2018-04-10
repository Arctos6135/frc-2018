
package org.usfirst.frc.team6135.robot;

import java.util.Timer;

import org.usfirst.frc.team6135.robot.commands.autocommands.DrivePastBaseline;
import org.usfirst.frc.team6135.robot.commands.autocommands.MultiCubeAligned;
import org.usfirst.frc.team6135.robot.commands.autocommands.MultiCubeFromMiddle;
import org.usfirst.frc.team6135.robot.commands.autocommands.MultiCubeFromSide;
import org.usfirst.frc.team6135.robot.commands.autocommands.PlaceCubeAligned;
import org.usfirst.frc.team6135.robot.commands.autocommands.PlaceCubeFromMiddle;
import org.usfirst.frc.team6135.robot.commands.autocommands.PlaceCubeFromMiddleDiagonal;
import org.usfirst.frc.team6135.robot.commands.autocommands.PlaceCubeFromSide;
import org.usfirst.frc.team6135.robot.commands.autocommands.ScaleCubeOppositeSide;
import org.usfirst.frc.team6135.robot.commands.autocommands.ScaleCubeSameSide;
import org.usfirst.frc.team6135.robot.commands.autocommands.VisionAuto;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurnPID;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.BrakePID;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.misc.CameraCaptureTask;
import org.usfirst.frc.team6135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.GearShiftSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.IntakeSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.WristPIDSubsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
	
	//SUBSYSTEMS
	//public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	public static DriveTrain drive;
	public static IntakeSubsystem intakeSubsystem;
	public static GearShiftSubsystem gearShiftSubsystem;
	public static ElevatorSubsystem elevatorSubsystem;
	public static WristPIDSubsystem wristSubsystem;
	public static VisionSubsystem visionSubsystem;
	
	public static Alliance color;
	public static int station; //Starting position of robot
	public static String gameData;
	
	//These commands are combined with the alliance colour and switch location and used later
	//They are the options that are shown in the auto menu
	public static PlaceCubeFromMiddle placeCubeFromMiddle;
	public static PlaceCubeFromMiddleDiagonal placeCubeFromMiddleFast;
	public static PlaceCubeAligned placeCubeLeftSide, placeCubeRightSide;
	public static PlaceCubeFromSide placeCubeLeftSideOffset, placeCubeRightSideOffset;
	public static VisionAuto visionAuto;
	public static MultiCubeFromSide multiCubeLeftSide, multiCubeRightSide;
	public static MultiCubeAligned multiCubeLeftAligned, multiCubeRightAligned;
	public static MultiCubeFromMiddle multiCubeFromMiddle;
	public static ScaleCubeSameSide scaleSameSideLeft, scaleSameSideRight;
	
	//Autonomous command chooser
	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();
	
	//Camera recording timer task
	public static CameraCaptureTask captureTask;
	public static Timer captureTimer = new Timer();
	//Capture FPS
	static final int CAPTURE_FPS = 8;
	static final int CAPTURE_PERIOD = 1000 / CAPTURE_FPS;
	
	void putTunables() {
		//Output these values to the SmartDashboard for tuning
		SmartDashboard.putNumber("Wrist kP", WristPIDSubsystem.kP);
		SmartDashboard.putNumber("Wrist kI", WristPIDSubsystem.kI);
		SmartDashboard.putNumber("Wrist kD", WristPIDSubsystem.kD);
		SmartDashboard.putNumber("Brake kP", BrakePID.kP);
		SmartDashboard.putNumber("Brake kI", BrakePID.kI);
		SmartDashboard.putNumber("Brake kD", BrakePID.kD);
		SmartDashboard.putNumber("Drive kP", DriveStraightDistancePID.kP);
		SmartDashboard.putNumber("Drive kI", DriveStraightDistancePID.kI);
		SmartDashboard.putNumber("Drive kD", DriveStraightDistancePID.kD);
		SmartDashboard.putNumber("Turn kP", AutoTurnPID.kP);
		SmartDashboard.putNumber("Turn kI", AutoTurnPID.kI);
		SmartDashboard.putNumber("Turn kD", AutoTurnPID.kD);
	}
	void updateTunables() {
		//Read the tunable values and overwrite them
		WristPIDSubsystem.kP = SmartDashboard.getNumber("Wrist kP", WristPIDSubsystem.kP);
		WristPIDSubsystem.kI = SmartDashboard.getNumber("Wrist kI", WristPIDSubsystem.kI);
		WristPIDSubsystem.kD = SmartDashboard.getNumber("Wrist kD", WristPIDSubsystem.kD);
		BrakePID.kP = SmartDashboard.getNumber("Brake kP", BrakePID.kP);
		BrakePID.kI = SmartDashboard.getNumber("Brake kI", BrakePID.kI);
		BrakePID.kD = SmartDashboard.getNumber("Brake kD", BrakePID.kD);
		DriveStraightDistancePID.kP = SmartDashboard.getNumber("Drive kP", DriveStraightDistancePID.kP);
		DriveStraightDistancePID.kI = SmartDashboard.getNumber("Drive kI", DriveStraightDistancePID.kI);
		DriveStraightDistancePID.kD = SmartDashboard.getNumber("Drive kD", DriveStraightDistancePID.kD);
		AutoTurnPID.kP = SmartDashboard.getNumber("Turn kP", AutoTurnPID.kP);
		AutoTurnPID.kI = SmartDashboard.getNumber("Turn kI", AutoTurnPID.kI);
		AutoTurnPID.kD = SmartDashboard.getNumber("Turn kD", AutoTurnPID.kD);
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Initialize our subsystems
		RobotMap.init();
		drive = new DriveTrain();
		intakeSubsystem = new IntakeSubsystem();
		gearShiftSubsystem = new GearShiftSubsystem();
		elevatorSubsystem = new ElevatorSubsystem();
		wristSubsystem = new WristPIDSubsystem();
		//Already done
		//RobotMap.wristGyro.calibrate();
		//wristSubsystem.START_TIME = System.currentTimeMillis();
		
		//Get the team's colour and station number
		station = DriverStation.getInstance().getLocation();
		color = DriverStation.getInstance().getAlliance();
		
		//Initialize camera stream and vision subsystem
        visionSubsystem = new VisionSubsystem(CameraServer.getInstance().startAutomaticCapture());
        //Set camera config
        visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO); //For vision, change to Mode.VISION
        
        oi = new OI();
        //(new Thread(new TestingThread())).start();

        //Add commands into the autonomous command chooser
        //chooser.addObject("DriveStraightDistancePID", new DriveStraightDistancePID(60));
        //chooser.addObject("AutoTurnPID", new AutoTurnPID(90));
        //Direction doesn't matter
		placeCubeFromMiddle = new PlaceCubeFromMiddle(1);
		placeCubeFromMiddleFast = new PlaceCubeFromMiddleDiagonal(1);
		placeCubeLeftSide = new PlaceCubeAligned();
		placeCubeRightSide = new PlaceCubeAligned();
		placeCubeLeftSideOffset = new PlaceCubeFromSide(PlaceCubeFromSide.SIDE_LEFT);
		placeCubeRightSideOffset = new PlaceCubeFromSide(PlaceCubeFromSide.SIDE_RIGHT);
		scaleSameSideLeft = new ScaleCubeSameSide(ScaleCubeSameSide.SIDE_LEFT);
		scaleSameSideRight = new ScaleCubeSameSide(ScaleCubeSameSide.SIDE_RIGHT);
		visionAuto = new VisionAuto(VisionAuto.DIRECTION_LEFT);
		multiCubeLeftSide = new MultiCubeFromSide(MultiCubeFromSide.SIDE_LEFT);
		multiCubeRightSide = new MultiCubeFromSide(MultiCubeFromSide.SIDE_RIGHT);
		multiCubeLeftAligned = new MultiCubeAligned(MultiCubeAligned.SIDE_LEFT);
		multiCubeRightAligned = new MultiCubeAligned(MultiCubeAligned.SIDE_RIGHT);
		//Direction doesn't matter
		multiCubeFromMiddle = new MultiCubeFromMiddle(1);
		//chooser.addDefault("No Auto", null);
		chooser.addDefault("Drive Past Baseline (Better to use one of the commands below)", new DrivePastBaseline());
		chooser.addObject("Place Cube from left side", placeCubeLeftSideOffset);
		chooser.addObject("Place Cube from right side", placeCubeRightSideOffset);
		chooser.addObject("Place Cube (Aligned with switch): Left", placeCubeLeftSide);
		chooser.addObject("Place Cube (Aligned with switch): Right", placeCubeRightSide);
		chooser.addObject("Place Cube: Middle", placeCubeFromMiddle);
		chooser.addObject("Place Cube From Middle (FASTER)", placeCubeFromMiddleFast);
		chooser.addObject("Shoot Cube into Scale: Left", scaleSameSideLeft);
		chooser.addObject("Shoot Cube into Scale: Right", scaleSameSideRight);
		chooser.addObject("Multi-Cube from left side", multiCubeLeftSide);
		chooser.addObject("Multi-Cube from right side", multiCubeRightSide);
		chooser.addObject("Multi-Cube (Aligned with switch): Left", multiCubeLeftAligned);
		chooser.addObject("Multi-Cube (Aligned with switch): Right", multiCubeRightAligned);
		chooser.addObject("Multi-Cube from middle", multiCubeFromMiddle);
		//chooser.addObject("Place Cube With Vision: Middle", visionAuto);
		//Display the chooser
		SmartDashboard.putData("Auto mode", chooser);
		
		//Camera capture is paused during disabled
		captureTask = new CameraCaptureTask();
		captureTask.pause();
		//If camera capture is not desired, comment out this line
		//captureTimer.schedule(captureTask, 1000, CAPTURE_PERIOD);
		
		putTunables();
		//SmartDashboard.putData("Pause/Resume Camera Capture", new ToggleCameraCapture());
	}
	
	/**
	 * This function is called periodically during all robot modes.
	 * Code that needs to be run regardless of mode, such as the printing of information,
	 * can be put here.
	 */
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Left Encoder", RobotMap.leftEncoder.getDistance());
    	SmartDashboard.putNumber("Right Encoder", RobotMap.rightEncoder.getDistance());

    	SmartDashboard.putNumber("Wrist Gyro Reading", RobotMap.wristGyro.getAngle());
    	
    	SmartDashboard.putBoolean("Wrist PID is enabled", Robot.wristSubsystem.isEnabled());
    	
    	SmartDashboard.putBoolean("Elevator Top Switch", Robot.elevatorSubsystem.notAtTop());
    	SmartDashboard.putBoolean("Elevator Bottom Switch", Robot.elevatorSubsystem.notAtBottom());
    	SmartDashboard.putBoolean("Wrist Switch", Robot.wristSubsystem.notAtTop());
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		captureTask.pause();
		//Set drivetrain's default so there's no more braking
		Robot.drive.setDefaultCommand(new TeleopDrive());
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString code to get the auto name from the text box below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the
	 * chooser code above (like the commented example) or additional comparisons
	 * to the switch structure below with additional strings & commands.
	 */
	@Override
	public void autonomousInit() {
		
		//Retrieve the selected auto command
		autonomousCommand = chooser.getSelected();
		
		if(autonomousCommand != null) {
			gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase();
			if(gameData.length() > 0){
				//Depending on which side the alliance switch is on, some commands need to change
				//Check if command is a scale command
				if(autonomousCommand instanceof ScaleCubeSameSide) {
					//Check the second character of the game data for the direction of the alliance scale
					if(gameData.charAt(1) == 'L') {
						if(autonomousCommand == scaleSameSideLeft) {
							autonomousCommand.start();
						}
						else {
							new ScaleCubeOppositeSide(ScaleCubeOppositeSide.SIDE_LEFT).start();
							//(new DrivePastBaseLine()).start();
						}
					}
					else {
						if(autonomousCommand == scaleSameSideRight) {
							autonomousCommand.start();
						}
						else {
							new ScaleCubeOppositeSide(ScaleCubeOppositeSide.SIDE_RIGHT).start();
							//(new DrivePastBaseLine()).start();
						}
					}
				}
				else {
					//Check the first character of the game data for the direction of the alliance switch
					
					if(gameData.charAt(0) == 'L'){
						//If the alliance switch is on the left side
						if(autonomousCommand == placeCubeFromMiddle) {
							//If command is to place a cube from the middle
							(new PlaceCubeFromMiddle(PlaceCubeFromMiddle.DIRECTION_LEFT)).start();
						}
						else if(autonomousCommand == placeCubeFromMiddleFast){
							new PlaceCubeFromMiddleDiagonal(PlaceCubeFromMiddleDiagonal.DIRECTION_LEFT).start();
						}
						//Use == to check if they're the exact same object
						else if(autonomousCommand == placeCubeRightSide) {
							//If command is to place a cube from the right, give up placing the cube and
							//instead drive past the baseline
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
						}
						else if(autonomousCommand == placeCubeRightSideOffset) {
							(new DrivePastBaseline()).start();
						}
						else if(autonomousCommand == visionAuto) {
							(new VisionAuto(VisionAuto.DIRECTION_LEFT)).start();
						}
						else if(autonomousCommand == multiCubeRightSide) {
							(new DrivePastBaseline()).start();
						}
						else if(autonomousCommand == multiCubeRightAligned) {
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
						}
						else if(autonomousCommand == multiCubeFromMiddle) {
							(new MultiCubeFromMiddle(MultiCubeFromMiddle.DIRECTION_LEFT)).start();
						}
						else {
							autonomousCommand.start();
						}
					} 
					else {
						if(autonomousCommand == placeCubeFromMiddle) {
							(new PlaceCubeFromMiddle(PlaceCubeFromMiddle.DIRECTION_RIGHT)).start();
						}
						else if(autonomousCommand == placeCubeFromMiddleFast){
							new PlaceCubeFromMiddleDiagonal(PlaceCubeFromMiddleDiagonal.DIRECTION_RIGHT).start();
						}
						else if(autonomousCommand == placeCubeLeftSide) {
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
						}
						else if(autonomousCommand == placeCubeLeftSideOffset) {
							(new DrivePastBaseline()).start();
						}
						else if(autonomousCommand == visionAuto) {
							(new VisionAuto(VisionAuto.DIRECTION_RIGHT)).start();
						}
						else if(autonomousCommand == multiCubeLeftSide) {
							(new DrivePastBaseline()).start();
						}
						else if(autonomousCommand == multiCubeRightAligned) {
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
						}
						else if(autonomousCommand == multiCubeFromMiddle) {
							(new MultiCubeFromMiddle(MultiCubeFromMiddle.DIRECTION_RIGHT)).start();
						}
						else {
							autonomousCommand.start();
						}
					}
				}
			}
		}
		

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		//Set camera config
		visionSubsystem.setMode(VisionSubsystem.Mode.VISION);
		//Set motors to be in brake mode
		RobotMap.leftDriveTalon1.setNeutralMode(NeutralMode.Brake);
		RobotMap.leftDriveTalon2.setNeutralMode(NeutralMode.Brake);
		RobotMap.rightDriveTalon1.setNeutralMode(NeutralMode.Brake);
		RobotMap.rightDriveTalon2.setNeutralMode(NeutralMode.Brake);
		RobotMap.leftDriveVictor.setNeutralMode(NeutralMode.Brake);
		RobotMap.rightDriveVictor.setNeutralMode(NeutralMode.Brake);
		//Set the drivetrain's default command to enable braking
		Robot.drive.setDefaultCommand(new BrakePID());
		
		captureTask.resume();
		
		updateTunables();
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		//Set camera config
		visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO);
		//Set the drivetrain's default command to disable braking
		Robot.drive.setDefaultCommand(new TeleopDrive());
		RobotMap.leftDriveTalon1.setNeutralMode(NeutralMode.Coast);
		RobotMap.leftDriveTalon2.setNeutralMode(NeutralMode.Coast);
		RobotMap.rightDriveTalon1.setNeutralMode(NeutralMode.Coast);
		RobotMap.rightDriveTalon2.setNeutralMode(NeutralMode.Coast);
		RobotMap.leftDriveVictor.setNeutralMode(NeutralMode.Coast);
		RobotMap.rightDriveVictor.setNeutralMode(NeutralMode.Coast);
		
		captureTask.resume();
		
		updateTunables();
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
