package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_51 {
	private final static String IN_FILE="2015/Advent51.txt";
	
	private final static String VOWELS="aeiou";
	private final static String FORBIDDEN="acpx";
	
	private static boolean isVowel(char v)	{
		return VOWELS.indexOf(v)>=0;
	}
	
	private static boolean isForbidden(char prev,char cur)	{
		return ((prev+1)==cur)&&(FORBIDDEN.indexOf(prev)>=0);
	}
	
	private static boolean isNice(String str)	{
		int len=str.length();
		char lastChar=str.charAt(0);
		int vowels=isVowel(lastChar)?1:0;
		boolean repeated=false;
		for (int i=1;i<len;++i)	{
			char curChar=str.charAt(i);
			if (isForbidden(lastChar,curChar)) return false;
			if (isVowel(curChar)) ++vowels;
			if (lastChar==curChar) repeated=true;
			lastChar=curChar;
		}
		return repeated&&(vowels>=3);
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int counter=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (isNice(line.toLowerCase())) ++counter;
		System.out.println(counter);
	}
}
