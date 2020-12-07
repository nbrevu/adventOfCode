package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharIntMap;
import com.koloboke.collect.map.hash.HashCharIntMaps;

public class Advent4_1 {
	private final static String IN_FILE="Advent4.txt";
	
	private final static Pattern SPLITTER_PATTERN=Pattern.compile("^([a-z\\-]+)(\\d+)\\[([a-z]{5})\\]$");
	private final static Comparator<Map.Entry<Character,Integer>> CHECKSUM_COMPARATOR=new Comparator<>()	{
		@Override
		public int compare(Entry<Character,Integer> o1,Entry<Character,Integer> o2) {
			int diff1=o2.getValue().compareTo(o1.getValue());
			if (diff1!=0) return diff1;
			else return o1.getKey().compareTo(o2.getKey());
		}
	};
	
	private static String getChecksum(String str)	{
		CharIntMap charMap=HashCharIntMaps.newMutableMap();
		int len=str.length();
		for (int i=0;i<len;++i) if (str.charAt(i)!='-') charMap.addValue(str.charAt(i),1,0);
		List<Map.Entry<Character,Integer>> entries=new ArrayList<>(charMap.entrySet());
		entries.sort(CHECKSUM_COMPARATOR);
		StringBuilder result=new StringBuilder();
		for (int i=0;i<5;++i) if (i>=entries.size()) break;
		else result.append(entries.get(i).getKey());
		return result.toString();
	}
	
	private static boolean isChecksum(String str,String checksum)	{
		return checksum.equals(getChecksum(str));
	}
	
	public static void main(String[] args) throws IOException	{
		int sum=0;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=SPLITTER_PATTERN.matcher(line);
			if (matcher.matches())	{
				String firstPart=matcher.group(1);
				String checkSum=matcher.group(3);
				if (isChecksum(firstPart,checkSum)) sum+=Integer.parseInt(matcher.group(2));
			}
		}
		System.out.println(sum);
	}
}
