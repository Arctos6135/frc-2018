package org.usfirst.frc.team6135.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
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
	public static final int DIAMETER = 8; //INCHES
	public static final double CIRCUMFERENCE = DIAMETER*Math.PI;
	public static final double DRIVE_ENCODER_PPR = 2048;
	public static final double DISTANCE_PER_PULSE = CIRCUMFERENCE/DRIVE_ENCODER_PPR;
	public static double DRIVE_SPEED = 1.0;
	
	public static TalonSRX leftFrontDriveMotor = new TalonSRX(3);
    public static TalonSRX leftBackDriveMotor = new TalonSRX(2);	
    public static TalonSRX rightFrontDriveMotor = new TalonSRX(1);
    public static TalonSRX rightBackDriveMotor = new TalonSRX(4);
    
    public static VictorSP testMotor = new VictorSP(4);
    public static VictorSP clampLeft = new VictorSP(5);
    public static VictorSP clampRight = new VictorSP(6);
    
    public static Encoder testEncoder = new Encoder(2, 3, false, EncodingType.k4X);
	
	public static void init() {
		
		//Set back motors to follow the front motors
		leftBackDriveMotor.set(ControlMode.Follower, leftFrontDriveMotor.getDeviceID());
		rightBackDriveMotor.set(ControlMode.Follower, rightFrontDriveMotor.getDeviceID());
		
		leftFrontDriveMotor.set(ControlMode.Velocity, 0);
		rightFrontDriveMotor.set(ControlMode.Velocity, 0);
		testEncoder.setDistancePerPulse(DISTANCE_PER_PULSE);
		clampRight.setInverted(false);
		clampLeft.setInverted(true);
	}
}
