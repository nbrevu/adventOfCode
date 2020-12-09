package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent13_1 {
	private final static String IN_FILE="Advent13.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		long result=0;
		for (String content:Resources.readLines(file,Charsets.UTF_8))	{
			String[] parsed=content.split(": ");
			int index=Integer.parseInt(parsed[0]);
			int value=Integer.parseInt(parsed[1]);
			if ((index%(2*(value-1)))==0) result+=index*value;
		}
		System.out.println(result);
	}
}
