package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;

public class Advent13_2 {
	private final static String IN_FILE="Advent13.txt";
	
	private static enum Direction	{
		RIGHT	{
			@Override
			public void move(Cart cart) {
				++cart.x;
			}
		},
		UP	{
			@Override
			public void move(Cart cart) {
				--cart.y;
			}
		},
		LEFT	{
			@Override
			public void move(Cart cart) {
				--cart.x;
			}
		},
		DOWN	{
			@Override
			public void move(Cart cart) {
				++cart.y;
			}
		};
		public abstract void move(Cart cart);
		public Direction slash45()	{
			return SLASH_45_MAP.get(this);
		}
		public Direction slash135()	{
			return SLASH_135_MAP.get(this);
		}
		public Direction rotateLeft()	{
			return LEFT_MAP.get(this);
		}
		public Direction rotateRight()	{
			return RIGHT_MAP.get(this);
		}
		private final static Map<Direction,Direction> SLASH_45_MAP=createSlash45Map();
		private final static Map<Direction,Direction> SLASH_135_MAP=createSlash135Map();
		private final static Map<Direction,Direction> LEFT_MAP=createLeftMap();
		private final static Map<Direction,Direction> RIGHT_MAP=createRightMap();
		private static Map<Direction,Direction> createSlash45Map()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,UP);
			result.put(UP,RIGHT);
			result.put(LEFT,DOWN);
			result.put(DOWN,LEFT);
			return result;
		}
		private static Map<Direction,Direction> createSlash135Map()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,DOWN);
			result.put(UP,LEFT);
			result.put(LEFT,UP);
			result.put(DOWN,RIGHT);
			return result;
		}
		private static Map<Direction,Direction> createLeftMap()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,UP);
			result.put(UP,LEFT);
			result.put(LEFT,DOWN);
			result.put(DOWN,RIGHT);
			return result;
		}
		private static Map<Direction,Direction> createRightMap()	{
			Map<Direction,Direction> result=new EnumMap<>(Direction.class);
			result.put(RIGHT,DOWN);
			result.put(UP,RIGHT);
			result.put(LEFT,UP);
			result.put(DOWN,LEFT);
			return result;
		}
	}
	
	private static enum JunctionAction	{
		ROTATE_LEFT	{
			@Override
			public void act(Cart cart) {
				cart.dir=cart.dir.rotateLeft();
				cart.onJunction=GO_STRAIGHT;
			}
		},
		GO_STRAIGHT	{	// https://www.youtube.com/watch?v=Dd_tcRO125Q
			@Override
			public void act(Cart cart) {
				cart.onJunction=ROTATE_RIGHT;
			}
		},
		ROTATE_RIGHT	{
			@Override
			public void act(Cart cart) {
				cart.dir=cart.dir.rotateRight();
				cart.onJunction=ROTATE_LEFT;
			}
		};
		public abstract void act(Cart cart);
	}
	
	private static class Cart	{
		private final static String NEUTRAL_CHARACTERS="|->^<v";
		private final static char JUNCTION='+';
		private final static char SLASH_45='/';
		private final static char SLASH_135='\\';
		private int x;
		private int y;
		private Direction dir;
		private JunctionAction onJunction;
		public Cart(int x,int y,Direction dir)	{
			this.x=x;
			this.y=y;
			this.dir=dir;
			onJunction=JunctionAction.ROTATE_LEFT;
		}
		public void move(char[][] map)	{
			dir.move(this);
			char c=map[y][x];
			if (JUNCTION==c) onJunction.act(this);
			else if (SLASH_45==c) dir=dir.slash45();
			else if (SLASH_135==c) dir=dir.slash135();
			else if (NEUTRAL_CHARACTERS.indexOf(c)<0) throw new IllegalArgumentException("Unexpected character: "+c+".");
		}
	}
	
	private final static CharObjMap<Direction> CART_INDICATORS=createCartIndicatorMap();
	private static CharObjMap<Direction> createCartIndicatorMap()	{
		CharObjMap<Direction> result=HashCharObjMaps.newMutableMap();
		result.put('>',Direction.RIGHT);
		result.put('^',Direction.UP);
		result.put('<',Direction.LEFT);
		result.put('v',Direction.DOWN);
		return result;
	}
	
	private static class Position implements Comparable<Position>	{
		// This class is used as a map key and therefore it must be immutable to avoid subtle mistakes.
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
		@Override
		public int compareTo(Position o) {
			int diffY=y-o.y;
			return (diffY!=0)?diffY:(x-o.x);
		}
	}
	
	private static NavigableMap<Position,Cart> findCarts(char[][] map)	{
		NavigableMap<Position,Cart> result=new TreeMap<>();
		for (int i=0;i<map.length;++i) for(int j=0;j<map[i].length;++j)	{
			Direction dir=CART_INDICATORS.get(map[i][j]);
			if (dir!=null) result.put(new Position(j,i),new Cart(j,i,dir));
		}
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		char[][] map=Resources.readLines(file,Charsets.UTF_8).stream().map(String::toCharArray).toArray(char[][]::new);
		NavigableMap<Position,Cart> carts=findCarts(map);
		Set<Cart> removed=new HashSet<>();
		for (;;)	{
			// By virtue of coming from a multimap, carts are iterated in the correct order.
			List<Map.Entry<Position,Cart>> sortedCartList=new ArrayList<>(carts.entrySet());
			for (Map.Entry<Position,Cart> entry:sortedCartList)	{
				Cart c=entry.getValue();
				if (removed.contains(c)) continue;
				Position p=entry.getKey();
				carts.remove(p);
				c.move(map);
				Position newPos=new Position(c.x,c.y);
				// Upon collision, we remove the colliding part AND we don't reinsert the current cart, thereby effectively removing both carts. 
				if (carts.containsKey(newPos)) removed.add(carts.remove(newPos));
				else carts.put(newPos,c);
			}
			if (carts.size()==1)	{
				Position p=carts.firstKey();
				System.out.println(p.x+","+p.y);
				return;
			}
			removed.clear();
		}
	}
}
