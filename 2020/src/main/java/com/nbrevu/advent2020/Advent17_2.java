package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent17_2 {
	private final static String IN_FILE="Advent17.txt";
	private final static int ITERATIONS=6;
	
	private static boolean isActive(boolean[] array,int i)	{
		return (i>=0)&&(i<array.length)&&array[i];
	}
	
	private static boolean isActive(boolean[][] array,int i,int j)	{
		if ((i<0)||(i>=array.length)) return false;
		else return isActive(array[i],j);
	}
	
	private static boolean isActive(boolean[][][] array,int i,int j,int k)	{
		if ((i<0)||(i>=array.length)) return false;
		else return isActive(array[i],j,k);
	}
	
	private static boolean isActive(boolean[][][][] array,int i,int j,int k,int l)	{
		if ((i<0)||(i>=array.length)) return false;
		else return isActive(array[i],j,k,l);
	}
	
	private static int countNeighbours(boolean[][][][] array,int i,int j,int k,int l)	{
		int result=0;
		for (int x=i-1;x<=i+1;++x) for (int y=j-1;y<=j+1;++y) for (int z=k-1;z<=k+1;++z) for (int w=l-1;w<=l+1;++w) if ((x==i)&&(y==j)&&(z==k)&&(w==l)) continue;
		else if (isActive(array,x,y,z,w)) ++result;
		return result;
	}
	
	private static boolean[][][][] iterate(boolean[][][][] in)	{
		int size1=in.length;
		int size2=in[0].length;
		int size3=in[0][0].length;
		int size4=in[0][0][0].length;
		int expanded1=2+size1;
		int expanded2=2+size2;
		int expanded3=2+size3;
		int expanded4=2+size4;
		boolean[][][][] result=new boolean[expanded1][expanded2][expanded3][expanded4];
		for (int i=0;i<expanded1;++i) for (int j=0;j<expanded2;++j) for (int k=0;k<expanded3;++k) for (int l=0;l<expanded4;++l)	{
			boolean previousValue=isActive(in,i-1,j-1,k-1,l-1);
			int neighbours=countNeighbours(in,i-1,j-1,k-1,l-1);
			if ((neighbours==3)||((neighbours==2)&&previousValue)) result[i][j][k][l]=true;
		}
		return result;
	}
	
	private static boolean[] parse(String line)	{
		boolean[] result=new boolean[line.length()];
		for (int i=0;i<line.length();++i) result[i]=(line.charAt(i)=='#');
		return result;
	}
	
	private static int countActive(boolean[][][][] array)	{
		int result=0;
		for (int i=0;i<array.length;++i) for (int j=0;j<array[i].length;++j) for (int k=0;k<array[i][j].length;++k) for (int l=0;l<array[i][j][k].length;++l) if (array[i][j][k][l]) ++result;
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		boolean[][][][] configuration=new boolean[1][1][][];
		configuration[0][0]=Resources.readLines(file,Charsets.UTF_8).stream().map(Advent17_2::parse).toArray(boolean[][]::new);
		for (int i=0;i<ITERATIONS;++i) configuration=iterate(configuration);
		System.out.println(countActive(configuration));
	}
}
