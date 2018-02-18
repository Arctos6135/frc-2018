package org.usfirst.frc.team6135.robot.subsystems;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *	Vision Subsystem
 */
public class VisionSubsystem extends Subsystem {
	
	public static class VisionException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 4674148715358218109L;
		public VisionException(String message) {
			super(message);
		}
		public VisionException() {
			super();
		}
	}
	public static class ByteArrayImg {
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
		//Does nothing if x and y are out of range
		public void setPixelByte(int x, int y, int colour) {
			if(x < width && y < height && x >= 0 && y >= 0)
				data[y * width + x] = (byte) colour;
		}
		public byte[] getBytes() {
			return data;
		}
		public Mat toMat(int type) {
			Mat output = new Mat(this.height, this.width, type);
			output.put(0, 0, this.data);
			return output;
		}
	}
	public static class ImgPoint {
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
	
	 public static enum Mode {
    	VISION,
    	VIDEO,
    }
	
	public static int cameraInitBrightness;
	public static final Scalar redUpperBound1 = new Scalar(10, 255, 255);
	public static final Scalar redLowerBound1 = new Scalar(0, 210, 115);
	public static final Scalar redLowerBound2 = new Scalar(245, 210, 115);
	public static final Scalar redUpperBound2 = new Scalar(255, 255, 255);
	public static final Scalar blueUpperBound = new Scalar(170, 255, 255);
	public static final Scalar blueLowerBound = new Scalar(145, 190, 75);
	public static final Scalar cubeUpperBound = new Scalar(49, 255, 255);
	public static final Scalar cubeLowerBound = new Scalar(32, 170, 10);
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
	
	/*
	 * Non-Recursive Flood Fill
	 */
	static void visionFloodFill(int id, int x, int y, int[][] fillRef, ByteArrayImg img, HashMap<Integer, Integer> occurrences, HashMap<Integer, ImgPoint> centers) {
		ArrayDeque<ImgPoint> stack = new ArrayDeque<ImgPoint>();
		HashSet<ImgPoint> set = new HashSet<ImgPoint>();
		if(!occurrences.containsKey(id))
			occurrences.put(id, 0);
		int maxX = x; int minX = x;
		int maxY = y; int minY = y;
		ImgPoint firstPoint = new ImgPoint(x, y);
		stack.push(firstPoint);
		set.add(firstPoint);
		try {
			if(id == 0)
				throw new IllegalArgumentException("ID is 0");
			int pixels = occurrences.get(id);
			while(!stack.isEmpty()) {
				ImgPoint elem = stack.pop();
				set.remove(elem);
				fillRef[elem.x][elem.y] = id;
				if(elem.x > maxX)
					maxX = elem.x;
				else if(elem.x < minX)
					minX = elem.x;
				if(elem.y > maxY)
					maxY = elem.y;
				else if(elem.y < minY)
					minY = elem.y;
				pixels ++;
				
				for(int i = 0; i < fillLocationsX.length; i ++) {
					int newX = elem.x + fillLocationsX[i];
					int newY = elem.y + fillLocationsY[i];
					if(newX >= 0 && newX < img.width && newY >= 0 && newY < img.height
							&& fillRef[newX][newY] == 0
							&& img.getPixelByte(newX, newY) != 0x00) {
						ImgPoint nextPoint = new ImgPoint(newX, newY);
						if(!set.contains(nextPoint)) {
							stack.push(nextPoint);
							set.add(nextPoint);
						}
					}
					
				}
				SmartDashboard.putNumber("Queue length", stack.size());
			}
			occurrences.put(id, pixels);
			centers.put(id, new ImgPoint((maxX+minX)/2, (maxY+minY)/2));
		}
		catch(Throwable t) {
			SmartDashboard.putString("ERROR", t.toString());
		}
	}
	//Return value is in RADIANS
	public static double getKeyPointAngle(Mat processedImg) throws VisionException {
		return getKeyPointAngle(processedImg, 0);
	}
	public static double getKeyPointAngle(Mat processedImg, int minSize) throws VisionException {
		//Do a flood fill
		byte[] imgData = new byte[(int) (processedImg.total() * processedImg.channels())];
		processedImg.get(0, 0, imgData);
		ByteArrayImg imgOut = new ByteArrayImg(imgData, processedImg.width(), processedImg.height());
		int[][] fillRef = new int[processedImg.width()][processedImg.height()];
		HashMap<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		HashMap<Integer, ImgPoint> centers = new HashMap<Integer, ImgPoint>();
		int sectionId = 1;
		for(int y = 0; y < processedImg.height(); y ++) {
			for(int x = 0; x < processedImg.width(); x ++) {
				if(imgOut.getPixelByte(x, y) != 0x00 && fillRef[x][y] == 0) {
					visionFloodFill(sectionId ++, x, y, fillRef, imgOut, occurrences, centers);
				}
			}
		}
		
		//Find out what the largest section is
		//Assuming that there is at least one section
		int maxSectionId = 1;
		for(int i = 1; i < sectionId; i ++) {
			if(occurrences.get(i) > occurrences.get(maxSectionId)) {
				maxSectionId = i;
			}
		}
		if(occurrences.get(maxSectionId) < minSize)
			throw new VisionException("The largest section is smaller than the minimum limit");
		ImgPoint center = centers.get(maxSectionId);
		double angle = Math.atan((center.x - RobotMap.VISION_CENTER) / RobotMap.VISION_FOCAL_LEN);
		
		return angle;
	}
	public static boolean expandPixels(ByteArrayImg img, ByteArrayImg imgOut) {
		boolean detected = false;
		for(int y = 0; y < img.height; y ++) {
			for(int x = 0; x < img.width; x ++) {
				if(img.getPixelByte(x, y) != 0x00) {
					detected = true;
					for(int i = 0; i < expandLocationsX.length; i ++) {
						imgOut.setPixelByte(x + expandLocationsX[i], y + expandLocationsY[i], 0xFF);
					}
				}
			}
		}
		return detected;
	}
	
	
	UsbCamera camera;
	CvSink sink;
	CvSource source;
	
	public VisionSubsystem(UsbCamera cam) {
		camera = cam;
		cameraInitBrightness = camera.getBrightness();
		camera.setResolution(RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
		sink = CameraServer.getInstance().getVideo();
		source = CameraServer.getInstance().putVideo("Vision Subsystem", RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT);
	}
	
	public void initDefaultCommand() {
		// Set the default command for a subsystem here.
		//setDefaultCommand(new MySpecialCommand());
	}
	
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
	
	public double getSwitchAngle() throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg1 = new Mat();
		Mat filteredImg2 = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		sink.grabFrame(originalImg, 1);
		Imgproc.resize(originalImg, buf, new Size(RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT));
		originalImg = buf;
		Imgproc.medianBlur(originalImg, buf, 3);
		//Convert the colour space from BGR to HSV
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		//Filter out the colours
		if(Robot.color.equals(Alliance.Red)) {
			Core.inRange(hsvImg, redLowerBound1, redUpperBound1, filteredImg1);
			Core.inRange(hsvImg, redLowerBound2, redUpperBound2, filteredImg2);
			Core.addWeighted(filteredImg1, 1.0, filteredImg2, 1.0, 0.0, filteredImg);
		}
		else {
			Core.inRange(hsvImg, blueLowerBound, blueUpperBound, filteredImg);
		}
		//Free up RAM
		filteredImg1 = null;
		filteredImg2 = null;
		originalImg = null;
		
		Imgproc.medianBlur(filteredImg, buf, 7);
		filteredImg = buf;
		//Process the image
		byte[] imgData = new byte[(int) (filteredImg.total() * filteredImg.channels())];
		filteredImg.get(0, 0, imgData);
		byte[] processed = new byte[imgData.length];
		ByteArrayImg img = new ByteArrayImg(imgData, filteredImg.width(), filteredImg.height());
		ByteArrayImg imgOut = new ByteArrayImg(processed, filteredImg.width(), filteredImg.height());
		
		//If the filter did not detect any pixels skip the next part
		if(!expandPixels(img, imgOut))
			throw new VisionException("Alliance colour not detected");
		//Free up some ram
		Mat processedImg = imgOut.toMat(filteredImg.type());
		imgOut = null;
		img = null;
		imgData = null;
		processed = null;
		
		return getKeyPointAngle(processedImg);
	}
	public double getCubeAngle() throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		sink.grabFrame(originalImg, 1);
		Imgproc.resize(originalImg, buf, new Size(RobotMap.VISION_WIDTH, RobotMap.VISION_HEIGHT));
		originalImg = buf;
		Imgproc.medianBlur(originalImg, buf, 3);
		//Convert the colour space from BGR to HSV
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		//Filter out the colours
		Core.inRange(hsvImg, cubeLowerBound, cubeUpperBound, filteredImg);

		
		//Process the image
		byte[] imgData = new byte[(int) (filteredImg.total() * filteredImg.channels())];
		filteredImg.get(0, 0, imgData);
		byte[] processed = new byte[imgData.length];
		ByteArrayImg img = new ByteArrayImg(imgData, filteredImg.width(), filteredImg.height());
		ByteArrayImg imgOut = new ByteArrayImg(processed, filteredImg.width(), filteredImg.height());
		
		//If the filter did not detect any pixels skip the next part
		if(!expandPixels(img, imgOut)) {
			throw new VisionException("Cube not detected");
		}
		//Free up some ram
		Mat processedImg = imgOut.toMat(filteredImg.type());
		source.putFrame(processedImg);
		imgOut = null;
		img = null;
		imgData = null;
		processed = null;
		
		return getKeyPointAngle(processedImg, 300);
	}
	
}
