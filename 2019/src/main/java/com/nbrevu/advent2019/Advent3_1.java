package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent3_1 {
	private final static String IN_FILE="Advent3.txt";
	
	private final static Pattern MOVE=Pattern.compile("^([RULD])(\\d+)$");
	
	private static class Point	{
		public final int x;
		public final int y;
		public Point(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		public int manhattanDist()	{
			return Math.abs(x)+Math.abs(y);
		}
	}
	private static class Segment	{
		public final int constantCoord;
		public final int start;
		public final int end;
		public Segment(int constantCoord,int start,int end)	{
			this.constantCoord=constantCoord;
			this.start=start;
			this.end=end;
		}
	}
	
	private static class State	{
		public int x;
		public int y;
		public final List<Segment> horizontalSegments;
		public final List<Segment> verticalSegments;
		public State()	{
			this.x=0;
			this.y=0;
			horizontalSegments=new ArrayList<>();
			verticalSegments=new ArrayList<>();
		}
	}
	
	private static enum Direction	{
		R	{
			@Override
			public void move(State s,int units) {
				int xf=s.x+units;
				s.horizontalSegments.add(new Segment(s.y,s.x,xf));
				s.x=xf;
			}
		},
		U	{
			@Override
			public void move(State s,int units) {
				int yf=s.y+units;
				s.verticalSegments.add(new Segment(s.x,s.y,yf));
				s.y=yf;
			}
		},
		L	{
			@Override
			public void move(State s,int units) {
				int x0=s.x-units;
				s.horizontalSegments.add(new Segment(s.y,x0,s.x));
				s.x=x0;
			}
		},
		D	{
			@Override
			public void move(State s,int units) {
				int y0=s.y-units;
				s.verticalSegments.add(new Segment(s.x,y0,s.y));
				s.y=y0;
			}
		};
		public abstract void move(State s,int units);
	}
	
	private static boolean isBetween(int x,int x0,int xf)	{
		return (x0<=x)&&(x<=xf);
	}
	
	private static Point intersection(Segment horiz,Segment vert)	{
		if (isBetween(horiz.constantCoord,vert.start,vert.end)&&isBetween(vert.constantCoord,horiz.start,horiz.end)) return new Point(vert.constantCoord,horiz.constantCoord);
		else return null;
	}
	
	private static int getMinDistance(State s1,State s2)	{
		int minDist=Integer.MAX_VALUE;
		for (Segment h:s1.horizontalSegments) for (Segment v:s2.verticalSegments)	{
			Point p=intersection(h,v);
			if (p!=null) minDist=Math.min(minDist,p.manhattanDist());
		}
		for (Segment h:s2.horizontalSegments) for (Segment v:s1.verticalSegments)	{
			Point p=intersection(h,v);
			if (p!=null) minDist=Math.min(minDist,p.manhattanDist());
		}
		return minDist;
	}
	
	private static State move(String wire)	{
		State result=new State();
		for (String s:wire.split(","))	{
			Matcher matcher=MOVE.matcher(s);
			if (!matcher.matches()) throw new IllegalArgumentException();
			Direction dir=Direction.valueOf(matcher.group(1));
			int amount=Integer.parseInt(matcher.group(2));
			dir.move(result,amount);
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		String wire1=content.get(0);
		String wire2=content.get(1);
		State state1=move(wire1);
		State state2=move(wire2);
		int minDist=getMinDistance(state1,state2);
		System.out.println(minDist);
	}
}
