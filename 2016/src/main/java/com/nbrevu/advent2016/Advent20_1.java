package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.io.Resources;

public class Advent20_1 {
	private final static String IN_FILE="Advent20.txt";
	
	private static long getFirstNotInRange(RangeSet<Long> ranges)	{
		long firstAvailable=0l;
		for (Range<Long> range:ranges.asRanges())	{
			long start=range.lowerEndpoint();
			long end=range.upperEndpoint();
			if (start>firstAvailable) return firstAvailable;
			else firstAvailable=1+end;
		}
		return firstAvailable;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		RangeSet<Long> ranges=TreeRangeSet.create();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			String[] split=line.split("-");
			if (split.length!=2) throw new IllegalArgumentException("Me estoy volviendo loco, poco a poco, poco a poco.");
			long start=Long.parseLong(split[0]);
			long end=Long.parseLong(split[1]);
			ranges.add(Range.closed(start,end));
		}
		System.out.println(getFirstNotInRange(ranges));
	}
}
