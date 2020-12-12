package com.nbrevu.advent2018;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.google.common.math.IntMath;

public class Advent14_2 {
	private final static int INPUT=880751;
	
	private static int addAndCheck(List<Integer> workingList,int digitToAdd,int[] goal)	{
		workingList.add(digitToAdd);
		if (workingList.size()<goal.length) return -1;
		for (int i=0;i<goal.length;++i) if (goal[i]!=workingList.get(workingList.size()-goal.length+i)) return -1;
		return workingList.size()-goal.length;
	}
	
	private static int[] getDigits(int value)	{
		int size=IntMath.log10(value+1,RoundingMode.UP);
		int[] result=new int[size];
		for (int i=size-1;i>=0;--i)	{
			result[i]=value%10;
			value/=10;
		}
		return result;
	}
	
	public static void main(String[] args)	{
		List<Integer> workingList=new ArrayList<>();
		workingList.add(3);
		workingList.add(7);
		int pos1=0;
		int pos2=1;
		int[] digits=getDigits(INPUT);
		for (;;)	{
			int val1=workingList.get(pos1);
			int val2=workingList.get(pos2);
			int sum=val1+val2;
			if (sum<10)	{
				int result=addAndCheck(workingList,sum,digits);
				if (result!=-1)	{
					System.out.println(result);
					return;
				}
			}	else	{
				int result=addAndCheck(workingList,1,digits);
				if (result!=-1)	{
					System.out.println(result);
					return;
				}
				result=addAndCheck(workingList,sum%10,digits);
				if (result!=-1)	{
					System.out.println(result);
					return;
				}
			}
			pos1=(pos1+1+val1)%workingList.size();
			pos2=(pos2+1+val2)%workingList.size();
		}
	}
}
