package com.nbrevu.advent2015;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

public class Advent15_121 {
	private final static String IN_FILE="2015/Advent121.txt";
	
	private static long addNumbers(JsonElement element)	{
		if (element.isJsonPrimitive())	{
			JsonPrimitive value=element.getAsJsonPrimitive();
			return (value.isNumber())?value.getAsLong():0l;
		}	else if (element.isJsonArray())	{
			long result=0l;
			JsonArray array=element.getAsJsonArray();
			for (int i=0;i<array.size();++i) result+=addNumbers(array.get(i));
			return result;
		}	else if (element.isJsonObject())	{
			JsonObject object=element.getAsJsonObject();
			long result=0;
			for (Map.Entry<String,JsonElement> entry:object.entrySet()) result+=addNumbers(entry.getValue());
			return result;
		}	else throw new IllegalArgumentException("No puedo entender algo como esto.");
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		String jsonContent=Resources.toString(file,Charsets.UTF_8);
		JsonElement content=JsonParser.parseString(jsonContent);
		System.out.println(addNumbers(content));
	}
}
