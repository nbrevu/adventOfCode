package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.stream.LongStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		LongStream.Builder builder=LongStream.builder();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			try	{
				builder.accept(Long.parseLong(line));
			}	catch (NumberFormatException nfe)	{
				System.out.println("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		long[] allValues=builder.build().toArray();
		int counter=0;
		for (int i=3;i<allValues.length;++i) if (allValues[i]>allValues[i-3]) ++counter;
		System.out.println(counter);
	}
}
