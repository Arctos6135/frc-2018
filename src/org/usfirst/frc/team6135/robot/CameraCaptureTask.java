package org.usfirst.frc.team6135.robot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import org.opencv.core.Mat;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;

/**
 * This TimerTask peroidically captures frames from the camera of {@code VisionSubsystem} 
 * and stores them as JPEGs in a USB drive.
 * @author Tyler
 *
 */
public class CameraCaptureTask extends TimerTask {

	/**
	 * The path to the usb drive. Note that the roboRIO uses Linux, and thus the path separator is '/' instead of '\'.
	 */
	public static final String PATH = "/u/";
	
	/**
	 * Converts a Mat to a BufferedImage, assuming the image is in BGR format.
	 * @param m - The source Mat
	 * @return The image represented as a BufferedImage
	 */
	static BufferedImage matToBufferedImage(Mat m) {
		byte[] data = new byte[(int) (m.width() * m.height() * m.elemSize())];
		m.get(0, 0, data);
		BufferedImage img = new BufferedImage(m.width(), m.height(), BufferedImage.TYPE_3BYTE_BGR);
		img.getRaster().setDataElements(0, 0, m.width(), m.height(), data);
		return img;
	}
	
	//The format used for the date
	//Year-Month-Day_Hour-Minute-Second_Millisecond
	SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss_SSS");
	
	boolean paused = false;

	public void pause() {
		paused = true;
	}
	public void resume() {
		paused = false;
	}
	public boolean getPaused() {
		return paused;
	}
	
	@Override
	public void run() {
		//Only store images if the vision subsystem is in regular video capture mode
		//When the mode is VISION it is likely that the camera is in use
		if(!paused && (Robot.visionSubsystem.getMode() == VisionSubsystem.Mode.VIDEO)) {
			Mat m = Robot.visionSubsystem.grabFrame();
			BufferedImage img = matToBufferedImage(m);
			m.release();
			
			Date time = new Date();
			
			File out = new File("RioCapture_" + dateFormat.format(time) + ".jpg");
			try {
				ImageIO.write(img, "jpg", out);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
