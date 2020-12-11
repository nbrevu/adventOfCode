package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_1 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int result=lines.stream().mapToInt(Integer::parseInt).sum();
		System.out.println(result);
	}
}
