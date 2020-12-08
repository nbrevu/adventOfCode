package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent21_1 {
	private final static String IN_FILE="Advent21.txt";
	private final static String INITIAL_STRING="abcdefgh";
	
	private static interface Scrambler	{
		public void scramble(char[] str);
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
		public void scramble(char[] str)	{
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
		public void scramble(char[] str)	{
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
		public void scramble(char[] str)	{
			char[] oldArray=Arrays.copyOf(str,str.length);
			for (int i=0;i<oldArray.length;++i)	{
				int pos=i;
				if (isLeft) pos+=str.length-(steps%str.length);
				else pos+=steps;
				pos%=str.length;
				str[pos]=oldArray[i];
			}
		}
	}
	private static class RotateBasedOnLetter implements Scrambler	{
		private final static Pattern PATTERN=Pattern.compile("^rotate based on position of letter ([a-z])$");
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
		public void scramble(char[] str)	{
			int index=-1;
			for (int i=0;i<str.length;++i) if (str[i]==letter)	{
				index=i;
				break;
			}
			if (index<0) throw new IllegalArgumentException("No.");
			int steps=1+index+((index>=4)?1:0);
			char[] oldArray=Arrays.copyOf(str,str.length);
			for (int i=0;i<oldArray.length;++i)	{
				int pos=i;
				pos+=steps;
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
		public void scramble(char[] str)	{
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
		public void scramble(char[] str)	{
			char letter=str[pos1];
			if (pos1<pos2) for (int i=pos1;i<pos2;++i) str[i]=str[i+1];
			else if (pos2<pos1) for (int i=pos1;i>pos2;--i) str[i]=str[i-1];
			str[pos2]=letter;
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
		List<Scrambler> scramblers=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) scramblers.add(Parser.INSTANCE.parse(line));
		char[] chars=INITIAL_STRING.toCharArray();
		for (Scrambler s:scramblers) s.scramble(chars);
		System.out.println(String.copyValueOf(chars));
	}
}
