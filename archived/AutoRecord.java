package org.usfirst.frc.team6135.robot.misc;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import org.usfirst.frc.team6135.robot.RobotMap;

public class AutoRecord {
	//this object writes values into the file we specify
	BufferedWriter writer;
	BufferedWriter flipped;
	long startTime;
	
	public AutoRecord(String auto) throws IOException {
		this(auto, true);
	}
	public AutoRecord(String auto, boolean createFlipped) throws IOException{
		if(auto != null) {
			//record the time we started recording
			startTime = System.currentTimeMillis();
			
			//put the filesystem location you are supposed to write to as a string 
			//as the argument in this method, it is /home/lvuser/[auto].csv
			writer = new BufferedWriter(new FileWriter(auto));
			if(createFlipped) {
				if(auto.length() >= 19) {
					// && auto.charAt(18) != 'M'
					String flippedAuto = auto.substring(0, 18) + (auto.charAt(18) == 'L'?'R':'L');
					if(auto.length() > 19) flippedAuto += auto.substring(19);
					flipped = new BufferedWriter(new FileWriter(flippedAuto));
				}
			}
		}
	}

	public void record() throws IOException{
		if(writer != null){
			//start each "frame" with the elapsed time since we started recording
			long time = (System.currentTimeMillis()-startTime);
			double leftOutput = RobotMap.leftDriveTalon1.getMotorOutputPercent();
			double rightOutput = RobotMap.rightDriveTalon1.getMotorOutputPercent();
			double intakeLeftOutput = RobotMap.intakeLeft.get();
			double intakeRightOutput = RobotMap.intakeRight.get();
			double elevatorOutput = RobotMap.elevatorVictor.get();
			double wristOutput = RobotMap.wristVictor.get();
			
			writer.append("" + time);
			
			//drive motors + elevator + intake
			
			writer.append("," + leftOutput);
			writer.append("," + rightOutput);
			writer.append("," + elevatorOutput);
			writer.append("," + intakeLeftOutput);
			writer.append("," + intakeRightOutput);
			/*
			 * THE LAST ENTRY OF THINGS YOU RECORD NEEDS TO HAVE A DELIMITER CONCATENATED TO 
			 * THE STRING AT THE END. OTHERWISE GIVES NOSUCHELEMENTEXCEPTION
			 */ 
			writer.append("," + wristOutput + "\n");
			/*
			 * CAREFUL. KEEP THE LAST THING YOU RECORD BETWEEN THESE TWO COMMENTS AS A
			 * REMINDER TO APPEND THE DELIMITER
			 */
			if(flipped != null) {
				flipped.append("" + time);
				flipped.append("," + rightOutput);
				flipped.append("," + leftOutput);
				flipped.append("," + elevatorOutput);
				flipped.append("," + intakeLeftOutput);
				flipped.append("," + intakeRightOutput);
				flipped.append("," + wristOutput + "\n");
			}
		}
	}
	
	//this method closes the writer and makes sure that all the data you recorded makes it into the file
	public void end() throws IOException{
		if(writer != null){
			writer.flush();
			writer.close();
		}
		if(flipped != null) {
			flipped.flush();
			flipped.close();
		}
	}
	
}
