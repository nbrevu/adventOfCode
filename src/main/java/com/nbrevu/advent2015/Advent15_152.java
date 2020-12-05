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

public class Advent15_152 {
	private final static String IN_FILE="2015/Advent151.txt";
	private final static int MAX_TEASPOONS=100;
	private final static int CALORIES_GOAL=500;
	private final static Pattern LINE_PATTERN=Pattern.compile("^(.+): capacity (\\-?\\d+), durability (\\-?\\d+), flavor (\\-?\\d+), texture (\\-?\\d+), calories (\\-?\\d+)$");
	
	private final static class Ingredient	{
		public final int capacity;
		public final int durability;
		public final int flavor;
		public final int texture;
		public final int calories;
		public Ingredient(int capacity,int durability,int flavor,int texture,int calories)	{
			this.capacity=capacity;
			this.durability=durability;
			this.flavor=flavor;
			this.texture=texture;
			this.calories=calories;
		}
	}
	
	private final static class Recipe	{
		public int capacity;
		public int durability;
		public int flavor;
		public int texture;
		public int calories;
		public Recipe()	{
			capacity=0;
			durability=0;
			flavor=0;
			texture=0;
			calories=0;
		}
		public void addIngredient(Ingredient ing,int quantity)	{
			capacity+=quantity*ing.capacity;
			durability+=quantity*ing.durability;
			flavor+=quantity*ing.flavor;
			texture+=quantity*ing.texture;
			calories+=quantity*ing.calories;
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
		if (currentIngredient==ingredients.size()-2)	{
			/*
			 * Let N1,C1,N2,C2 be the amount of the first ingredient, the calories per teaspoon in the first ingredient, and then the same
			 * but for the second ingredient.
			 * We need to solve:
			 * 
			 * N1+N2 = remainingTeaspoons
			 * C1*N1+C2*N2 = remainingCalories
			 * 
			 * This is easy to solve using Cramer's method. If we call T=remainingTeaspoons and K=remainingCalories, then:
			 * N1=(T*C2-K)/(C2-C1).
			 * N2=(K-T*C1)/(C2-C1).
			 * 
			 * Since C2-C1 is not 0, we don't need to take care of the degenerate case.
			 */
			int remainingCalories=CALORIES_GOAL-recipe.calories;
			int c1=ingredients.get(currentIngredient).calories;
			int c2=ingredients.get(currentIngredient+1).calories;
			int num1=remainingTeaspoons*c2-remainingCalories;
			int num2=remainingCalories-remainingTeaspoons*c1;
			int den=c2-c1;
			if (((num1%den)!=0)||((num2%den)!=0)) return;
			int q1=num1/den;
			int q2=num2/den;
			if ((q1>=0)&&(q2>=0))	{
				recipe.addIngredient(ingredients.get(currentIngredient),q1);
				recipe.addIngredient(ingredients.get(currentIngredient+1),q2);
				updateMaximum(maxScore,recipe);
				recipe.addIngredient(ingredients.get(currentIngredient),-q1);
				recipe.addIngredient(ingredients.get(currentIngredient+1),-q2);
			}
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
				int calories=Integer.parseInt(matcher.group(6));
				ingredients.add(new Ingredient(capacity,durability,flavor,texture,calories));
			}
		}
		long result=getMaxScore(ingredients,MAX_TEASPOONS);
		System.out.println(result);
	}
}
