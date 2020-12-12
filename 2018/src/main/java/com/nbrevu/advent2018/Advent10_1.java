package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent10_1 {
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
		public Particle copy()	{
			return new Particle(x,y,vx,vy);
		}
	}
	
	private static void iterate(List<Particle> ps)	{
		ps.forEach(Particle::advance);
	}
	
	private static List<Particle> copy(List<Particle> ps)	{
		return ps.stream().map(Particle::copy).collect(Collectors.toUnmodifiableList());
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
	
	private static class MessageWriter	{
		private final Rectangle area;
		private final char[][] canvas;
		public MessageWriter(Rectangle area)	{
			this.area=area;
			int dx=area.maxX+1-area.minX;
			int dy=area.maxY+1-area.minY;
			canvas=new char[dy][dx];
			for (int i=0;i<dy;++i) Arrays.fill(canvas[i],' ');
		}
		public void writeChar(int posX,int posY)	{
			canvas[posY-area.minY][posX-area.minX]='#';
		}
		public List<String> getMessages()	{
			return Arrays.stream(canvas).map(String::copyValueOf).collect(Collectors.toUnmodifiableList());
		}
	}
	
	public static List<String> writeMessage(Rectangle area,List<Particle> message)	{
		MessageWriter writer=new MessageWriter(area);
		for (Particle p:message) writer.writeChar(p.x,p.y);
		return writer.getMessages();
	}
	
	private static List<String> getEncodedMessage(List<Particle> ps,int maxIterations)	{
		Rectangle bestRectangle=getEnclosingRectangle(ps);
		long bestArea=bestRectangle.getArea();
		List<Particle> bestState=copy(ps);
		for (int i=0;i<maxIterations;++i)	{
			iterate(ps);
			Rectangle currentRect=getEnclosingRectangle(ps);
			long currentArea=currentRect.getArea();
			if (currentArea<bestArea)	{
				bestRectangle=currentRect;
				bestArea=currentArea;
				bestState=copy(ps);
			}
		}
		return writeMessage(bestRectangle,bestState);
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
		List<String> message=getEncodedMessage(particles,ITERATIONS);
		message.forEach(System.out::println);
	}
}
