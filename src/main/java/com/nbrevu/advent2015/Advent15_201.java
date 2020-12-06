package com.nbrevu.advent2015;

import java.io.IOException;

import com.google.common.math.LongMath;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;

public class Advent15_201 {
	private final static int GOAL=29000000;
	
	private final static int PRIME_LIMIT=1000000;
	
	private static long[] firstPrimeSieve(long maxNumber)	{
		long[] firstPrimes=new long[1+(int)maxNumber];
		firstPrimes[0]=firstPrimes[1]=1;
		for (int j=4;j<=maxNumber;j+=2) firstPrimes[j]=2;
		int sq=(int)(Math.sqrt((double)maxNumber));
		for (int i=3;i<=sq;i+=2) if (firstPrimes[i]==0) for (int j=i*i;j<=maxNumber;j+=i+i) if (firstPrimes[j]==0) firstPrimes[j]=i;
		return firstPrimes;
	}
	
	private static LongIntMap getPrimeDecomposition(int n,long[] firstPrimes)	{
		LongIntMap divisors=HashLongIntMaps.newMutableMap();
		while (n>1)	{
			long p=firstPrimes[n];
			if (p==0)	{
				divisors.addValue(n,1);
				return divisors;
			}	else	{
				divisors.addValue(p,1);
				n/=p;
			}
		}
		return divisors;
	}
	
	private static long calculateSumOfDivisors(int in,long[] firstPrimes)	{
		LongIntMap divisors=getPrimeDecomposition(in,firstPrimes);
		long result=1;
		for (LongIntCursor cursor=divisors.cursor();cursor.moveNext();)	{
			long prime=cursor.key();
			int power=cursor.value();
			long factor=(LongMath.pow(prime,power+1)-1)/(prime-1);
			result*=factor;
		}
		return result;
	}

	public static void main(String[] args) throws IOException	{
		int realGoal=GOAL/10;
		long[] firstPrimes=firstPrimeSieve(PRIME_LIMIT);
		for (int i=2;i<PRIME_LIMIT;++i) if (calculateSumOfDivisors(i,firstPrimes)>=realGoal)	{
			System.out.println(i);
			return;
		}
	}
}
