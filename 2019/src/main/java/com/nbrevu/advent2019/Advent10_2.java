package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent10_2 {
	private final static String IN_FILE="Advent10.txt";
	private final static int GOAL=200;
	
	private static boolean[] getFieldLine(String line)	{
		boolean[] result=new boolean[line.length()];
		for (int i=0;i<result.length;++i) result[i]=(line.charAt(i)=='#');
		return result;
	}
	
	private static boolean[][] getField(List<String> lines)	{
		return lines.stream().map(Advent10_2::getFieldLine).toArray(boolean[][]::new);
	}
	
	private static class Position	{
		public final int row;
		public final int col;
		public Position(int row,int col)	{
			this.row=row;
			this.col=col;
		}
		@Override
		public int hashCode()	{
			return row+col;
		}
		@Override
		public boolean equals(Object other)	{
			Position p=(Position)other;
			return (row==p.row)&&(col==p.col);
		}
	}
	
	private static int gcd(int a,int b)	{
		if (b==0) return a;
		for (;;)	{
			int rem=a%b;
			if (rem==0) return b;
			a=b;
			b=rem;
		}
	}
	
	private static boolean isVisible(boolean[][] asteroids,int i,int j,int k,int l)	{
		int diffR=k-i;
		int diffC=l-j;
		int gcd=gcd(Math.abs(diffR),Math.abs(diffC));
		if (gcd==1) return true;
		int incrR=diffR/gcd;
		int incrC=diffC/gcd;
		for (int m=1;m<gcd;++m) if (asteroids[i+m*incrR][j+m*incrC]) return false;
		return true;
	}
	
	private static Multimap<Position,Position> getVisiblePositions(boolean[][] asteroids)	{
		Multimap<Position,Position> result=HashMultimap.create();
		for (int i=0;i<asteroids.length;++i) for (int j=0;j<asteroids[i].length;++j) if (asteroids[i][j])	{
			Position me=new Position(i,j);
			// Rest of the first row.
			for (int l=j+1;l<asteroids[i].length;++l) if (asteroids[i][l]&&isVisible(asteroids,i,j,i,l))	{
				Position other=new Position(i,l);
				result.put(me,other);
				result.put(other,me);
			}
			// Rest of the rows.
			for (int k=i+1;k<asteroids.length;++k) for (int l=0;l<asteroids[k].length;++l) if (asteroids[k][l]&&isVisible(asteroids,i,j,k,l))	{
				Position other=new Position(k,l);
				result.put(me,other);
				result.put(other,me);
			}
		}
		return result;
	}
	
	private static Position getBestPosition(Multimap<Position,Position> pos)	{
		Position result=null;
		int highestCount=Integer.MIN_VALUE;
		for (Map.Entry<Position,Collection<Position>> entry:pos.asMap().entrySet())	{
			int count=entry.getValue().size();
			if (count>highestCount)	{
				result=entry.getKey();
				highestCount=count;
			}
		}
		return result;
	}
	
	private static SortedMap<Double,NavigableMap<Integer,Position>> sortByAngle(boolean[][] asteroids,Position start)	{
		SortedMap<Double,NavigableMap<Integer,Position>> result=new TreeMap<>();
		/*
		 * atan2 is repeatable, but floating point issues might yield sliiiiiiiiightly different results for same angles but different values.
		 * To be extra sure, I use the "minimum" angle, obtained by dividing both coordinates by their gcd. This way I'm 100% sure that the keys
		 * will match, because I'm calling atan2 with the same parameters.
		 */
		for (int i=0;i<asteroids.length;++i) for (int j=0;j<asteroids[i].length;++j) if (asteroids[i][j]&&((i!=start.row)||(j!=start.col)))	{
			/*
			 * Rotation! The angle goes in negative direction (or "clockwise", as non-math people say, pffft) starting from the vertical direction
			 * (what would usually be pi/2). In order to reflect this properly, we redefine the coordinates:
			 * Vertical distance (up=positive) will be the X coordinate.
			 * Horizontal distance (right=positive) will be the Y coordinate.
			 */
			int x=start.row-i;
			int y=j-start.col;
			int distance=Math.abs(x)+Math.abs(y);
			int gcd=gcd(Math.abs(x),Math.abs(y));
			x/=gcd;
			y/=gcd;
			double angle=Math.atan2(y,x);
			if (angle<0) angle+=2*Math.PI;
			NavigableMap<Integer,Position> container=result.computeIfAbsent(angle,(Double unused)->new TreeMap<>());
			container.put(distance,new Position(i,j));
		}
		return result;
	}
	
	private static Position getNthPosition(SortedMap<Double,NavigableMap<Integer,Position>> sortedAsteroids,int n)	{
		int destroyed=0;
		// I'm not even going to bother and remove empty angles.
		for (;;) for (NavigableMap<Integer,Position> entry:sortedAsteroids.values()) if (!entry.isEmpty())	{
			Position pos=entry.pollFirstEntry().getValue();
			++destroyed;
			if (n==destroyed) return pos;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		boolean[][] field=getField(Resources.readLines(file,Charsets.UTF_8));
		Multimap<Position,Position> visiblePositions=getVisiblePositions(field);
		Position stationPosition=getBestPosition(visiblePositions);
		SortedMap<Double,NavigableMap<Integer,Position>> sortedAsteroids=sortByAngle(field,stationPosition);
		Position result=getNthPosition(sortedAsteroids,GOAL);
		int value=100*result.col+result.row;
		System.out.println(value);
	}
}
