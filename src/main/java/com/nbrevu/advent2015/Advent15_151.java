package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.mutable.MutableLong;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

public class Advent15_151 {
	private final static String IN_FILE="2015/Advent151.txt";
	private final static int MAX_TEASPOONS=100;
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+): capacity (\\-?\\d+), durability (\\-?\\d+), flavor (\\-?\\d+), texture (\\-?\\d+), calories (\\-?\\d+)$");
	
	private final static class Ingredient	{
		public final int capacity;
		public final int durability;
		public final int flavor;
		public final int texture;
		public Ingredient(int capacity,int durability,int flavor,int texture)	{
			this.capacity=capacity;
			this.durability=durability;
			this.flavor=flavor;
			this.texture=texture;
		}
	}
	
	private final static class Recipe	{
		public int capacity;
		public int durability;
		public int flavor;
		public int texture;
		public Recipe()	{
			capacity=0;
			durability=0;
			flavor=0;
			texture=0;
		}
		public void addIngredient(Ingredient ing,int quantity)	{
			capacity+=quantity*ing.capacity;
			durability+=quantity*ing.durability;
			flavor+=quantity*ing.flavor;
			texture+=quantity*ing.texture;
		}
		public long getScore()	{
			long result=1l;
			if (capacity<=0) return 0;
			else result*=capacity;
			if (durability<=0) return 0;
			else result*=durability;
			if (flavor<=0) return 0;
			else result*=flavor;
			if (texture<=0) return 0;
			else result*=texture;
			return result;
		}
	}
	
	private static long getMaxScore(List<Ingredient> ingredients,int maxTeaspoons)	{
		MutableLong result=new MutableLong(-1l);
		getMaxScoreRecursive(ingredients,0,maxTeaspoons,new Recipe(),result);
		return result.longValue();
	}
	
	private static void updateMaximum(MutableLong result,Recipe recipe)	{
		long recipeScore=recipe.getScore();
		if (recipeScore>result.longValue()) result.setValue(recipeScore);
	}
	
	private static void getMaxScoreRecursive(List<Ingredient> ingredients,int currentIngredient,int remainingTeaspoons,Recipe recipe,MutableLong maxScore)	{
		if (currentIngredient==ingredients.size()-1)	{
			recipe.addIngredient(ingredients.get(currentIngredient),remainingTeaspoons);
			updateMaximum(maxScore,recipe);
			recipe.addIngredient(ingredients.get(currentIngredient),-remainingTeaspoons);
		}	else for (int i=0;i<remainingTeaspoons;++i)	{
			recipe.addIngredient(ingredients.get(currentIngredient),i);
			int reRemaining=remainingTeaspoons-i;
			if (reRemaining<=0) updateMaximum(maxScore,recipe);
			else getMaxScoreRecursive(ingredients,1+currentIngredient,reRemaining,recipe,maxScore);
			recipe.addIngredient(ingredients.get(currentIngredient),-i);
		}
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<Ingredient> ingredients=new ArrayList<>();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				int capacity=Integer.parseInt(matcher.group(2));
				int durability=Integer.parseInt(matcher.group(3));
				int flavor=Integer.parseInt(matcher.group(4));
				int texture=Integer.parseInt(matcher.group(5));
				ingredients.add(new Ingredient(capacity,durability,flavor,texture));
			}
		}
		long result=getMaxScore(ingredients,MAX_TEASPOONS);
		System.out.println(result);
	}
}
