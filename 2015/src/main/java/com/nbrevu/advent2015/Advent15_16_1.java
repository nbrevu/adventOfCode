package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent15_16_1 {
	private final static String IN_FILE="Advent16.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Sue (\\d+): ([a-z]+): (\\d+), ([a-z]+): (\\d+), ([a-z]+): (\\d+)$");
	private final static ObjIntMap<String> LETTER_FINDINGS=HashObjIntMaps.newImmutableMap(new String[] {"children","cats","samoyeds","pomeranians","akitas","vizslas","goldfish","trees","cars","perfumes"},new int[] {3,7,2,3,0,0,5,3,2,1});

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		IntSet result=HashIntSets.newMutableSet();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int index=Integer.parseInt(matcher.group(1));
				String s1=matcher.group(2);
				int q1=Integer.parseInt(matcher.group(3));
				String s2=matcher.group(4);
				int q2=Integer.parseInt(matcher.group(5));
				String s3=matcher.group(6);
				int q3=Integer.parseInt(matcher.group(7));
				int real1=LETTER_FINDINGS.getInt(s1);
				int real2=LETTER_FINDINGS.getInt(s2);
				int real3=LETTER_FINDINGS.getInt(s3);
				if ((real1==q1)&&(real2==q2)&&(real3==q3)) result.add(index);
			}
		}
		if (result.size()!=1) throw new IllegalStateException("Nicht möglich!!!!!");
		System.out.println(result.toArray(new int[1])[0]);
	}
}
