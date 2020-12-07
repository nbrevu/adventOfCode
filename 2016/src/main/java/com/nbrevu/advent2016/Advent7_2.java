package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent7_2 {
	private final static String IN_FILE="Advent7.txt";
	
	private static class TwoChars	{
		public final char p13;
		public final char p2;
		public TwoChars(char p13,char p2)	{
			this.p13=p13;
			this.p2=p2;
		}
	}
	
	private static boolean isAba(String str,int startPos)	{
		char c1=str.charAt(startPos);
		char c2=str.charAt(startPos+1);
		return (c1!=c2)&&(c1==str.charAt(startPos+2));
	}
	
	private static List<TwoChars> getAbas(String str)	{
		List<TwoChars> result=new ArrayList<>();
		int last=str.length()-2;
		for (int i=0;i<last;++i) if (isAba(str,i)) result.add(new TwoChars(str.charAt(i),str.charAt(i+1)));
		return result;
	}
	
	private static class StringSets	{
		private final static Pattern BRACKET_PATTERN=Pattern.compile("^([a-z]+)\\[([a-z]+)\\](.+)$");
		private final List<String> outBrackets;
		private final List<String> inBrackets;
		public StringSets(List<String> outBrackets,List<String> inBrackets)	{
			this.outBrackets=outBrackets;
			this.inBrackets=inBrackets;
		}
		public boolean isSsl()	{
			List<TwoChars> allAbas=collectAbas(outBrackets);
			List<TwoChars> allBabs=collectAbas(inBrackets);
			for (TwoChars aba:allAbas) for (TwoChars bab:allBabs) if ((aba.p13==bab.p2)&&(aba.p2==bab.p13)) return true;
			return false;
		}
		private List<TwoChars> collectAbas(List<String> strs)	{
			return strs.stream().map(Advent7_2::getAbas).flatMap(List::stream).collect(Collectors.toUnmodifiableList());
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
			if (sets.isSsl()) ++count;
		}
		System.out.println(count);
	}
}
