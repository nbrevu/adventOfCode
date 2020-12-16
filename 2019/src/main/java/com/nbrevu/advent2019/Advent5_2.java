package com.nbrevu.advent2019;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent5_2 {
	private final static String IN_FILE="Advent5.txt";
	private final static int[] INPUTS=new int[] {5};
	
	private static int[] parse(String line)	{
		return Pattern.compile(",").splitAsStream(line).mapToInt(Integer::parseInt).toArray();
	}
	
	private static int[] extractModes(int baseOpcode,int howMany)	{
		int[] result=new int[howMany];
		baseOpcode/=100;
		for (int i=0;i<howMany;++i)	{
			result[i]=baseOpcode%10;
			baseOpcode/=10;
		}
		return result;
	}
	
	private static List<Integer> run(int[] baseProgram,int[] inputs)	{
		int inputIndex=0;
		List<Integer> outputs=new ArrayList<>();
		int[] program=Arrays.copyOf(baseProgram,baseProgram.length);
		for (int index=0;index<program.length;)	{
			int baseOpcode=program[index];
			int opCode=baseOpcode%100;
			switch (opCode)	{
				case 1:	{
					int[] modes=extractModes(baseOpcode,3);
					int op1=program[index+1];
					int op2=program[index+2];
					int op3=program[index+3];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (modes[2]!=0) throw new RuntimeException("KHÉ?");
					program[op3]=val1+val2;
					index+=4;
					break;
				}	case 2:	{
					int[] modes=extractModes(baseOpcode,3);
					int op1=program[index+1];
					int op2=program[index+2];
					int op3=program[index+3];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (modes[2]!=0) throw new RuntimeException("KHÉ?");
					program[op3]=val1*val2;
					index+=4;
					break;
				}	case 3:	{
					if (baseOpcode!=opCode) throw new RuntimeException("Esto no me lo esperaba.");
					int op1=program[index+1];
					int input=inputs[inputIndex];
					++inputIndex;
					program[op1]=input;
					index+=2;
					break;
				}	case 4:	{
					int[] modes=extractModes(baseOpcode,1);
					int op1=program[index+1];
					int output=(modes[0]==1)?op1:program[op1];
					outputs.add(output);
					index+=2;
					break;
				}	case 5:	{
					int[] modes=extractModes(baseOpcode,2);
					int op1=program[index+1];
					int op2=program[index+2];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (val1!=0) index=val2;
					else index+=3;
					break;
				}	case 6:	{
					int[] modes=extractModes(baseOpcode,2);
					int op1=program[index+1];
					int op2=program[index+2];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (val1==0) index=val2;
					else index+=3;
					break;
				}	case 7:	{
					int[] modes=extractModes(baseOpcode,3);
					int op1=program[index+1];
					int op2=program[index+2];
					int op3=program[index+3];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (modes[2]!=0) throw new RuntimeException("KHÉ?");
					program[op3]=(val1<val2)?1:0;
					index+=4;
					break;
				}	case 8:	{
					int[] modes=extractModes(baseOpcode,3);
					int op1=program[index+1];
					int op2=program[index+2];
					int op3=program[index+3];
					int val1=(modes[0]==1)?op1:program[op1];
					int val2=(modes[1]==1)?op2:program[op2];
					if (modes[2]!=0) throw new RuntimeException("KHÉ?");
					program[op3]=(val1==val2)?1:0;
					index+=4;
					break;
				}	case 99:return outputs;
				default:System.out.println(index);System.out.println(baseOpcode); throw new RuntimeException("Something did indeed go wrong.");
			}
		}
		return outputs;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		int[] program=parse(Resources.readLines(file,Charsets.UTF_8).get(0));
		List<Integer> result=run(program,INPUTS);
		System.out.println(result);
	}
}
