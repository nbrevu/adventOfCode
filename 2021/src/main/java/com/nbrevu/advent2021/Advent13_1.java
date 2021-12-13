package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent13_1 {
	private final static String IN_FILE="Advent13.txt";
	
	private final static Pattern FOLD_LINE_PATTERN=Pattern.compile("^fold along ([xy])=(\\d+)$");
	
	private static int max(int[] array)	{
		int result=array[0];
		for (int i=1;i<array.length;++i) result=Math.max(result,array[i]);
		return result;
	}
	
	private static class FoldInfo	{
		public final boolean foldY;
		public final int where;
		public FoldInfo(boolean foldY,int where)	{
			this.foldY=foldY;
			this.where=where;
		}
	}
	
	private static class Paper	{
		private final BitSet[] rows;
		public Paper(int[] xs,int[] ys)	{
			int maxX=max(xs);
			int maxY=max(ys);
			rows=new BitSet[1+maxY];
			for (int i=0;i<=maxY;++i) rows[i]=new BitSet(1+maxX);
			for (int i=0;i<xs.length;++i) rows[ys[i]].set(xs[i]);
		}
		private Paper(BitSet[] rows)	{
			this.rows=rows;
		}
		public Paper fold(FoldInfo f)	{
			return f.foldY?foldY(f.where):foldX(f.where);
		}
		private Paper foldY(int where)	{
			BitSet[] newRows=new BitSet[where];
			for (int i=0;i<where;++i)	{
				newRows[i]=new BitSet(rows[i].size());
				newRows[i].or(rows[i]);
				// Si doble por el 48, el  47 engancha con el 49
				newRows[i].or(rows[2*where-i]);
			}
			return new Paper(newRows);
		}
		private Paper foldX(int where)	{
			BitSet[] newRows=new BitSet[rows.length];
			for (int i=0;i<newRows.length;++i)	{
				newRows[i]=new BitSet(where);
				for (int j=0;j<where;++j) newRows[i].set(j,rows[i].get(j)||rows[i].get(2*where-j));
			}
			return new Paper(newRows);
		}
		public int count()	{
			int result=0;
			for (BitSet b:rows) result+=b.cardinality();
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		IntStream.Builder xStream=IntStream.builder();
		IntStream.Builder yStream=IntStream.builder();
		int lineIndex=0;
		for (;;)	{
			String line=lines.get(lineIndex);
			++lineIndex;
			if (line.isBlank()) break;
			String[] split=line.split(",");
			xStream.accept(Integer.parseInt(split[0]));
			yStream.accept(Integer.parseInt(split[1]));
		}
		int[] xs=xStream.build().toArray();
		int[] ys=yStream.build().toArray();
		List<FoldInfo> folds=new ArrayList<>();
		for (;lineIndex<lines.size();++lineIndex)	{
			String line=lines.get(lineIndex);
			Matcher m=FOLD_LINE_PATTERN.matcher(line);
			if (m.matches())	{
				boolean isY=m.group(1).equals("y");
				int where=Integer.parseInt(m.group(2));
				folds.add(new FoldInfo(isY,where));
			}	else throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		}
		Paper paper=new Paper(xs,ys);
		paper=paper.fold(folds.get(0));
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(paper.count());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
