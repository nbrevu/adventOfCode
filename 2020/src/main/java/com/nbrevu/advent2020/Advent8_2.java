package com.nbrevu.advent2020;

import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent8_2 {
	private final static String IN_FILE="Advent8.txt";
	
	private static interface Instruction	{
		public void run(ComputerStatus status);
		public Optional<Instruction> tryAlternate();
	}
	
	private static class Acc implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^acc ([\\+\\-]\\d+)$");
		private final BigInteger value;
		private Acc(BigInteger value)	{
			this.value=value;
		}
		@Override
		public void run(ComputerStatus status) {
			BigInteger accValue=status.readAccumulator();
			status.writeAccumulator(accValue.add(value));
		}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Acc(new BigInteger(matcher.group(1)));
		}
		@Override
		public Optional<Instruction> tryAlternate() {
			return Optional.empty();
		}
	}
	private static class Nop implements Instruction	{
		private final BigInteger value;
		private Nop(BigInteger value)	{
			this.value=value;
		}
		private final static Pattern PATTERN=Pattern.compile("^nop (.+)$");
		@Override
		public void run(ComputerStatus status) {}
		public static Instruction parse(String line)	{
			Matcher matcher=PATTERN.matcher(line);
			if (!matcher.matches()) return null;
			else return new Nop(new BigInteger(matcher.group(1)));
		}
		@Override
		public Optional<Instruction> tryAlternate() {
			return Optional.of(new Jmp(value.intValue()));
		}
	}
	private static class Jmp implements Instruction	{
		private final static Pattern PATTERN=Pattern.compile("^jmp ([\\+\\-]\\d+)$");
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
			int offset=Integer.parseInt(matcher.group(1));
			return new Jmp(offset);
		}
		@Override
		public Optional<Instruction> tryAlternate() {
			return Optional.of(new Nop(BigInteger.valueOf(offset)));
		}
	}
	
	private static enum InstructionParser	{
		INSTANCE;
		private final List<Function<String,Instruction>> parserFunctions=Arrays.asList(Acc::parse,Nop::parse,Jmp::parse);
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
		private BigInteger accumulator;
		private int instrPointer;
		public ComputerStatus(List<Instruction> instructions)	{
			this.instructions=instructions;
			accumulator=BigInteger.ZERO;
			instrPointer=0;
		}
		public BigInteger readAccumulator()	{
			return accumulator;
		}
		public void writeAccumulator(BigInteger value)	{
			accumulator=value;
		}
		public void jump(int offset)	{
			// YES, minus one. Because there is an automatic +1 after each instruction.
			instrPointer+=offset-1;
		}
		public BigInteger run()	{
			Set<Instruction> alreadyRun=new HashSet<>();
			for (;;)	{
				if (instrPointer>=instructions.size()) return accumulator;
				Instruction nextInstruction=instructions.get(instrPointer);
				if (alreadyRun.contains(nextInstruction)) return null;
				alreadyRun.add(nextInstruction);
				nextInstruction.run(this);
				++instrPointer;
			}
		}
	}
	
	private static BigInteger tryAll(List<Instruction> instructions)	{
		for (int i=0;i<instructions.size();++i)	{
			Instruction instr=instructions.get(i);
			Optional<Instruction> alternate=instr.tryAlternate();
			if (alternate.isPresent())	{
				instructions.set(i,alternate.get());
				BigInteger result=new ComputerStatus(instructions).run();
				if (result!=null) return result;
				instructions.set(i,instr);
			}
		}
		throw new IllegalStateException("Leider funktioniert es nicht.");
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Instruction> instructions=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8)) if (!line.isBlank()) instructions.add(InstructionParser.INSTANCE.parse(line));
		BigInteger result=tryAll(instructions);
		System.out.println(result);
	}
}
