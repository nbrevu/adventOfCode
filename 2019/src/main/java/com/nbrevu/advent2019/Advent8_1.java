package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_1 {
	private final static String IN_FILE="Advent8.txt";
	
	private final static int WIDTH=25;
	private final static int HEIGHT=6;
	
	private static int[] parseString(String line)	{
		int[] result=new int[line.length()];
		for (int i=0;i<line.length();++i)	{
			int val=line.charAt(i)-'0';
			if ((val<0)||(val>2)) throw new IllegalArgumentException("Unexpected character: "+val+".");
			result[i]=val;
		}
		return result;
	}
	
	private static class LayerSummary	{
		public final int num0;
		public final int num1;
		public final int num2;
		public LayerSummary(int num0,int num1,int num2)	{
			this.num0=num0;
			this.num1=num1;
			this.num2=num2;
		}
	}
	
	private static LayerSummary parseLayer(int[] values,int start,int end)	{
		int num0=0;
		int num1=0;
		int num2=0;
		for (int i=start;i<end;++i) switch (values[i])	{
			case 0:++num0;break;
			case 1:++num1;break;
			case 2:++num2;break;
			default:throw new IllegalArgumentException("Nicht möglich!!!!!");
		}
		return new LayerSummary(num0,num1,num2);
	}
	
	private static int getBestLayer(int[] values)	{
		int layerSize=HEIGHT*WIDTH;
		LayerSummary bestLayer=parseLayer(values,0,layerSize);
		for (int start=layerSize;start+layerSize<=values.length;start+=layerSize)	{
			LayerSummary layer=parseLayer(values,start,start+layerSize);
			if (layer.num0<bestLayer.num0) bestLayer=layer;
		}
		return bestLayer.num1*bestLayer.num2;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] image=parseString(Resources.readLines(file,Charsets.UTF_8).get(0));
		System.out.println(getBestLayer(image));
	}
}
