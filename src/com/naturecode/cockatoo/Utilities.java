package com.naturecode.cockatoo;

public class Utilities {
	/**
	 * Function to convert milliseconds time to
	 * Timer Format
	 * Hours:Minutes:Seconds
	 * */
	public String milliSecondsToTimer(long milliseconds){
		String finalTimerString = "";
		String secondsString = "";
		String minutesString = "";

//		Convert total duration into time
		int hours = (int)( milliseconds / (1000*60*60));
		int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
		int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
		
		secondsString = seconds < 10?"0"+seconds:""+seconds;	
		minutesString = minutes < 10?"0"+minutes:""+minutes; 
		finalTimerString = hours > 0? hours + ":" + minutesString + ":" + secondsString:minutesString + ":" + secondsString;

		return finalTimerString;
	}

	/**
	 * Function to get Progress percentage
	 * @param currentDuration
	 * @param totalDuration
	 * */
	public int getProgressPercentage(long currentDuration, long totalDuration){
		Double percentage = (double) 0;
		long currentSeconds = (int) (currentDuration / 1000);
		long totalSeconds = (int) (totalDuration / 1000);

		percentage =(((double)currentSeconds)/totalSeconds)*100;
		return percentage.intValue();
	}

	/**
	 * Function to change progress to timer
	 * @param progress - 
	 * @param totalDuration
	 * returns current duration in milliseconds
	 * */
	public int progressToTimer(int progress, int totalDuration) {
		int currentDuration = 0;
		totalDuration = (int) (totalDuration / 1000);
		currentDuration = (int) ((((double)progress) / 100) * totalDuration);

		return currentDuration * 1000;
	}
}