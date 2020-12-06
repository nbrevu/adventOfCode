package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Resources;

public class Advent4_1 {
	private final static String IN_FILE="Advent4.txt";
	private final static Set<String> REQUIRED_FIELDS=ImmutableSet.of("byr","iyr","eyr","hgt","hcl","ecl","pid");
	
	public static void main(String[] args) throws IOException	{
		int counter=0;
		URL file=Resources.getResource(IN_FILE);
		Set<String> currentSet=new HashSet<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (line.isBlank())	{
			if (currentSet.containsAll(REQUIRED_FIELDS)) ++counter;
			currentSet.clear();
		}	else for (String split:line.split(" ")) currentSet.add(split.split(":")[0]);
		if (currentSet.containsAll(REQUIRED_FIELDS)) ++counter;
		System.out.println(counter);
	}
}
