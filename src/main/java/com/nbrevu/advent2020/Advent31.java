package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent31 {
	private final static String IN_FILE="Advent31.txt";
	
	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		int xpos=-3;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			xpos+=3;
			xpos%=line.length();
			if (line.charAt(xpos)=='#') ++counter;
		}
		System.out.println(counter);
	}
}
