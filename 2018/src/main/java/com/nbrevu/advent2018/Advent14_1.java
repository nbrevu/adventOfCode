package com.nbrevu.advent2018;

public class Advent14_1 {
	private final static int INPUT=880751;
	
	public static void main(String[] args)	{
		int[] workingArray=new int[INPUT+11];
		workingArray[0]=3;
		workingArray[1]=7;
		int pos1=0;
		int pos2=1;
		int realLimit=2;
		while (realLimit<INPUT+10)	{
			int val1=workingArray[pos1];
			int val2=workingArray[pos2];
			int sum=val1+val2;
			if (sum<10)	{
				workingArray[realLimit]=sum;
				++realLimit;
			}	else	{
				workingArray[realLimit]=1;
				workingArray[realLimit+1]=sum%10;
				realLimit+=2;
			}
			pos1=(pos1+1+val1)%realLimit;
			pos2=(pos2+1+val2)%realLimit;
		}
		StringBuilder result=new StringBuilder();
		for (int i=INPUT;i<INPUT+10;++i) result.append(workingArray[i]);
		System.out.println(result.toString());
	}
}
