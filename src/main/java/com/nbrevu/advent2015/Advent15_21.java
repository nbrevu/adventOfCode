package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_21 {
	private final static String IN_FILE="2015/Advent21.txt";
	private final static Pattern PATTERN=Pattern.compile("^(\\d+)x(\\d+)x(\\d+)$");
	
	private static long getRequiredPaperArea(long a,long b,long c)	{
		long prod1=a*b;
		long prod2=a*c;
		long prod3=b*c;
		long minProd=Math.min(Math.min(prod1,prod2),prod3);
		return 2*(prod1+prod2+prod3)+minProd;
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long[] holder=new long[3];
		long result=0l;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=PATTERN.matcher(line);
			if (matcher.matches())	{
				for (int i=0;i<3;++i) holder[i]=Long.parseLong(matcher.group(i+1));
				result+=getRequiredPaperArea(holder[0],holder[1],holder[2]);
			}
		}
		System.out.println(result);
	}
}
