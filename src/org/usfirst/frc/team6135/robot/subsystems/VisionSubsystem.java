package org.usfirst.frc.team6135.robot.subsystems;

import java.util.ArrayDeque;
import java.util.HashMap;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class VisionSubsystem extends Subsystem {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	UsbCamera camera;
	CvSink sink;
	public static int cameraInitBrightness;
	public static final Scalar redUpperBound1 = new Scalar(10, 255, 255);
	public static final Scalar redLowerBound1 = new Scalar(0, 210, 115);
	public static final Scalar redLowerBound2 = new Scalar(245, 210, 115);
	public static final Scalar redUpperBound2 = new Scalar(255, 255, 255);
	public static final Scalar blueUpperBound = new Scalar(170, 255, 255);
	public static final Scalar blueLowerBound = new Scalar(145, 190, 75);
	public static final Scalar cubeUpperBound = new Scalar(49, 255, 255);
	public static final Scalar cubeLowerBound = new Scalar(32, 190, 10);
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
	
	public static class VisionException extends Exception {
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
	
	
	/*
	 * Non-Recursive Flood Fill
	 * Same signature as recursive version
	 * Uses BFS
	 */
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
	
	public double getSwitchAngle() throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg1 = new Mat();
		Mat filteredImg2 = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		sink.grabFrame(originalImg, 1);
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
		
		//Stop referencing the byte array to free up precious RAM
		img = null;
		imgData = null;
		
		//If the filter did not detect any pixels skip the next part
		if(noDetection)
			throw new VisionException("Alliance colour not detected");
		//Do a flood fill
		int[][] fillRef = new int[filteredImg.width()][filteredImg.height()];
		HashMap<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		HashMap<Integer, ImgPoint> centers = new HashMap<Integer, ImgPoint>();
		int sectionId = 1;
		for(int y = 0; y < filteredImg.height(); y ++) {
			for(int x = 0; x < filteredImg.width(); x ++) {
				if(imgOut.getPixelByte(x, y) != 0x00 && fillRef[x][y] == 0) {
					visionFloodFill(sectionId ++, x, y, fillRef, imgOut, occurrences, centers);
				}
			}
		}
		
		//Find out what the largest section is
		//It is safe to assume that at least one section exists since this code won't run if there are no white pixels
		int maxSectionId = 1;
		for(int i = 1; i < sectionId; i ++) {
			if(occurrences.get(i) > occurrences.get(maxSectionId)) {
				maxSectionId = i;
			}
		}
		ImgPoint center = centers.get(maxSectionId);
		double angle = Math.atan((center.x - RobotMap.CAMERA_CENTER) / RobotMap.CAMERA_FOCAL_LEN);
		
		return angle;
	}
	public double getCubeAngle() throws VisionException {
		Mat originalImg = new Mat();
		Mat hsvImg = new Mat();
		Mat filteredImg = new Mat();
		Mat buf = new Mat();
		
		//Obtain the frame from the camera (1 second timeout)
		sink.grabFrame(originalImg, 1);
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
		
		//Stop referencing the byte array to free up precious RAM
		img = null;
		imgData = null;
		if(noDetection) {
			throw new VisionException("Cube not detected");
		}
		
		int[][] fillRef = new int[filteredImg.width()][filteredImg.height()];
		HashMap<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		HashMap<Integer, ImgPoint> centers = new HashMap<Integer, ImgPoint>();
		int sectionId = 1;
		for(int y = 0; y < filteredImg.height(); y ++) {
			for(int x = 0; x < filteredImg.width(); x ++) {
				if(imgOut.getPixelByte(x, y) != 0x00 && fillRef[x][y] == 0) {
					visionFloodFill(sectionId ++, x, y, fillRef, imgOut, occurrences, centers);
				}
			}
		}
		
		//Find out what the largest section is
		//It is safe to assume that at least one section exists since this code won't run if there are no white pixels
		int maxSectionId = 1;
		for(int i = 1; i < sectionId; i ++) {
			if(occurrences.get(i) > occurrences.get(maxSectionId)) {
				maxSectionId = i;
			}
		}
		ImgPoint center = centers.get(maxSectionId);
		double angle = Math.atan((center.x - RobotMap.CAMERA_CENTER) / RobotMap.CAMERA_FOCAL_LEN);
		return Math.toDegrees(angle);
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public VisionSubsystem(UsbCamera cam) {
    	camera = cam;
    	cameraInitBrightness = camera.getBrightness();
    	camera.setResolution(RobotMap.CAMERA_WIDTH, RobotMap.CAMERA_HEIGHT);
    	sink = CameraServer.getInstance().getVideo();
    }
    
    public static enum Mode {
    	VISION,
    	VIDEO,
    }
    
    public void setMode(Mode mode) {
    	if(mode == Mode.VISION) {
    		sink.setEnabled(true);
    		camera.setBrightness(100);
            camera.setExposureManual(20);
            camera.setFPS(8);
    	}
    	else {
    		sink.setEnabled(false);
    		sink.setEnabled(false);
    		camera.setBrightness(cameraInitBrightness);
    		camera.setExposureAuto();
    		camera.setFPS(24);
    	}
    }
}

