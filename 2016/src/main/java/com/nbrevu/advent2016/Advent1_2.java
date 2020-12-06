package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_2 {
	private final static String IN_FILE="Advent1.txt";
	
	private static enum Direction	{
		EAST	{
			@Override
			public Set<Pair<Integer,Integer>> advance(Location currentLocation,int blocks) {
				Set<Pair<Integer,Integer>> visited=new HashSet<>();
				int finalLocation=currentLocation.x+blocks;
				for (int x=currentLocation.x+1;x<=finalLocation;++x) visited.add(ImmutablePair.of(x,currentLocation.y));
				currentLocation.x=finalLocation;
				return visited;
			}
		},
		NORTH	{
			@Override
			public Set<Pair<Integer,Integer>> advance(Location currentLocation,int blocks) {
				Set<Pair<Integer,Integer>> visited=new HashSet<>();
				int finalLocation=currentLocation.y+blocks;
				for (int y=currentLocation.y+1;y<=finalLocation;++y) visited.add(ImmutablePair.of(currentLocation.x,y));
				currentLocation.y=finalLocation;
				return visited;
			}
		},
		WEST	{
			@Override
			public Set<Pair<Integer,Integer>> advance(Location currentLocation,int blocks) {
				Set<Pair<Integer,Integer>> visited=new HashSet<>();
				int finalLocation=currentLocation.x-blocks;
				for (int x=currentLocation.x-1;x>=finalLocation;--x) visited.add(ImmutablePair.of(x,currentLocation.y));
				currentLocation.x=finalLocation;
				return visited;
			}
		},
		SOUTH	{
			@Override
			public Set<Pair<Integer,Integer>> advance(Location currentLocation,int blocks) {
				Set<Pair<Integer,Integer>> visited=new HashSet<>();
				int finalLocation=currentLocation.y-blocks;
				for (int y=currentLocation.y-1;y>=finalLocation;--y) visited.add(ImmutablePair.of(currentLocation.x,y));
				currentLocation.y=finalLocation;
				return visited;
			}
		};
		public abstract Set<Pair<Integer,Integer>> advance(Location currentLocation,int blocks);
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
			this(0,0,Direction.NORTH);
		}
		public Location(int x,int y,Direction dir)	{
			this.x=x;
			this.y=y;
			this.dir=dir;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Location location=new Location();
		Set<Pair<Integer,Integer>> visitedLocations=new HashSet<>();
		visitedLocations.add(ImmutablePair.of(0,0));
		URL file=Resources.getResource(IN_FILE);
		String content=Resources.readLines(file,Charsets.UTF_8).get(0);
		String[] split=content.split(", ");
		for (String s:split)	{
			boolean isRight=s.charAt(0)=='R';
			if (isRight) Direction.rotateRight(location);
			else Direction.rotateLeft(location);
			Set<Pair<Integer,Integer>> newVisitedLocations=location.dir.advance(location,Integer.parseInt(s.substring(1)));
			for (Pair<Integer,Integer> v:newVisitedLocations)	{
				if (visitedLocations.contains(v))	{
					int result=v.getLeft()+v.getRight();
					System.out.println(result);
					return;
				}
				visitedLocations.add(v);
			}
		}
		throw new IllegalStateException("Leider habe ich das nicht gefunden.");
	}
}
