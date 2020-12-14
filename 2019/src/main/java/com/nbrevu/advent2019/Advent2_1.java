package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent2_1 {
	private final static String IN_FILE="Advent2.txt";
	
	private static int[] parse(String line)	{
		return Pattern.compile(",").splitAsStream(line).mapToInt(Integer::parseInt).toArray();
	}
	
	private static void run(int[] program)	{
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
			}	case 99:return;
			default: throw new RuntimeException("Something did indeed go wrong.");
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		program[1]=12;
		program[2]=2;
		run(program);
		System.out.println(program[0]);
	}
}
