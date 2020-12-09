package com.nbrevu.advent2017;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Advent17_1 {
	private final static int INPUT=369;
	private final static int FINAL_VALUE=2017;
	
	public static void main(String[] args) throws IOException	{
		List<Integer> list=new ArrayList<>(0);
		int pos=0;
		list.add(0);
		for (int i=1;i<=FINAL_VALUE;++i)	{
			pos=(pos+INPUT)%i;
			++pos;
			list.add(pos,i);
		}
		System.out.println(list.get(1+pos));
	}
}
