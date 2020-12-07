package com.nbrevu.advent2016;

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

public class Advent12_2 {
	private final static String IN_FILE="Advent12.txt";
	private final static String FINAL_REG="a";
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
	}
	
	private static class Cpy implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^cpy (.+) (.)$");
		private final String source;
		private final String reg;
		private Cpy(String source,String reg)	{
			this.source=source;
			this.reg=reg;
		}
		@Override
		public void run(ComputerStatus status)	{
			status.writeRegister(reg,status.readData(source));
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Cpy(matcher.group(1),matcher.group(2));
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
	private static class Dec implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^dec (.)$");
		private final String reg;
		private Dec(String reg)	{
			this.reg=reg;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger regValue=status.readRegister(reg);
			regValue=regValue.subtract(BigInteger.ONE);
			status.writeRegister(reg,regValue);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Dec(matcher.group(1));
		}
	}
	private static class Jnz implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jnz (.) ([\\+\\-]?)(\\d+)$");
		private final String reg;
		private final int offset;
		private Jnz(String reg,int offset)	{
			this.reg=reg;
			this.offset=offset;
		}
		@Override
		public void run(ComputerStatus status)	{
			BigInteger regValue=status.readData(reg);
			if (regValue.signum()!=0) status.jump(offset);
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			String reg=matcher.group(1);
			boolean isNegative=matcher.group(2).equals("-");
			int offset=Integer.parseInt(matcher.group(3));
			if (isNegative) offset=-offset;
			return new Jnz(reg,offset);
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(Cpy::parse,Inc::parse,Dec::parse,Jnz::parse);
		public Instruction parse(String line)	{
			for (Function<String,Instruction> parser:parserFunctions)	{
				Instruction instr=parser.apply(line);
				if (instr!=null) return instr;
			}
			throw new IllegalArgumentException("Was machst du?"+line);
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
			registers.put("c",BigInteger.ONE);
			registers.put("d",BigInteger.ZERO);
			instrPointer=0;
		}
		@Override
		public String toString()	{
			return String.format("%s, instrPointer=%d.",registers,instrPointer);
		}
		public BigInteger readRegister(String reg)	{
			return registers.get(reg);
		}
		public BigInteger readData(String reg)	{
			try	{
				return new BigInteger(reg);
			}	catch (NumberFormatException exc)	{
				return readRegister(reg);
			}
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
