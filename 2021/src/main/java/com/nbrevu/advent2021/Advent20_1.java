package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent20_1 {
	private final static String IN_FILE="Advent20.txt";
	private final static char OFF='.';
	private final static char ON='#';
	
	private static BitSet parseLine(String line)	{
		BitSet result=new BitSet(line.length());
		for (int i=0;i<line.length();++i)	{
			char c=line.charAt(i);
			if (c==ON) result.set(i);
			else if (c!=OFF) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
		}
		return result;
	}
	
	private static class Image	{
		private final List<BitSet> lines;
		private final int lineLength;
		private final boolean valueOutsideWindow;
		public Image(int lineLength,boolean valueOutsideWindow)	{
			this.lineLength=lineLength;
			this.valueOutsideWindow=valueOutsideWindow;
			lines=new ArrayList<>();
		}
		public void addLine(String line)	{
			if (line.length()!=lineLength) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			lines.add(parseLine(line));
		}
		private int getBitAt(int i,int j)	{
			if ((i<0)||(i>=lines.size())||(j<0)||(j>=lineLength)) return valueOutsideWindow?1:0;
			else return lines.get(i).get(j)?1:0;
		}
		public Image expand(BitSet template)	{
			int newLineLength=2+lineLength;
			int newLines=2+lines.size();
			boolean newValueOutsideWindow=template.get(valueOutsideWindow?511:0);
			Image result=new Image(newLineLength,newValueOutsideWindow);
			for (int i=0;i<newLines;++i)	{
				BitSet line=new BitSet(newLineLength);
				for (int j=0;j<newLineLength;++j)	{
					int index=0;
					for (int ii=-2;ii<=0;++ii) for (int jj=-2;jj<=0;++jj)	{
						index*=2;
						index+=getBitAt(i+ii,j+jj);
					}
					boolean pixel=template.get(index);
					line.set(j,pixel);
				}
				result.lines.add(line);
			}
			return result;
		}
		public int countOn()	{
			int result=0;
			for (BitSet b:lines) result+=b.cardinality();
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		BitSet template=parseLine(lines.get(0));
		Image im=new Image(lines.get(2).length(),false);
		for (int i=2;i<lines.size();++i) im.addLine(lines.get(i));
		for (int i=0;i<2;++i) im=im.expand(template);
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(im.countOn());
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
