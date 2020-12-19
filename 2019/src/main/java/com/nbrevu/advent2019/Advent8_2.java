package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_2 {
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
	
	private static class ImageDecoder	{
		public final int height;
		public final int width;
		public final int layerSize;
		public final int numLayers;
		private final int[] values;
		public ImageDecoder(int height,int width,int[] values)	{
			this.height=height;
			this.width=width;
			layerSize=height*width;
			numLayers=values.length/layerSize;
			this.values=values;
		}
		private boolean decodePixel(int i,int j)	{
			int pos=width*i+j;
			for (int k=0;k<numLayers;++k)	{
				if (values[pos]==0) return false;
				else if (values[pos]==1) return true;
				pos+=layerSize;
			}
			return false;
		}
		public boolean[][] decodeImage()	{
			boolean[][] result=new boolean[height][width];
			for (int i=0;i<height;++i) for (int j=0;j<width;++j) result[i][j]=decodePixel(i,j);
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] image=parseString(Resources.readLines(file,Charsets.UTF_8).get(0));
		boolean[][] decodedImage=new ImageDecoder(HEIGHT,WIDTH,image).decodeImage();
		for (int i=0;i<decodedImage.length;++i)	{
			for (int j=0;j<decodedImage[i].length;++j) System.out.print(decodedImage[i][j]?'█':' ');
			System.out.println();
		}
	}
}
