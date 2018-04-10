package org.usfirst.frc.team6135.robot.misc;

import java.io.FileWriter;
import java.io.IOException;

import org.usfirst.frc.team6135.robot.RobotMap;

public class AutoRecord {
	//this object writes values into the file we specify
	FileWriter writer;
	
	long startTime;
	
	public AutoRecord(String auto) throws IOException{
		if(auto != null) {
			//record the time we started recording
			startTime = System.currentTimeMillis();
			
			//put the filesystem location you are supposed to write to as a string 
			//as the argument in this method, it is /home/lvuser/[auto].csv
			writer = new FileWriter(auto);
		}
	}
	

	public void record() throws IOException{
		if(writer != null){
			//start each "frame" with the elapsed time since we started recording
			writer.append("" + (System.currentTimeMillis()-startTime));
			
			//drive motors + elevator
			writer.append("," + RobotMap.leftDriveTalon1.getMotorOutputPercent());
			writer.append("," + RobotMap.rightDriveTalon1.getMotorOutputPercent());
			writer.append("," + RobotMap.elevatorVictor.get());
			writer.append("," + RobotMap.intakeLeft.get());
			writer.append("," + RobotMap.intakeRight.get());
			/*
			 * THE LAST ENTRY OF THINGS YOU RECORD NEEDS TO HAVE A DELIMITER CONCATENATED TO 
			 * THE STRING AT THE END. OTHERWISE GIVES NOSUCHELEMENTEXCEPTION
			 */ 
			writer.append("," + RobotMap.wristVictor.get() + "\n");
			/*
			 * CAREFUL. KEEP THE LAST THING YOU RECORD BETWEEN THESE TWO COMMENTS AS A
			 * REMINDER TO APPEND THE DELIMITER
			 */
		}
	}
	
	
	//this method closes the writer and makes sure that all the data you recorded makes it into the file
	public void end() throws IOException{
		if(writer != null){
			writer.flush();
			writer.close();
		}
	}
}
