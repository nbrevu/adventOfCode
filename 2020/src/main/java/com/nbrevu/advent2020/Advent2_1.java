package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	private final static Pattern PATTERN=Pattern.compile("^(\\d+)-(\\d+) (.): (.+)$");
	
	private static boolean isValid(int min,int max,char letter,String password)	{
		int count=0;
		for (int i=password.indexOf(letter);i>=0;i=password.indexOf(letter,1+i))	{
			++count;
			if (count>max) return false;
		}
		return count>=min;
	}

	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			try	{
				Matcher matcher=PATTERN.matcher(line);
				if (matcher.matches())	{
					int min=Integer.parseInt(matcher.group(1));
					int max=Integer.parseInt(matcher.group(2));
					char letter=matcher.group(3).charAt(0);
					String password=matcher.group(4);
					if (isValid(min,max,letter,password)) ++counter;
				}
			}	catch (NumberFormatException nfe)	{
				System.out.println("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		System.out.println(counter);
	}
}
