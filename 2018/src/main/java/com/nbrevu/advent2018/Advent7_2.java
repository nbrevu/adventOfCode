package com.nbrevu.advent2018;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Charsets;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import com.google.common.io.Resources;

public class Advent7_2 {
	private final static String IN_FILE="Advent7.txt";
	private final static int WORKERS=5;
	
	private final static Pattern LINE_PATTERN=Pattern.compile("^Step ([A-Z]) must be finished before step ([A-Z]) can begin.$");
	
	private static class Graph	{
		private SetMultimap<Character,Character> directGraph;
		private SetMultimap<Character,Character> reverseGraph;
		public Graph()	{
			directGraph=HashMultimap.create();
			reverseGraph=HashMultimap.create();
		}
		public void addEdge(Character prerequisite,Character nextProcess)	{
			directGraph.put(prerequisite,nextProcess);
			reverseGraph.put(nextProcess,prerequisite);
		}
		public NavigableSet<Character> getInitialTasks()	{
			NavigableSet<Character> result=new TreeSet<>();
			for (Character c:directGraph.keySet()) if (!reverseGraph.containsKey(c)) result.add(c);
			return result;
		}
		public Set<Character> getPotentiallyUnlocked(Character c)	{
			return directGraph.get(c);
		}
		public Set<Character> getPrerequisites(Character c)	{
			return reverseGraph.get(c);
		}
	}
	
	private static class PendingTask implements Comparable<PendingTask>	{
		public final char task;
		public final int finishTime;
		public final int worker;
		public PendingTask(char task,int finishTime,int worker)	{
			this.task=task;
			this.finishTime=finishTime;
			this.worker=worker;
		}
		@Override
		public int compareTo(PendingTask o) {
			int timeDiff=finishTime-o.finishTime;
			if (timeDiff!=0) return timeDiff;
			else return task-o.task;
		}
	}
	
	private static int getCost(char task)	{
		return (task-'A')+61;	// Could have just been task-5 but this is clearer.
	}
	
	private static int simulate(Graph g,int workers)	{
		PendingTask[] workerTasks=new PendingTask[workers];
		NavigableSet<Character> availableCharacters=g.getInitialTasks();
		NavigableSet<PendingTask> taskQueue=new TreeSet<>();
		int currentTime=0;
		for (int i=0;(i<workers)&&!availableCharacters.isEmpty();++i)	{
			Character taskName=availableCharacters.pollFirst();
			int time=getCost(taskName);
			PendingTask task=new PendingTask(taskName,time,i);
			workerTasks[i]=task;
			taskQueue.add(task);
		}
		Set<Character> finished=new HashSet<>();
		while (!taskQueue.isEmpty())	{
			PendingTask finishedTask=taskQueue.pollFirst();
			currentTime=finishedTask.finishTime;
			finished.add(finishedTask.task);
			for (Character c:g.getPotentiallyUnlocked(finishedTask.task)) if (finished.containsAll(g.getPrerequisites(c))) availableCharacters.add(c);
			workerTasks[finishedTask.worker]=null;
			for (int i=0;(i<workers)&&!availableCharacters.isEmpty();++i) if (workerTasks[i]==null)	{
				Character taskName=availableCharacters.pollFirst();
				int time=currentTime+getCost(taskName);
				PendingTask task=new PendingTask(taskName,time,i);
				workerTasks[i]=task;
				taskQueue.add(task);
			}
		}
		return currentTime;
	}
	
	public static void main(String[] args) throws IOException	{
		URL file=Resources.getResource(IN_FILE);
		Graph g=new Graph();
		for (String line:Resources.readLines(file,Charsets.UTF_8))	{
			Matcher matcher=LINE_PATTERN.matcher(line);
			if (matcher.matches())	{
				char prerequisite=matcher.group(1).charAt(0);
				char nextProcess=matcher.group(2).charAt(0);
				g.addEdge(prerequisite,nextProcess);
			}
		}
		System.out.println(simulate(g,WORKERS));
	}
}
