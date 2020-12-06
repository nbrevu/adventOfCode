package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_1 {
	private final static String IN_FILE="Advent1.txt";
	
	private static enum Direction	{
		EAST	{
			@Override
			public void advance(Location currentLocation,int blocks) {
				currentLocation.x+=blocks;
			}
		},
		NORTH	{
			@Override
			public void advance(Location currentLocation,int blocks) {
				currentLocation.y+=blocks;
			}
		},
		WEST	{
			@Override
			public void advance(Location currentLocation,int blocks) {
				currentLocation.x-=blocks;
			}
		},
		SOUTH	{
			@Override
			public void advance(Location currentLocation,int blocks) {
				currentLocation.y-=blocks;
			}
		};
		public abstract void advance(Location currentLocation,int blocks);
		public static void rotateLeft(Location location)	{
			int currentDir=location.dir.ordinal();
			++currentDir;
			currentDir%=4;
			location.dir=values()[currentDir];
		}
		public static void rotateRight(Location location)	{
			int currentDir=location.dir.ordinal();
			currentDir+=3;
			currentDir%=4;
			location.dir=values()[currentDir];
		}
	}
	
	private static class Location	{
		public int x;
		public int y;
		public Direction dir;
		public Location()	{
			x=0;
			y=0;
			dir=Direction.NORTH;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Location location=new Location();
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		String[] split=content.split(", ");
		for (String s:split)	{
			boolean isRight=s.charAt(0)=='R';
			if (isRight) Direction.rotateRight(location);
			else Direction.rotateLeft(location);
			location.dir.advance(location,Integer.parseInt(s.substring(1)));
		}
		int result=location.x+location.y;
		System.out.println(result);
	}
}
