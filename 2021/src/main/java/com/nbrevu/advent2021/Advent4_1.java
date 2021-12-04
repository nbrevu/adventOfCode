package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent4_1 {
	private final static String IN_FILE="Advent4.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^\\s*(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s+(\\d+)\\s*$");
	
	private static class Position	{
		private final int x;
		private final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		@Override
		public int hashCode()	{
			return Objects.hash(x,y);
		}
		@Override
		public boolean equals(Object other)	{
			Position pOther=(Position)other;
			return (x==pOther.x)&&(y==pOther.y);
		}
	}
	
	private static class Bingo	{
		private final BitSet[] marked;
		private final IntObjMap<Position> numbers;
		private final IntSet unmarkedNumbers;
		public Bingo()	{
			marked=new BitSet[5];
			for (int i=0;i<5;++i) marked[i]=new BitSet(5);
			numbers=HashIntObjMaps.newMutableMap();
			unmarkedNumbers=HashIntSets.newMutableSet();
		}
		public void setNumber(int number,int row,int col)	{
			numbers.put(number,new Position(col,row));
			unmarkedNumbers.add(number);
		}
		public OptionalInt markNumber(int number)	{
			unmarkedNumbers.removeInt(number);
			Position p=numbers.get(number);
			if (p==null) return OptionalInt.empty();
			marked[p.y].set(p.x);
			for (int i=0;i<5;++i)	{
				boolean isRowComplete=true;
				boolean isColComplete=true;
				for (int j=0;j<5;++j)	{
					if (!marked[j].get(i)) isRowComplete=false;
					if (!marked[i].get(j)) isColComplete=false;
				}
				if (isRowComplete||isColComplete) return OptionalInt.of(unmarkedNumbers.stream().mapToInt(Integer::intValue).sum());
			}
			return OptionalInt.empty();
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> allLines=Resources.readLines(file,Charsets.UTF_8);
		List<Integer> numbers=new ArrayList<>();
		for (String s:allLines.get(0).split(",")) numbers.add(Integer.valueOf(s));
		int howMany=(allLines.size()-1)/6;
		List<Bingo> bingos=new ArrayList<>();
		for (int i=0;i<howMany;++i)	{
			int initialLine=2+6*i;
			Bingo b=new Bingo();
			for (int j=0;j<5;++j)	{
				String line=allLines.get(initialLine+j);
				Matcher matcher=LINE_PATTERN.matcher(line);
				if (!matcher.matches()) throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
				for (int k=0;k<5;++k)	{
					int number=Integer.parseInt(matcher.group(k+1));
					b.setNumber(number,j,k);
				}
			}
			bingos.add(b);
		}
		boolean hasFinished=false;
		for (int i:numbers)	{
			for (Bingo b:bingos)	{
				OptionalInt finish=b.markNumber(i);
				if (finish.isPresent())	{
					int result=finish.getAsInt()*i;
					System.out.println(result);
					hasFinished=true;
					break;
				}
			}
			if (hasFinished) break;
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
