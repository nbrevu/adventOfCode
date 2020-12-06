package com.nbrevu.advent2015;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_231 {
	private final static String IN_FILE="2015/Advent231.txt";
	private final static String FINAL_REG="b";
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
	}
	
	private static class Hlf implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^hlf (.)$");
		private final String reg;
		private Hlf(String reg)	{
			this.reg=reg;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger regValue=status.readRegister(reg);
			regValue=regValue.divide(BigInteger.TWO);
			status.writeRegister(reg,regValue);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Hlf(matcher.group(1));
		}
	}
	private static class Tpl implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^tpl (.)$");
		private final static BigInteger THREE=BigInteger.valueOf(3l);
		private final String reg;
		private Tpl(String reg)	{
			this.reg=reg;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger regValue=status.readRegister(reg);
			regValue=regValue.multiply(THREE);
			status.writeRegister(reg,regValue);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Tpl(matcher.group(1));
		}
	}
	private static class Inc implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^inc (.)$");
		private final String reg;
		private Inc(String reg)	{
			this.reg=reg;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger regValue=status.readRegister(reg);
			regValue=regValue.add(BigInteger.ONE);
			status.writeRegister(reg,regValue);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Inc(matcher.group(1));
		}
	}
	private static class Jmp implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jmp (\\+|\\-)(\\d+)$");
		private final int offset;
		private Jmp(int offset)	{
			this.offset=offset;
		}
		@Override
		public void run(ComputerStatus status)	{
			status.jump(offset);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			boolean isNegative=matcher.group(1).equals("-");
			int offset=Integer.parseInt(matcher.group(2));
			if (isNegative) offset=-offset;
			return new Jmp(offset);
		}
	}
	private static class Jie implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jie (.), (\\+|\\-)(\\d+)$");
		private final String reg;
		private final int offset;
		public Jie(String reg,int offset)	{
			this.reg=reg;
			this.offset=offset;
		}
		@Override
		public void run(ComputerStatus status)	{
			BigInteger regValue=status.readRegister(reg);
			if (!regValue.testBit(0)) status.jump(offset);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			String reg=matcher.group(1);
			boolean isNegative=matcher.group(2).equals("-");
			int offset=Integer.parseInt(matcher.group(3));
			if (isNegative) offset=-offset;
			return new Jie(reg,offset);
		}
	}
	private static class Jio implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jio (.), (\\+|\\-)(\\d+)$");
		private final String reg;
		private final int offset;
		public Jio(String reg,int offset)	{
			this.reg=reg;
			this.offset=offset;
		}
		@Override
		public void run(ComputerStatus status)	{
			BigInteger regValue=status.readRegister(reg);
			if (regValue.equals(BigInteger.ONE)) status.jump(offset);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			String reg=matcher.group(1);
			boolean isNegative=matcher.group(2).equals("-");
			int offset=Integer.parseInt(matcher.group(3));
			if (isNegative) offset=-offset;
			return new Jio(reg,offset);
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(Hlf::parse,Tpl::parse,Inc::parse,Jmp::parse,Jie::parse,Jio::parse);
		public Instruction parse(String line)	{
			for (Function<String,Instruction> parser:parserFunctions)	{
				Instruction instr=parser.apply(line);
				if (instr!=null) return instr;
			}
			throw new IllegalArgumentException("Was machst du?");
		}
	}
	
	private static class ComputerStatus	{
		private final List<Instruction> instructions;
		private final Map<String,BigInteger> registers;
		private int instrPointer;
		public ComputerStatus(List<Instruction> instructions)	{
			this.instructions=instructions;
			registers=new HashMap<>();
			registers.put("a",BigInteger.ZERO);
			registers.put("b",BigInteger.ZERO);
			instrPointer=0;
		}
		@Override
		public String toString()	{
			return String.format("a=%s, b=%s, instrPointer=%d.",registers.get("a"),registers.get("b"),instrPointer);
		}
		public BigInteger readRegister(String reg)	{
			return registers.get(reg);
		}
		public void writeRegister(String reg,BigInteger value)	{
			registers.put(reg,value);
		}
		public void jump(int offset)	{
			// YES, minus one. Because there is an automatic +1 after each instruction.
			instrPointer+=offset-1;
		}
		public void run()	{
			while ((instrPointer>=0)&&(instrPointer<instructions.size()))	{
				instructions.get(instrPointer).run(this);
				++instrPointer;
			}
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		ComputerStatus computer=new ComputerStatus(instructions);
		computer.run();
		BigInteger result=computer.readRegister(FINAL_REG);
		System.out.println(result);
	}
}
