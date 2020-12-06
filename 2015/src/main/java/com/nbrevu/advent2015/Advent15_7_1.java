package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_7_1 {
	private final static String IN_FILE="Advent7.txt";
	private final static String FINAL_REGISTER="a";

	private final static int MASK=(1<<16)-1;
	
	private static class Computer	{
		private abstract class Evaluator	{
			private OptionalInt cache;
			public Evaluator()	{
				cache=OptionalInt.empty();
			}
			protected abstract int doEvaluate();
			public int evaluate()	{
				if (cache.isPresent()) return cache.getAsInt();
				int result=doEvaluate();
				cache=OptionalInt.of(result);
				return result;
			}
		}
		private class Literal extends Evaluator	{
			private final String operand;
			public Literal(String operand)	{
				this.operand=operand;
			}
			@Override
			public int doEvaluate()	{
				return evaluateLiteral(operand);
			}
		}
		private class Not extends Evaluator	{
			private final String operand;
			public Not(String operand)	{
				this.operand=operand;
			}
			@Override
			public int doEvaluate()	{
				int complement=1-evaluateLiteral(operand);
				return complement&MASK;
			}
		}
		private class And extends Evaluator	{
			private final String operand1;
			private final String operand2;
			public And(String operand1,String operand2)	{
				this.operand1=operand1;
				this.operand2=operand2;
			}
			@Override
			public int doEvaluate()	{
				int value1=evaluateLiteral(operand1);
				int value2=evaluateLiteral(operand2);
				return value1&value2;
			}
		}
		private class Or extends Evaluator	{
			private final String operand1;
			private final String operand2;
			public Or(String operand1,String operand2)	{
				this.operand1=operand1;
				this.operand2=operand2;
			}
			@Override
			public int doEvaluate()	{
				int value1=evaluateLiteral(operand1);
				int value2=evaluateLiteral(operand2);
				return value1|value2;
			}
		}
		private class Lshift extends Evaluator	{
			private final String operand1;
			private final String operand2;
			public Lshift(String operand1,String operand2)	{
				this.operand1=operand1;
				this.operand2=operand2;
			}
			@Override
			public int doEvaluate()	{
				int value1=evaluateLiteral(operand1);
				int value2=evaluateLiteral(operand2);
				return (value1<<value2)&MASK;
			}
		}
		private class Rshift extends Evaluator	{
			private final String operand1;
			private final String operand2;
			public Rshift(String operand1,String operand2)	{
				this.operand1=operand1;
				this.operand2=operand2;
			}
			@Override
			public int doEvaluate()	{
				int value1=evaluateLiteral(operand1);
				int value2=evaluateLiteral(operand2);
				return value1>>>value2;
			}
		}
		private final Map<String,Evaluator> evaluators;
		public Computer()	{
			evaluators=new HashMap<>();
		}
		private Evaluator createEvaluator(String[] instruction,int length)	{
			if (length==1) return new Literal(instruction[0]);
			else if (length==2)	{
				if (!instruction[0].equals("NOT")) throw new IllegalArgumentException();
				return new Not(instruction[1]);
			}	else switch (instruction[1])	{
				case "AND":return new And(instruction[0],instruction[2]);
				case "OR":return new Or(instruction[0],instruction[2]);
				case "LSHIFT":return new Lshift(instruction[0],instruction[2]);
				case "RSHIFT":return new Rshift(instruction[0],instruction[2]);
				default:throw new IllegalArgumentException();
			}
		}
		public void parseInstruction(String instruction)	{
			String[] split=instruction.split(" ");
			if ((split.length<3)||(split.length>5)) throw new IllegalArgumentException();
			if (!split[split.length-2].equals("->")) throw new IllegalArgumentException();
			String target=split[split.length-1];
			if (evaluators.containsKey(target)) throw new IllegalArgumentException();
			evaluators.put(target,createEvaluator(split,split.length-2));
		}
		public int evaluateLiteral(String operand)	{
			try	{
				short value=Short.parseShort(operand);
				return value;
			}	catch (NumberFormatException exc)	{
				Evaluator evaluator=evaluators.get(operand);
				if (evaluator==null) throw new IllegalArgumentException();
				return evaluator.evaluate();
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		Computer comp=new Computer();
		URL file=Resources.getResource(IN_FILE);
		for (String line:Resources.readLines(file,Charsets.UTF_8)) comp.parseInstruction(line);
		System.out.println(comp.evaluateLiteral(FINAL_REGISTER));
	}
}
