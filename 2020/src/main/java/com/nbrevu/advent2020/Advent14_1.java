package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.IntLongMap;
import com.koloboke.collect.map.hash.HashIntLongMaps;

public class Advent14_1 {
	private final static String IN_FILE="Advent14.txt";
	
	private final static Pattern MASK_PATTERN=Pattern.compile("^mask = ([01X]{36})$");
	private final static Pattern ASSIGNMENT_PATTERN=Pattern.compile("^mem\\[(\\d+)\\] = (\\d+)$");
	
	private static class Mask	{
		private final long mask0;
		private final long mask1;
		private Mask(long mask0,long mask1)	{
			this.mask0=mask0;
			this.mask1=mask1;
		}
		public long apply(long in)	{
			return (in&mask0)|mask1;
		}
		public static Mask getMask(String in)	{
			long mask0=-1;
			long mask1=0;
			long bit=1;
			for (int i=in.length()-1;i>=0;--i)	{
				switch (in.charAt(i))	{
					case '0':mask0-=bit;break;
					case '1':mask1+=bit;break;
				}
				bit<<=1;
			}
			return new Mask(mask0,mask1);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		IntLongMap memory=HashIntLongMaps.newMutableMap();
		Mask currentMask=null;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher maskMatcher=MASK_PATTERN.matcher(line);
			if (maskMatcher.matches())	{
				currentMask=Mask.getMask(maskMatcher.group(1));
				continue;
			}
			Matcher assignmentMatcher=ASSIGNMENT_PATTERN.matcher(line);
			if (assignmentMatcher.matches())	{
				int address=Integer.parseInt(assignmentMatcher.group(1));
				long value=Long.parseLong(assignmentMatcher.group(2));
				memory.put(address,currentMask.apply(value));
			}
		}
		long result=0;
		for (LongCursor cursor=memory.values().cursor();cursor.moveNext();) result+=cursor.elem();
		System.out.println(result);
	}
}
