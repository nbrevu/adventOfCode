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
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;
import com.koloboke.collect.set.IntSet;
import com.koloboke.collect.set.hash.HashIntSets;

public class Advent24_1 {
	private final static String IN_FILE="Advent24.txt";
	
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
		int result=0;
		for (IntSet s:blackPositions.values()) result+=s.size();
		System.out.println(result);
	}
}