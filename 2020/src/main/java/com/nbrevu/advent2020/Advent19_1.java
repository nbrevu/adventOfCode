package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.koloboke.collect.map.CharObjMap;
import com.koloboke.collect.map.IntIntCursor;
import com.koloboke.collect.map.IntIntMap;
import com.koloboke.collect.map.IntObjCursor;
import com.koloboke.collect.map.IntObjMap;
import com.koloboke.collect.map.hash.HashCharObjMaps;
import com.koloboke.collect.map.hash.HashIntIntMaps;
import com.koloboke.collect.map.hash.HashIntObjMaps;

public class Advent19_1 {
	private final static String IN_FILE="Advent19.txt";
	
	private final static Pattern PATTERN_BASE=Pattern.compile("^(\\d+): \"([ab])\"$");
	private final static Pattern PATTERN_DIRECT=Pattern.compile("^(\\d+): (\\d+)$");
	private final static Pattern PATTERN_SIMPLE=Pattern.compile("^(\\d+): (\\d+) (\\d+)$");
	private final static Pattern PATTERN_OR=Pattern.compile("^(\\d+): (\\d+) \\| (\\d+)$");
	private final static Pattern PATTERN_COMBINED=Pattern.compile("^(\\d+): (\\d+) (\\d+) \\| (\\d+) (\\d+)$");
	
	private static interface Rule	{
		// Returns the position of the index after parsing the rule, or -1 if the string doesn't correspond to this rule.
		public int accept(String str,int pos);
	}
	
	private static enum ConstantRule implements Rule	{
		A('a'),B('b');
		public final char c;
		private ConstantRule(char c)	{
			this.c=c;
		}
		private final static CharObjMap<ConstantRule> ELEMENTS=getElementsMap();
		private static CharObjMap<ConstantRule> getElementsMap()	{
			CharObjMap<ConstantRule> result=HashCharObjMaps.newMutableMap();
			for (ConstantRule r:values()) result.put(r.c,r);
			return result;
		}
		public static ConstantRule getFor(char c)	{
			return ELEMENTS.get(c);
		}
		@Override
		public int accept(String str,int pos) {
			if (pos>=str.length()) return -1;
			else if (str.charAt(pos)==c) return 1+pos;
			else return -1;
		}
	}
	private static class DirectRule implements Rule	{
		private Rule reference;
		public DirectRule()	{
			reference=null;
		}
		public void setRule(Rule reference)	{
			this.reference=reference;
		}
		@Override
		public int accept(String str,int pos)	{
			return reference.accept(str,pos);
		}
	}
	private static class SingleRule implements Rule	{
		private Rule rule1;
		private Rule rule2;
		public SingleRule()	{
			rule1=null;
			rule2=null;
		}
		public void setRule1(Rule rule1) {
			this.rule1 = rule1;
		}
		public void setRule2(Rule rule2) {
			this.rule2 = rule2;
		}
		@Override
		public int accept(String str,int pos) {
			int pos2=rule1.accept(str,pos);
			if (pos2==-1) return -1;
			return rule2.accept(str,pos2);
		}
	}
	private static class OrRule implements Rule	{
		private Rule rule1;
		private Rule rule2;
		public OrRule()	{
			rule1=null;
			rule2=null;
		}
		public void setRule1(Rule rule1) {
			this.rule1 = rule1;
		}
		public void setRule2(Rule rule2) {
			this.rule2 = rule2;
		}
		@Override
		public int accept(String str,int pos) {
			int pos2=rule1.accept(str,pos);
			if (pos2!=-1) return pos2;
			return rule2.accept(str,pos);
		}
	}
	private static class DoubleRule implements Rule	{
		private final SingleRule rule1;
		private final SingleRule rule2;
		public DoubleRule()	{
			rule1=new SingleRule();
			rule2=new SingleRule();
		}
		public void setRule11(Rule rule)	{
			rule1.setRule1(rule);
		}
		public void setRule12(Rule rule)	{
			rule1.setRule2(rule);
		}
		public void setRule21(Rule rule)	{
			rule2.setRule1(rule);
		}
		public void setRule22(Rule rule)	{
			rule2.setRule2(rule);
		}
		@Override
		public int accept(String str,int pos) {
			int pos2=rule1.accept(str,pos);
			if (pos2!=-1) return pos2;
			return rule2.accept(str,pos);
		}
	}
	
	private static IntObjMap<Rule> parseAllRules(List<String> ruleStrings)	{
		IntObjMap<Rule> result=HashIntObjMaps.newMutableMap();
		IntObjMap<DirectRule> directRules=HashIntObjMaps.newMutableMap();
		IntObjMap<SingleRule> singleRules=HashIntObjMaps.newMutableMap();
		IntObjMap<OrRule> orRules=HashIntObjMaps.newMutableMap();
		IntObjMap<DoubleRule> doubleRules=HashIntObjMaps.newMutableMap();
		IntIntMap directRulesData=HashIntIntMaps.newMutableMap();
		IntObjMap<int[]> singleRulesData=HashIntObjMaps.newMutableMap();
		IntObjMap<int[]> orRulesData=HashIntObjMaps.newMutableMap();
		IntObjMap<int[]> doubleRulesData=HashIntObjMaps.newMutableMap();
		// First pass: generate the objects but not fill them (save the constant ones).
		for (String s:ruleStrings)	{
			Matcher constMatcher=PATTERN_BASE.matcher(s);
			if (constMatcher.matches())	{
				int ruleId=Integer.parseInt(constMatcher.group(1));
				char constant=constMatcher.group(2).charAt(0);
				result.put(ruleId,ConstantRule.getFor(constant));
				continue;
			}
			Matcher directMatcher=PATTERN_DIRECT.matcher(s);
			if (directMatcher.matches())	{
				int ruleId=Integer.parseInt(directMatcher.group(1));
				int reference=Integer.parseInt(directMatcher.group(2));
				DirectRule stub=new DirectRule();
				result.put(ruleId,stub);
				directRules.put(ruleId,stub);
				directRulesData.put(ruleId,reference);
				continue;
			}
			Matcher simpleMatcher=PATTERN_SIMPLE.matcher(s);
			if (simpleMatcher.matches())	{
				int ruleId=Integer.parseInt(simpleMatcher.group(1));
				int subRule1=Integer.parseInt(simpleMatcher.group(2));
				int subRule2=Integer.parseInt(simpleMatcher.group(3));
				SingleRule stub=new SingleRule();
				result.put(ruleId,stub);
				singleRules.put(ruleId,stub);
				singleRulesData.put(ruleId,new int[] {subRule1,subRule2});
				continue;
			}
			Matcher orMatcher=PATTERN_OR.matcher(s);
			if (orMatcher.matches())	{
				int ruleId=Integer.parseInt(orMatcher.group(1));
				int subRule1=Integer.parseInt(orMatcher.group(2));
				int subRule2=Integer.parseInt(orMatcher.group(3));
				OrRule stub=new OrRule();
				result.put(ruleId,stub);
				orRules.put(ruleId,stub);
				orRulesData.put(ruleId,new int[] {subRule1,subRule2});
				continue;
			}
			Matcher doubleMatcher=PATTERN_COMBINED.matcher(s);
			if (doubleMatcher.matches())	{
				int ruleId=Integer.parseInt(doubleMatcher.group(1));
				int subRule11=Integer.parseInt(doubleMatcher.group(2));
				int subRule12=Integer.parseInt(doubleMatcher.group(3));
				int subRule21=Integer.parseInt(doubleMatcher.group(4));
				int subRule22=Integer.parseInt(doubleMatcher.group(5));
				DoubleRule stub=new DoubleRule();
				result.put(ruleId,stub);
				doubleRules.put(ruleId,stub);
				doubleRulesData.put(ruleId,new int[] {subRule11,subRule12,subRule21,subRule22});
				continue;
			}
			throw new IllegalArgumentException("Unexpected string: "+s+".");
		}
		// Second pass: generate all the data.
		for (IntIntCursor cursor=directRulesData.cursor();cursor.moveNext();)	{
			DirectRule rule=directRules.get(cursor.key());
			int reference=cursor.value();
			rule.setRule(result.get(reference));
			continue;
		}
		for (IntObjCursor<int[]> cursor=singleRulesData.cursor();cursor.moveNext();)	{
			SingleRule rule=singleRules.get(cursor.key());
			int[] subRules=cursor.value();
			rule.setRule1(result.get(subRules[0]));
			rule.setRule2(result.get(subRules[1]));
		}
		for (IntObjCursor<int[]> cursor=orRulesData.cursor();cursor.moveNext();)	{
			OrRule rule=orRules.get(cursor.key());
			int[] subRules=cursor.value();
			rule.setRule1(result.get(subRules[0]));
			rule.setRule2(result.get(subRules[1]));
		}
		for (IntObjCursor<int[]> cursor=doubleRulesData.cursor();cursor.moveNext();)	{
			DoubleRule rule=doubleRules.get(cursor.key());
			int[] subRules=cursor.value();
			rule.setRule11(result.get(subRules[0]));
			rule.setRule12(result.get(subRules[1]));
			rule.setRule21(result.get(subRules[2]));
			rule.setRule22(result.get(subRules[3]));
		}
		// Now all the data should be in place.
		return result;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		int separator=content.indexOf("");
		if (separator==-1) throw new IllegalArgumentException("Wenn ist das Nunstück git und Slotermeyer? Ja! Beiherhund das Oder die Flipperwaldt gersput!");
		IntObjMap<Rule> rules=parseAllRules(content.subList(0,separator));
		Rule goal=rules.get(0);
		int result=0;
		for (String s:content.subList(separator+1,content.size())) if (goal.accept(s,0)==s.length()) ++result;
		System.out.println(result);
	}
}
