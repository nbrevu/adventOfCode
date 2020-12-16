package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.collect.TreeRangeSet;
import com.google.common.io.Resources;

public class Advent16_1 {
	private final static String IN_FILE="Advent16.txt";
	
	private final static Pattern INITIAL_PATTERN=Pattern.compile("^.+: (\\d+)\\-(\\d+) or (\\d+)\\-(\\d+)$");
	
	private static RangeSet<Integer> parseRanges(List<String> ticketDefinitions)	{
		List<Range<Integer>> ranges=new ArrayList<>();
		for (String s:ticketDefinitions)	{
			Matcher matcher=INITIAL_PATTERN.matcher(s);
			if (!matcher.matches()) throw new IllegalArgumentException("El formato está mal. Claramente sólo se podrá arreglar con format c:");
			ranges.add(Range.closed(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2))));
			ranges.add(Range.closed(Integer.parseInt(matcher.group(3)),Integer.parseInt(matcher.group(4))));
		}
		return TreeRangeSet.create(ranges);
	}
	
	private static List<Integer> parseValues(List<String> ticketValues)	{
		List<Integer> result=new ArrayList<>();
		for (String s:ticketValues) for (String split:s.split(",")) result.add(Integer.parseInt(split));
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		int separator=content.indexOf("");
		if (separator<0) throw new IllegalArgumentException("Wrong.");
		RangeSet<Integer> validRanges=parseRanges(content.subList(0,separator));
		List<Integer> ticketValues=parseValues(content.subList(separator+5,content.size()));
		int result=0;
		for (Integer i:ticketValues) if (!validRanges.contains(i)) result+=i.intValue();
		System.out.println(result);
	}
}
