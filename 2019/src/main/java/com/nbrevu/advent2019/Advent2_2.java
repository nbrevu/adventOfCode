package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_2 {
	private final static String IN_FILE="Advent2.txt";
	private final static int GOAL=19690720;
	
	private static int[] parse(String line)	{
		return Pattern.compile(",").splitAsStream(line).mapToInt(Integer::parseInt).toArray();
	}
	
	private static int run(int[] baseProgram,int noun,int verb)	{
		int[] program=Arrays.copyOf(baseProgram,baseProgram.length);
		program[1]=noun;
		program[2]=verb;
		for (int index=0;index<program.length;index+=4) switch (program[index])	{
			case 1:	{
				int index1=program[index+1];
				int index2=program[index+2];
				int index3=program[index+3];
				program[index3]=program[index1]+program[index2];
				break;
			}	case 2:	{
				int index1=program[index+1];
				int index2=program[index+2];
				int index3=program[index+3];
				program[index3]=program[index1]*program[index2];
				break;
			}	case 99:return program[0];
			default: throw new RuntimeException("Something did indeed go wrong.");
		}
		return program[0];
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		for (int i=0;i<=99;++i) for (int j=0;j<=99;++j) if (run(program,i,j)==GOAL)	{
			System.out.println(100*i+j);
			return;
		}
	}
}
