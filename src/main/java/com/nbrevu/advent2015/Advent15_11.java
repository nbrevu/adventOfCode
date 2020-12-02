package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_11 {
	private final static String IN_FILE="2015/Advent11.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		int len=content.length();
		int counter=0;
		for (int i=0;i<len;++i)	{
			char c=content.charAt(i);
			if (c=='(') ++counter;
			else if (c==')') --counter;
		}
		System.out.println(counter);
	}
}
