
package org.usfirst.frc.team6135.robot;

import org.usfirst.frc.team6135.robot.commands.defaultcommands.TeleopDrive;
import org.usfirst.frc.team6135.robot.subsystems.DriveTrain;
import org.usfirst.frc.team6135.robot.subsystems.ElevatorSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.GearShiftSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.IntakeSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.VisionSubsystem;
import org.usfirst.frc.team6135.robot.subsystems.WristSubsystem;

import com.ctre.phoenix.motorcontrol.NeutralMode;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends TimedRobot {
	
	//SUBSYSTEMS
	public static OI oi;
	public static DriveTrain drive;
	public static IntakeSubsystem intakeSubsystem;
	public static GearShiftSubsystem gearShiftSubsystem;
	public static ElevatorSubsystem elevatorSubsystem;
	public static WristSubsystem wristSubsystem;
	public static VisionSubsystem visionSubsystem;
	
	public static Alliance color; //Red or Blue
	public static int station; //Driver station number (1, 2 or 3)
	public static String gameData; //Used to tell the locations of the switch/scale plates
	
	//Location consts, used in auto choosing
	public static final int LOCATION_LEFT = -1;
	public static final int LOCATION_MID = 0;
	public static final int LOCATION_RIGHT = 1;
	
	//Auto mode consts, used in auto choosing
	public static final int AUTO_DEBUG = 0x00;
	public static final int AUTO_BASELINE = 0x01;
	public static final int AUTO_ALIGNED = 0x02;
	public static final int AUTO_SIDE = 0x03;
	public static final int AUTO_MIDDLE = 0x04;
	
	//Autonomous command choosers
	public static SendableChooser<Object> nullChooser = new SendableChooser<>();
	
	public void putTunables() {
		SmartDashboard.putNumber("Drive Speed Percentage", Robot.drive.getSpeedMultiplier());
	}
	public void updateTunables() {
		Robot.drive.setSpeedMultiplier(SmartDashboard.getNumber("Drive Speed Percentage", Robot.drive.getSpeedMultiplier()));
	}
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	@Override
	public void robotInit() {
		//Initialize our subsystems
		RobotMap.init();
		drive = new DriveTrain();
		intakeSubsystem = new IntakeSubsystem();
		gearShiftSubsystem = new GearShiftSubsystem();
		elevatorSubsystem = new ElevatorSubsystem();
		wristSubsystem = new WristSubsystem();
		
		//Initialize camera stream and vision subsystem
        visionSubsystem = new VisionSubsystem(CameraServer.getInstance().startAutomaticCapture());
        //Set camera config
        visionSubsystem.setMode(VisionSubsystem.Mode.VIDEO); //For vision, change to Mode.VISION
        
        //Get the team's colour and station number
        station = DriverStation.getInstance().getLocation();
        color = DriverStation.getInstance().getAlliance();
        //Game data is retrieved later
        
        //OI must be initialized after all subsystems, because it maps buttons to commands, and those commands
        //require subsystems to be properly initialized
        oi = new OI();

        nullChooser.addDefault("(Autonomous Not Available)", null);
        SmartDashboard.putData(nullChooser);
        putTunables();
	}
	
	/**
	 * This function is called periodically during all robot modes.
	 * Code that needs to be run regardless of mode, such as the printing of information,
	 * can be put here.
	 */
	@Override
	public void robotPeriodic() {
    	SmartDashboard.putBoolean("Elevator Top Switch", Robot.elevatorSubsystem.notAtTop());
    	SmartDashboard.putBoolean("Elevator Bottom Switch", Robot.elevatorSubsystem.notAtBottom());
    	SmartDashboard.putBoolean("Wrist Switch", Robot.wristSubsystem.notAtTop());
		
    	SmartDashboard.putBoolean("Demo Mode", OI.isInDemoMode);
    	SmartDashboard.putBoolean("Demo Mode Operator Enabled", !OI.attachmentsControllerBlocked);

    	SmartDashboard.putBoolean("Drive Ramping", TeleopDrive.isRamped());
	}

	/**
	 * This function is called once each time the robot enters Disabled mode.
	 * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
	 */
	@Override
	public void disabledInit() {
		RobotMap.setAllMotorNeuralModes(NeutralMode.Coast);
	}

	@Override
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * Called when the robot first enters autonomous mode. Start auto commands here.
	 */
	@Override
	public void autonomousInit() {
	
	}
	
	@Override
	public void autonomousPeriodic() {
		Scheduler.getInstance().run();
		
	}

	@Override
	public void teleopInit() {
		updateTunables();

		RobotMap.setAllMotorNeuralModes(NeutralMode.Coast);
	}

	/**
	 * This function is called periodically during operator control
	 */
	@Override
	public void teleopPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This function is called periodically during test mode
	 */
	@Override
	public void testPeriodic() {
		Scheduler.getInstance().run();
	}
}
