package com.nbrevu.advent2015;

import java.io.IOException;
import java.math.RoundingMode;
import java.util.function.LongConsumer;

import com.google.common.math.IntMath;
import com.koloboke.collect.LongCursor;
import com.koloboke.collect.map.LongIntCursor;
import com.koloboke.collect.map.LongIntMap;
import com.koloboke.collect.map.hash.HashLongIntMaps;
import com.koloboke.collect.set.LongSet;
import com.koloboke.collect.set.hash.HashLongSets;

public class Advent15_20_2 {
	private final static int GOAL=29000000;
	
	private final static int PRIME_LIMIT=10000000;
	private final static int MIN_FACTOR=50;
	
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
	
	private static LongSet getAllDivisors(LongIntMap decomposition)	{
		LongSet result=HashLongSets.newMutableSet(new long[] {1});
		for (LongIntCursor cursor1=decomposition.cursor();cursor1.moveNext();)	{
			long prime=cursor1.key();
			int power=cursor1.value();
			LongSet toAdd=HashLongSets.newMutableSet();
			for (LongCursor previous=result.cursor();previous.moveNext();)	{
				long elem=previous.elem();
				for (int i=1;i<=power;++i)	{
					elem*=prime;
					toAdd.add(elem);
				}
			}
			toAdd.forEach((LongConsumer)result::add);
		}
		return result;
	}
	
	private static long calculateCappedSumOfDivisors(int in,long[] firstPrimes,int minFactor)	{
		LongSet allDivisors=getAllDivisors(getPrimeDecomposition(in,firstPrimes));
		long result=0;
		for (LongCursor cursor=allDivisors.cursor();cursor.moveNext();)	{
			long divisor=cursor.elem();
			if (divisor*minFactor>=in) result+=divisor;
		}
		return result;
	}

	public static void main(String[] args) throws IOException	{
		int realGoal=IntMath.divide(GOAL,11,RoundingMode.UP);
		long[] firstPrimes=firstPrimeSieve(PRIME_LIMIT);
		for (int i=2;i<PRIME_LIMIT;++i) if (calculateCappedSumOfDivisors(i,firstPrimes,MIN_FACTOR)>=realGoal)	{
			System.out.println(i);
			return;
		}
	}
}
