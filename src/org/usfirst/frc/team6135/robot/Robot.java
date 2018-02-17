
package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.subsystems.*;

import java.util.HashMap;
import java.util.ArrayDeque;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.commands.autocommands.*;
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
	public static Scalar redLowerBound1 = new Scalar(0, 210, 115);
	public static Scalar redLowerBound2 = new Scalar(245, 210, 115);
	public static Scalar redUpperBound2 = new Scalar(255, 255, 255);
	public static Scalar blueUpperBound = new Scalar(170, 255, 255);
	public static Scalar blueLowerBound = new Scalar(145, 190, 75);
	static final int[] expandLocationsX = new int[] {
		-1, 0, 1,
		-1, 0, 1,
		-1, 0, 1,
	};
	static final int[] expandLocationsY = new int[] {
			-1, -1, -1,
			0, 0, 0,
			1, 1, 1,
	};
	static final int[] fillLocationsX = new int[] {
			-1, 0, 1,
			-1,    1,
			-1, 0, 1
	};
	static final int[] fillLocationsY = new int[] {
			-1, -1, -1,
			 0,      0,
			 1,  1,  1
	};
	
	public static PlaceCubeFromMiddle placeCubeFromMiddle;
	public static PlaceCubeSameSide placeCubeLeftSide;
	public static PlaceCubeFromSideOffset placeCubeLeftSideOffset;
	public static PlaceCubeFromSideOffset placeCubeRightSideOffset;
	public static PlaceCubeSameSide placeCubeRightSide;
	

	Command autonomousCommand;
	SendableChooser<Command> chooser = new SendableChooser<>();
	

	static class ByteArrayImg {
		byte[] data;
		public final int width;
		public final int height;
		
		public ByteArrayImg(byte[] data, final int width, final int height) {
			this.data = data;
			this.width = width;
			this.height = height;
		}
		public byte getPixelByte(int x, int y) {
			return data[y * width + x];
		}
		//Does nothign if x and y are out of range
		public void setPixelByte(int x, int y, int colour) {
			if(x < width && y < height && x >= 0 && y >= 0)
				data[y * width + x] = (byte) colour;
		}
		public byte[] getBytes() {
			return data;
		}
	}
	
	/*
	 * Flood Fill function used for vision
	 * id - the section id
	 * x - the x position of the pixel to be filled
	 * y - the y position of the pixel to be filled
	 * fillRef - a two-dimensional array storing section ids
	 * img - a ByteArrayImg storing the image base to be filled (pixels with a value of 0x00 will not be filled.)
	 * occurrences - a HashMap that stores the occurrences of each section, used later to figure out which section is the largest
	 * 
	 * STACK OVERFLOW WARNING
	 *
	static void visionFloodFill(int id, int x, int y, int[][] fillRef, ByteArrayImg img, HashMap<Integer, Integer> occurrences) {
		if(img.getPixelByte(x, y) == 0x00 || fillRef[x][y] != 0)
			return;
		try {
			fillRef[x][y] = id;
			if(occurrences.containsKey(id))
				occurrences.put(id, occurrences.get(id) + 1);
			else
				occurrences.put(id, 1);
			for(int i = 0; i < fillLocationsX.length; i ++) {
				if(x + fillLocationsX[i] >= 0 && x + fillLocationsX[i] < img.width
						&& y + fillLocationsY[i] >= 0 && y + fillLocationsY[i] < img.height
						&& fillRef[x + fillLocationsX[i]][y + fillLocationsY[i]] == 0)
					visionFloodFill(id, x + fillLocationsX[i], y + fillLocationsY[i], fillRef, img, occurrences);
			}
		}
		catch(Throwable t) {
			SmartDashboard.putString("ERROR", t.toString());
		}
	}*/
	/*
	 * Non-Recursive Flood Fill
	 * Same signature as recursive version
	 * Uses BFS
	 */
	static class ImgPoint {
		public int x, y;
		public ImgPoint(int x, int y) {
			this.x = x;
			this.y = y;
		}
		@Override
		public boolean equals(Object anotherObj) {
			if(!(anotherObj instanceof ImgPoint))
				return false;
			ImgPoint otherPoint = (ImgPoint) anotherObj;
			boolean result = this.x == otherPoint.x && this.y == otherPoint.y;
			return result;
		}
	}
	static void visionFloodFill(int id, int x, int y, int[][] fillRef, ByteArrayImg img, HashMap<Integer, Integer> occurrences, HashMap<Integer, ImgPoint> centers) {
		ArrayDeque<ImgPoint> queue = new ArrayDeque<ImgPoint>();
		if(!occurrences.containsKey(id))
			occurrences.put(id, 0);
		int maxX = x; int minX = x;
		int maxY = y; int minY = y;
		queue.add(new ImgPoint(x, y));
		try {
			if(id == 0)
				throw new IllegalArgumentException("ID is 0");
			while(!queue.isEmpty()) {
				ImgPoint elem = queue.poll();
				fillRef[elem.x][elem.y] = id;
				occurrences.put(id, occurrences.get(id) + 1);
				
				for(int i = 0; i < fillLocationsX.length; i ++) {
					if(elem.x + fillLocationsX[i] >= 0 && elem.x + fillLocationsX[i] < img.width
							&& elem.y + fillLocationsY[i] >= 0 && elem.y + fillLocationsY[i] < img.height
							&& fillRef[elem.x + fillLocationsX[i]][elem.y + fillLocationsY[i]] == 0
							&& img.getPixelByte(elem.x, elem.y) != 0x00) {
						ImgPoint nextPoint = new ImgPoint(elem.x + fillLocationsX[i], elem.y + fillLocationsY[i]);
						if(!queue.contains(nextPoint)) {
							maxX = Math.max(maxX, elem.x+fillLocationsX[i]);
							maxY = Math.max(maxY, elem.y+fillLocationsY[i]);
							minX = Math.min(minX, elem.x+fillLocationsX[i]);
							minY = Math.min(minY, elem.y+fillLocationsY[i]);
							queue.add(nextPoint);
						}
					}
						
				}
				SmartDashboard.putNumber("Queue length", queue.size());
			}
			centers.put(id, new ImgPoint((maxX+minX)/2, (maxY+minY)/2));
		}
		catch(Throwable t) {
			SmartDashboard.putString("ERROR", t.toString());
		}
	}
	
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
		
		RobotMap.gyro.calibrate();
		
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
        camera.setResolution(RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
        camera.setBrightness(100);
        camera.setExposureManual(20);
        camera.setFPS(1);
        //----------------WARNING: EXPERIMENTAL CODE--------------------------
        //Vision processing is done in a separate thread
        boolean teamIsRed = true;
        (new Thread(new Runnable() {
        	@Override
        	public void run() {
        		//Create a sink and a source
        		CvSink sink = CameraServer.getInstance().getVideo();
        		CvSource source = CameraServer.getInstance().putVideo("Test Stream", RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
        		CvSource semiProcessed = CameraServer.getInstance().putVideo("No Blur", RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
        		//Create matrices to store the images later
        		Mat originalImg = new Mat();
        		Mat hsvImg = new Mat();
        		Mat filteredImg1 = new Mat();
        		Mat filteredImg2 = new Mat();
        		Mat filteredImg = new Mat();
        		Mat buf = new Mat();
        		//Mat processedImg = new Mat();
        		
        		while(!Thread.interrupted()) {
        			
        			//Obtain the frame from the camera (1 second timeout)
        			sink.grabFrame(originalImg, 1);
        			Imgproc.medianBlur(originalImg, buf, 3);
        			//Convert the colour space from BGR to HSV
        			Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
        			//Filter out the colours
        			if(teamIsRed) {
        				Core.inRange(hsvImg, redLowerBound1, redUpperBound1, filteredImg1);
        				Core.inRange(hsvImg, redLowerBound2, redUpperBound2, filteredImg2);
        				Core.addWeighted(filteredImg1, 1.0, filteredImg2, 1.0, 0.0, filteredImg);
        			}
        			else {
        				Core.inRange(hsvImg, blueLowerBound, blueUpperBound, filteredImg);
        			}
        			
        			//Blur
        			Imgproc.medianBlur(filteredImg, buf, 7);
        			filteredImg = buf;
        			//Process the image
        			byte[] imgData = new byte[(int) (filteredImg.total() * filteredImg.channels())];
        			filteredImg.get(0, 0, imgData);
        			byte[] processed = new byte[imgData.length];
        			ByteArrayImg img = new ByteArrayImg(imgData, filteredImg.width(), filteredImg.height());
        			ByteArrayImg imgOut = new ByteArrayImg(processed, filteredImg.width(), filteredImg.height());
        			//Expand each pixel to fill in possible gaps in the shape
        			boolean noDetection = true;
        			for(int y = 0; y < filteredImg.height(); y ++) {
        				for(int x = 0; x < filteredImg.width(); x ++) {
        					if(img.getPixelByte(x, y) != 0x00) {
        						noDetection = false;
        						for(int i = 0; i < expandLocationsX.length; i ++) {
        							imgOut.setPixelByte(x + expandLocationsX[i], y + expandLocationsY[i], 0xFF);
        						}
        					}
        				}
        			}
        			/*Mat firstProcessed = new Mat(filteredImg.height(), filteredImg.width(), filteredImg.type());
        			firstProcessed.put(0, 0, imgOut.getBytes());
        			semiProcessed.putFrame(firstProcessed);*/
        			
        			//Stop referencing the byte array to free up precious RAM
        			img = null;
        			imgData = null;
        			SmartDashboard.putString("Status", "Finished initial processing");
        			
        			//If the filter did not detect any pixels skip the next part
        			if(noDetection)
        				continue;
        			SmartDashboard.putString("Status", "Flood Filling...");
        			//Do a flood fill
        			int[][] fillRef = new int[filteredImg.width()][filteredImg.height()];
        			HashMap<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
        			HashMap<Integer, ImgPoint> centers = new HashMap<Integer, ImgPoint>();
        			int sectionId = 1;
        			for(int y = 0; y < filteredImg.height(); y ++) {
        				for(int x = 0; x < filteredImg.width(); x ++) {
        					if(imgOut.getPixelByte(x, y) != 0x00 && fillRef[x][y] == 0) {
        						try {
        							visionFloodFill(sectionId ++, x, y, fillRef, imgOut, occurrences, centers);
        						}
        						catch(Throwable e) {
        							SmartDashboard.putString("Status", e.toString() + e.getMessage());
        						}
        					}
        				}
        			}
        			SmartDashboard.putString("Status", "Floodfill finished");
        			
        			SmartDashboard.putNumber("Number Of Sections", sectionId - 1);
        			
        			//Find out what the largest section is
        			//It is safe to assume that at least one section exists since this code won't run if there are no white pixels
        			int maxSectionId = 1;
        			SmartDashboard.putString("Status", "Finding Largest Section");
        			for(int i = 1; i < sectionId; i ++) {
        				if(occurrences.get(i) > occurrences.get(maxSectionId)) {
        					maxSectionId = i;
        				}
        			}
        			ImgPoint center = centers.get(maxSectionId);
        			double angle = Math.atan((center.x - RobotMap.CAMERA_CENTER) / RobotMap.CAMERA_FOCAL_LEN);
        			SmartDashboard.putNumber("Key Pixel Angle", Math.toDegrees(angle));
        			
        			SmartDashboard.putString("Status", "Cycle finished");
        			SmartDashboard.putNumber("Max Section Size", occurrences.get(maxSectionId));
        			
        			Mat firstProcessed = new Mat(filteredImg.height(), filteredImg.width(), filteredImg.type());
        			firstProcessed.put(0, 0, imgOut.getBytes());
        			semiProcessed.putFrame(firstProcessed);
        			SmartDashboard.putString("Center", center.x + ", " + center.y);
        			try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
