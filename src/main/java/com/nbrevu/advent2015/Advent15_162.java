package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.Range;
import com.google.common.io.Resources;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent15_162 {
	private final static String IN_FILE="2015/Advent161.txt";
	
	// Sue 498: perfumes: 7, vizslas: 6, cats: 9
	private final static Pattern LINE_PATTERN=Pattern.compile("^Sue (\\d+): ([a-z]+): (\\d+), ([a-z]+): (\\d+), ([a-z]+): (\\d+)$");
	private final static Map<String,Range<Integer>> LETTER_FINDINGS=getLetterFindings();
	private static Map<String,Range<Integer>> getLetterFindings()	{
		Map<String,Range<Integer>> result=new HashMap<>();
		result.put("children",Range.singleton(3));
		result.put("cats",Range.greaterThan(7));
		result.put("samoyeds",Range.singleton(2));
		result.put("pomeranians",Range.lessThan(3));
		result.put("akitas",Range.singleton(0));
		result.put("vizslas",Range.singleton(0));
		result.put("goldfish",Range.lessThan(5));
		result.put("trees",Range.greaterThan(3));
		result.put("cars",Range.singleton(2));
		result.put("perfumes",Range.singleton(1));
		return result;
	}

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
				Range<Integer> range1=LETTER_FINDINGS.get(s1);
				Range<Integer> range2=LETTER_FINDINGS.get(s2);
				Range<Integer> range3=LETTER_FINDINGS.get(s3);
				if (range1.contains(q1)&&range2.contains(q2)&&range3.contains(q3)) result.add(index);
			}
		}
		if (result.size()!=1) throw new IllegalStateException("Nicht möglich!!!!!");
		System.out.println(result.toArray(new int[1])[0]);
	}
}
