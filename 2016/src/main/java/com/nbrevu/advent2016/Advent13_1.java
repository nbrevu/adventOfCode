package com.nbrevu.advent2016;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.TreeMap;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;

public class Advent13_1 {
	private final static int SECRET_NUMBER=1350;
	private final static int GOAL_X=31;
	private final static int GOAL_Y=39;
	
	private static class MazeCache	{
		private final Table<Long,Long,Boolean> cache;
		public MazeCache()	{
			cache=HashBasedTable.create();
		}
		public boolean isOpen(long x,long y)	{
			Boolean result=cache.get(x,y);
			if (result!=null) return result.booleanValue();
			result=calculateIsOpen(x,y);
			cache.put(x,y,result);
			return result;
		}
		private static boolean calculateIsOpen(long x,long y)	{
			long n=SECRET_NUMBER+x*x+3*x+2*x*y+y+y*y;
			return (Long.bitCount(n)&1)==0;
		}
	}
	
	private static class Position	{
		public final int stepsTaken;
		public final int x;
		public final int y;
		public final int heuristic;
		public Position(int stepsTaken,int x,int y)	{
			this.stepsTaken=stepsTaken;
			this.x=x;
			this.y=y;
			heuristic=stepsTaken+Math.abs(GOAL_X-x)+Math.abs(GOAL_Y-y);
		}
	}
	
	private static class AStar	{
		private final NavigableMap<Integer,Queue<Position>> nodes;
		private final MazeCache maze;
		private final Multimap<Integer,Integer> visited;
		public AStar(Position initial)	{
			nodes=new TreeMap<>();
			maze=new MazeCache();
			add(initial);
			visited=HashMultimap.create();
		}
		private void add(Position p)	{
			nodes.computeIfAbsent(p.heuristic,(Integer unused)->new ArrayDeque<>()).add(p);
		}
		private Position poll()	{
			Map.Entry<Integer,Queue<Position>> entry=nodes.firstEntry();
			Queue<Position> queue=entry.getValue();
			Position result=queue.poll();
			if (queue.isEmpty()) nodes.remove(entry.getKey());
			return result;
		}
		private boolean isValid(int x,int y)	{
			return (x>=0)&&(y>=0)&&maze.isOpen(x,y)&&!visited.containsEntry(x,y);
		}
		private List<Position> children(Position p)	{
			List<Position> result=new ArrayList<>();
			if (isValid(p.x+1,p.y)) result.add(new Position(p.stepsTaken+1,p.x+1,p.y));
			if (isValid(p.x,p.y+1)) result.add(new Position(p.stepsTaken+1,p.x,p.y+1));
			if (isValid(p.x-1,p.y)) result.add(new Position(p.stepsTaken+1,p.x-1,p.y));
			if (isValid(p.x,p.y-1)) result.add(new Position(p.stepsTaken+1,p.x,p.y-1));
			return result;
		}
		private boolean isFinal(Position p)	{
			return (p.x==GOAL_X)&&(p.y==GOAL_Y);
		}
		public Position run()	{
			while (!nodes.isEmpty())	{
				Position p=poll();
				if (visited.containsEntry(p.x,p.y)) continue;
				if (isFinal(p)) return p;
				visited.put(p.x,p.y);
				children(p).forEach(this::add);
			}
			throw new IllegalStateException("¿Estás seguro de que no estabas buscando la página 105?");
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Position initial=new Position(0,1,1);
		AStar search=new AStar(initial);
		Position result=search.run();
		System.out.println(result.stepsTaken);
	}
}
