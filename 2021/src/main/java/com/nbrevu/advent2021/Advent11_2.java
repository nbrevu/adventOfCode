package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent11_2 {
	private final static String IN_FILE="Advent11.txt";
	
	private static class Flasher	{
		private final int[][] energy;
		private final int r;
		private final int c;
		public Flasher(int[][] energy)	{
			this.energy=energy;
			r=energy.length;
			c=energy[0].length;
		}
		public boolean hasSynchronised()	{
			for (int i=0;i<r;++i) for (int j=0;j<c;++j) increase(i,j);
			int result=0;
			for (int i=0;i<r;++i) for (int j=0;j<c;++j) if (energy[i][j]>=10)	{
				++result;
				energy[i][j]=0;
			}
			return result==(r*c);
		}
		private void increase(int i,int j)	{
			if (energy[i][j]>=10) return;
			++energy[i][j];
			if (energy[i][j]!=10) return;
			for (int x=-1;x<=1;++x) for (int y=-1;y<=1;++y) if ((x!=0)||(y!=0))	{
				int newI=i+x;
				int newJ=j+y;
				if ((newI>=0)&&(newI<r)&&(newJ>=0)&&(newJ<c)) increase(newI,newJ);
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		URL file=Resources.getResource(IN_FILE);
		List<String> lines=Resources.readLines(file,Charsets.UTF_8);
		int len=lines.get(0).length();
		int[][] energy=new int[lines.size()][len];
		for (int i=0;i<lines.size();++i)	{
			String line=lines.get(i);
			if (line.length()!=len) throw new IllegalArgumentException("Lo que me habéis dao pa papear me roe las tripas.");
			for (int j=0;j<len;++j) energy[i][j]=line.charAt(j)-'0';
		}
		int result=0;
		Flasher f=new Flasher(energy);
		do ++result; while (!f.hasSynchronised());
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(result);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
