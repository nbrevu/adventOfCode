package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_6_2 {
	private final static String IN_FILE="Advent6.txt";
	private final static int SIZE=1000;
	private final static Pattern PATTERN=Pattern.compile("^(.+) (\\d+),(\\d+) through (\\d+),(\\d+)$");
	
	private static class LightsArray	{
		private final int[][] array;
		public LightsArray(int size)	{
			array=new int[size][size];
		}
		public void turnOn(int x0,int y0,int xf,int yf)	{
			for (int i=x0;i<=xf;++i) for (int j=y0;j<=yf;++j) ++array[i][j];
		}
		public void turnOff(int x0,int y0,int xf,int yf)	{
			for (int i=x0;i<=xf;++i) for (int j=y0;j<=yf;++j) if (array[i][j]>0) --array[i][j];
		}
		public void toggle(int x0,int y0,int xf,int yf)	{
			for (int i=x0;i<=xf;++i) for (int j=y0;j<=yf;++j) array[i][j]+=2;
		}
		public long count()	{
			long result=0;
			for (int i=0;i<array.length;++i) for (int j=0;j<array[i].length;++j) result+=array[i][j];
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		LightsArray array=new LightsArray(SIZE);
		int[] numbers=new int[4];
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=PATTERN.matcher(line);
			if (matcher.matches())	{
				String command=matcher.group(1);
				for (int i=0;i<4;++i) numbers[i]=Integer.parseInt(matcher.group(i+2));
				switch (command)	{
				case "turn on":array.turnOn(numbers[0],numbers[1],numbers[2],numbers[3]);break;
				case "turn off":array.turnOff(numbers[0],numbers[1],numbers[2],numbers[3]);break;
				case "toggle":array.toggle(numbers[0],numbers[1],numbers[2],numbers[3]);break;
				}
			}
		}
		System.out.println(array.count());
	}
}
