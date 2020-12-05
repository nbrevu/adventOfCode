package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_81 {
	private final static String IN_FILE="2015/Advent81.txt";
	
	private static int count(String literal)	{
		if (literal.isBlank()) return 0;
		int index=literal.indexOf('\"');
		int end=literal.lastIndexOf('\"');
		int result=(index+1)+(literal.length()-end);
		++index;
		while (index<end) if (literal.charAt(index)=='\\')	{
			if (literal.charAt(index+1)=='x')	{
				index+=4;
				result+=3;
			}	else	{
				index+=2;
				++result;
			}
		}	else ++index;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) counter+=count(line);
		System.out.println(counter);
	}
}
