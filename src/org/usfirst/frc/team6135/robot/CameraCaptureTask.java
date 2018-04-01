package org.usfirst.frc.team6135.robot;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
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
	
	//A value incremented by each capture to ensure no two images have the same names.
	static long identifier = 0x00;
	
	@Override
	public void run() {
		//Only store images if the vision subsystem is in regular video capture mode
		//When the mode is VISION it is likely that the camera is in use
		if(Robot.visionSubsystem.getMode() == VisionSubsystem.Mode.VIDEO) {
			Mat m = Robot.visionSubsystem.grabFrame();
			BufferedImage img = matToBufferedImage(m);
			m.release();
			LocalDateTime time = LocalDateTime.now();
			//RIOCAPTURE_YYYY_MM_DD-HH_MM_SS-ID
			StringBuilder name = new StringBuilder("RIOCAPTURE_");
			name.append(time.getYear())
			.append(time.getMonthValue())
			.append("_")
			.append(time.getDayOfMonth())
			.append("-")
			.append(time.getHour())
			.append("_")
			.append(time.getMinute())
			.append("_")
			.append(time.getSecond())
			.append("-")
			.append(identifier ++)
			.append(".jpg");
			File out = new File(name.toString());
			try {
				ImageIO.write(img, "jpg", out);
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
