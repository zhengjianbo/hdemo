package com.kettle.debug;

import java.util.ArrayList;

public class UnitAction {
	public static void x() {
		long start = System.currentTimeMillis();

		ArrayList list = new ArrayList();
		for (int i = 0; i < 100000000; i++) {
			list.add("1");

		}

//		for (int i = 0; i < 100000000; i++) { 
//			String z=""+list.get(i);
//		}
			 for(Object o :list)
			   {
				 String x=o+"";
			   }
		long end = System.currentTimeMillis();

		System.out.println("start:" + (end - start));

	}

	public static void main(String args[]) {
		x();
	}
}
