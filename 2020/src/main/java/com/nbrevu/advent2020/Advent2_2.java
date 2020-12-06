package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	private final static Pattern PATTERN=Pattern.compile("^(\\d+)-(\\d+) (.): (.+)$");
	
	private static boolean isValid(int pos1,int pos2,char letter,String password)	{
		if ((pos1<1)||(pos1>password.length())) return false;
		char c1=password.charAt(pos1-1);
		if ((pos2<1)||(pos2>password.length())) return false;
		char c2=password.charAt(pos2-1);
		return (c1==letter)!=(c2==letter);
	}

	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			try	{
				Matcher matcher=PATTERN.matcher(line);
				if (matcher.matches())	{
					int pos1=Integer.parseInt(matcher.group(1));
					int pos2=Integer.parseInt(matcher.group(2));
					char letter=matcher.group(3).charAt(0);
					String password=matcher.group(4);
					if (isValid(pos1,pos2,letter,password)) ++counter;
				}
			}	catch (NumberFormatException nfe)	{
				System.out.println("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		System.out.println(counter);
	}
}
