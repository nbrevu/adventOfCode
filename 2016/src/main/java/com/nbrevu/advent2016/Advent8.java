package com.nbrevu.advent2016;

import java.io.IOException;
import java.net.URL;
import java.util.BitSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8 {
	private final static String IN_FILE="Advent8.txt";
	private final static int WIDTH=50;
	private final static int HEIGHT=6;
	
	private static class Screen	{
		private final static Pattern RECT_PATTERN=Pattern.compile("^rect (\\d+)x(\\d+)$");
		private final static Pattern ROTATE_ROW_PATTERN=Pattern.compile("^rotate row y=(\\d+) by (\\d+)$");
		private final static Pattern ROTATE_COL_PATTERN=Pattern.compile("^rotate column x=(\\d+) by (\\d+)$");
		private final boolean[][] pixels;
		public Screen(int width,int height)	{
			pixels=new boolean[height][width];
		}
		public void rect(int a,int b)	{
			for (int i=0;i<a;++i) for (int j=0;j<b;++j) pixels[j][i]=true;
		}
		public void rotateRow(int a,int b)	{
			int width=pixels[a].length;
			BitSet tmpStorage=new BitSet(width);
			for (int i=0;i<width;++i) if (pixels[a][i]) tmpStorage.set(i);
			for (int i=0;i<width;++i) pixels[a][(i+b)%width]=tmpStorage.get(i);
		}
		public void rotateCol(int a,int b)	{
			int width=pixels.length;
			BitSet tmpStorage=new BitSet(width);
			for (int i=0;i<width;++i) if (pixels[i][a]) tmpStorage.set(i);
			for (int i=0;i<width;++i) pixels[(i+b)%width][a]=tmpStorage.get(i);
		}
		public void runInstruction(String str)	{
			Matcher matcher=RECT_PATTERN.matcher(str);
			if (matcher.matches())	{
				int a=Integer.parseInt(matcher.group(1));
				int b=Integer.parseInt(matcher.group(2));
				rect(a,b);
				return;
			}
			matcher=ROTATE_ROW_PATTERN.matcher(str);
			if (matcher.matches())	{
				int a=Integer.parseInt(matcher.group(1));
				int b=Integer.parseInt(matcher.group(2));
				rotateRow(a,b);
				return;
			}
			matcher=ROTATE_COL_PATTERN.matcher(str);
			if (matcher.matches())	{
				int a=Integer.parseInt(matcher.group(1));
				int b=Integer.parseInt(matcher.group(2));
				rotateCol(a,b);
				return;
			}
			throw new IllegalArgumentException("HCF instruction detected!");
		}
		public int countPixels()	{
			int result=0;
			for (int i=0;i<pixels.length;++i) for (int j=0;j<pixels[i].length;++j) if (pixels[i][j]) ++result;
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Screen result=new Screen(WIDTH,HEIGHT);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) result.runInstruction(line);
		System.out.println(result.countPixels());
		for (int i=0;i<HEIGHT;++i)	{
			for (int j=0;j<WIDTH;++j) System.out.print(result.pixels[i][j]?'#':'.');
			System.out.println();
		}
		// "CFLELOYFCS"?
	}
}
