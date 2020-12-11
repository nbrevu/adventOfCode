package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	
	private static String getDiffIfValid(String s1,String s2)	{
		int diffChar=-1;
		if (s1.length()!=s2.length()) return null;	// I don't think this actually happens.
		for (int i=0;i<s1.length();++i) if (s1.charAt(i)!=s2.charAt(i))	{
			if (diffChar==-1) diffChar=i;
			else return null;
		}
		return s1.substring(0,diffChar)+s1.substring(diffChar+1);
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		for (int i=0;i<lines.size();++i) for (int j=i+1;j<lines.size();++j)	{
			String diff=getDiffIfValid(lines.get(i),lines.get(j));
			if (diff!=null)	{
				System.out.println(diff);
				return;
			}
		}
	}

}
