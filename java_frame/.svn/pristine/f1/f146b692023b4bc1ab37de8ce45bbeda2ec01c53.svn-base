package com.ram.task.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.ram.task.AsyncTaskExecutor;

public class ExceptionHandlingAsyncTaskExecutor implements AsyncTaskExecutor {
	private AsyncTaskExecutor executor;

	public ExceptionHandlingAsyncTaskExecutor(AsyncTaskExecutor executor) {
		this.executor = executor;
	}

	public void execute(Runnable task) {
		executor.execute(createWrappedRunnable(task));
	}

	public void execute(Runnable task, long startTimeout) {
		// /�ö������߳�����װ��@Async�䱾�ʾ������
		executor.execute(createWrappedRunnable(task), startTimeout);
	}

	public Future submit(Runnable task) {
		return executor.submit(createWrappedRunnable(task));
		// �ö������߳�����װ��@Async�䱾�ʾ�����ˡ�
	}

	public Future submit(final Callable task) {
		// �ö������߳�����װ��@Async�䱾�ʾ�����ˡ�
		return executor.submit(createCallable(task));
	}

	private Callable createCallable(final Callable task) {
		return new Callable() {
			public Object call() throws Exception {
				try {
					return task.call();
				} catch (Exception ex) {
					handle(ex);
					throw ex;
				}
			}
		};
	}

	private Runnable createWrappedRunnable(final Runnable task) {
		return new Runnable() {
			public void run() {
				try {
					task.run();
				} catch (Exception ex) {
					handle(ex);
				}
			}
		};
	}

	private void handle(Exception ex) {
		//统一处理
		System.err.println("Error during @Async execution: " + ex);
	}
}