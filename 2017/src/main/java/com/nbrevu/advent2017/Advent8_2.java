package com.nbrevu.advent2017;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.koloboke.collect.map.ObjIntMap;
import com.koloboke.collect.map.hash.HashObjIntMaps;

public class Advent8_2 {
	private final static String IN_FILE="Advent8.txt";
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^([^\\s]+) (inc|dec) ([\\+\\-]?\\d+) if ([^\\s]+) ([^\\s]+) ([\\+\\-]?\\d+)$");
	
	private static enum Instruction	{
		INC	{
			@Override
			public int run(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				int newValue=value+operand;
				registers.put(target,newValue);
				return newValue;
			}
		},
		DEC	{
			@Override
			public int run(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				int newValue=value-operand;
				registers.put(target,newValue);
				return newValue;
			}
		};
		public abstract int run(ObjIntMap<String> registers,String target,int operand);
		private final static Map<String,Instruction> INSTRUCTION_MAP=ImmutableMap.of("inc",INC,"dec",DEC);
		public static Instruction parse(String str)	{
			Instruction result=INSTRUCTION_MAP.get(str);
			if (result==null) throw new IllegalArgumentException("That's more than unknown. That's Ulik Norman Owen.");
			return result;
		}
	}
	private static enum Condition	{
		GT	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value>operand;
			}
		},
		LT	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value<operand;
			}
		},
		GE	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value>=operand;
			}
		},
		LE	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value<=operand;
			}
		},
		EQ	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value==operand;
			}
		},
		NEQ	{
			@Override
			public boolean evaluate(ObjIntMap<String> registers,String target,int operand) {
				int value=registers.getOrDefault(target,0);
				return value!=operand;
			}
		};
		public abstract boolean evaluate(ObjIntMap<String> registers,String target,int operand);
		private final static Map<String,Condition> CONDITION_MAP=createConditionsMap();
		private static Map<String,Condition> createConditionsMap()	{
			Map<String,Condition> result=new HashMap<>();
			result.put(">",Condition.GT);
			result.put("<",Condition.LT);
			result.put(">=",Condition.GE);
			result.put("<=",Condition.LE);
			result.put("==",Condition.EQ);
			result.put("!=",Condition.NEQ);
			return result;
		}
		public static Condition parse(String str)	{
			Condition result=CONDITION_MAP.get(str);
			if (result==null) throw new IllegalArgumentException("That's more than unknown. That's Ulik Norman Owen.");
			return result;
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		ObjIntMap<String> registers=HashObjIntMaps.newMutableMap();
		int maxValue=0;
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				String targetReg=matcher.group(1);
				Instruction instruction=Instruction.parse(matcher.group(2));
				int value=Integer.parseInt(matcher.group(3));
				String conditionReg=matcher.group(4);
				Condition condition=Condition.parse(matcher.group(5));
				int conditionValue=Integer.parseInt(matcher.group(6));
				if (condition.evaluate(registers,conditionReg,conditionValue))	{
					int instrResult=instruction.run(registers,targetReg,value);
					maxValue=Math.max(maxValue,instrResult);
				}
			}
		}
		System.out.println(maxValue);
	}
}
