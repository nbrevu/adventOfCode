package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent7_1 {
	private final static String IN_FILE="Advent7.txt";
	
	private static boolean isAbba(String str,int startPos)	{
		char c1=str.charAt(startPos);
		char c2=str.charAt(startPos+1);
		return (c1!=c2)&&(c2==str.charAt(startPos+2))&&(c1==str.charAt(startPos+3));
	}
	
	private static boolean containsAbba(String str)	{
		int last=str.length()-3;
		for (int i=0;i<last;++i) if (isAbba(str,i)) return true;
		return false;
	}
	
	private static class StringSets	{
		private final static Pattern BRACKET_PATTERN=Pattern.compile("^([a-z]+)\\[([a-z]+)\\](.+)$");
		private final List<String> outBrackets;
		private final List<String> inBrackets;
		public StringSets(List<String> outBrackets,List<String> inBrackets)	{
			this.outBrackets=outBrackets;
			this.inBrackets=inBrackets;
		}
		public boolean isTls()	{
			for (String s:inBrackets) if (containsAbba(s)) return false;
			for (String s:outBrackets) if (containsAbba(s)) return true;
			return false;
		}
		public static StringSets parse(String str)	{
			List<String> outBrackets=new ArrayList<>();
			List<String> inBrackets=new ArrayList<>();
			for (;;)	{
				Matcher matcher=BRACKET_PATTERN.matcher(str);
				if (matcher.matches())	{
					outBrackets.add(matcher.group(1));
					inBrackets.add(matcher.group(2));
					str=matcher.group(3);
				}	else	{
					outBrackets.add(str);
					return new StringSets(outBrackets,inBrackets);
				}
			}
		}
		@Override
		public String toString()	{
			return "{"+outBrackets+","+inBrackets+"}";
		}
	}
	
	public static void main(String[] args) throws IOException	{
		int count=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			StringSets sets=StringSets.parse(line);
			if (sets.isTls()) ++count;
		}
		System.out.println(count);
	}
}
