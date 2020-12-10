package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.io.Resources;

public class Advent22_2 {
	private final static String IN_FILE="Advent22.txt";
	private final static int BURSTS=10000000;
	
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
		private final static Map<Direction,Direction> REVERSE=getReversals();
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
		private static Map<Direction,Direction> getReversals()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,LEFT);
			result.put(UP,DOWN);
			result.put(LEFT,RIGHT);
			result.put(DOWN,UP);
			return result;
		}
		public abstract void advance(Maze maze);
		public Direction rotateLeft()	{
			return ROTATE_LEFT.get(this);
		}
		public Direction rotateRight()	{
			return ROTATE_RIGHT.get(this);
		}
		public Direction reverse()	{
			return REVERSE.get(this);
		}
	}
	
	private static enum NodeState	{
		CLEAN	{
			@Override
			public boolean burst(Maze maze) {
				maze.rotateLeft();
				maze.weaken();
				maze.advance();
				return false;
			}
		},
		WEAKENED	{
			@Override
			public boolean burst(Maze maze) {
				maze.infect();
				maze.advance();
				return true;
			}
		},
		INFECTED	{
			@Override
			public boolean burst(Maze maze) {
				maze.rotateRight();
				maze.flag();
				maze.advance();
				return false;
			}
		},
		FLAGGED	{
			@Override
			public boolean burst(Maze maze) {
				maze.reverse();
				maze.clean();
				maze.advance();
				return false;
			}
		};
		public abstract boolean burst(Maze maze);
	}
	
	private static class Maze	{
		private int virusX;
		private int virusY;
		private Direction virusDir;
		private Table<Integer,Integer,NodeState> infections;
		public Maze()	{
			virusX=0;
			virusY=0;
			virusDir=Direction.UP;
			infections=HashBasedTable.create();
		}
		public void markInfected(int x,int y)	{
			infections.put(x,y,NodeState.INFECTED);
		}
		public NodeState getState()	{
			NodeState result=infections.get(virusX,virusY);
			return (result==null)?NodeState.CLEAN:result;
		}
		public void weaken()	{
			infections.put(virusX,virusY,NodeState.WEAKENED);
		}
		public void infect()	{
			infections.put(virusX,virusY,NodeState.INFECTED);
		}
		public void flag()	{
			infections.put(virusX,virusY,NodeState.FLAGGED);
		}
		public void clean()	{
			infections.put(virusX,virusY,NodeState.CLEAN);
		}
		public void rotateLeft()	{
			virusDir=virusDir.rotateLeft();
		}
		public void rotateRight()	{
			virusDir=virusDir.rotateRight();
		}
		public void reverse()	{
			virusDir=virusDir.reverse();
		}
		public void advance()	{
			virusDir.advance(this);
		}
		public boolean burst()	{
			return getState().burst(this);
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
