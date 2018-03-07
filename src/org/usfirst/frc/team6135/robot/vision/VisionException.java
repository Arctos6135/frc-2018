package org.usfirst.frc.team6135.robot.vision;

/**
 * Custom exception thrown by methods in Vision
 */
public class VisionException extends Exception {
	private static final long serialVersionUID = 4674148715358218109L;
	public VisionException(String message) {
		super(message);
	}
	public VisionException() {
		super();
	}
}