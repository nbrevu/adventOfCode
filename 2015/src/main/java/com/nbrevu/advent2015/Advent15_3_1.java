package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_3_1 {
	private final static String IN_FILE="Advent3.txt";
	
	private static class Point	{
		public final int x;
		public final int y;
		public Point(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return x+y;
		}
		@Override
		public boolean equals(Object other)	{
			Point pOther=(Point)other;
			return (x==pOther.x)&&(y==pOther.y);
		}
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		Set<Point> visited=new HashSet<>();
		int x=0;
		int y=0;
		visited.add(new Point(x,y));
		int len=content.length();
		for (int i=0;i<len;++i)	{
			switch (content.charAt(i))	{
				case '>':++x;break;
				case '^':++y;break;
				case '<':--x;break;
				case 'v':--y;break;
			}
			visited.add(new Point(x,y));
		}
		System.out.println(visited.size());
	}
}
