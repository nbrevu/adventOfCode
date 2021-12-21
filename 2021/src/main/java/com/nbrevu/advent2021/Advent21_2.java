package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.ObjIntCursor;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.ObjLongCursor;
import com.koloboke.collect.map.ObjLongMap;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashObjIntMaps;
import com.koloboke.collect.map.hash.HashObjLongMaps;

public class Advent21_2 {
	private final static String IN_FILE="Advent21.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Player (\\d) starting position: (\\d)$");
	
	private static class TurnResult	{
		public final int wins;
		public final ObjIntMap<State> nextStates;
		public TurnResult(int wins,ObjIntMap<State> nextStates)	{
			this.wins=wins;
			this.nextStates=nextStates;
		}
	}
	
	private static class State	{
		private final static IntIntMap TURN_CASES=HashIntIntMaps.newImmutableMap(new int[] {3,4,5,6,7,8,9},new int[] {1,3,6,7,6,3,1});
		private final int pos1;
		private final int score1;
		private final int pos2;
		private final int score2;
		private State(int pos1,int score1,int pos2,int score2)	{
			this.pos1=pos1;
			this.score1=score1;
			this.pos2=pos2;
			this.score2=score2;
		}
		private final static State[][][][] COLLECTION=getAllStates();
		private static State[][][][] getAllStates()	{
			State[][][][] result=new State[11][21][11][21];
			for (int p1=1;p1<=10;++p1) for (int s1=0;s1<=20;++s1) for (int p2=1;p2<=10;++p2) for (int s2=0;s2<=20;++s2) result[p1][s1][p2][s2]=new State(p1,s1,p2,s2);
			return result;
		}
		public static State getInitial(int start1,int start2)	{
			return COLLECTION[start1][0][start2][0];
		}
		private State moveP1(int pos)	{
			int newPos=pos1+pos;
			if (newPos>=11) newPos-=10;
			int newScore=score1+newPos;
			if (newScore>=21) return null;
			else return COLLECTION[newPos][newScore][pos2][score2];
		}
		private State moveP2(int pos)	{
			int newPos=pos2+pos;
			if (newPos>=11) newPos-=10;
			int newScore=score2+newPos;
			if (newScore>=21) return null;
			else return COLLECTION[pos1][score1][newPos][newScore];
		}
		private TurnResult turn(boolean isP2)	{
			int wins=0;
			ObjIntMap<State> states=HashObjIntMaps.newMutableMap();
			for (IntIntCursor c=TURN_CASES.cursor();c.moveNext();)	{
				State s=isP2?moveP2(c.key()):moveP1(c.key());
				if (s==null) wins+=c.value();
				else states.addValue(s,c.value());
			}
			return new TurnResult(wins,states);
		}
	}
	
	private static class FinalGameResult	{
		public final long p1Wins;
		public final long p2Wins;
		public FinalGameResult(long p1Wins,long p2Wins)	{
			this.p1Wins=p1Wins;
			this.p2Wins=p2Wins;
		}
	}
	
	private static class GenerationResult	{
		public final long wins;
		public final ObjLongMap<State> nextGen;
		public GenerationResult(long wins,ObjLongMap<State> nextGen)	{
			this.wins=wins;
			this.nextGen=nextGen;
		}
	}
	
	private static GenerationResult playTurn(ObjLongMap<State> states,boolean isP2)	{
		long wins=0;
		ObjLongMap<State> nextGen=HashObjLongMaps.newMutableMap();
		for (ObjLongCursor<State> cursor=states.cursor();cursor.moveNext();)	{
			State state=cursor.key();
			long counter=cursor.value();
			TurnResult turnResult=state.turn(isP2);
			wins+=turnResult.wins*counter;
			for (ObjIntCursor<State> cursor2=turnResult.nextStates.cursor();cursor2.moveNext();) nextGen.addValue(cursor2.key(),counter*cursor2.value());
		}
		return new GenerationResult(wins,nextGen);
	}
	
	private static FinalGameResult playGame(int start1,int start2)	{
		ObjLongMap<State> currentGen=HashObjLongMaps.newImmutableMapOf(State.getInitial(start1,start2),1);
		long wins1=0;
		long wins2=0;
		boolean isP2=false;
		do	{
			GenerationResult turnResult=playTurn(currentGen,isP2);
			if (isP2) wins2+=turnResult.wins;
			else wins1+=turnResult.wins;
			currentGen=turnResult.nextGen;
			isP2=!isP2;
		}	while (!currentGen.isEmpty());
		return new FinalGameResult(wins1,wins2);
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		Matcher m1=LINE_PATTERN.matcher(lines.get(0));
		Matcher m2=LINE_PATTERN.matcher(lines.get(1));
		if (!m1.matches()||!m2.matches()) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		int n1=Integer.parseInt(m1.group(1));
		int p1=Integer.parseInt(m1.group(2));
		int n2=Integer.parseInt(m2.group(1));
		int p2=Integer.parseInt(m2.group(2));
		if ((n1!=1)||(n2!=2)) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		FinalGameResult r=playGame(p1,p2);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(Math.max(r.p1Wins,r.p2Wins));
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
