package com.nbrevu.advent2017;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

public class Advent3_2 {
	private final static int INPUT=312051;
	
	private static class Position	{
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
		public Position next()	{
			if (((x+y)==0)&&(x>=0)) return new Position(x+1,y);
			else if ((x>0)&&(x>Math.abs(y))) return new Position(x,y+1);
			else if ((y>0)&&((x==y)||(y>Math.abs(x)))) return new Position(x-1,y);
			else if ((x<0)&&((y==-x)||(Math.abs(x)>Math.abs(y)))) return new Position(x,y-1);
			else if ((y<0)&&((x==y)||(Math.abs(y)>Math.abs(x)))) return new Position(x+1,y);
			else throw new IllegalArgumentException("Was?");
		}
	}
	
	public static long sumNeighbours(Position p,Table<Integer,Integer,Long> values)	{
		long result=0;
		for (int i=p.x-1;i<=p.x+1;++i) for (int j=p.y-1;j<=p.y+1;++j)	{
			Long value=values.get(i,j);
			if (value!=null) result+=value.longValue();
		}
		return result;
	}
	
	public static void main(String[] args)	{
		Table<Integer,Integer,Long> values=HashBasedTable.create();
		Position p=new Position(0,0);
		values.put(p.x,p.y,1l);
		for (;;)	{
			p=p.next();
			long thisResult=sumNeighbours(p,values);
			if (thisResult>INPUT)	{
				System.out.println(thisResult);
				return;
			}
			values.put(p.x,p.y,thisResult);
		}
	}
}
