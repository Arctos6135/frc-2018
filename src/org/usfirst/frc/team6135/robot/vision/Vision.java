package org.usfirst.frc.team6135.robot.vision;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.usfirst.frc.team6135.robot.RobotMap;

import edu.wpi.cscore.CvSink;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Utilities class for vision processing with OpenCV.<br>
 * <b>Note:</b> For this class to function properly, the following public member variables in RobotMap must be set:<br>
 * <b>VISION_CENTER</b> - The X center of the vision camera, in <em>PIXELS</em><br>
 * <b>VISION_FOCAL_LEN</b> - The <em>focal length</em> of the vision camera
 */
public final class Vision {
	//Private constructor
	private Vision() {
	}
	
	//Locations for expanding pixels in processing
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
	//Locations for flood filling pixels in processing
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
	
	
	/**
	 * Non-Recursive Flood Fill.
	 * Used to find the centre point of sections and detect the largest section.
	 * 
	 * @param id - The section id
	 * @param x - The x coordinate of the starting pixel
	 * @param y - The y coordinate of the starting pixel
	 * @param fillRef - A two dimensional array used to store data about which pixel belongs to which section
	 * @param img - A black and white ByteArrayImg used as boundaries (flood fill ends upon reaching a black pixel)
	 * @param occurrences - A map used to store section sizes
	 * @param centers - A map used to store section center points
	 */
	public static void visionFloodFill(int id, int x, int y, int[][] fillRef, ByteArrayImg img, HashMap<Integer, Integer> occurrences, HashMap<Integer, ImgPoint> centers) {
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
				//SmartDashboard.putNumber("Queue length", stack.size());
			}
			occurrences.put(id, pixels);
			centers.put(id, new ImgPoint((maxX+minX)/2, (maxY+minY)/2));
		}
		catch(Throwable t) {
			SmartDashboard.putString("ERROR", t.toString());
		}
	}
	
	/**
	 * Returns the angle, in radians, of the "key point" (center point) of the largest blob
	 * from the image. The angle is based on focal length and applies to real life.
	 * Calling this method is the same as calling getKeyPointAngle(img, 1).
	 * 
	 * @param processedImg - The thresholded image
	 * @return The angle, in radians, of the "key point"'s X offset.
	 * @deprecated Better and faster results can be achieved using OpenCV libraries.
	 */
	public static double getKeyPointAngle(Mat processedImg) throws VisionException {
		return getKeyPointAngle(processedImg, 0);
	}
	/**
	 * Returns the angle, in radians, of the "key point" (center point) of the largest blob
	 * from the image. The angle is based on focal length and applies to real life.
	 * If the size of the largest blob is smaller than minSize, a VisionException will be thrown.
	 * 
	 * @param processedImg - The thresholded image
	 * @param minSize - The minimum size allowed
	 * @return The angle, in radians, of the "key point"'s X offset.
	 * @deprecated Better and faster results can be achieved using OpenCV libraries.
	 */
	public static double getKeyPointAngle(Mat processedImg, int minSize) throws VisionException {
		//Convert the Mat to a ByteArrayImg
		byte[] imgData = new byte[(int) (processedImg.total() * processedImg.channels())];
		processedImg.get(0, 0, imgData);
		ByteArrayImg imgOut = new ByteArrayImg(imgData, processedImg.width(), processedImg.height());
		//Create other parameters for flood fill
		int[][] fillRef = new int[processedImg.width()][processedImg.height()];
		HashMap<Integer, Integer> occurrences = new HashMap<Integer, Integer>();
		HashMap<Integer, ImgPoint> centers = new HashMap<Integer, ImgPoint>();
		int sectionId = 1;
		for(int y = 0; y < processedImg.height(); y ++) {
			for(int x = 0; x < processedImg.width(); x ++) {
				//Flood fill on every single pixel that's not already filled or black
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
		//Test to see if the size of the largest blob is bigger than the value specified
		//This reduces false positives
		if(occurrences.get(maxSectionId) < minSize)
			throw new VisionException("The largest section is smaller than the minimum limit");
		ImgPoint center = centers.get(maxSectionId);
		double angle = Math.atan((center.x - RobotMap.VISION_CENTER) / RobotMap.VISION_FOCAL_LEN);
		
		return angle;
	}
	
	/**
	 * Expands each white pixel in the image from 1x1 to 3x3.
	 * Used to connect different sections to make vision more accurate.
	 * 
	 * @param img - The source image
	 * @param imgOut - The destination image
	 * @return A boolean value, indicating if any white pixels were found in the image
	 * @deprecated Better results can be achieved using OpenCV's Imgproc.dilate()
	 */
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
	
	/**
	 * Gets all of the bounding rects of the contours in an image, then returns the biggest one.
	 * This assumes that the input image is already thresholded.
	 * 
	 * @param img - The input image
	 * @return A Rect, the bounding rectangle of the largest contour in the input image.
	 */
	public static Rect getBiggestBoundingRect(Mat img) throws VisionException {
		Mat buf = new Mat();
		//Get the structuring element for our morphological operation
		//NOTE: These docs are for Python and C++, but should be easy to understand with the help of JavaDocs.
		//TIP: In Eclipse, you can Ctrl + click to open the link in a browser
		//https://docs.opencv.org/2.4/modules/imgproc/doc/filtering.html#getstructuringelement
		Mat structuringElem = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
		//Dilate the image to join potentially separated elements
		//https://docs.opencv.org/2.4/doc/tutorials/imgproc/erosion_dilatation/erosion_dilatation.html
		Imgproc.dilate(img, buf, structuringElem);
		
		//Find the contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		//https://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html?highlight=findcontours#findcontours
		//Discard the hierarchy; we don't need that
		Imgproc.findContours(buf, contours, new Mat(), Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
		//Create a List to store the bounding rectangles in
		ArrayList<Rect> boundingRects = new ArrayList<Rect>();
		//Iterate through all the contours
		for(MatOfPoint contour : contours) {
			//Commented out for a performance boost. However precision may be affected; the specific effects are not known.
			/*
			//Convert this MatOfPoint to a MatOfPoint2f
			//The only difference is one uses floats and one uses ints
			//https://stackoverflow.com/questions/11273588/how-to-convert-matofpoint-to-matofpoint2f-in-opencv-java-api
			MatOfPoint2f contourf = new MatOfPoint2f(contour.toArray());
			//Approximate a polygonal curve
			//This will supposedly simplify the contour and approximate it to a polygon
			//https://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html#approxpolydp
			//https://docs.opencv.org/3.1.0/dd/d49/tutorial_py_contour_features.html
			//https://docs.opencv.org/3.4.0/dd/d49/tutorial_py_contour_features.html
			//First obtain the epsilon - the multiplier 0.1 was chosen according to the docs above
			int epsilon = (int) Math.round(0.1 * Imgproc.arcLength(contourf, true));
			//Then apply the approximation
			MatOfPoint2f approximated = new MatOfPoint2f();
			Imgproc.approxPolyDP(contourf, approximated, epsilon, true);
			//Now find the bounding rect
			//https://docs.opencv.org/3.1.0/d3/dc0/group__imgproc__shape.html#gacb413ddce8e48ff3ca61ed7cf626a366
			boundingRects.add(Imgproc.boundingRect(new MatOfPoint(approximated.toArray())));
			*/
			
			//Note that all of the above can be skipped for a potential accuracy penalty
			boundingRects.add(Imgproc.boundingRect(contour));
		}
		//Find the largest rect
		int maxRectIndex = -1;
		int maxRectSize = 0;
		for(int i = 0; i < boundingRects.size(); i ++) {
			int rectSize = (int) boundingRects.get(i).area();
			if(rectSize > maxRectSize) {
				maxRectSize = rectSize;
				maxRectIndex = i;
			}
		}
		if(maxRectIndex == -1)
			throw new VisionException("No bounding rect has a size > 0!");
		if(maxRectSize < 2500)
			throw new VisionException("Cube was not found");
		return boundingRects.get(maxRectIndex);
	}
	
	/**
	 * Finds the X angle offset from the center of the camera (in radians).
	 * 
	 * @param pt - An ImgPoint, the point of interest
	 * @return A double, the X angle offset from the center of the camera.
	 */
	public static double getXAngleOffset(ImgPoint pt) {
		return Math.atan((pt.x - RobotMap.VISION_CENTER) / RobotMap.VISION_FOCAL_LEN);
	}
	
	/**
	 * Retrieves a frame from a CvSink, resizes it and applies a threshold to it.
	 * 
	 * @param sink - The CvSink object representing the image source
	 * @param timeout - The timeout, in seconds, for getting the frame
	 * @param width - The width of the output
	 * @param height - The height of the output
	 * @param range - The colour filter to apply to the image, in HSV
	 * @return An image obtained from sink, with dimensions width and height and thresholded with range
	 * @exception VisionException If getting the frame takes longer than timeout seconds
	 */
	public static Mat grabThresholdedFrame(CvSink sink, double timeout, int width, int height, ColorRange range) throws VisionException {
		Mat originalImg = new Mat();
		Mat buf = new Mat();
		Mat buf2 = new Mat();
		Mat hsvImg = new Mat();
		//Grab the frame and filter + resize
		if(sink.grabFrame(originalImg, timeout) == 0) 
			throw new VisionException("Failed to obtain frame within timeout");
		Imgproc.resize(originalImg, buf, new Size(width, height));
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		Core.inRange(hsvImg, range.getLower(), range.getUpper(), buf2);
		return buf2;
	}
	/**
	 * Retrieves a frame from a CvSink, resizes it and applies a threshold to it.
	 * The two ColorRanges are applied separately and the result added together; 
	 * this comes in useful for filtering out red since in HSV its hue loops back after reaching 255, requiring 2 separate pairs of bounds.
	 * 
	 * @param sink - The CvSink object representing the image source
	 * @param timeout - The timeout, in seconds, for getting the frame
	 * @param width - The width of the output
	 * @param height - The height of the output
	 * @param range1 - The 1st colour filter to apply to the image, in HSV
	 * @param range2 - The 2nd colour filter to apply to the image, in HSV
	 * @return An image obtained from sink, with dimensions width and height and thresholded with range
	 * @exception VisionException If getting the frame takes longer than timeout seconds
	 */
	public static Mat grabThresholdedFrame(CvSink sink, double timeout, int width, int height, ColorRange range1, ColorRange range2) throws VisionException {
		Mat originalImg = new Mat();
		Mat buf = new Mat();
		Mat buf2 = new Mat();
		Mat buf3 = new Mat();
		Mat hsvImg = new Mat();
		//Grab the frame and filter + resize
		if(sink.grabFrame(originalImg, timeout) == 0) 
			throw new VisionException("Failed to obtain frame within timeout");
		Imgproc.resize(originalImg, buf, new Size(width, height));
		Imgproc.cvtColor(buf, hsvImg, Imgproc.COLOR_BGR2HSV_FULL);
		Core.inRange(hsvImg, range1.getLower(), range1.getUpper(), buf2);
		Core.inRange(hsvImg, range2.getLower(), range2.getUpper(), buf3);
		Core.addWeighted(buf2, 1.0, buf3, 1.0, 0.0, buf);
		return buf;
	}
}
