package org.usfirst.frc.team6135.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;


/**
 * The RobotMap is a mapping from the motor controllers, sensors and other hardware
 * to objects in the code, as well as some constants such as the max drive speed, making checking
 * the wiring easier and significantly reduces the number of magic numbers floating around.
 */
public class RobotMap {
	// For example to map the left and right motors, you could define the
	// following variables to use with your drivetrain subsystem.
	// public static TalonSRX leftMotor = new TalonSRX(0);
	// public static TalonSRX rightMotor = new TalonSRX(1);
	
	/*
	 * A mapping of the XBox controller. Use this static class instead of 
	 * magic numbers or individual constants to keep everything clear.
	 */
	public static class ControllerMap {
		public static final int LSTICK_X_AXIS = 0;
		public static final int LSTICK_Y_AXIS = 1;
		public static final int RSTICK_X_AXIS = 4;
		public static final int RSTICK_Y_AXIS = 5;
		public static final int LTRIGGER = 2;
		public static final int RTRIGGER = 3;
		
		public static final int BUTTON_A = 1;
		public static final int BUTTON_B = 2;
		public static final int BUTTON_X = 3;
		public static final int BUTTON_Y = 4;
		public static final int LBUMPER = 5;
		public static final int RBUMPER = 6;
		public static final int BUTTON_BACK = 7;
		public static final int BUTTON_START = 8;
		public static final int BUTTON_LSTICK = 9;
		public static final int BUTTON_RSTICK = 10;
	}
	
	/*
	 * Holds values for the dimensions of the arena. 
	 * Used in fallback (hard-coded) auto
	 * All units are in inches
	 */
	public static class ArenaDimensions {
		//12.0 feet
		//Actual auto line is 10 feet from the alliance wall, but an extra 2ft is added just in case
		public static final double BASELINE_DISTANCE = 12.0 * 12;
		//Minus 28 inches since 14ft is to the middle of the switch
		public static final double SWITCH_DISTANCE = 14.0 * 12 - 28;
		public static final double VISION_SAMPLING_DISTANCE = SWITCH_DISTANCE / 4;
		public static final double SWITCH_CENTRE_DISTANCE = 14.0 * 12;
		public static final double SWITCH_EDGE_OFFSET = 3.5 * 12.0 - ROBOT_LENGTH / 2;
		//Minus 3ft since we want to target the middle
		public static final double SWITCH_SIZE = 12.0 * 12 - 3.0 * 12;
		public static final double BASELINE_OFFSET = 3.0 * 12;
	}
	
	//+7in for the bumpers
	public static final double ROBOT_LENGTH = 32.0 + 7.0;
	/*
	 * Holds constants for the the top speeds of things to keep stuff organized 
	 */
	public static class Speeds {
		public static final double ELEVATOR_SPEED = 0.75;
		public static final double TILT_SPEED = 0.3;
		public static final double AUTO_SPEED = 0.3; //0.6
		public static final double AUTO_INTAKE_SPEED = 0.3;
		//This value is not final since there might be commands that change it
		public static double DRIVE_SPEED = 1.0;
	}
	
	public static final int WHEEL_DIAMETER = 6; //INCHES
	public static final double WHEEL_CIRCUMFRENCE = WHEEL_DIAMETER*Math.PI;
	public static final double DRIVE_ENCODER_PPR = 2048;
	public static final double DISTANCE_PER_PULSE = WHEEL_CIRCUMFRENCE/DRIVE_ENCODER_PPR;
	
	public static final int CAMERA_WIDTH = 640;
	public static final int CAMERA_HEIGHT = 360;
	public static final double CAMERA_FOV = 61.0;
	public static final int CAMERA_CENTER = CAMERA_WIDTH / 2;
	public static final double CAMERA_FOCAL_LEN = CAMERA_WIDTH / (2 * Math.tan(Math.toRadians(CAMERA_FOV / 2)));
	public static final int VISION_WIDTH = 320;
	public static final int VISION_HEIGHT = 180;
	public static final int VISION_CENTER = VISION_WIDTH / 2;
	public static final double VISION_FOCAL_LEN = VISION_WIDTH / (2 * Math.tan(Math.toRadians(CAMERA_FOV / 2)));
	
	public static TalonSRX leftDriveTalon1 = new TalonSRX(3);
    public static TalonSRX leftDriveTalon2 = new TalonSRX(2);	
    public static TalonSRX rightDriveTalon1 = new TalonSRX(1);
    public static TalonSRX rightDriveTalon2 = new TalonSRX(4);
    public static VictorSPX leftDriveVictor = new VictorSPX(5);
    public static VictorSPX rightDriveVictor = new VictorSPX(6);
    
    public static DoubleSolenoid gearshiftSolenoid = new DoubleSolenoid(0, 1);
    
    public static VictorSP elevatorVictor = new VictorSP(7);
    public static VictorSP wristVictor = new VictorSP(8);
    public static VictorSP intakeLeft = new VictorSP(5);
    public static VictorSP intakeRight = new VictorSP(6);
    
    public static Encoder rightEncoder = new Encoder(2, 3, false, EncodingType.k4X);
    public static Encoder leftEncoder = new Encoder(0, 1, true, EncodingType.k4X);
    
    public static DigitalInput elevatorTopSwitch = new DigitalInput(4);
    //public static DigitalInput elevatorBottomSwitch = new DigitalInput(5);
    
    //This is the gyroscope that is mounted in the SPI port of the roboRIO
    //Use the ADXRS450_Gyro class instead of AnalogGyro
    //NOT USED - The gyro is not positioned correctly on the robot and thus cannot be used
    public static ADXRS450_Gyro gyro = new ADXRS450_Gyro();
	
	public static void init() {
		//Set back motors to follow the front motors
		leftDriveTalon2.set(ControlMode.Follower, leftDriveTalon1.getDeviceID());
		rightDriveTalon2.set(ControlMode.Follower, rightDriveTalon1.getDeviceID());
		//leftDriveVictor.set(ControlMode.Follower, leftDriveTalon1.getDeviceID());
		//rightDriveVictor.set(ControlMode.Follower, rightDriveTalon1.getDeviceID());
		leftDriveVictor.setInverted(true);
		rightDriveVictor.setInverted(true);
		
		leftDriveTalon1.set(ControlMode.PercentOutput, 0);
		rightDriveTalon1.set(ControlMode.PercentOutput, 0);
		leftEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		intakeRight.setInverted(false);
		intakeLeft.setInverted(true);
	}
}
