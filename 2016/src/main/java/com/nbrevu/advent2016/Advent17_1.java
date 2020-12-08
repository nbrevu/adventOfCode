package com.nbrevu.advent2016;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import com.google.common.collect.Iterables;

public class Advent17_1 {
	private final static String PASSCODE="qljzarfv";
	
	private static class Position	{
		public final int x;
		public final int y;
		public Position(int x,int y)	{
			this.x=x;
			this.y=y;
		}
	}
	
	private static enum Direction	{
		R(3)	{
			@Override
			public Position move(Position p) {
				return new Position(p.x+1,p.y);
			}
		},
		U(0)	{
			@Override
			public Position move(Position p) {
				return new Position(p.x,p.y-1);
			}
		},
		L(2)	{
			@Override
			public Position move(Position p) {
				return new Position(p.x-1,p.y);
			}
		},
		D(1)	{
			@Override
			public Position move(Position p) {
				return new Position(p.x,p.y+1);
			}
		};
		public final int index;
		private Direction(int index)	{
			this.index=index;
		}
		public abstract Position move(Position p);
	}

	private static class HashCalculator	{
		private final static Direction[] DIRS=Direction.values();
		private final byte[] constantBytes;
		private final MessageDigest md5;
		public HashCalculator(String key) throws NoSuchAlgorithmException	{
			constantBytes=key.getBytes();
			md5=MessageDigest.getInstance("MD5");
		}
		public EnumSet<Direction> getOpenDoors(String suffix)	{
			EnumSet<Direction> result=EnumSet.noneOf(Direction.class);
			char[] section=getMd5Section(suffix);
			for (Direction d:DIRS) if (isOpen(section[d.index])) result.add(d);
			return result;
		}
		private static boolean isOpen(char c)	{
			return c>=0xb;
		}
		private char[] getMd5Section(String suffix)	{
			md5.update(constantBytes);
			md5.update(suffix.getBytes());
			byte[] hash=md5.digest();
			char[] result=bytesToString(hash);
			md5.reset();
			return result;
		}
		private static char[] bytesToString(byte[] bytes)	{
			char[] result=new char[4];
			for (int i=0;i<2;++i)	{
				result[2*i]=(char)((bytes[i]>>4)&0x0f);
				result[2*i+1]=(char)(bytes[i]&0x0f);
			}
			return result;
		}
	}
	
	private static class FullPosition	{
		public final Position pos;
		public final String suffix;
		public FullPosition()	{
			this(new Position(0,0),"");
		}
		private FullPosition(Position pos,String suffix)	{
			this.pos=pos;
			this.suffix=suffix;
		}
		public List<FullPosition> getChildren(HashCalculator calculator)	{
			List<FullPosition> result=new ArrayList<>();
			for (Direction d:calculator.getOpenDoors(suffix))	{
				Position newPos=d.move(pos);
				if (isValid(newPos)) result.add(new FullPosition(newPos,suffix+d.name()));
			}
			return result;
		}
		public boolean isFinal()	{
			return (pos.x==3)&&(pos.y==3);
		}
		private static boolean isValid(Position pos)	{
			return (pos.x>=0)&&(pos.x<4)&&(pos.y>=0)&&(pos.y<4);
		}
	}
	
	private static String breadthFirst(FullPosition pos,HashCalculator calculator)	{
		List<List<FullPosition>> thisGen=Collections.singletonList(Collections.singletonList(pos));
		for (;;)	{
			List<List<FullPosition>> nextGen=new ArrayList<>();
			for (FullPosition p:Iterables.concat(thisGen))	{
				List<FullPosition> children=p.getChildren(calculator);
				for (FullPosition p2:children) if (p2.isFinal()) return p2.suffix;
				nextGen.add(children);
			}
			thisGen=nextGen;
		}
	}
	
	public static void main(String[] args) throws NoSuchAlgorithmException	{
		FullPosition initial=new FullPosition();
		HashCalculator calculator=new HashCalculator(PASSCODE);
		String result=breadthFirst(initial,calculator);
		System.out.println(result);
	}
}
