package com.nbrevu.advent2021;

import java.io.IOException;
import java.util.List;

public class Advent24_2 {
	// Represents a constraint in the form x_i=x_j+N
	private static class Equation	{
		public final int leftIndex;
		public final int rightIndex;
		public final int constant;
		public Equation(int leftIndex,int rightIndex,int constant)	{
			this.leftIndex=leftIndex;
			this.rightIndex=rightIndex;
			this.constant=constant;
		}
		private void fillMin(int[] digits)	{
			if (constant<0)	{
				digits[leftIndex]=1;
				digits[rightIndex]=1-constant;
			}	else	{
				digits[leftIndex]=1+constant;
				digits[rightIndex]=1;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		List<Equation> allEqs=List.of(new Equation(0,13,8),new Equation(1,12,-7),new Equation(2,3,3),new Equation(4,7,-2),new Equation(5,6,-3),new Equation(8,9,5),new Equation(10,11,1));
		int[] digits=new int[14];
		for (Equation eq:allEqs) eq.fillMin(digits);
		for (int d:digits) System.out.print(d);
		System.out.println();
		long tic=System.nanoTime();
		long tac=System.nanoTime();
		double seconds=1e-9*(tac-tic);
		System.out.println("Elapsed "+seconds+" seconds.");
	}
}
