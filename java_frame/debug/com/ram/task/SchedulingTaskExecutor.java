package com.ram.task; 

public interface SchedulingTaskExecutor extends AsyncTaskExecutor {
 
	boolean prefersShortLivedTasks();

}
