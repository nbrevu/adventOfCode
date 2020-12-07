package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent9_2 {
	private final static String IN_FILE="Advent9.txt";
	
	private final static Pattern COMPRESSION_PATTERN=Pattern.compile("^([A-Z]*)\\((\\d+)x(\\d+)\\)(.+)$");
	
	private static long getDecompressedLengthRecursive(String str)	{
		long result=0;
		for (;;)	{
			Matcher matcher=COMPRESSION_PATTERN.matcher(str);
			if (matcher.matches())	{
				result+=matcher.group(1).length();
				int nextLength=Integer.parseInt(matcher.group(2));
				int times=Integer.parseInt(matcher.group(3));
				String remaining=matcher.group(4);
				String toRepeat=remaining.substring(0,nextLength);
				str=remaining.substring(nextLength);
				result+=times*getDecompressedLengthRecursive(toRepeat);
			}	else	{
				result+=str.length();
				return result;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		long result=getDecompressedLengthRecursive(content);
		System.out.println(result);
	}
}
