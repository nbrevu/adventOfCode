package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

// This is AWESOME. A difficulty leap, and one of the best kind. This is not obvious at all!
public class Advent12_2 {
	private final static String IN_FILE="Advent12.txt";
	private final static Pattern LINE_PATTERN=Pattern.compile("^<x=(\\-?\\d+), y=(\\-?\\d+), z=(\\-?\\d+)>$");
	
	private static class DimensionData	{
		/*
		 * Yep. 8-digit array: (moon1-x, moon2-x, moon3-x, moon4-x, moon1-vx, moon2-vx, moon3-vx, moon4-vx).
		 * This is done to take advantage of the Arrays class.
		 */
		private final int[] data;
		private DimensionData(int[] data)	{
			this.data=data;
		}
		public DimensionData step()	{
			int s=data.length/2;
			int[] result=Arrays.copyOf(data,data.length);
			for (int i=0;i<s;++i)	{
				int acc=0;
				for (int j=0;j<s;++j) if (i!=j) acc+=Integer.signum(result[j]-result[i]);
				result[i+s]+=acc;
			}
			for (int i=0;i<s;++i) result[i]+=result[i+s];
			return new DimensionData(result);
		}
		private static class Builder	{
			private final List<Integer> coords;
			public Builder()	{
				coords=new ArrayList<>();
			}
			public void addMoon(int coord)	{
				coords.add(coord);
			}
			public DimensionData build()	{
				int size=coords.size();
				int[] result=new int[2*size];
				for (int i=0;i<size;++i) result[i]=coords.get(i).intValue();
				return new DimensionData(result);
			}
		}
		@Override
		public int hashCode()	{
			return Arrays.hashCode(data);
		}
		@Override
		public boolean equals(Object other)	{
			DimensionData dd=(DimensionData)other;
			return Arrays.equals(data,dd.data);
		}
	}
	
	private static int findLoop(DimensionData x)	{
		DimensionData initial=x;
		for (int i=1;;++i)	{
			x=x.step();
			if (x.equals(initial)) return i;
		}
	}
	
	public static long gcd(long a,long b)	{
		for (;;)	{
			long rem=a%b;
			if (rem==0l) return b;
			a=b;
			b=rem;
		}
	}
	public static long lcm(long a,long b)	{
		return a*b/gcd(a,b);
	}
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		DimensionData.Builder xBuilder=new DimensionData.Builder();
		DimensionData.Builder yBuilder=new DimensionData.Builder();
		DimensionData.Builder zBuilder=new DimensionData.Builder();
		for(String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				xBuilder.addMoon(Integer.parseInt(matcher.group(1)));
				yBuilder.addMoon(Integer.parseInt(matcher.group(2)));
				zBuilder.addMoon(Integer.parseInt(matcher.group(3)));
			}
		}
		DimensionData x=xBuilder.build();
		DimensionData y=yBuilder.build();
		DimensionData z=zBuilder.build();
		long loopX=findLoop(x);
		long loopY=findLoop(y);
		long loopZ=findLoop(z);
		long result=lcm(lcm(loopX,loopY),loopZ);
		System.out.println(result);
	}
}
