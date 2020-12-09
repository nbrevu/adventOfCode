package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent19_2 {
	private final static String IN_FILE="Advent19.txt";
	
	private static class MazeRunner	{
		private static enum Direction	{
			RIGHT	{
				@Override
				public void move(MazeRunner runner) {
					++runner.x;
				}
			},
			UP	{
				@Override
				public void move(MazeRunner runner) {
					--runner.y;
				}
			},
			LEFT	{
				@Override
				public void move(MazeRunner runner) {
					--runner.x;
				}
			},
			DOWN	{
				@Override
				public void move(MazeRunner runner) {
					++runner.y;
				}
			};
			public abstract void move(MazeRunner runner);
		}
		private final static Map<Direction,List<Direction>> NEXT_DIRS=createNextDirsMap();
		private static Map<Direction,List<Direction>> createNextDirsMap()	{
			Map<Direction,List<Direction>> result=new EnumMap<>(Direction.class);
			result.put(Direction.RIGHT,Arrays.asList(Direction.UP,Direction.DOWN));
			result.put(Direction.UP,Arrays.asList(Direction.LEFT,Direction.RIGHT));
			result.put(Direction.LEFT,Arrays.asList(Direction.DOWN,Direction.UP));
			result.put(Direction.DOWN,Arrays.asList(Direction.RIGHT,Direction.LEFT));
			return result;
		}
		private final char[][] maze;
		private int x;
		private int y;
		private Direction dir;
		public MazeRunner(char[][] maze)	{
			this.maze=maze;
			y=0;
			x=0;
			while (maze[y][x]!='|') ++x;
			dir=Direction.DOWN;
		}
		private Optional<Direction> tryDirs(List<Direction> dirs)	{
			for (Direction d:dirs)	{
				int oldX=x;
				int oldY=y;
				d.move(this);
				boolean dirFound=(maze[y][x]!=' ');
				x=oldX;
				y=oldY;
				if (dirFound) return Optional.of(d);
			}
			return Optional.empty();
		}
		public int run()	{
			int steps=0;
			for (;;)	{
				++steps;
				dir.move(this);
				char c=maze[y][x];
				if ((c=='|')||(c=='-')) continue;
				else if (c==' ') break;
				else if (c=='+')	{
					Optional<Direction> newDir=tryDirs(NEXT_DIRS.get(dir));
					if (newDir.isEmpty()) throw new IllegalArgumentException("Perdidos, ¡perdiditos!");
					dir=newDir.get();
				}	
			}
			return steps;
		}
	}
	
	private static int runMaze(char[][] maze)	{
		return new MazeRunner(maze).run();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> contents=Resources.readLines(file,Charsets.UTF_8);
		char[][] maze=contents.stream().map(String::toCharArray).toArray(char[][]::new);
		System.out.println(runMaze(maze));
	}
}
