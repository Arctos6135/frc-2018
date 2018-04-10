package org.usfirst.frc.team6135.robot.misc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.usfirst.frc.team6135.robot.Robot;
import org.usfirst.frc.team6135.robot.RobotMap;

import com.ctre.phoenix.motorcontrol.ControlMode;

public class AutoPlayback {
		BufferedReader reader;
		//Use String.split instead?
		StringTokenizer st;
		public static final String delim = ",\n";
		long startTime;
		boolean onTime = true;
		double nextDouble;
		

		public AutoPlayback(String auto) throws FileNotFoundException{
			//create a BufferedReader to read the file created during AutoRecord
			//scanner is able to read out the doubles recorded into [auto].csv
			if(auto != null) {
				reader = new BufferedReader(new InputStreamReader(new FileInputStream(auto)));
				
				//lets set start time to the current time you begin autonomous
				startTime = System.currentTimeMillis();	
			}
		}
		
		public void play() throws IOException{
			//if recordedAuto.csv has a double to read next, then read it
			String line;
			if ((reader != null) && (line = reader.readLine()) != null){
				st = new StringTokenizer(line, delim);
				double t_delta;
				
				//if we have waited the recorded amount of time assigned to each respective motor value,
				//then move on to the next double value
				//prevents the macro playback from getting ahead of itself and writing different
				//motor values too quickly
				if(onTime){
					//take next value
					nextDouble = Double.parseDouble(st.nextToken());
				}
				
				//time recorded for values minus how far into replaying it we are--> if not zero, hold up
				t_delta = nextDouble - (System.currentTimeMillis()-startTime);
				
				//if we are on time, then set motor values
				if (t_delta <= 0){
					double leftOutput = Double.parseDouble(st.nextToken());
					double rightOutput = Double.parseDouble(st.nextToken());
					//same order as record
					Robot.drive.setMotorsVBus(leftOutput, rightOutput);
					
					double elevatorOutput = Double.parseDouble(st.nextToken());
					double wristOutput = Double.parseDouble(st.nextToken());
					Robot.elevatorSubsystem.setSpeed(elevatorOutput);
					RobotMap.wristVictor.set(wristOutput);
					//go to next double
					onTime = true;
				}
				//else don't change the values of the motors until we are "onTime"
				else{
					onTime = false;
				}
			}
			//end play, there are no more values to find
			else{
				end();
				if (reader != null) {
					reader.close();
					st = null;
					reader = null;
				}
			}
			
		}
		
		//stop motors and end playing the recorded file
		public void end() throws IOException{
			
			Robot.drive.setMotorsVBus(0, 0);
			RobotMap.wristVictor.set(0);
			Robot.elevatorSubsystem.setSpeed(0);
			
			if (reader != null){
				reader.close();
			}
			
		}
		
}
