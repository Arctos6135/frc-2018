package org.usfirst.frc.team6135.robot.subsystems;

import org.usfirst.frc.team6135.robot.vision.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.RobotMap;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Vision Subsystem
 */
public class VisionSubsystem extends Subsystem {
	/*
	 * Camera mode
	 * VISION mode has low exposure for better object detection, 
	 * However VIDEO mode's high exposure gives a brighter picture, which is better for humans to view
	 * Note that switching between the two modes takes time; the program should wait for approx. 1s to compensate.
	 */
	 public static enum Mode {
    	VISION,
    	VIDEO,
    }
	
	//Initial brightness of the camera
	public static int cameraInitBrightness;
	//Colour filters
	//Note that red has two sets since its hue is split
	public static final Scalar redUpperBound1 = new Scalar(10, 255, 255);
	public static final Scalar redLowerBound1 = new Scalar(0, 210, 115);
	public static final Scalar redLowerBound2 = new Scalar(245, 210, 115);
	public static final Scalar redUpperBound2 = new Scalar(255, 255, 255);
	public static final Scalar blueUpperBound = new Scalar(170, 255, 255);
	public static final Scalar blueLowerBound = new Scalar(145, 190, 75);
	//49, 255, 255         32, 170, 10
	public static final Scalar cubeUpperBound = new Scalar(73, 255, 255);
	public static final Scalar cubeLowerBound = new Scalar(23, 170, 10);
	
	UsbCamera camera;
	CvSink sink;
	CvSource source;
	
	public VisionSubsystem(UsbCamera cam) {
		camera = cam;
		//Get our initial brightness
		cameraInitBrightness = camera.getBrightness();
		//Set resolution
		camera.setResolution(RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
		//Get our sink, the source of our pictures
		sink = CameraServer.getInstance().getVideo();
		//Create a source, used to stream half-processed pictures
		source = CameraServer.getInstance().putVideo("Vision Subsystem", RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
	/*
	 * Sets the mode of the camera
	 * For more information on modes see the Mode enum
	 */
	public void setMode(Mode mode) {
		if(mode == Mode.VISION) {
			sink.setEnabled(true);
			camera.setBrightness(100);
			camera.setExposureManual(20);
			camera.setExposureHoldCurrent();
			camera.setFPS(8);
		}
		else {
			sink.setEnabled(false);
			camera.setBrightness(cameraInitBrightness);
			camera.setExposureManual(35);
			if(!camera.setFPS(24)) {
				SmartDashboard.putString("VisionError", "Failed to set FPS");
			}
		}
	}
	/*
	 * Returns, in radians, the angle between the camera and the center of our alliance's switch
	 */
	@Deprecated
	public double getSwitchAngle(DriverStation.Alliance color) throws VisionException {
		return getSwitchAngle(color, 1);
	}
	@Deprecated
	public double getSwitchAngle(DriverStation.Alliance color, double timeout) throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg1 = new Mat();
		Mat filteredImg2 = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		if(sink.grabFrame(originalImg, timeout) == 0)
			throw new VisionException("Failed to obtain frame within timeout");
		Imgproc.resize(originalImg, buf, new Size(RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT));
		originalImg = buf;
		//Do a median blur to remove noise
		Imgproc.medianBlur(originalImg, buf, 3);
		//Convert the colour space from BGR to HSV
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		//ColorRange out the colours
		if(color.equals(DriverStation.Alliance.Red)) {
			Core.inRange(hsvImg, redLowerBound1, redUpperBound1, filteredImg1);
			Core.inRange(hsvImg, redLowerBound2, redUpperBound2, filteredImg2);
			Core.addWeighted(filteredImg1, 1.0, filteredImg2, 1.0, 0.0, filteredImg);
		}
		else if(color.equals(DriverStation.Alliance.Blue)) {
			Core.inRange(hsvImg, blueLowerBound, blueUpperBound, filteredImg);
		}
		else {
			throw new IllegalArgumentException("Invalid alliance colour");
		}
		//Free up RAM
		filteredImg1 = null;
		filteredImg2 = null;
		originalImg = null;
		//Median blur again to make processing more accurate
		Imgproc.medianBlur(filteredImg, buf, 7);
		filteredImg = buf;
		//Process the image
		byte[] imgData = new byte[(int) (filteredImg.total() * filteredImg.channels())];
		filteredImg.get(0, 0, imgData);
		byte[] processed = new byte[imgData.length];
		ByteArrayImg img = new ByteArrayImg(imgData, filteredImg.width(), filteredImg.height());
		ByteArrayImg imgOut = new ByteArrayImg(processed, filteredImg.width(), filteredImg.height());
		
		//If the filter did not detect any pixels skip the next part
		if(!Vision.expandPixels(img, imgOut))
			throw new VisionException("Alliance colour not detected");
		//Free up some ram
		Mat processedImg = imgOut.toMat(filteredImg.type());
		imgOut = null;
		img = null;
		imgData = null;
		processed = null;
		
		return Vision.getKeyPointAngle(processedImg);
	}
	/*
	 * Returns, in radians, the angle between the camera and the center of the nearest
	 * (largest on-screen) Power Cube.
	 */
	@Deprecated
	public double getCubeAngle() throws VisionException {
		return getCubeAngle(1);
	}
	@Deprecated
	public double getCubeAngle(double timeout) throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		if(sink.grabFrame(originalImg, timeout) == 0)
			throw new VisionException("Failed to obtain frame within timeout");
		Imgproc.resize(originalImg, buf, new Size(RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT));
		originalImg = buf;
		Imgproc.medianBlur(originalImg, buf, 3);
		//Convert the colour space from BGR to HSV
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		//ColorRange out the colours
		Core.inRange(hsvImg, cubeLowerBound, cubeUpperBound, filteredImg);

		
		//Process the image
		byte[] imgData = new byte[(int) (filteredImg.total() * filteredImg.channels())];
		filteredImg.get(0, 0, imgData);
		byte[] processed = new byte[imgData.length];
		ByteArrayImg img = new ByteArrayImg(imgData, filteredImg.width(), filteredImg.height());
		ByteArrayImg imgOut = new ByteArrayImg(processed, filteredImg.width(), filteredImg.height());
		
		//If the filter did not detect any pixels skip the next part
		if(!Vision.expandPixels(img, imgOut)) {
			source.putFrame(imgOut.toMat(filteredImg.type()));
			throw new VisionException("Cube not detected");
		}
		//Free up some ram
		Mat processedImg = imgOut.toMat(filteredImg.type());
		source.putFrame(processedImg);
		//source.putFrame(processedImg);
		imgOut = null;
		img = null;
		imgData = null;
		processed = null;
		
		return Vision.getKeyPointAngle(processedImg, 300);
	}
	
	/*
	 * Returns, in radians, the angle between the camera and the center of our alliance's switch
	 */
	public double getSwitchAngleEx(DriverStation.Alliance color) throws VisionException {
		return getSwitchAngleEx(color, 1);
	}
	public double getSwitchAngleEx(DriverStation.Alliance color, double timeout) throws VisionException {
		Mat buf;
		if(color.equals(DriverStation.Alliance.Red)) {
			buf = Vision.grabThresholdedFrame(sink, timeout, RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT, 
					new ColorRange(redLowerBound1, redUpperBound1), new ColorRange(redLowerBound2, redUpperBound2));
		}
		else if(color.equals(DriverStation.Alliance.Blue)) {
			buf = Vision.grabThresholdedFrame(sink, timeout, RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT, 
					new ColorRange(blueLowerBound, blueUpperBound));
		}
		else {
			throw new IllegalArgumentException("Invalid alliance colour");
		}
		
		Rect rect;
		try {
			rect = Vision.getBiggestBoundingRect(buf);
			if(rect.area() < 20)
				throw new VisionException();
		}
		catch(Exception ve) {
			throw new VisionException("Switch could not be located");
		}
		ImgPoint centre = new ImgPoint(rect.x + rect.width / 2, rect.y + rect.height / 2);
		return Vision.getXAngleOffset(centre);
	}
	/*
	 * Returns, in radians, the angle between the camera and the center of the nearest
	 * (largest on-screen) Power Cube.
	 */
	public double getCubeAngleEx() throws VisionException {
		return getCubeAngleEx(1);
	}
	public double getCubeAngleEx(double timeout) throws VisionException {
		Mat buf = Vision.grabThresholdedFrame(sink, timeout, RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT, 
				new ColorRange(cubeLowerBound, cubeUpperBound));
		Rect rect;
		try {
			rect = Vision.getBiggestBoundingRect(buf);
			if(rect.area() < 75)
				throw new VisionException();
		}
		catch(Exception ve) {
			throw new VisionException("Cube could not be located");
		}
		ImgPoint centre = new ImgPoint(rect.x + rect.width / 2, rect.y + rect.height / 2);
		return Vision.getXAngleOffset(centre);
	}
	
}

