package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_1 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		long result=0l;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			long mass=Long.parseLong(line);
			result+=(mass/3)-2;
		}
		System.out.println(result);
	}

}
