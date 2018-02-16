
package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.subsystems.*;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.commands.autocommands.*;
import org.usfirst.frc.team6135.robot.commands.autoutils.AutoTurn;
import org.usfirst.frc.team6135.robot.commands.autoutils.DriveStraightDistance;

import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
//import edu.wpi.first.wpilibj.livewindow.LiveWindow;
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

	//public static final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
	public static OI oi;
	public static DriveTrain drive;
	public static IntakeSubsystem intake;
	public static GearShiftSubsystem gearShiftSubsystem;
	public static ElevatorSubsystem elevatorSubsystem;
	public static TiltSubsystem tiltSubsystem;
	
	public static Alliance color;
	public static int station; //Starting position of robot
	public static String gameData;
	
	public static UsbCamera camera;
	public static Scalar redUpperBound1 = new Scalar(10, 255, 255);
	public static Scalar redLowerBound1 = new Scalar(0, 210, 130);
	public static Scalar redLowerBound2 = new Scalar(245, 210, 130);
	public static Scalar redUpperBound2 = new Scalar(255, 255, 255);
	public static Scalar blueUpperBound = new Scalar(170, 255, 255);
	public static Scalar blueLowerBound = new Scalar(145, 190, 75);
	
	public static PlaceCubeFromMiddle placeCubeFromMiddle;
	public static PlaceCubeSameSide placeCubeLeftSide;
	public static PlaceCubeFromSideOffset placeCubeLeftSideOffset;
	public static PlaceCubeFromSideOffset placeCubeRightSideOffset;
	public static PlaceCubeSameSide placeCubeRightSide;
	

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		RobotMap.init();
		drive = new DriveTrain();
		intake = new IntakeSubsystem();
		gearShiftSubsystem = new GearShiftSubsystem();
		elevatorSubsystem = new ElevatorSubsystem();
		tiltSubsystem = new TiltSubsystem();
		oi = new OI();
		
		//chooser.addDefault("Drive straight distance", new DriveStraightDistance(5.0, 0.5));
		//chooser.addObject("Turn 90 degrees", new AutoTurn(90, 0.5));
		placeCubeFromMiddle = new PlaceCubeFromMiddle(PlaceCubeFromMiddle.DIRECTION_LEFT);
		placeCubeLeftSide = new PlaceCubeSameSide();
		placeCubeRightSide = new PlaceCubeSameSide();
		placeCubeLeftSideOffset = new PlaceCubeFromSideOffset(PlaceCubeFromSideOffset.DIRECTION_LEFT);
		placeCubeRightSideOffset = new PlaceCubeFromSideOffset(PlaceCubeFromSideOffset.DIRECTION_RIGHT);
		chooser.addDefault("Drive Past Baseline", new DrivePastBaseLine());
		chooser.addObject("Place Cube: Robot is on the left (Robot lines up with switch)", placeCubeLeftSide);
		chooser.addObject("Place Cube: Robot is on the right (Robot lines up with switch)", placeCubeRightSide);
		chooser.addObject("Place Cube: Robot is on the left (Robot is to the side of switch)", placeCubeLeftSideOffset);
		chooser.addObject("Place Cube: Robot is on the right (Robot is to the side of switch)", placeCubeRightSideOffset);
		chooser.addObject("Place Cube: Robot is in the middle", placeCubeFromMiddle);
		SmartDashboard.putData("Auto mode", chooser);
		
		station = DriverStation.getInstance().getLocation();
		color = DriverStation.getInstance().getAlliance();
		
		//Camera feed initialization
        camera = CameraServer.getInstance().startAutomaticCapture();
        camera.setResolution(RobotMap.CAMFEED_WIDTH, RobotMap.CAMFEED_HEIGHT);
        camera.setBrightness(100);
        camera.setExposureManual(20);
        camera.setFPS(1);
        //Vision processing is done in a separate thread
        boolean teamIsRed = true;
        (new Thread(new Runnable() {
        	@Override
        	public void run() {
        		//Create a sink and a source
        		CvSink sink = CameraServer.getInstance().getVideo();
        		CvSource source = CameraServer.getInstance().putVideo("Test Stream", RobotMap.CAMFEED_WIDTH, RobotMap.CAMFEED_HEIGHT);
        		//Create matrices to store the images later
        		Mat originalImg = new Mat();
        		Mat hsvImg = new Mat();
        		Mat filteredImg1 = new Mat();
        		Mat filteredImg2 = new Mat();
        		Mat processedImg = new Mat();
        		
        		while(!Thread.interrupted()) {
        			//Obtain the frame from the camera (1 second timeout)
        			sink.grabFrame(originalImg, 1);
        			//Convert the colour space from BGR to HSV
        			Imgproc.cvtColor(originalImg, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
        			//Filter out the colours
        			if(teamIsRed) {
        				Core.inRange(hsvImg, redLowerBound1, redUpperBound1, filteredImg1);
        				Core.inRange(hsvImg, redLowerBound2, redUpperBound2, filteredImg2);
        				Core.addWeighted(filteredImg1, 1.0, filteredImg2, 1.0, 0.0, processedImg);
        			}
        			else {
        				Core.inRange(hsvImg, blueLowerBound, blueUpperBound, processedImg);
        			}
        			source.putFrame(processedImg);
        		}
        	}
        })).start();
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {

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
			
		
		gameData = DriverStation.getInstance().getGameSpecificMessage();//update wpilib
		if(gameData.length() > 0){
			//Depending on which side the alliance switch is on, some commands need to change
			if(gameData.charAt(0) == 'L'){
				if(autonomousCommand.equals(placeCubeFromMiddle)) {
					(new PlaceCubeFromMiddle(PlaceCubeFromMiddle.DIRECTION_LEFT)).start();
				}
				//Use == to check if they're the exact same object
				else if(autonomousCommand == placeCubeLeftSide) {
					autonomousCommand.start();
				}
				else if(autonomousCommand == placeCubeRightSide) {
					(new DrivePastBaseLineOffset(DrivePastBaseLineOffset.DIRECTION_LEFT)).start();
				}
				else if(autonomousCommand == placeCubeLeftSideOffset) {
					autonomousCommand.start();
				}
				else if(autonomousCommand == placeCubeRightSideOffset) {
					(new DrivePastBaseLine()).start();
				}
				else {
					autonomousCommand.start();
				}
			} 
			else {
				if(autonomousCommand.equals(placeCubeFromMiddle)) {
					(new PlaceCubeFromMiddle(PlaceCubeFromMiddle.DIRECTION_RIGHT)).start();
				}
				else if(autonomousCommand == placeCubeRightSide) {
					autonomousCommand.start();
				}
				else if(autonomousCommand == placeCubeLeftSide) {
					(new DrivePastBaseLineOffset(DrivePastBaseLineOffset.DIRECTION_RIGHT)).start();
				}
				else if(autonomousCommand == placeCubeRightSideOffset) {
					autonomousCommand.start();
				}
				else if(autonomousCommand == placeCubeLeftSideOffset) {
					(new DrivePastBaseLine()).start();
				}
				else {
					autonomousCommand.start();
				}
			}
		}
		
		

		/*
		 * String autoSelected = SmartDashboard.getString("Auto Selector",
		 * "Default"); switch(autoSelected) { case "My Auto": autonomousCommand
		 * = new MyAutoCommand(); break; case "Default Auto": default:
		 * autonomousCommand = new ExampleCommand(); break; }
		 */

		//Run the selected auto command
		//if (autonomousCommand != null)
			//autonomousCommand.start();
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
		//According to documentation, this method is deprecated since it's no longer required
		//LiveWindow.run();
	}
}
