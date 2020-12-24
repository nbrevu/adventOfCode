package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.IntCursor;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent24_2 {
	private final static String IN_FILE="Advent24.txt";
	private final static int ITERATIONS=100;
	
	/*
	 * First coordinate ("x"): normal, horizontal.
	 * Second coordinate ("y"): at 60 degrees, not really vertical. 
	 */
	private static class Position	{
		public int x;
		public int y;
	}
	
	private static enum Step	{
		E	{
			@Override
			public void advance(Position pos) {
				++pos.x;
			}
		},
		NE	{
			@Override
			public void advance(Position pos) {
				++pos.y;
			}
		},
		NW	{
			@Override
			public void advance(Position pos) {
				--pos.x;
				++pos.y;
			}
		},
		W	{
			@Override
			public void advance(Position pos) {
				--pos.x;
			}
		},
		SW	{
			@Override
			public void advance(Position pos) {
				--pos.y;
			}
		},
		SE	{
			@Override
			public void advance(Position pos) {
				++pos.x;
				--pos.y;
			}
		};
		public abstract void advance(Position pos);
	}
	
	// I'm going to overengineer the shit out of this BECAUSE I WANT TO!
	private static class DirectionParser	{
		private static class ParserAction	{
			public final ParserState nextState;
			public final Optional<Step> stepToAdd;
			public ParserAction(ParserState nextState)	{
				this.nextState=nextState;
				this.stepToAdd=Optional.empty();
			}
			public ParserAction(Step stepToAdd)	{
				this.nextState=ParserState.NORMAL;
				this.stepToAdd=Optional.of(stepToAdd);
			}
		}
		private static enum ParserState	{
			NORMAL,N,S;
			private final static Map<ParserState,CharObjMap<ParserAction>> ACTIONS=createActionsTable();
			private static Map<ParserState,CharObjMap<ParserAction>> createActionsTable()	{
				Map<ParserState,CharObjMap<ParserAction>> result=new EnumMap<>(ParserState.class);
				CharObjMap<ParserAction> normalActions=HashCharObjMaps.newMutableMap();
				normalActions.put('e',new ParserAction(Step.E));
				normalActions.put('n',new ParserAction(N));
				normalActions.put('w',new ParserAction(Step.W));
				normalActions.put('s',new ParserAction(S));
				result.put(NORMAL,normalActions);
				CharObjMap<ParserAction> nActions=HashCharObjMaps.newMutableMap();
				nActions.put('e',new ParserAction(Step.NE));
				nActions.put('w',new ParserAction(Step.NW));
				result.put(N,nActions);
				CharObjMap<ParserAction> sActions=HashCharObjMaps.newMutableMap();
				sActions.put('e',new ParserAction(Step.SE));
				sActions.put('w',new ParserAction(Step.SW));
				result.put(S,sActions);
				return result;
			}
			public ParserAction takeAction(char c)	{
				return ACTIONS.get(this).get(c);
			}
		}
		public static List<Step> parseString(String str)	{
			ParserState state=ParserState.NORMAL;
			List<Step> result=new ArrayList<>();
			for (int i=0;i<str.length();++i)	{
				ParserAction action=state.takeAction(str.charAt(i));
				if (action==null) throw new IllegalStateException("Unexpected character.");
				state=action.nextState;
				if (action.stepToAdd.isPresent()) result.add(action.stepToAdd.get());
			}
			if (state!=ParserState.NORMAL) throw new IllegalStateException("Unexpected end of string.");
			return result;
		}
	}
	
	private static boolean[][] initState(IntObjMap<IntSet> positions)	{
		int minX=Integer.MAX_VALUE;
		int maxX=Integer.MIN_VALUE;
		int minY=Integer.MAX_VALUE;
		int maxY=Integer.MIN_VALUE;
		for (IntObjCursor<IntSet> cursorX=positions.cursor();cursorX.moveNext();)	{
			int x=cursorX.key();
			minX=Math.min(minX,x);
			maxX=Math.max(maxX,x);
			for (IntCursor cursorY=cursorX.value().cursor();cursorY.moveNext();)	{
				int y=cursorY.elem();
				minY=Math.min(minY,y);
				maxY=Math.max(maxY,y);
			}
		}
		int sizeX=maxX+1-minX;
		int sizeY=maxY+1-minY;
		int offsetX=-minX;
		int offsetY=-minY;
		boolean[][] result=new boolean[sizeY][sizeX];
		for (IntObjCursor<IntSet> cursorX=positions.cursor();cursorX.moveNext();)	{
			int x=cursorX.key()+offsetX;
			for (IntCursor cursorY=cursorX.value().cursor();cursorY.moveNext();)	{
				int y=cursorY.elem()+offsetY;
				result[y][x]=true;
			}
		}
		return result;
	}
	
	private static boolean getValue(boolean[][] array,int i,int j)	{
		if ((i<0)||(i>=array.length)) return false;
		else if ((j<0)||(j>=array[i].length)) return false;
		else return array[i][j];
	}
	
	private static boolean isDirectionValid(int i,int j)	{
		if ((i==0)&&(j==0)) return false;
		else return i*j!=1;
	}
	
	private static int countNeighbours(boolean[][] array,int i,int j)	{
		int result=0;
		for (int k=-1;k<=1;++k) for (int l=-1;l<=1;++l) if (isDirectionValid(k,l)&&getValue(array,i+k,j+l)) ++result;
		return result;
	}
	
	private static boolean advanceState(boolean currentState,int neighbours)	{
		if (currentState) return (neighbours==1)||(neighbours==2);
		else return neighbours==2;
	}
	
	private static boolean[][] iterate(boolean[][] state)	{
		int size1=state.length+2;
		int size2=state[0].length+2;
		boolean[][] result=new boolean[size1][size2];
		for (int i=0;i<size1;++i) for (int j=0;j<size2;++j)	{
			boolean currentValue=getValue(state,i-1,j-1);
			int neighbours=countNeighbours(state,i-1,j-1);
			result[i][j]=advanceState(currentValue,neighbours);
		}
		return result;
	}
	
	private static int countBlackTiles(boolean[][] state)	{
		int result=0;
		for (boolean[] array:state) for (boolean b:array) if (b) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		IntObjMap<IntSet> blackPositions=HashIntObjMaps.newMutableMap();
		for (String str:content)	{
			Position pos=new Position();
			for (Step s:DirectionParser.parseString(str)) s.advance(pos);
			IntSet subMap=blackPositions.computeIfAbsent(pos.x,(int unused)->HashIntSets.newMutableSet());
			if (subMap.contains(pos.y)) subMap.removeInt(pos.y);
			else subMap.add(pos.y);
		}
		boolean[][] state=initState(blackPositions);
		for (int i=0;i<ITERATIONS;++i) state=iterate(state);
		System.out.println(countBlackTiles(state));
	}
}