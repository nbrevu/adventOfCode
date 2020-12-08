package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Advent24_2 {
	private final static String IN_FILE="Advent24.txt";
	
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
		public List<Position> children()	{
			return Arrays.asList(new Position(x+1,y),new Position(x,y+1),new Position(x-1,y),new Position(x,y-1));
		}
	}
	private static class PathStatus	{
		public final Position position;
		public final BitSet pending;
		public PathStatus(Position position,BitSet pending)	{
			this.position=position;
			this.pending=pending;
		}
		@Override
		public int hashCode()	{
			return position.hashCode()+pending.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			PathStatus ps=(PathStatus)other;
			return position.equals(ps.position)&&pending.equals(ps.pending);
		}
	}
	
	private static class Maze	{
		// True if wall.
		private final boolean[][] walls;
		private final Position[] specialPoints;
		private final ObjIntMap<Position> specialPointMap;
		public Maze(boolean[][] walls,Position[] specialPoints)	{
			this.walls=walls;
			this.specialPoints=specialPoints;
			specialPointMap=HashObjIntMaps.newMutableMap();
			for (int i=0;i<specialPoints.length;++i) specialPointMap.put(specialPoints[i],i);
		}
		public boolean isValid(Position p)	{
			return (p.x>=0)&&(p.x<walls[0].length)&&(p.y>=0)&&(p.y<walls.length)&&!walls[p.y][p.x];
		}
		public int getSpecialPoint(Position p)	{
			return specialPointMap.getOrDefault(p,-1);
		}
		public List<PathStatus> getChildren(PathStatus status)	{
			List<PathStatus> result=new ArrayList<>();
			for (Position p:status.position.children()) if (isValid(p))	{
				int specialPoint=getSpecialPoint(p);
				if (specialPoint>=0)	{
					BitSet newPending=(BitSet)(status.pending.clone());
					newPending.clear(specialPoint);
					result.add(new PathStatus(p,newPending));
				}	else result.add(new PathStatus(p,status.pending));
			}
			return result;
		}
	}
	
	private static int breadthFirstSearch(Maze maze)	{
		Position start=maze.specialPoints[0];
		BitSet pending=new BitSet(maze.specialPoints.length);
		for (int i=1;i<maze.specialPoints.length;++i) pending.set(i);
		PathStatus initialStatus=new PathStatus(start,pending);
		Set<PathStatus> visited=new HashSet<>();
		Set<PathStatus> currentGen=Collections.singleton(initialStatus);
		int steps=0;
		while (!currentGen.isEmpty())	{
			++steps;
			Set<PathStatus> nextGen=new HashSet<>();
			for (PathStatus status:currentGen)	{
				List<PathStatus> children=maze.getChildren(status);
				for (PathStatus child:children) if (!visited.contains(child))	{
					if ((child.pending.cardinality()==0)&&(child.position.equals(start))) return steps;
					else nextGen.add(child);
				}
				visited.add(status);
			}
			currentGen=nextGen;
		}
		throw new IllegalArgumentException("A que voy yo y lo encuentro.");
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<boolean[]> mazeWalls=new ArrayList<>();
		IntObjMap<Position> specialPointMap=HashIntObjMaps.newMutableMap();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			int lineIndex=mazeWalls.size();
			int len=line.length();
			boolean[] parsedLine=new boolean[len];
			for (int i=0;i<len;++i)	{
				char c=line.charAt(i);
				if (c=='#') parsedLine[i]=true;
				else if (c=='.') parsedLine[i]=false;
				else	{
					int point=c-'0';
					specialPointMap.put(point,new Position(i,lineIndex));
					parsedLine[i]=false;
				}
			}
			mazeWalls.add(parsedLine);
		}
		Position[] pointList=new Position[specialPointMap.size()];
		for (IntObjCursor<Position> cursor=specialPointMap.cursor();cursor.moveNext();) pointList[cursor.key()]=cursor.value();
		Maze maze=new Maze(mazeWalls.toArray(boolean[][]::new),pointList);
		int result=breadthFirstSearch(maze);
		System.out.println(result);
	}
}
