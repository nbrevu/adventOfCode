package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent5_2 {
	private final static String IN_FILE="Advent5.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^(\\d+),(\\d+) -> (\\d+),(\\d+)$");
	
	private final static int SIZE=1000;
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		int[][] points=new int[SIZE][SIZE];
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int r1=Integer.parseInt(matcher.group(1));
				int c1=Integer.parseInt(matcher.group(2));
				int r2=Integer.parseInt(matcher.group(3));
				int c2=Integer.parseInt(matcher.group(4));
				if (r1==r2)	{
					int min=Math.min(c1,c2);
					int max=Math.max(c1,c2);
					for (int c=min;c<=max;++c) ++points[r1][c];
				}	else if (c1==c2)	{
					int min=Math.min(r1,r2);
					int max=Math.max(r1,r2);
					for (int r=min;r<=max;++r) ++points[r][c1];
				}	else	{
					int sign=(int)Math.signum((r2-r1)*(c2-c1));
					int minR=Math.min(r1,r2);
					int startC=(sign<0)?Math.max(c1,c2):Math.min(c1,c2);
					int len=Math.abs(c2-c1);
					for (int i=0;i<=len;++i) ++points[minR+i][(sign>0)?(startC+i):(startC-i)];
				}
			}	else throw new RuntimeException("Lo que me habéis dao pa papear me roe las tripas.");
		}
		int counter=0;
		for (int i=0;i<SIZE;++i) for (int j=0;j<SIZE;++j) if (points[i][j]>1) ++counter;
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(counter);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
