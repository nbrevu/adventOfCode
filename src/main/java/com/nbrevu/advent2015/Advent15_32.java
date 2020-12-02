package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_32 {
	private final static String IN_FILE="2015/Advent31.txt";
	
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
	
	private static class Mover	{
		private int x;
		private int y;
		public Mover()	{
			x=0;
			y=0;
		}
		public Point move(char character)	{
			switch (character)	{
				case '>':++x;break;
				case '^':++y;break;
				case '<':--x;break;
				case 'v':--y;break;
			}
			return new Point(x,y);
		}
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		Set<Point> visited=new HashSet<>();
		Mover currentMover=new Mover();
		Mover reserveMover=new Mover();
		visited.add(new Point(0,0));
		int len=content.length();
		for (int i=0;i<len;++i)	{
			visited.add(currentMover.move(content.charAt(i)));
			Mover swap=currentMover;
			currentMover=reserveMover;
			reserveMover=swap;
		}
		System.out.println(visited.size());
	}
}
