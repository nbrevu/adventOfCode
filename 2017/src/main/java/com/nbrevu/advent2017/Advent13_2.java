package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent13_2 {
	private final static String IN_FILE="Advent13.txt";
	
	private static class Layer	{
		private final int index;
		private final int value;
		public Layer(int index,int value)	{
			this.index=index;
			this.value=value;
		}
		public boolean isSafe(int delay)	{
			return (index+delay)%(2*(value-1))!=0;
		}
	}
	
	private static boolean isSafe(List<Layer> layers,int delay)	{
		for (Layer l:layers) if (!l.isSafe(delay)) return false;
		return true;
	}
	
	private static int getFirstSafeValue(List<Layer> layers)	{
		for (int i=0;;++i) if (isSafe(layers,i)) return i;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Layer> layers=new ArrayList<>();
		for (String content:Resources.readLines(file,Charsets.UTF_8))	{
			String[] parsed=content.split(": ");
			int index=Integer.parseInt(parsed[0]);
			int value=Integer.parseInt(parsed[1]);
			layers.add(new Layer(index,value));
		}
		int result=getFirstSafeValue(layers);
		System.out.println(result);
	}
}
