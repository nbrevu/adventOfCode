package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_61 {
	private final static String IN_FILE="2015/Advent61.txt";
	private final static int SIZE=1000;
	private final static Pattern PATTERN=Pattern.compile("^(.+) (\\d+),(\\d+) through (\\d+),(\\d+)$");
	
	private static class LightsArray	{
		private final BitSet[] array;
		private final int size;
		public LightsArray(int size)	{
			array=new BitSet[size];
			for (int i=0;i<size;++i) array[i]=new BitSet(size);
			this.size=size;
		}
		private BitSet ones(int x0,int xf)	{
			BitSet result=new BitSet(size);
			result.set(x0,xf+1);
			return result;
		}
		private BitSet zeros(int x0,int xf)	{
			BitSet result=new BitSet(size);
			result.set(0,x0);
			result.set(1+xf,size);
			return result;
		}
		public void turnOn(int x0,int y0,int xf,int yf)	{
			BitSet mask=ones(x0,xf);
			for (int y=y0;y<=yf;++y) array[y].or(mask);
		}
		public void turnOff(int x0,int y0,int xf,int yf)	{
			BitSet mask=zeros(x0,xf);
			for (int y=y0;y<=yf;++y) array[y].and(mask);
		}
		public void toggle(int x0,int y0,int xf,int yf)	{
			BitSet mask=ones(x0,xf);
			for (int y=y0;y<=yf;++y) array[y].xor(mask);
		}
		public int count()	{
			int result=0;
			for (int i=0;i<size;++i) result+=array[i].cardinality();
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
