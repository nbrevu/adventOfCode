package com.nbrevu.advent2021;

import java.io.IOException;
import java.net.URL;
import java.util.OptionalLong;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent1_1 {
	private final static String IN_FILE="Advent1.txt";
	
	public static void main(String[] args) throws IOException	{
		long tic=System.nanoTime();
		OptionalLong lastNumber=OptionalLong.empty();
		URL file=Resources.getResource(IN_FILE);
		int counter=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			try	{
				long parsed=Long.parseLong(line);
				if (lastNumber.isPresent())	{
					long prev=lastNumber.getAsLong();
					if (parsed>prev) ++counter;
				}
				lastNumber=OptionalLong.of(parsed);
			}	catch (NumberFormatException nfe)	{
				System.out.println("Lo que me habéis dao pa papear me roe las tripas.");
			}
		}
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println(counter);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
