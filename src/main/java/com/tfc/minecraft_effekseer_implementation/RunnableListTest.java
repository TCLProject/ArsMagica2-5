package com.tfc.minecraft_effekseer_implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RunnableListTest {
	static Runnable recursiveRunnable = ()->{};
	static ArrayList<Runnable> runnables = new ArrayList<>();
	
	static void addRunnable(Runnable r) {
		Runnable old = recursiveRunnable;
		recursiveRunnable = ()->{
			r.run();
			old.run();
		};
		runnables.add(r);
	}
	
	public static void main(String[] args) {
		for (int i = 0; i < 9000; i++) {
			addRunnable(()->{
				int[] ints = new int[10];
				for (int index = 0; index < ints.length; index++) ints[index] = new Random().nextInt(100);
				System.out.println(Arrays.toString(ints));
			});
		}
		long s0 = System.nanoTime();
		recursiveRunnable.run();
		long e0 = System.nanoTime();
		long s1 = System.nanoTime();
		for (Runnable runnable : runnables) runnable.run();
		long e1 = System.nanoTime();
		System.out.println(Math.abs(s0 - e0));
		System.out.println(Math.abs(s1 - e1));
	}
}
