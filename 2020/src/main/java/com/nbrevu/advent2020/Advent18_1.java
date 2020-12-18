package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent18_1 {
	private final static String IN_FILE="Advent18.txt";
	
	private static class ArithmeticParser	{
		private final char[] tokens;
		private int currentIndex;
		public ArithmeticParser(String line)	{
			tokens=line.replaceAll("[^\\(\\)\\+\\*0-9]","").toCharArray();
			currentIndex=0;
		}
		private boolean isCurrentLevelUnfinished()	{
			return (currentIndex<tokens.length)&&(peekChar()!=')');
		}
		private char peekChar()	{
			return tokens[currentIndex];
		}
		private char consumeChar()	{
			char result=tokens[currentIndex];
			++currentIndex;
			return result;
		}
		private long parseOperator()	{
			// Parse either a digit or a operation between parenthesis.
			char c=consumeChar();
			if (c=='(')	{
				long result=evaluate();
				char c2=consumeChar();
				if (c2!=')') throw new IllegalStateException("Badly formed string. Or bad parser and bad programer, probably.");
				return result;
			}	else if (('0'<=c)&&(c<='9')) return c-'0';
			else throw new IllegalStateException("Unexpected character? "+c+".");
		}
		public long evaluate()	{
			long value=parseOperator();
			while (isCurrentLevelUnfinished())	{
				char operator=consumeChar();
				long value2=parseOperator();
				switch (operator)	{
					case '+':value+=value2;break;
					case '*':value*=value2;break;
					default:throw new IllegalStateException("Bad parser. Bad programmer.");
				}
			}
			return value;
		}
	}
	
	private static long evaluateLine(String line)	{
		return new ArithmeticParser(line).evaluate();
	}
	
	public static void main(String[] args) throws IOException	{
		long result=0l;
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) result+=evaluateLine(line);
		System.out.println(result);
	}
}
