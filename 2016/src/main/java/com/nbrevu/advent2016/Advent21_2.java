package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent21_2 {
	private final static String IN_FILE="Advent21.txt";
	private final static String INITIAL_STRING="fbgdceah";
	
	private static interface Scrambler	{
		public void unscramble(char[] str);
	}
	private static class SwapPosition implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^swap position (\\d+) with position (\\d+)$");
		private final int pos1;
		private final int pos2;
		public SwapPosition(int pos1,int pos2)	{
			this.pos1=pos1;
			this.pos2=pos2;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new SwapPosition(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			char swap=str[pos1];
			str[pos1]=str[pos2];
			str[pos2]=swap;
		}
	}
	private static class SwapLetter implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^swap letter ([a-z]) with letter ([a-z])$");
		private final char letter1;
		private final char letter2;
		public SwapLetter(char letter1,char letter2)	{
			this.letter1=letter1;
			this.letter2=letter2;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new SwapLetter(matcher.group(1).charAt(0),matcher.group(2).charAt(0));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			for (int i=0;i<str.length;++i) if (str[i]==letter1) str[i]=letter2;
			else if (str[i]==letter2) str[i]=letter1;
		}
	}
	private static class RotateSteps implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^rotate (left|right) (\\d+) steps?$");
		private final boolean isLeft;
		private final int steps;
		public RotateSteps(boolean isLeft,int steps)	{
			this.isLeft=isLeft;
			this.steps=steps;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new RotateSteps(matcher.group(1).equals("left"),Integer.parseInt(matcher.group(2)));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			char[] oldArray=Arrays.copyOf(str,str.length);
			for (int i=0;i<oldArray.length;++i)	{
				int pos=i;
				if (!isLeft) pos+=str.length-(steps%str.length);
				else pos+=steps;
				pos%=str.length;
				str[pos]=oldArray[i];
			}
		}
	}
	private static class RotateBasedOnLetter implements Scrambler	{
		/*-
		 * This is tricky. Let's work in reverse, starting for the UNSCRAMBLED position. Let p be the unscrambled position and q the scrambled one.
		 * p=0 -> rotate 1 -> q=1.
		 * p=1 -> rotate 2 -> q=3.
		 * p=2 -> rotate 3 -> q=5.
		 * p=3 -> rotate 4 -> q=7.
		 * p=4 -> rotate 6 -> q=2.
		 * p=5 -> rotate 7 -> q=4.
		 * p=6 -> rotate 8 -> q=6.
		 * p=7 -> rotate 9 -> q=0.
		 * Therefore, the amount to rotate can be stored in an array where the key is the position of the letter:
		 * q=0 -> rotate 16-9=7.
		 * q=1 -> rotate 8-1=7.
		 * q=2 -> rotate 8-6=2.
		 * q=3 -> rotate 8-2=6.
		 * q=4 -> rotate 8-7=1.
		 * q=5 -> rotate 8-3=5.
		 * q=6 -> rotate 8-8=0.
		 * q=7 -> rotate 8-4=4.
		 */
		private final static Pattern PATTERN=Pattern.compile("^rotate based on position of letter ([a-z])$");
		private final static int[] STEPS=new int[] {7,7,2,6,1,5,0,4};
		private final char letter;
		public RotateBasedOnLetter(char letter)	{
			this.letter=letter;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new RotateBasedOnLetter(matcher.group(1).charAt(0));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			int index=-1;
			for (int i=0;i<str.length;++i) if (str[i]==letter)	{
				index=i;
				break;
			}
			if (index<0) throw new IllegalArgumentException("No.");
			int toRotate=STEPS[index];
			if (toRotate==0) return;
			char[] oldArray=Arrays.copyOf(str,str.length);
			for (int i=0;i<oldArray.length;++i)	{
				int pos=i;
				pos+=toRotate;
				pos%=str.length;
				str[pos]=oldArray[i];
			}
		}
	}
	private static class Reverse implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^reverse positions (\\d+) through (\\d+)$");
		private final int pos1;
		private final int pos2;
		public Reverse(int pos1,int pos2)	{
			this.pos1=pos1;
			this.pos2=pos2;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new Reverse(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			for (int i=pos1;;++i)	{
				int swapPos=pos2+pos1-i;
				if (swapPos<=i) break;
				char swap=str[i];
				str[i]=str[swapPos];
				str[swapPos]=swap;
			}
		}
	}
	private static class Move implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^move position (\\d+) to position (\\d+)$");
		private final int pos1;
		private final int pos2;
		public Move(int pos1,int pos2)	{
			this.pos1=pos1;
			this.pos2=pos2;
		}
		public static Scrambler parse(String in)	{
			Matcher matcher=PATTERN.matcher(in);
			if (matcher.matches()) return new Move(Integer.parseInt(matcher.group(1)),Integer.parseInt(matcher.group(2)));
			else return null;
		}
		@Override
		public void unscramble(char[] str)	{
			char letter=str[pos2];
			if (pos2<pos1) for (int i=pos2;i<pos1;++i) str[i]=str[i+1];
			else if (pos1<pos2) for (int i=pos2;i>pos1;--i) str[i]=str[i-1];
			str[pos1]=letter;
		}
	}
	
	private static enum Parser	{
		INSTANCE;
		private final static List<Function<String,Scrambler>> PARSERS=Arrays.asList(SwapPosition::parse,SwapLetter::parse,RotateSteps::parse,RotateBasedOnLetter::parse,Reverse::parse,Move::parse);
		public Scrambler parse(String line)	{
			for (Function<String,Scrambler> parser:PARSERS)	{
				Scrambler result=parser.apply(line);
				if (result!=null) return result;
			}
			throw new IllegalArgumentException("Can't touch this.");
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Scrambler> scramblers=new LinkedList<>();
		/*
		 * Note that the elements are added at the START of the list because the scramble process must be executed in reverse, which means
		 * that the instruction must be reversed both in execution (hence the "unscramble" that undoes the scrambling) and the add(0,x) that
		 * adds each instruction at the start, ultimately creating a reversed list. This is also why a LinkedList and not an ArrayList.
		 */
		for (String line:Resources.readLines(file,Charsets.UTF_8)) scramblers.add(0,Parser.INSTANCE.parse(line));
		char[] chars=INITIAL_STRING.toCharArray();
		for (Scrambler s:scramblers) s.unscramble(chars);
		System.out.println(String.copyValueOf(chars));
	}
}
