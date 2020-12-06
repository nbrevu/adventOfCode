package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_2_2 {
	private final static String IN_FILE="Advent2.txt";
	private final static Pattern PATTERN=Pattern.compile("^(\\d+)x(\\d+)x(\\d+)$");
	
	private static long getRequiredRibbonLength(long a,long b,long c)	{
		long prod=a*b*c;
		long sum=a+b+c;
		long max=Math.max(a,Math.max(b,c));
		return prod+2*(sum-max);
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] holder=new long[3];
		long result=0l;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=PATTERN.matcher(line);
			if (matcher.matches())	{
				for (int i=0;i<3;++i) holder[i]=Long.parseLong(matcher.group(i+1));
				result+=getRequiredRibbonLength(holder[0],holder[1],holder[2]);
			}
		}
		System.out.println(result);
	}
}
