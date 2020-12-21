package com.nbrevu.advent2020;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.common.base.Charsets;
import com.google.common.collect.Sets;
import com.google.common.io.Resources;

public class Advent21_1 {
	private final static String IN_FILE="Advent21.txt";
	
	private final static Pattern BASE_PATTERN=Pattern.compile("(.+) \\(contains (.+)\\)");
	private final static Pattern SPACE_SEPARATOR=Pattern.compile(" ");
	private final static Pattern COMMA_SEPARATOR=Pattern.compile(", ");
	
	private static class Food	{
		public final Set<String> ingredients;
		public final Set<String> allergens;
		public Food(Set<String> ingredients,Set<String> allergens)	{
			this.ingredients=ingredients;
			this.allergens=allergens;
		}
	}
	
	public static Food parseLine(String line)	{
		Matcher matcher=BASE_PATTERN.matcher(line);
		if (!matcher.matches()) throw new IllegalArgumentException("This is inedible!: "+line+".");
		Set<String> ingredients=SPACE_SEPARATOR.splitAsStream(matcher.group(1)).collect(Collectors.toUnmodifiableSet());
		Set<String> allergens=COMMA_SEPARATOR.splitAsStream(matcher.group(2)).collect(Collectors.toUnmodifiableSet());
		return new Food(ingredients,allergens);
	}
	
	private static Map<String,Set<String>> findAllergens(List<Food> foods)	{
		Map<String,Set<String>> result=new HashMap<>();
		for (Food f:foods) for (String a:f.allergens) if (result.containsKey(a)) result.put(a,new HashSet<>(Sets.intersection(result.get(a),f.ingredients)));
		else result.put(a,f.ingredients);
		return result;
	}
	
	private static int countInstances(List<Food> foods,Set<String> ingredients)	{
		int result=0;
		for (Food f:foods) result+=Sets.intersection(f.ingredients,ingredients).size();
		return result;
	}
	
	private static Set<String> getSafeIngredients(List<Food> foods,Map<String,Set<String>> allergens)	{
		Set<String> ingredientsWithAllergen=new HashSet<>();
		allergens.values().forEach(ingredientsWithAllergen::addAll); 
		Set<String> allIngredients=new HashSet<>();
		for (Food f:foods) allIngredients.addAll(f.ingredients);
		allIngredients.removeAll(ingredientsWithAllergen);
		return allIngredients;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		List<String> content=Resources.readLines(file,Charsets.UTF_8);
		List<Food> allFood=content.stream().map(Advent21_1::parseLine).collect(Collectors.toUnmodifiableList());
		Map<String,Set<String>> allergens=findAllergens(allFood);
		Set<String> safeIngredients=getSafeIngredients(allFood,allergens);
		System.out.println(countInstances(allFood,safeIngredients));
	}
}
