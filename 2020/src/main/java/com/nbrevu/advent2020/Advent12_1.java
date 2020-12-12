package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent12_1 {
	private final static String IN_FILE="Advent12.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([NSEWLRF])(\\d+)");
	
	private static enum Direction	{
		EAST	{
			@Override
			public void moveForward(Position p,int amount) {
				p.x+=amount;
			}			
		},
		NORTH	{
			@Override
			public void moveForward(Position p,int amount) {
				p.y+=amount;
			}			
		},
		WEST	{
			@Override
			public void moveForward(Position p,int amount) {
				p.x-=amount;
			}			
		},
		SOUTH	{
			@Override
			public void moveForward(Position p,int amount) {
				p.y-=amount;
			}			
		};
		public abstract void moveForward(Position p,int amount);
		private final static Map<Direction,Direction> LEFT_ROTATIONS=createLeftRotations();
		private final static Map<Direction,Direction> RIGHT_ROTATIONS=createRightRotations();
		private static Map<Direction,Direction> createLeftRotations()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(EAST,NORTH);
			result.put(NORTH,WEST);
			result.put(WEST,SOUTH);
			result.put(SOUTH,EAST);
			return result;
		}
		private static Map<Direction,Direction> createRightRotations()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(EAST,SOUTH);
			result.put(NORTH,EAST);
			result.put(WEST,NORTH);
			result.put(SOUTH,WEST);
			return result;
		}
		public Direction rotateLeft()	{
			return LEFT_ROTATIONS.get(this);
		}
		public Direction rotateRight()	{
			return RIGHT_ROTATIONS.get(this);
		}
	}
	
	private static class Position	{
		public Direction dir;
		public int x;
		public int y;
		public Position()	{
			dir=Direction.EAST;
			x=0;
			y=0;
		}
	}
	
	private static enum Action	{
		N	{
			@Override
			public void act(Position pos,int amount) {
				Direction.NORTH.moveForward(pos,amount);
			}
		},
		S	{
			@Override
			public void act(Position pos,int amount) {
				Direction.SOUTH.moveForward(pos,amount);
			}
		},
		E	{
			@Override
			public void act(Position pos,int amount) {
				Direction.EAST.moveForward(pos,amount);
			}
		},
		W	{
			@Override
			public void act(Position pos,int amount) {
				Direction.WEST.moveForward(pos,amount);
			}
		},
		L	{
			@Override
			public void act(Position pos,int amount) {
				int realAmount=amount/90;
				for (int i=0;i<realAmount;++i) pos.dir=pos.dir.rotateLeft();
			}
		},
		R	{
			@Override
			public void act(Position pos,int amount) {
				int realAmount=amount/90;
				for (int i=0;i<realAmount;++i) pos.dir=pos.dir.rotateRight();
			}
		},
		F	{
			@Override
			public void act(Position pos,int amount) {
				pos.dir.moveForward(pos,amount);
			}
		};
		public abstract void act(Position pos,int amount);
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Position p=new Position();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				Action a=Action.valueOf(matcher.group(1));
				int amount=Integer.parseInt(matcher.group(2));
				a.act(p,amount);
			}
		}
		int result=Math.abs(p.x)+Math.abs(p.y);
		System.out.println(result);
	}
}
