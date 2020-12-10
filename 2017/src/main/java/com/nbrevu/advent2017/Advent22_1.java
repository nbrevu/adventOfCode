package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent22_1 {
	private final static String IN_FILE="Advent22.txt";
	private final static int BURSTS=10000;
	
	private static enum Direction	{
		RIGHT	{
			@Override
			public void advance(Maze maze) {
				++maze.virusX;
			}
		},
		UP	{
			@Override
			public void advance(Maze maze) {
				--maze.virusY;
			}
		},
		LEFT	{
			@Override
			public void advance(Maze maze) {
				--maze.virusX;
			}
		},
		DOWN	{
			@Override
			public void advance(Maze maze) {
				++maze.virusY;
			}
		};
		private final static Map<Direction,Direction> ROTATE_LEFT=getLeftRotations();
		private final static Map<Direction,Direction> ROTATE_RIGHT=getRightRotations();
		private static Map<Direction,Direction> getLeftRotations()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,UP);
			result.put(UP,LEFT);
			result.put(LEFT,DOWN);
			result.put(DOWN,RIGHT);
			return result;
		}
		private static Map<Direction,Direction> getRightRotations()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,DOWN);
			result.put(UP,RIGHT);
			result.put(LEFT,UP);
			result.put(DOWN,LEFT);
			return result;
		}
		public abstract void advance(Maze maze);
		public Direction rotateLeft()	{
			return ROTATE_LEFT.get(this);
		}
		public Direction rotateRight()	{
			return ROTATE_RIGHT.get(this);
		}
	}
	
	private static class Maze	{
		private int virusX;
		private int virusY;
		private Direction virusDir;
		private Multimap<Integer,Integer> infections;
		public Maze()	{
			virusX=0;
			virusY=0;
			virusDir=Direction.UP;
			infections=HashMultimap.create();
		}
		public void markInfected(int x,int y)	{
			infections.put(x,y);
		}
		public void markClean(int x,int y)	{
			infections.remove(x,y);
		}
		public boolean burst()	{
			if (infections.containsEntry(virusX,virusY))	{
				virusDir=virusDir.rotateRight();
				markClean(virusX,virusY);
				virusDir.advance(this);
				return false;
			}	else	{
				virusDir=virusDir.rotateLeft();
				markInfected(virusX,virusY);
				virusDir.advance(this);
				return true;
			}
		}
	}

	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		int offsetX=(contents.get(0).length()-1)/2;
		int offsetY=(contents.size()-1)/2;
		Maze maze=new Maze();
		for (int i=0;i<contents.size();++i)	{
			String line=contents.get(i);
			for (int j=0;j<line.length();++j) if (line.charAt(j)=='#') maze.markInfected(j-offsetX,i-offsetY);
		}
		int result=0;
		for (int i=0;i<BURSTS;++i) if (maze.burst()) ++result;
		System.out.println(result);
	}
}
