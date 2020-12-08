package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		char[] chars=content.toCharArray();
		int result=0;
		int toAdd=chars.length/2;
		for (int i=0;i<chars.length;++i)	{
			char c=chars[i];
			char next=chars[(i+toAdd)%chars.length];
			if (c==next) result+=c-'0';
		}
		System.out.println(result);
	}
}
