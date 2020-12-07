package com.nbrevu.advent2016;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Advent13_2 {
	private final static int SECRET_NUMBER=1350;
	private final static int ITERATIONS=50;
	
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
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return x+y;
		}
		@Override
		public boolean equals(Object other)	{
			Position p=(Position)other;
			return (x==p.x)&&(y==p.y);
		}
	}
	
	private static class Searcher	{
		private final Set<Position> visited;
		private final MazeCache maze;
		public Searcher()	{
			visited=new HashSet<>();
			maze=new MazeCache();
		}
		private boolean isValid(Position p)	{
			return (p.x>=0)&&(p.y>=0)&&maze.isOpen(p.x,p.y)&&!visited.contains(p);
		}
		private void addIfValid(Position p,List<Position> ps)	{
			if (isValid(p)) ps.add(p);
		}
		private List<Position> children(Position p)	{
			List<Position> result=new ArrayList<>();
			addIfValid(new Position(p.x+1,p.y),result);
			addIfValid(new Position(p.x,p.y+1),result);
			addIfValid(new Position(p.x-1,p.y),result);
			addIfValid(new Position(p.x,p.y-1),result);
			return result;
		}
		public int countAccessed(Position initial,int iterations)	{
			visited.clear();
			Set<Position> pending=Collections.singleton(initial);
			for (int i=0;i<iterations;++i)	{
				Set<Position> nextGen=new HashSet<>();
				for (Position p:pending)	{
					for (Position child:children(p)) nextGen.add(child);
					visited.add(p);
				}
				pending=nextGen;
			}
			visited.addAll(pending);
			return visited.size();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Position initial=new Position(1,1);
		Searcher s=new Searcher();
		System.out.println(s.countAccessed(initial,ITERATIONS));
	}
}
