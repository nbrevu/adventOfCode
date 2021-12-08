package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_1 {
	private final static String IN_FILE="Advent8.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) (\\w+) \\| (\\w+) (\\w+) (\\w+) (\\w+)$");
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		int count=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher m=LINE_PATTERN.matcher(line);
			if (!m.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int i=11;i<=14;++i)	{
				int length=m.group(i).length();
				if ((length!=5)&&(length!=6)) ++count;
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(count);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
