package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent13_1 {
	private final static String IN_FILE="Advent13.txt";
	
	private static int[] parseIds(String idString)	{
		return Pattern.compile(",").splitAsStream(idString).filter((String s)->!s.equals("x")).mapToInt(Integer::parseInt).toArray();
	}
	
	private static int getMinWait(int currentTime,int[] ids)	{
		int minWait=Integer.MAX_VALUE;
		int result=0;
		for (int mod:ids)	{
			int rem=currentTime%mod;
			if (rem==0) return 0;
			int wait=mod-rem;
			if (wait<minWait)	{
				minWait=wait;
				result=wait*mod;
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		int value=Integer.parseInt(contents.get(0));
		int[] ids=parseIds(contents.get(1));
		System.out.println(getMinWait(value,ids));
	}
}
