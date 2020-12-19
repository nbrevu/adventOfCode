package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Resources;

public class Advent10_1 {
	private final static String IN_FILE="Advent10.txt";
	
	private static boolean[] getFieldLine(String line)	{
		boolean[] result=new boolean[line.length()];
		for (int i=0;i<result.length;++i) result[i]=(line.charAt(i)=='#');
		return result;
	}
	
	private static boolean[][] getField(List<String> lines)	{
		return lines.stream().map(Advent10_1::getFieldLine).toArray(boolean[][]::new);
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
	
	private static int getBestSight(Multimap<Position,Position> pos)	{
		return pos.asMap().values().stream().mapToInt(Collection::size).max().getAsInt();
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		boolean[][] field=getField(Resources.readLines(file,Charsets.UTF_8));
		Multimap<Position,Position> visiblePositions=getVisiblePositions(field);
		System.out.println(getBestSight(visiblePositions));
	}
}
