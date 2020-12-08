package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

/*
 * OK. It SEEMS that a careful treatment of nodes is not necessary, since we can just divide the nodes in three kinds:
 * The "zero" node. The one that will be moved around. It has size=86.
 * "Heavy" nodes with 500T. These won't move.
 * Every other node has an amount of used space which is below the minimum size (max used=73, min size=85).
 * Therefore the size of any normal mode is always big enough to accomodate the contents of any other node. The "zero" node itself is a normal one.
 * The search space only needs to take into account three things: heavy (unpassable) nodes, the position of the zero, and the position of the
 * relevant data.
 */
public class Advent22_2 {
	private final static String IN_FILE="Advent22.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^/dev/grid/node\\-x(\\d+)\\-y(\\d+)\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)T\\s+(\\d+)%$");
	
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
	
	private static class Maze	{
		private final int maxX;
		private final int maxY;
		private final Set<Position> heavyNodes;
		public Maze(int maxX,int maxY,Set<Position> heavyNodes)	{
			this.maxX=maxX;
			this.maxY=maxY;
			this.heavyNodes=heavyNodes;
		}
		public boolean isValid(Position p)	{
			return (p.x>=0)&&(p.x<=maxX)&&(p.y>=0)&&(p.y<=maxY)&&!heavyNodes.contains(p);
		}
	}
	
	private static class DataStatus	{
		public final Position zeroNode;
		public final Position dataNode;
		public DataStatus(Position zeroNode,Position dataNode)	{
			this.zeroNode=zeroNode;
			this.dataNode=dataNode;
		}
		@Override
		public int hashCode()	{
			return zeroNode.hashCode()+dataNode.hashCode();
		}
		@Override
		public boolean equals(Object other)	{
			DataStatus ds=(DataStatus)other;
			return zeroNode.equals(ds.zeroNode)&&dataNode.equals(ds.dataNode);
		}
		public int heuristic()	{
			return dataNode.x+dataNode.y+Math.abs(dataNode.x-zeroNode.x)+Math.abs(dataNode.y-zeroNode.y);
		}
		public DataStatus move(Position newZeroNode)	{
			Position newDataNode=(newZeroNode.equals(dataNode))?zeroNode:dataNode;
			return new DataStatus(newZeroNode,newDataNode);
		}
	}
	
	private static class FullStatus	{
		public final DataStatus data;
		public final int currentSteps;
		public final int heuristic;
		public FullStatus(DataStatus data,int currentSteps)	{
			this.data=data;
			this.currentSteps=currentSteps;
			heuristic=data.heuristic()+currentSteps;
		}
	}
	
	private static class AStar	{
		private final static Position GOAL=new Position(0,0);
		private final NavigableMap<Integer,Queue<FullStatus>> nodes;
		private final Maze maze;
		private final Set<DataStatus> visited;
		public AStar(DataStatus initial,Maze maze)	{
			FullStatus fsInitial=new FullStatus(initial,0);
			nodes=new TreeMap<>();
			this.maze=maze;
			add(fsInitial);
			visited=new HashSet<>();
		}
		private void add(FullStatus fs)	{
			nodes.computeIfAbsent(fs.heuristic,(Integer unused)->new ArrayDeque<>()).add(fs);
		}
		private FullStatus poll()	{
			Map.Entry<Integer,Queue<FullStatus>> entry=nodes.firstEntry();
			Queue<FullStatus> queue=entry.getValue();
			FullStatus result=queue.poll();
			if (queue.isEmpty()) nodes.remove(entry.getKey());
			return result;
		}
		private List<FullStatus> children(FullStatus fs)	{
			List<FullStatus> result=new ArrayList<>();
			for (Position newZero:fs.data.zeroNode.children()) if (maze.isValid(newZero))	{
				DataStatus newStatus=fs.data.move(newZero);
				if (!visited.contains(newStatus)) result.add(new FullStatus(newStatus,1+fs.currentSteps));
			}
			return result;
		}
		private boolean isFinal(FullStatus fs)	{
			return GOAL.equals(fs.data.dataNode);
		}
		public int run()	{
			while (!nodes.isEmpty())	{
				FullStatus fs=poll();
				if (visited.contains(fs.data)) continue;
				if (isFinal(fs)) return fs.currentSteps;
				visited.add(fs.data);
				children(fs).forEach(this::add);
			}
			throw new IllegalStateException("¿Estás seguro de que no estabas buscando la página 105?");
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Set<Position> heavyNodes=new HashSet<>();
		Position zeroNode=null;
		int maxX=0;
		int maxY=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int xPos=Integer.parseInt(matcher.group(1));
				int yPos=Integer.parseInt(matcher.group(2));
				// int size=Integer.parseInt(matcher.group(3));
				int used=Integer.parseInt(matcher.group(4));
				// int available=Integer.parseInt(matcher.group(5));
				maxX=Math.max(maxX,xPos);
				maxY=Math.max(maxY,yPos);
				if (used>100) heavyNodes.add(new Position(xPos,yPos));
				else if (used==0)	{
					if (zeroNode!=null) throw new IllegalStateException("Un novio, ¿dos novios?");
					zeroNode=new Position(xPos,yPos);
				}
			}
		}
		if (zeroNode==null) throw new IllegalStateException("Un novio, ¿cero novios?");
		Maze maze=new Maze(maxX,maxY,heavyNodes);
		Position dataNode=new Position(maxX,0);
		DataStatus initialStatus=new DataStatus(zeroNode,dataNode);
		AStar search=new AStar(initialStatus,maze);
		int result=search.run();
		System.out.println(result);
	}
}
