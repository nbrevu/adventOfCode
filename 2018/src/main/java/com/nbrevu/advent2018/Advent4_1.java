package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Advent4_1 {
	private final static String IN_FILE="Advent4.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^\\[(\\d{4})\\-(\\d{2})\\-(\\d{2}) (\\d{2}):(\\d{2})\\] (.+)$");
	private final static String WAKE_UP="wakes up";
	private final static String ASLEEP="falls asleep";
	private final static Pattern GUARD_PATTERN=Pattern.compile("^Guard #(\\d+) begins shift$");
	
	private static class FullTime implements Comparable<FullTime>	{
		public final int year;
		public final int month;
		public final int day;
		public final int hour;
		public final int minute;
		private final long comparator;
		public FullTime(int year,int month,int day,int hour,int minute)	{
			this.year=year;
			this.month=month;
			this.day=day;
			this.hour=hour;
			this.minute=minute;
			comparator=minute+100l*(hour+100l*(day+100*(month+100*year)));
		}
		@Override
		public int compareTo(FullTime o) {
			return Long.compare(comparator,o.comparator);
		}
	}
	
	private static class Guard	{
		public final int id;
		private final int[] minuteCounters;
		public Guard(int id)	{
			this.id=id;
			minuteCounters=new int[60];
		}
		public int totalMinutesAsleep()	{
			int result=0;
			for (int c:minuteCounters) result+=c;
			return result;
		}
		public int getHighestMinute()	{
			int result=0;
			int maxCounter=minuteCounters[0];
			for (int i=1;i<60;++i) if (minuteCounters[i]>maxCounter)	{
				result=i;
				maxCounter=minuteCounters[i];
			}
			return result;
		}
		public void sleep(FullTime start,FullTime end)	{
			if ((start.year!=end.year)||(start.month!=end.month)||(start.day!=end.day)||(start.hour!=end.hour)) throw new IllegalArgumentException("¡Crono! ¡Crono!");
			else if (start.minute>=end.minute) throw new IllegalArgumentException("¡En esta casa se respetan las leyes de la termodinámica!");
			for (int i=start.minute;i<end.minute;++i) ++minuteCounters[i];
		}
	}
	
	private static Collection<Guard> analyseLogs(SortedMap<FullTime,String> logs)	{
		IntObjMap<Guard> result=HashIntObjMaps.newMutableMap();
		Guard currentGuard=null;
		Iterator<Map.Entry<FullTime,String>> iterator=logs.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry<FullTime,String> entry=iterator.next();
			String event=entry.getValue();
			Matcher guardMatcher=GUARD_PATTERN.matcher(event);
			if (guardMatcher.matches()) currentGuard=result.computeIfAbsent(Integer.parseInt(guardMatcher.group(1)),Guard::new);
			else if (ASLEEP.equals(event))	{
				FullTime sleepStart=entry.getKey();
				if (!iterator.hasNext()) throw new IllegalArgumentException("Eternal slumber.");
				Map.Entry<FullTime,String> entry2=iterator.next();
				if (!WAKE_UP.equals(entry2.getValue())) throw new IllegalArgumentException("¿Cu... culoman?");
				FullTime sleepEnd=entry2.getKey();
				currentGuard.sleep(sleepStart,sleepEnd);
			}
		}
		return result.values();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		SortedMap<FullTime,String> sortedLog=new TreeMap<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int year=Integer.parseInt(matcher.group(1));
				int month=Integer.parseInt(matcher.group(2));
				int day=Integer.parseInt(matcher.group(3));
				int hour=Integer.parseInt(matcher.group(4));
				int minute=Integer.parseInt(matcher.group(5));
				FullTime time=new FullTime(year,month,day,hour,minute);
				sortedLog.put(time,matcher.group(6));
			}
		}
		int higherSleep=Integer.MIN_VALUE;
		Guard bestGuard=null;
		for (Guard g:analyseLogs(sortedLog))	{
			int sleepTime=g.totalMinutesAsleep();
			if (sleepTime>higherSleep)	{
				higherSleep=sleepTime;
				bestGuard=g;
			}
		}
		int result=bestGuard.id*bestGuard.getHighestMinute();
		System.out.println(result);
	}

}
