
package org.usfirst.frc.team6135.robot;

import java.io.IOException;

import org.usfirst.frc.team6135.robot.commands.autocommands.DrivePastBaseline;
import org.usfirst.frc.team6135.robot.commands.autocommands.SwitchAligned;
import org.usfirst.frc.team6135.robot.commands.autocommands.SwitchMiddle;
import org.usfirst.frc.team6135.robot.commands.autocommands.SwitchSide;
import org.usfirst.frc.team6135.robot.commands.autonomous.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autonomous.DriveStraightDistance;
import org.usfirst.frc.team6135.robot.commands.autonomous.FollowTrajectory;
import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.misc.AutoPaths;
import org.usfirst.frc.team6135.robot.misc.AutoPlayback;
import org.usfirst.frc.team6135.robot.misc.AutoRecord;
import org.usfirst.frc.team6135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.GearShiftSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.IntakeSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.WristSubsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import robot.pathfinder.TankDriveTrajectory;
import robot.pathfinder.Waypoint;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
	
	//SUBSYSTEMS
	public static OI oi;
	public static DriveTrain drive;
	public static IntakeSubsystem intakeSubsystem;
	public static GearShiftSubsystem gearShiftSubsystem;
	public static ElevatorSubsystem elevatorSubsystem;
	public static WristSubsystem wristSubsystem;
	public static VisionSubsystem visionSubsystem;
	
	public static Alliance color; //Red or Blue
	public static int station; //Driver station number (1, 2 or 3)
	public static String gameData; //Used to tell the locations of the switch/scale plates
	
	//Location consts, used in auto choosing
	public static final int LOCATION_LEFT = -1;
	public static final int LOCATION_MID = 0;
	public static final int LOCATION_RIGHT = 1;
	
	//Auto mode consts, used in auto choosing
	public static final int AUTO_DEBUG = 0x00;
	public static final int AUTO_BASELINE = 0x01;
	public static final int AUTO_ALIGNED = 0x02;
	public static final int AUTO_SIDE = 0x03;
	public static final int AUTO_MIDDLE = 0x04;
	
	//Autonomous command choosers
	public static SendableChooser<Integer> robotLocationChooser = new SendableChooser<>();
	public static SendableChooser<Integer> prewrittenAutoChooser = new SendableChooser<>();
	public static SendableChooser<String> recordedAutoChooser = new SendableChooser<>();
	
	//This keeps track of the command that runs in autonomous so we can cancel it when entering teleop
	static Command autonomousCommand;
	
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
	
	public static boolean inDebugMode = false;
	static void putTunables() {
		if(!inDebugMode)
			return;
		//Output these values to the SmartDashboard for tuning
		//They will show up as modifiable text boxes on the SmartDashboard
		SmartDashboard.putNumber("Turn kP", AutoTurn.kP);
		SmartDashboard.putNumber("Turn kI", AutoTurn.kI);
		SmartDashboard.putNumber("Turn kD", AutoTurn.kD);
		SmartDashboard.putNumber("Path Follower kP", FollowTrajectory.kP);
		SmartDashboard.putNumber("Path Follower kD", FollowTrajectory.kD);
		SmartDashboard.putNumber("Path Follower kV", FollowTrajectory.kV);
		SmartDashboard.putNumber("Path Follower kA", FollowTrajectory.kA);
		SmartDashboard.putNumber("Teleop Drive Ramp Band", TeleopDrive.rampBand);
		
		SmartDashboard.putString("Auto Recording Save Name", "");
		SmartDashboard.putString("Auto Playback File Name", "");
	}
	static void updateTunables() {
		if(!inDebugMode)
			return;
		//Read the textbox values and overwrite the old ones with the new ones
		//The value is not changed by default (new value = old value)
		AutoTurn.kP = SmartDashboard.getNumber("Turn kP", AutoTurn.kP);
		AutoTurn.kI = SmartDashboard.getNumber("Turn kI", AutoTurn.kI);
		AutoTurn.kD = SmartDashboard.getNumber("Turn kD", AutoTurn.kD);
		FollowTrajectory.kP = SmartDashboard.getNumber("Path Follower kP", FollowTrajectory.kP);
		FollowTrajectory.kD = SmartDashboard.getNumber("Path Follower kD", FollowTrajectory.kD);
		FollowTrajectory.kV = SmartDashboard.getNumber("Path Follower kV", FollowTrajectory.kV);
		FollowTrajectory.kA = SmartDashboard.getNumber("Path Follower kA", FollowTrajectory.kA);
		TeleopDrive.rampBand = SmartDashboard.getNumber("Teleop Drive Ramp Band", TeleopDrive.rampBand);
		
		String autoRecordingName;
		//This makes sure that the file we save to/read from is in the valid location
		//"/home/lvuser/"
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
		wristSubsystem = new WristSubsystem();
		
		//Initialize camera stream and vision subsystem
        visionSubsystem = new VisionSubsystem(CameraServer.getInstance().startAutomaticCapture());
        //Set camera config
        visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO); //For vision, change to Mode.VISION
        
        //Get the team's colour and station number
        station = DriverStation.getInstance().getLocation();
        color = DriverStation.getInstance().getAlliance();
        //Game data is retrieved later
        
        //OI must be initialized after all subsystems, because it maps buttons to commands, and those commands
        //require subsystems to be properly initialized
        oi = new OI();
        
        //Generate all autonomous paths/trajectories
        //Takes quite some time so we do it here instead of in the autos themselves
        AutoPaths.generateAll(RobotMap.specs);
        
        //Initialize the correct auto chooser
        if(useRecordedAutos) {
        	//Recorded autos
            initRecordedAutoChooser();
        } else {
        	//Pre-written autos
        	initAutoChooser();
        }
		
        //Output the tunable values
		putTunables();
	}
	
	/**
	 * Initializes the autonomous chooser for pre-written autos
	 */
	public static void initAutoChooser() {
		//Add options to choosers
		robotLocationChooser.addObject("Left", LOCATION_LEFT);
		robotLocationChooser.addObject("Middle", LOCATION_MID);
		robotLocationChooser.addObject("Right", LOCATION_RIGHT);
		
		prewrittenAutoChooser.addDefault("Drive Past Baseline", AUTO_BASELINE);
		prewrittenAutoChooser.addObject("Switch Auto: Side", AUTO_SIDE);
		prewrittenAutoChooser.addObject("Switch Auto: Aligned", AUTO_ALIGNED);
		prewrittenAutoChooser.addObject("Switch Auto: Middle", AUTO_MIDDLE);
		
		prewrittenAutoChooser.addObject("Debug Auto", AUTO_DEBUG);
		
		//Display the choosers by sending them over the SmartDashboard
		SmartDashboard.putData("Auto Mode", prewrittenAutoChooser);
		SmartDashboard.putData("Robot Location", robotLocationChooser);
	}
	
	/**
	 * Initializes the autonomous chooser for recorded autos
	 */
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
	
	public static double leftMaxVel, rightMaxVel, leftMaxAccel, rightMaxAccel;
	/**
	 * This function is called periodically during all robot modes.
	 * Code that needs to be run regardless of mode, such as the printing of information,
	 * can be put here.
	 */
	@Override
	public void robotPeriodic() {
		if(inDebugMode) {
			SmartDashboard.putNumber("Left Distance", Robot.drive.getLeftDistance());
	    	SmartDashboard.putNumber("Right Distance", Robot.drive.getRightDistance());
	    	double leftVel = Robot.drive.getLeftSpeed();
	    	double rightVel = Robot.drive.getRightSpeed();
	    	SmartDashboard.putNumber("Left Speed", leftVel);
	    	SmartDashboard.putNumber("Right Speed", rightVel);
	    	double[] accel = Robot.drive.getAccelerations();
	    	SmartDashboard.putNumber("Left Acceleration", accel[0]);
	    	SmartDashboard.putNumber("Right Acceleration", accel[1]);
	    	
	    	//Update max speed and accel
	    	if(Math.abs(leftVel) > Math.abs(leftMaxVel)) {
	    		leftMaxVel = leftVel;
	    	}
	    	if(Math.abs(rightVel) > Math.abs(rightMaxVel)) {
	    		rightMaxVel = rightVel;
	    	}
	    	if(Math.abs(accel[0]) > Math.abs(leftMaxAccel)) {
	    		leftMaxAccel = accel[0];
	    	}
	    	if(Math.abs(accel[1]) > Math.abs(rightMaxAccel)) {
	    		rightMaxAccel = accel[1];
	    	}
	    	SmartDashboard.putNumber("Left Max Speed", leftMaxVel);
	    	SmartDashboard.putNumber("Right Max Speed", rightMaxVel);
	    	SmartDashboard.putNumber("Left Max Acceleration", leftMaxAccel);
	    	SmartDashboard.putNumber("Right Max Acceleration", rightMaxAccel);
    	
	    	SmartDashboard.putBoolean("Elevator Top Switch", Robot.elevatorSubsystem.notAtTop());
	    	SmartDashboard.putBoolean("Elevator Bottom Switch", Robot.elevatorSubsystem.notAtBottom());
	    	SmartDashboard.putBoolean("Wrist Switch", Robot.wristSubsystem.notAtTop());
		}
    	
    	SmartDashboard.putBoolean("Drive Ramping", TeleopDrive.isRamped());
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		RobotMap.setAllMotorNeuralModes(NeutralMode.Coast);
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * Called when the robot first enters autonomous mode. Start auto commands here.
	 */
	@Override
	public void autonomousInit() {
		
		if(useRecordedAutos) {
			//Set up recorded auto
			//Get the selected auto
			String macroAuto = recordedAutoChooser.getSelected();
			setUpMacroAutos(macroAuto);
			if(useRecordedAutos) {
				try {
		    		 player = new AutoPlayback(selectedAuto);
				} catch (Exception e){
					e.printStackTrace();
				}
				//Because the recorded autos are recorded with motors in coast, set them to coast
				RobotMap.setAllMotorNeuralModes(NeutralMode.Coast);
			}
		} else {
			//Get the location and auto mode from choosers
			int location = robotLocationChooser.getSelected();
			int autoMode = robotLocationChooser.getSelected();
			
			//Set motors to be in brake mode
			RobotMap.setAllMotorNeuralModes(NeutralMode.Brake);
			
			runSetAuto(location, autoMode);
		}

		//Set camera config
		visionSubsystem.setMode(VisionSubsystem.Mode.VISION);
		
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
							new DriveStraightDistance(RobotMap.ArenaDimensions.SWITCH_DISTANCE).start();
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
							(new DriveStraightDistance(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
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
							new DriveStraightDistance(RobotMap.ArenaDimensions.SWITCH_DISTANCE).start();
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
							(new DriveStraightDistance(RobotMap.ArenaDimensions.SWITCH_DISTANCE)).start();
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
	
	/**
	 * Starts an autonomous command by setting the static member {@code autonomousCommand} to the given command
	 * and calling {@link Command#start() start()} on it.
	 * @param autoCommand - The command to start
	 */
	public static void startAutoCommand(Command autoCommand) {
		autonomousCommand = autoCommand;
		autoCommand.start();
	}
	public static void runSetAuto(int location, int mode) {
		//Retrieve the locations of the switch plates (in game data)
		gameData = DriverStation.getInstance().getGameSpecificMessage().toUpperCase();
		if(gameData.length() > 0) {
			switch(mode) {
			case AUTO_BASELINE:
				startAutoCommand(new DrivePastBaseline());
				break;
			case AUTO_ALIGNED:
				//Run aligned with switch command only if the robot's position is the same as the switch plate's
				if((location == LOCATION_LEFT && gameData.charAt(0) == 'L') 
						|| (location == LOCATION_RIGHT && gameData.charAt(0) == 'R')) {
					startAutoCommand(new SwitchAligned());
				}
				else {
					startAutoCommand(new DriveStraightDistance(RobotMap.ArenaDimensions.SWITCH_DISTANCE - RobotMap.ROBOT_LENGTH));
				}
				break;
			case AUTO_MIDDLE:
				//Start the middle auto command with the correct direction
				startAutoCommand(new SwitchMiddle(gameData.charAt(0) == 'L' ? SwitchMiddle.DIRECTION_LEFT : SwitchMiddle.DIRECTION_RIGHT));
				break;
			case AUTO_SIDE:
				if((location == LOCATION_LEFT && gameData.charAt(0) == 'L') 
						|| (location == LOCATION_RIGHT && gameData.charAt(0) == 'R')) {
					startAutoCommand(new SwitchSide(location == LOCATION_LEFT ? SwitchSide.SIDE_LEFT : SwitchSide.SIDE_RIGHT));
				}
				else {
					startAutoCommand(new DrivePastBaseline());
				}
				break;
			//For debug purposes only
			case AUTO_DEBUG:
				startAutoCommand(new FollowTrajectory(new TankDriveTrajectory(new Waypoint[] {
						new Waypoint(0, 0, Math.PI / 2),
						new Waypoint(60, 144, Math.PI / 2),
				}, RobotMap.specs, 300, 5000)));
				break;
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
			//Run the auto player if we are playing a recorded auto
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
			//Shut down the player
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
		RobotMap.setAllMotorNeuralModes(NeutralMode.Coast);
		
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
				//Dynamic recording, so we don't have to enable/disable teleop
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
