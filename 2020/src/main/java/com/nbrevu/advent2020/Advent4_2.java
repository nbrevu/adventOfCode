package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

public class Advent4_2 {
	private final static String IN_FILE="Advent4.txt";
	
	private static class YearPredicate implements Predicate<String>	{
		private final static Pattern YEAR_PATTERN=Pattern.compile("^\\d\\d\\d\\d$");
		private final int minYear;
		private final int maxYear;
		public YearPredicate(int minYear,int maxYear)	{
			this.minYear=minYear;
			this.maxYear=maxYear;
		}
		@Override
		public boolean test(String t) {
			Matcher matcher=YEAR_PATTERN.matcher(t);
			if (!matcher.matches()) return false;
			int year=Integer.parseInt(t);
			return (minYear<=year)&&(year<=maxYear);
		}
	}
	
	private final static Pattern CM_PATTERN=Pattern.compile("^(\\d\\d\\d)cm$");
	private final static Pattern IN_PATTERN=Pattern.compile("^(\\d\\d)in$");
	private final static Predicate<String> HEIGHT_PREDICATE=new Predicate<>()	{
		@Override
		public boolean test(String t) {
			Matcher cmMatcher=CM_PATTERN.matcher(t);
			if (cmMatcher.matches())	{
				int height=Integer.parseInt(cmMatcher.group(1));
				return (150<=height)&&(height<=193);
			}
			Matcher inMatcher=IN_PATTERN.matcher(t);
			if (inMatcher.matches())	{
				int height=Integer.parseInt(inMatcher.group(1));
				return (59<=height)&&(height<=76);
			}
			return false;
		}
	};

	private final static Predicate<String> HAIR_COLOUR_PREDICATE=Pattern.compile("^#[0-9a-f]{6}$").asMatchPredicate();
	private final static Predicate<String> EYE_COLOUR_PREDICATE=ImmutableSet.of("amb","blu","brn","gry","grn","hzl","oth")::contains;
	private final static Predicate<String> PASSPORT_ID_PREDICATE=Pattern.compile("^[0-9]{9}$").asMatchPredicate();
	
	private final static Map<String,Predicate<String>> PREDICATES=createPredicatesMap();
	
	private static Map<String,Predicate<String>> createPredicatesMap()	{
		Map<String,Predicate<String>> result=new HashMap<>();
		result.put("byr",new YearPredicate(1920,2002));
		result.put("iyr",new YearPredicate(2010,2020));
		result.put("eyr",new YearPredicate(2020,2030));
		result.put("hgt",HEIGHT_PREDICATE);
		result.put("hcl",HAIR_COLOUR_PREDICATE);
		result.put("ecl",EYE_COLOUR_PREDICATE);
		result.put("pid",PASSPORT_ID_PREDICATE);
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		Set<String> validatedSet=new HashSet<>();
		Set<String> keys=PREDICATES.keySet();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (line.isBlank())	{
			if (validatedSet.containsAll(keys)) ++counter;
			validatedSet.clear();
		}	else for (String split:line.split(" "))	{
			String[] split2=split.split(":");
			if (PREDICATES.getOrDefault(split2[0],Predicates.alwaysTrue()).test(split2[1])) validatedSet.add(split2[0]);
		}
		if (validatedSet.containsAll(keys)) ++counter;
		System.out.println(counter);
	}
}
