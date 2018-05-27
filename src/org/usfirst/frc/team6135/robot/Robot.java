
package org.usfirst.frc.team6135.robot;

import java.io.IOException;
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
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistancePID;
import org.usfirst.frc.team6135.robot.commands.autonomous.FollowTrajectory;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.misc.AutoPlayback;
import org.usfirst.frc.team6135.robot.misc.AutoRecord;
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
	SendableChooser<String> recordedAutoChooser = new SendableChooser<>();
	
	//Camera recording timer task
	public static CameraCaptureTask captureTask;
	public static Timer captureTimer = new Timer();
	//Capture FPS
	static final int CAPTURE_FPS = 8;
	static final int CAPTURE_PERIOD = 1000 / CAPTURE_FPS;
	
	/*
	 * RECORDING AUTOS AND PLAYBACK
	 */
	
	//Prefix for recorded autos
	public static final String CSV_FILE_PREFIX = "/home/lvuser/auto";

	/* Recorded auto ids
	 * 
	 * Default: Baseline
	 * 
	 * Structure:
	 * prefix + (optional) direction + side + (optional)suffixes
	 * 
	 * Prefixes:
	 * s = Switch
	 * S = scale
	 * N = multi-cube
	 * 
	 * Side:
	 * L/R = left/right side or direction
	 * M = middle
	 * 
	 * Suffixes:
	 * A = aligned
	 * O = opposite
	 * 
	 */
	public static final String BASELINE = "B";
	
	public static final String SWITCH_LEFT = "sL"; //Record
	public static final String SWITCH_RIGHT = "sR"; //Record
	public static final String SWITCH_ALIGNED_LEFT = "sLA";
	public static final String SWITCH_ALIGNED_RIGHT = "sRA";
	public static final String SWITCH_ALIGNED = "sA"; //Record
	public static final String SWITCH_MIDDLE = "sM";
	public static final String SWITCH_MIDDLE_LEFT = "sLM"; //Record
	public static final String SWITCH_MIDDLE_RIGHT = "sRM"; //Record
	
	public static final String SCALE_LEFT = "SL"; //Record
	public static final String SCALE_LEFT_OPPOSITE = "SLO"; //Record
	public static final String SCALE_RIGHT = "SR"; //Record
	public static final String SCALE_RIGHT_OPPOSITE = "SRO"; //Record
	
	public static final String MULTI_LEFT = "NL";
	public static final String MULTI_RIGHT = "NR";
	public static final String MULTI_ALIGNED_LEFT = "NLA";
	public static final String MULTI_ALIGNED_RIGHT = "NRA";
	public static final String MULTI_MIDDLE = "NM";
	public static final String MULTI_MIDDLE_LEFT = "NLM";
	public static final String MULTI_MIDDLE_RIGHT = "NLM";
	
	public static final String CUSTOM = "$CUSTOM";
	
	//Toggle using recorded autos
	//Must be changed in code
	//Playback
	public static boolean useRecordedAutos = false;
	public static String selectedAuto = null;
	public static String customAutoFileName = null;
	
	//Recording
	public static String recordingString;
	public static boolean recording = false;
	public static boolean doneRecording = true;
	
	AutoPlayback player;
	AutoRecord recorder;
	
	void putTunables() {
		//Output these values to the SmartDashboard for tuning
<<<<<<< HEAD
		//SmartDashboard.putNumber("Wrist kP", WristPIDSubsystem.kP);
		//SmartDashboard.putNumber("Wrist kI", WristPIDSubsystem.kI);
		//SmartDashboard.putNumber("Wrist kD", WristPIDSubsystem.kD);
		/*SmartDashboard.putNumber("Brake kP", BrakePID.kP);
		SmartDashboard.putNumber("Brake kI", BrakePID.kI);
		SmartDashboard.putNumber("Brake kD", BrakePID.kD);
=======
>>>>>>> 02ff91e... Archive unused BrakePID and remove related code
		SmartDashboard.putNumber("Drive kP", DriveStraightDistancePID.kP);
		SmartDashboard.putNumber("Drive kI", DriveStraightDistancePID.kI);
		SmartDashboard.putNumber("Drive kD", DriveStraightDistancePID.kD);
		SmartDashboard.putNumber("Turn kP", AutoTurnPID.kP);
		SmartDashboard.putNumber("Turn kI", AutoTurnPID.kI);
		SmartDashboard.putNumber("Turn kD", AutoTurnPID.kD);
		SmartDashboard.putNumber("TMP Drive kP", DriveStraightDistanceTMP.kP);
		SmartDashboard.putNumber("TMP Drive kD", DriveStraightDistanceTMP.kD);
		SmartDashboard.putNumber("TMP Drive kV", DriveStraightDistanceTMP.kV);
		SmartDashboard.putNumber("TMP Drive kA", DriveStraightDistanceTMP.kA);*/
		SmartDashboard.putNumber("Path Follower kP", FollowTrajectory.kP);
		SmartDashboard.putNumber("Path Follower kD", FollowTrajectory.kD);
		SmartDashboard.putNumber("Path Follower kV", FollowTrajectory.kV);
		SmartDashboard.putNumber("Path Follower kA", FollowTrajectory.kA);
		SmartDashboard.putNumber("Teleop Drive Ramp Band", TeleopDrive.rampBand);
		
		SmartDashboard.putString("Auto Recording Save Name", "");
		SmartDashboard.putString("Auto Playback File Name", "");
	}
	void updateTunables() {
		//Read the tunable values and overwrite them
<<<<<<< HEAD
		//WristPIDSubsystem.kP = SmartDashboard.getNumber("Wrist kP", WristPIDSubsystem.kP);
		//WristPIDSubsystem.kI = SmartDashboard.getNumber("Wrist kI", WristPIDSubsystem.kI);
		//WristPIDSubsystem.kD = SmartDashboard.getNumber("Wrist kD", WristPIDSubsystem.kD);
		/*BrakePID.kP = SmartDashboard.getNumber("Brake kP", BrakePID.kP);
		BrakePID.kI = SmartDashboard.getNumber("Brake kI", BrakePID.kI);
		BrakePID.kD = SmartDashboard.getNumber("Brake kD", BrakePID.kD);
=======
>>>>>>> 02ff91e... Archive unused BrakePID and remove related code
		DriveStraightDistancePID.kP = SmartDashboard.getNumber("Drive kP", DriveStraightDistancePID.kP);
		DriveStraightDistancePID.kI = SmartDashboard.getNumber("Drive kI", DriveStraightDistancePID.kI);
		DriveStraightDistancePID.kD = SmartDashboard.getNumber("Drive kD", DriveStraightDistancePID.kD);
		AutoTurnPID.kP = SmartDashboard.getNumber("Turn kP", AutoTurnPID.kP);
		AutoTurnPID.kI = SmartDashboard.getNumber("Turn kI", AutoTurnPID.kI);
		AutoTurnPID.kD = SmartDashboard.getNumber("Turn kD", AutoTurnPID.kD);
		DriveStraightDistanceTMP.kP = SmartDashboard.getNumber("TMP Drive kP", DriveStraightDistanceTMP.kP);
		DriveStraightDistanceTMP.kD = SmartDashboard.getNumber("TMP Drive kD", DriveStraightDistanceTMP.kD);
		DriveStraightDistanceTMP.kV = SmartDashboard.getNumber("TMP Drive kV", DriveStraightDistanceTMP.kV);
		DriveStraightDistanceTMP.kA = SmartDashboard.getNumber("TMP Drive kA", DriveStraightDistanceTMP.kA);*/
		FollowTrajectory.kP = SmartDashboard.getNumber("Path Follower kP", FollowTrajectory.kP);
		FollowTrajectory.kD = SmartDashboard.getNumber("Path Follower kD", FollowTrajectory.kD);
		FollowTrajectory.kV = SmartDashboard.getNumber("Path Follower kV", FollowTrajectory.kV);
		FollowTrajectory.kA = SmartDashboard.getNumber("Path Follower kA", FollowTrajectory.kA);
		TeleopDrive.rampBand = SmartDashboard.getNumber("Teleop Drive Ramp Band", TeleopDrive.rampBand);
		
		String autoRecordingName;
		if((autoRecordingName = SmartDashboard.getString("Auto Recording Save Name", "")).length() > 0)
			recordingString = CSV_FILE_PREFIX + autoRecordingName + ".csv";
		else
			recordingString = null;
		
		String autoPlaybackName;
		if((autoPlaybackName = SmartDashboard.getString("Auto Playback File Name", "")).length() > 0)
			customAutoFileName = CSV_FILE_PREFIX + autoPlaybackName + ".csv";
		else
			customAutoFileName = null;
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
		
		//Get the team's colour and station number
		station = DriverStation.getInstance().getLocation();
		color = DriverStation.getInstance().getAlliance();
		
		//Initialize camera stream and vision subsystem
        visionSubsystem = new VisionSubsystem(CameraServer.getInstance().startAutomaticCapture());
        //Set camera config
        visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO); //For vision, change to Mode.VISION
        
        oi = new OI();
        
        if(useRecordedAutos) {
        	//Recorded autos
            initRecordedAutoChooser();
        } else {
        	//Pre-written autos
        	initAutoChooser();
        }
		
		//Camera capture is paused during disabled
		captureTask = new CameraCaptureTask();
		captureTask.pause();
		//If camera capture is not desired, comment out this line
		//captureTimer.schedule(captureTask, 1000, CAPTURE_PERIOD);
		
		putTunables();
		//SmartDashboard.putData("Pause/Resume Camera Capture", new ToggleCameraCapture());
	}
	public void initAutoChooser() {
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
		chooser.addObject("Place Cube: Middle", placeCubeFromMiddleFast);
		//chooser.addObject("Place Cube From Middle (FASTER)", placeCubeFromMiddleFast);
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
	}
	
	public void initRecordedAutoChooser() {
		recordedAutoChooser.addDefault("Drive Past Baseline (Better to use one of the commands below)", BASELINE);
		recordedAutoChooser.addObject("Place Cube from left side", SWITCH_LEFT);
		recordedAutoChooser.addObject("Place Cube from right side", SWITCH_RIGHT);
		recordedAutoChooser.addObject("Place Cube (Aligned with switch): Left", SWITCH_ALIGNED_LEFT);
		recordedAutoChooser.addObject("Place Cube (Aligned with switch): Right", SWITCH_ALIGNED_RIGHT);
		recordedAutoChooser.addObject("Place Cube From Middle", SWITCH_MIDDLE);
		recordedAutoChooser.addObject("Shoot Cube into Scale: Left", SCALE_LEFT);
		recordedAutoChooser.addObject("Shoot Cube into Scale: Right", SCALE_RIGHT);
		recordedAutoChooser.addObject("Multi-Cube from left side", MULTI_LEFT);
		recordedAutoChooser.addObject("Multi-Cube from right side", MULTI_RIGHT);
		recordedAutoChooser.addObject("Multi-Cube (Aligned with switch): Left", MULTI_ALIGNED_LEFT);
		recordedAutoChooser.addObject("Multi-Cube (Aligned with switch): Right", MULTI_ALIGNED_RIGHT);
		recordedAutoChooser.addObject("Multi-Cube from middle", MULTI_MIDDLE);
		recordedAutoChooser.addObject("Custom File", CUSTOM);
		
		SmartDashboard.putData("Auto mode (Recorded)", recordedAutoChooser);
	}
	/**
	 * This function is called periodically during all robot modes.
	 * Code that needs to be run regardless of mode, such as the printing of information,
	 * can be put here.
	 */
	@Override
	public void robotPeriodic() {
		SmartDashboard.putNumber("Left Distance", Robot.drive.getLeftDistance());
    	SmartDashboard.putNumber("Right Distance", Robot.drive.getRightDistance());
    	SmartDashboard.putNumber("Left Speed", Robot.drive.getLeftSpeed());
    	SmartDashboard.putNumber("Right Speed", Robot.drive.getRightSpeed());
    	double[] accel = Robot.drive.getAccelerations();
    	SmartDashboard.putNumber("Left Acceleration", accel[0]);
    	SmartDashboard.putNumber("Right Acceleration", accel[1]);
    	
    	SmartDashboard.putBoolean("Elevator Top Switch", Robot.elevatorSubsystem.notAtTop());
    	SmartDashboard.putBoolean("Elevator Bottom Switch", Robot.elevatorSubsystem.notAtBottom());
    	SmartDashboard.putBoolean("Wrist Switch", Robot.wristSubsystem.notAtTop());
    	
    	SmartDashboard.putBoolean("Demo Mode", OI.isInDemoMode);
    	SmartDashboard.putBoolean("Demo Mode Operator Enabled", !OI.attachmentsControllerBlocked);
    	SmartDashboard.putBoolean("Drive Ramping", TeleopDrive.isRamped());
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		captureTask.pause();
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
		
		if(useRecordedAutos) {
			String macroAuto = recordedAutoChooser.getSelected();
			setUpMacroAutos(macroAuto);
			if(useRecordedAutos) {
				try {
		    		 player = new AutoPlayback(selectedAuto);
				} catch (Exception e){
					e.printStackTrace();
				}
				RobotMap.leftDriveTalon1.setNeutralMode(NeutralMode.Coast);
				RobotMap.leftDriveTalon2.setNeutralMode(NeutralMode.Coast);
				RobotMap.rightDriveTalon1.setNeutralMode(NeutralMode.Coast);
				RobotMap.rightDriveTalon2.setNeutralMode(NeutralMode.Coast);
				RobotMap.leftDriveVictor.setNeutralMode(NeutralMode.Coast);
				RobotMap.rightDriveVictor.setNeutralMode(NeutralMode.Coast);
			}
		} else {
			autonomousCommand = chooser.getSelected();
			runSetAutos(autonomousCommand);
			//Set motors to be in brake mode
			RobotMap.leftDriveTalon1.setNeutralMode(NeutralMode.Brake);
			RobotMap.leftDriveTalon2.setNeutralMode(NeutralMode.Brake);
			RobotMap.rightDriveTalon1.setNeutralMode(NeutralMode.Brake);
			RobotMap.rightDriveTalon2.setNeutralMode(NeutralMode.Brake);
			RobotMap.leftDriveVictor.setNeutralMode(NeutralMode.Brake);
			RobotMap.rightDriveVictor.setNeutralMode(NeutralMode.Brake);
		}

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		//Set camera config
		visionSubsystem.setMode(VisionSubsystem.Mode.VISION);
		
		captureTask.resume();
		
		updateTunables();
	}
	public void setUpMacroAutos(String macroAuto) {
		if(macroAuto != null) {
			//If a the "Custom" option is chosen, the auto to run is just the one given in the text box
			if(macroAuto.equals(CUSTOM)) {
				selectedAuto = customAutoFileName;
				return;
			}
			gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase();
			if(gameData.length() > 0){
				//Depending on which side the alliance switch is on, some commands need to change
				//Check if command is a scale command
				if(macroAuto.equals(BASELINE)) {
					new DrivePastBaseline().start();
					useRecordedAutos = false;
				} else if(macroAuto.startsWith("S")) {
					//Check the second character of the game data for the direction of the alliance scale
					if(gameData.charAt(1) == 'L') {
						if(macroAuto.charAt(1) == 'L') {
							selectedAuto = CSV_FILE_PREFIX + SCALE_LEFT + ".csv";
						} else {
							selectedAuto = CSV_FILE_PREFIX + SCALE_LEFT_OPPOSITE + ".csv";
						}
					} else {
						if(macroAuto.charAt(1) == 'R') {
							selectedAuto = CSV_FILE_PREFIX + SCALE_RIGHT + ".csv";
						} else {
							selectedAuto = CSV_FILE_PREFIX + SCALE_RIGHT_OPPOSITE + ".csv";
						}
					}
				} else {
					//Check the first character of the game data for the direction of the alliance switch
					
					if(gameData.charAt(0) == 'L'){
						//If the alliance switch is on the left side
						if(macroAuto.equals(SWITCH_MIDDLE)) {
							selectedAuto = CSV_FILE_PREFIX + SWITCH_MIDDLE_LEFT + ".csv";
						} else if(macroAuto.equals(SWITCH_ALIGNED_RIGHT)) {
							//If command is to place a cube from the right, give up placing the cube and
							//instead drive past the baseline
							new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(SWITCH_RIGHT)) {
							new DrivePastBaseline().start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(SWITCH_ALIGNED_LEFT)) {
							selectedAuto = CSV_FILE_PREFIX + SWITCH_ALIGNED + ".csv";
						} else if(macroAuto.equals(MULTI_RIGHT)) {
							(new DrivePastBaseline()).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(MULTI_ALIGNED_RIGHT)) {
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(MULTI_MIDDLE)) {
							selectedAuto = CSV_FILE_PREFIX + MULTI_MIDDLE_LEFT + ".csv";
						} else {
							selectedAuto = CSV_FILE_PREFIX + macroAuto + ".csv";
						}
					} else {
						if(macroAuto.equals(SWITCH_MIDDLE)) {
							selectedAuto = CSV_FILE_PREFIX + SWITCH_MIDDLE_RIGHT + ".csv";
						} else if(macroAuto.equals(SWITCH_ALIGNED_LEFT)) {
							//If command is to place a cube from the right, give up placing the cube and
							//instead drive past the baseline
							new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(SWITCH_ALIGNED_RIGHT)) {
							selectedAuto = CSV_FILE_PREFIX + SWITCH_ALIGNED + ".csv";
						} else if(macroAuto.equals(SWITCH_LEFT)) {
							new DrivePastBaseline().start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(MULTI_LEFT)) {
							(new DrivePastBaseline()).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(MULTI_ALIGNED_LEFT)) {
							(new DriveStraightDistancePID(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
							useRecordedAutos = false;
						} else if(macroAuto.equals(MULTI_MIDDLE)) {
							selectedAuto = CSV_FILE_PREFIX + MULTI_MIDDLE_RIGHT + ".csv";
						} else {
							selectedAuto = CSV_FILE_PREFIX + macroAuto + ".csv";
						}
					}
				}
			}
		}
	}
	
	public void runSetAutos(Command autonomousCommand) {
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
	}

	/**
	 * This function is called periodically during autonomous
	 */
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		if(useRecordedAutos) {
			try {
				if (player != null && !player.finished()){
					player.play();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void teleopInit() {
		// This makes sure that the autonomous stops running when
		// teleop starts running. If you want the autonomous to
		// continue until interrupted by another command, remove
		// this line or comment it out.
		if (autonomousCommand != null)
			autonomousCommand.cancel();
		
		if(useRecordedAutos) {
			if(player != null) {
				try {
					player.end();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		//Set camera config
		visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO);
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
		if(recording) {
			try{
				//Dynamic recording, so we dont have to enable/disable teleop
				if(doneRecording) {
					String autoRecordingName;
					if((autoRecordingName = SmartDashboard.getString("Auto Recording Save Name", null)) != null)
						recordingString = CSV_FILE_PREFIX + autoRecordingName + ".csv";
					else
						recordingString = null;
				}
				//If recordingString is not null, then start recording
				if(recordingString != null) {
					if(recorder == null)
						recorder = new AutoRecord(recordingString);
					recorder.record();
					doneRecording = false;
					SmartDashboard.putString("Recording file name", recordingString);
				} else {
					recording = false;
				}
			} catch (IOException e) {
				e.printStackTrace();
				recording = false;
			}
		} else {
			if(!doneRecording) {
				try {
					recorder.end();
					//Set to null, to allow for dynamic recording
					recorder = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
				doneRecording = true;
			}
		}
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
	}
}
