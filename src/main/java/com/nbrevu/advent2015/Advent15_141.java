package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_141 {
	private final static String IN_FILE="2015/Advent141.txt";
	private final static int RUN_TIME=2503;
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+) can fly (\\d+) km/s for (\\d+) seconds, but then must rest for (\\d+) seconds.$");
	
	private static class Reindeer	{
		public final int speed;
		public final int sprintLength;
		public final int restLength;
		public Reindeer(int speed,int sprintLength,int restLength)	{
			this.speed=speed;
			this.sprintLength=sprintLength;
			this.restLength=restLength;
		}
	}
	
	private static class RaceStatus	{
		private int distanceRun;
		private boolean isRunning;
		private int secondsToNextChange;
		private Reindeer reindeer;
		public RaceStatus(Reindeer reindeer)	{
			distanceRun=0;
			isRunning=true;
			secondsToNextChange=reindeer.sprintLength;
			this.reindeer=reindeer;
		}
		public void advanceOneSecond()	{
			if (isRunning) distanceRun+=reindeer.speed;
			--secondsToNextChange;
			if (secondsToNextChange<=0)	{
				isRunning=!isRunning;
				secondsToNextChange=isRunning?reindeer.sprintLength:reindeer.restLength;
			}
		}
		public int getDistance()	{
			return distanceRun;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<RaceStatus> reindeers=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int speed=Integer.parseInt(matcher.group(2));
				int sprintLength=Integer.parseInt(matcher.group(3));
				int restLength=Integer.parseInt(matcher.group(4));
				Reindeer reindeer=new Reindeer(speed,sprintLength,restLength);
				reindeers.add(new RaceStatus(reindeer));
			}
		}
		for (int i=0;i<RUN_TIME;++i) reindeers.forEach(RaceStatus::advanceOneSecond);
		int maxDistance=reindeers.stream().mapToInt(RaceStatus::getDistance).max().getAsInt();
		System.out.println(maxDistance);
	}
}
