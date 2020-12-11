package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent3_1 {
	private final static String IN_FILE="Advent3.txt";
	private final static int SIZE=1000;
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^#(\\d+) @ (\\d+),(\\d+): (\\d+)x(\\d+)$");
	
	private static class Fabric	{
		private final int[][] claims;
		public Fabric(int size)	{
			claims=new int[size][size];
		}
		public void claim(int offsetX,int offsetY,int width,int height)	{
			int finalX=offsetX+width;
			int finalY=offsetY+height;
			for (int i=offsetX;i<finalX;++i) for (int j=offsetY;j<finalY;++j) ++claims[i][j];
		}
		public int countMultipleClaims()	{
			int result=0;
			for (int i=0;i<claims.length;++i) for (int j=0;j<claims[i].length;++j) if (claims[i][j]>=2) ++result;
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Fabric fabric=new Fabric(SIZE);
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches()) fabric.claim(Integer.parseInt(matcher.group(2)),Integer.parseInt(matcher.group(3)),Integer.parseInt(matcher.group(4)),Integer.parseInt(matcher.group(5)));
		}
		System.out.println(fabric.countMultipleClaims());
	}
}
