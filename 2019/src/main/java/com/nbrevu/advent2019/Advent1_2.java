package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		long result=0l;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			// There might be better ways to do this, but discretisation really works against that in this case.
			long lastMass=Long.parseLong(line);
			for (;;)	{
				long newFuel=(lastMass/3)-2;
				if (newFuel<=0) break;
				result+=newFuel;
				lastMass=newFuel;
			}
		}
		System.out.println(result);
	}

}
