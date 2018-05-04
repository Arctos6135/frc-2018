package org.usfirst.frc.team6135.robot.misc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.usfirst.frc.team6135.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoPlayback {
	
	//Read from a BufferedReader since it's faster
	BufferedReader reader;
	//Value separator
	public static final String SEPARATOR = ",";
	//The time we started
	long startTime;
	//Whether playback is finished
	boolean playbackFinished = false;
	
	//The current line, split using the separator
	String[] currentLine;
	//The time the current line is supposed to execute at
	long currentLineTime;
	
	public AutoPlayback(String name) throws Exception{
		//Create our reader
		reader = new BufferedReader(new InputStreamReader(new FileInputStream(name)));
		//Set our start time to the current time
		startTime = System.currentTimeMillis();
	}
	
	public void play() throws IOException {
		//If the current line is not initalized and our reader is not null
		if(currentLine == null && reader != null) {
			String line = reader.readLine();
			
			if(line != null) {
				//If line is not null, set the value of currentLine and currentLineTime
				currentLine = line.split(SEPARATOR);
				currentLineTime = Long.parseLong(currentLine[0]);
			}
			else {
				//Otherwise we've already gone though all lines and playback is finished
				playbackFinished = true;
			}
		}
		
		if(!playbackFinished && reader != null) {
			//After waiting for how long do we have to run the line
			double t_delta = currentLineTime - (System.currentTimeMillis() - startTime);
			
			//If we are off by 10 milliseconds in both directions or less, run the line
			//Math.abs(t_delta) <= 10
			if(t_delta <= 0) {
				//Set the motors one by one in the exact order we stored them
				//Start at index 1 since index 0 is the time
				RobotMap.leftDriveTalon1.set(ControlMode.PercentOutput, Double.parseDouble(currentLine[1]));
				RobotMap.rightDriveTalon1.set(ControlMode.PercentOutput, Double.parseDouble(currentLine[2]));
				RobotMap.elevatorVictor.set(Double.parseDouble(currentLine[3]));
				RobotMap.intakeLeft.set(Double.parseDouble(currentLine[4]));
				RobotMap.intakeRight.set(Double.parseDouble(currentLine[5]));
				RobotMap.wristVictor.set(Double.parseDouble(currentLine[6]));
				//Move on to the next line
				String line = reader.readLine();
				
				if(line != null) {
					//If line is not null, set the value of currentLine and currentLineTime
					currentLine = line.split(SEPARATOR);
					currentLineTime = Long.parseLong(currentLine[0]);
				}
				else {
					//Otherwise we've already gone though all lines and playback is finished
					playbackFinished = true;
				}
			}
			//Otherwise if we are behind for more than 10 milliseconds, skip it
			/*
			else if(t_delta < -20) {
				//Loop until we get to the correct time
				while(true) {
					//Move on to the next line
					String line = reader.readLine();
					
					if(line != null) {
						//If line is not null, set the value of currentLine and currentLineTime
						currentLine = line.split(SEPARATOR);
						currentLineTime = Long.parseLong(currentLine[0]);
						//If with the new line we are not behind anymore, exit the loop
						//Otherwise, get the next line and try again
						if(currentLineTime - (System.currentTimeMillis() - startTime) >= 0) {
							break;
						}
					}
					else {
						//Otherwise we've already gone though all lines and playback is finished
						playbackFinished = true;
						break;
					}
				}
			}*/
		}
		else if(playbackFinished) {
			SmartDashboard.putBoolean("Recorded auto playback ended", playbackFinished);
			end();
		}
	}
	
	public boolean finished() {
		return playbackFinished;
	}
	
	public void end() throws IOException {
		//Stop everything
		RobotMap.leftDriveTalon1.set(ControlMode.PercentOutput, 0);
		RobotMap.rightDriveTalon1.set(ControlMode.PercentOutput, 0);
		RobotMap.elevatorVictor.set(0);
		RobotMap.intakeLeft.set(0);
		RobotMap.intakeRight.set(0);
		RobotMap.wristVictor.set(0);
		//Clean up
		currentLine = null;
		if(reader != null)
			reader.close();
	}
}
