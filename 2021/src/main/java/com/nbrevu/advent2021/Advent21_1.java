package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent21_1 {
	private final static String IN_FILE="Advent21.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Player (\\d) starting position: (\\d)$");
	
	private static class Player	{
		private int position;
		private int score;
		public Player(int start)	{
			position=start;
			score=0;
		}
		public void advance(int n)	{
			int newPosX=position+n;
			int mod=newPosX%10;
			int newPos=(mod==0)?10:mod;
			score+=newPos;
			position=newPos;
		}
	}
	
	private static class Game	{
		private final Player p1;
		private final Player p2;
		private int die;
		private int turns;
		public Game(int start1,int start2)	{
			p1=new Player(start1);
			p2=new Player(start2);
			die=1;
			turns=0;
		}
		private int throwDie()	{
			int dieSum;
			switch (die)	{
				case 98:
					dieSum=98+99+100;
					die=1;
					break;
				case 99:
					dieSum=99+100+1;
					die=2;
					break;
				case 100:
					dieSum=100+1+2;
					die=3;
					break;
				default:
					dieSum=3*die+3;
					die+=3;
					break;
			}
			turns+=3;
			return dieSum;
		}
		public int play()	{
			Player turn=p1;
			Player other=p2;
			for (;;)	{
				int die=throwDie();
				turn.advance(die);
				if (turn.score>=1000) return other.score*turns;
				Player swap=other;
				other=turn;
				turn=swap;
			}
		}
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
		Game g=new Game(p1,p2);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(g.play());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
