package com.nbrevu.advent2017;

import java.math.RoundingMode;

import com.google.common.math.IntMath;

public class Advent3_1 {
	private final static int INPUT=312051;
	
	public static void main(String[] args)	{
		/*
		 * Ulam spiral.
		 * 1st corner: (2n-1)^2+2n=4n^2-2n+1
		 * 2nd corner: 4n^2+1
		 * 3rd corner: 4n^2+2n+1
		 * 4th corner: (2n+1)^2
		 * Each "square" goes from (2n-1)^2+1 to (2n+1)^2, both included.
		 */
		int sq=IntMath.sqrt(INPUT,RoundingMode.UP);
		if ((sq&1)==0) ++sq;
		int n=(sq-1)/2;
		int n2_4=4*n*n;
		int nn=n*2;
		// First arm: from 4n^2-4n+2 to 4n^2-2n+1
		int start=n2_4-2*nn+2;
		int cor1=n2_4-nn+1;
		int cor2=cor1+nn;
		int cor3=cor2+nn;
		int cor4=cor3+nn;
		if (INPUT<start) throw new IllegalStateException("Unmöglich!");
		else if (INPUT<cor1)	{
			int diff=cor1-INPUT;
			int distX=n-diff;
			System.out.println(n+Math.abs(distX));
			return;
		}	else if (INPUT<cor2)	{
			int diff=cor2-INPUT;
			int distY=n-diff;
			System.out.println(n+Math.abs(distY));
			return;
		}	else if (INPUT<cor3)	{
			int diff=cor3-INPUT;
			int distX=n-diff;
			System.out.println(n+Math.abs(distX));
			return;
		}	else if (INPUT<=cor4)	{
			int diff=cor4-INPUT;
			int distY=n-diff;
			System.out.println(n+Math.abs(distY));
			return;
		}	else throw new IllegalStateException("Sehr unmöglich!!!!!");
	}
}
