package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent12_2 {
	private final static String IN_FILE="Advent12.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([NSEWLRF])(\\d+)");
	
	private static class Waypoint	{
		public int x;
		public int y;
		public Waypoint()	{
			this.x=0;
			this.y=0;
		}
		public void forward(Position p,int amount)	{
			p.x+=x*amount;
			p.y+=y*amount;
		}
		public void rotateLeft()	{
			int newX=-y;
			int newY=x;
			x=newX;
			y=newY;
		}
		public void rotateRight()	{
			int newX=y;
			int newY=-x;
			x=newX;
			y=newY;
		}
	}
	
	private static class Position	{
		public int x;
		public int y;
		public Position()	{
			x=0;
			y=0;
		}
	}
	
	private static enum Action	{
		N	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				wp.y+=amount;
			}
		},
		S	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				wp.y-=amount;
			}
		},
		E	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				wp.x+=amount;
			}
		},
		W	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				wp.x-=amount;
			}
		},
		L	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				int realAmount=amount/90;
				for (int i=0;i<realAmount;++i) wp.rotateLeft();
			}
		},
		R	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				int realAmount=amount/90;
				for (int i=0;i<realAmount;++i) wp.rotateRight();
			}
		},
		F	{
			@Override
			public void act(Position pos,Waypoint wp,int amount) {
				wp.forward(pos,amount);
			}
		};
		public abstract void act(Position pos,Waypoint wp,int amount);
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Position p=new Position();
		Waypoint wp=new Waypoint();
		wp.x=10;
		wp.y=1;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				Action a=Action.valueOf(matcher.group(1));
				int amount=Integer.parseInt(matcher.group(2));
				a.act(p,wp,amount);
			}
		}
		int result=Math.abs(p.x)+Math.abs(p.y);
		System.out.println(result);
	}
}
