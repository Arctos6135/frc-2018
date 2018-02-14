package org.usfirst.frc.team6135.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;


/**
 * The RobotMap is a mapping from the ports sensors and actuators are wired into
 * to a variable name. This provides flexibility changing wiring, makes checking
 * the wiring easier and significantly reduces the number of magic numbers
 * floating around.
 */
public class RobotMap {
	// For example to map the left and right motors, you could define the
	// following variables to use with your drivetrain subsystem.
	// public static int leftMotor = 1;
	// public static int rightMotor = 2;

	// If you are using multiple modules, make sure to define both the port
	// number and the module. For example you with a rangefinder:
	// public static int rangefinderPort = 1;
	// public static int rangefinderModule = 1;
	
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
	
	public static final double ELEVATOR_TOP_SPEED = 0.5;
	public static final double TILT_TOP_SPEED = 0.3;
	
	public static final int DIAMETER = 8; //INCHES
	public static final double CIRCUMFERENCE = DIAMETER*Math.PI;
	public static final double DRIVE_ENCODER_PPR = 2048;
	public static final double DISTANCE_PER_PULSE = CIRCUMFERENCE/DRIVE_ENCODER_PPR;
	public static double DRIVE_SPEED = 1.0;
	
	//NOTE: 2018 Robot has 6 minicims instead of 4!
	public static TalonSRX leftFrontDriveMotor = new TalonSRX(3);
    public static TalonSRX leftBackDriveMotor = new TalonSRX(2);	
    public static TalonSRX rightFrontDriveMotor = new TalonSRX(1);
    public static TalonSRX rightBackDriveMotor = new TalonSRX(4);
    
    public static DoubleSolenoid gearshiftSolenoid = new DoubleSolenoid(0, 1);
    
    public static VictorSP elevatorVictor = new VictorSP(3);
    public static VictorSP tiltVictor = new VictorSP(4);
    public static VictorSP intakeLeft = new VictorSP(5);
    public static VictorSP intakeRight = new VictorSP(6);
    
    public static Encoder rightEncoder = new Encoder(2, 3, false, EncodingType.k4X);
    public static Encoder leftEncoder = new Encoder(0, 1, false, EncodingType.k4X);
	
	public static void init() {
		//Set back motors to follow the front motors
		leftBackDriveMotor.set(ControlMode.Follower, leftFrontDriveMotor.getDeviceID());
		rightBackDriveMotor.set(ControlMode.Follower, rightFrontDriveMotor.getDeviceID());
		
		leftFrontDriveMotor.set(ControlMode.PercentOutput, 0);
		rightFrontDriveMotor.set(ControlMode.PercentOutput, 0);
		leftEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		rightEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		intakeRight.setInverted(false);
		intakeLeft.setInverted(true);
	}
}
