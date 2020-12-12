package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_2 {
	private final static String IN_FILE="Advent10.txt";
	
	private final static int ITERATIONS=50000;
	private final static Pattern LINE_PATTERN=Pattern.compile("^position=<\\s*(\\-?\\d+),\\s*(\\-?\\d+)> velocity=<\\s*(\\-?\\d+),\\s*(\\-?\\d+)>$");
	
	private static class Particle	{
		public int x;
		public int y;
		public final int vx;
		public final int vy;
		public Particle(int x,int y,int vx,int vy)	{
			this.x=x;
			this.y=y;
			this.vx=vx;
			this.vy=vy;
		}
		public void advance()	{
			x+=vx;
			y+=vy;
		}
	}
	
	private static void iterate(List<Particle> ps)	{
		ps.forEach(Particle::advance);
	}
	
	private static class Rectangle	{
		public final int minX;
		public final int maxX;
		public final int minY;
		public final int maxY;
		public Rectangle(int minX,int maxX,int minY,int maxY)	{
			this.minX=minX;
			this.maxX=maxX;
			this.minY=minY;
			this.maxY=maxY;
		}
		public long getArea()	{
			// This doesn't return the exact area because of off-by-one errors, but it does what I want it to do because I just want to find a minimum.
			return ((long)(maxX-minX))*((long)(maxY-minY));
		}
	}
	
	private static Rectangle getEnclosingRectangle(List<Particle> ps)	{
		int minX=Integer.MAX_VALUE;
		int maxX=Integer.MIN_VALUE;
		int minY=Integer.MAX_VALUE;
		int maxY=Integer.MIN_VALUE;
		for (Particle p:ps)	{
			minX=Math.min(minX,p.x);
			maxX=Math.max(maxX,p.x);
			minY=Math.min(minY,p.y);
			maxY=Math.max(maxY,p.y);
		}
		return new Rectangle(minX,maxX,minY,maxY);
	}
	
	private static int getMessageTime(List<Particle> ps,int maxIterations)	{
		long bestArea=getEnclosingRectangle(ps).getArea();
		int bestTime=0;
		for (int i=1;i<=maxIterations;++i)	{
			iterate(ps);
			Rectangle currentRect=getEnclosingRectangle(ps);
			long currentArea=currentRect.getArea();
			if (currentArea<bestArea)	{
				bestArea=currentArea;
				bestTime=i;
			}
		}
		return bestTime;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Particle> particles=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int x=Integer.parseInt(matcher.group(1));
				int y=Integer.parseInt(matcher.group(2));
				int vx=Integer.parseInt(matcher.group(3));
				int vy=Integer.parseInt(matcher.group(4));
				particles.add(new Particle(x,y,vx,vy));
			}
		}
		int result=getMessageTime(particles,ITERATIONS);
		System.out.println(result);
	}
}
