package hello;
 
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import com.ram.task.AsyncTaskExecutor;
import com.ram.task.impl.ExceptionHandlingAsyncTaskExecutor;
import com.ram.task.impl.ThreadPoolTaskExecutor;

public class HelloWorld {

	static AsyncTaskExecutor executor = null; 

	public static void main(String[] args) throws InterruptedException,
			ExecutionException {
		// ApplicationContext ctx = new ClassPathXmlApplicationContext(
		// "spring-context.xml");
		// Greeter performer = (Greeter) ctx.getBean("hank");
		// TestAsyncBean testAsyncBean = (TestAsyncBean)
		// ctx.getBean("testAsyncBean");
		try {
			// testAsyncBean.sayHello3();
			// performer.testAsyncMethod();
			// while (true) { // /����ʹ����ѭ���жϣ��ȴ���ȡ�����Ϣ
			// System.out.println("Continue doing something else. ");
			// Thread.sleep(1000);
			//
			// }

			ThreadPoolTaskExecutor xexecutor = new ThreadPoolTaskExecutor();
			xexecutor.setThreadFactory(null);
			// �������100
			xexecutor.setQueueCapacity(6);
			xexecutor.setMaxPoolSize(5);
			
//			 xexecutor.setAllowCoreThreadTimeOut(true);
//			 xexecutor.setKeepAliveSeconds(1);
			 
			xexecutor
					.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
			xexecutor.setCorePoolSize(3);
			xexecutor.initialize();
			
			ExceptionHandlingAsyncTaskExecutor taskExecutor=new ExceptionHandlingAsyncTaskExecutor(xexecutor);
			
 			for (int i = 0; i < 20; i++) {
				// xexecutor.execute(new Runnable() {
				// public void run() {
				// try {
				// Thread.sleep(5000);
				// } catch (InterruptedException e) {
				// // TODO Auto-generated catch block
				// e.printStackTrace();
				// }
				// System.out.println("Runnable. ");
				// }
				// });
			 try {
				Future<Boolean> future = taskExecutor
						.submit(new Callable<Boolean>() {
							public Boolean call() throws Exception {
								Thread.sleep(1000 * 6);
								System.out.println("");
								return true;
							}
						});
			 } catch (Exception e) {
				 System.out.println("ERROR Exception:"+e.getMessage());
			}
 			}

			while (true) { // /����ʹ����ѭ���жϣ��ȴ���ȡ�����Ϣ
				System.out.println("Continue doing something else. "
						+ xexecutor.getActiveCount());
				Thread.sleep(1000);

				System.out.println(" getCompletedTaskCount:"
						+ xexecutor.getThreadPoolExecutor()
								.getCompletedTaskCount());
				// xexecutor.getThreadPoolExecutor().
				//检验是否超时
				
//				 try {
//				 Boolean isDo=future.get(1, TimeUnit.MILLISECONDS);
//				 System.out.println("����ִ�����isDo:"+isDo);
//				 future.cancel(true);
//				 } catch (InterruptedException e) {
//				 e.printStackTrace(); // getΪһ���ȴ����̣��쳣��ֹget���׳��쳣
//				 } catch (ExecutionException e) {
//				 e.printStackTrace(); // submit��������쳣
//				 } catch (TimeoutException e) {
//				 e.printStackTrace(); // ��ʱ�쳣
//				 try {
//				 future.cancel(true); // ��ʱ��ȡ������
//				 } catch (Exception ex) {
//				 ex.printStackTrace();
//				 }finally {
//				 }
//				 } catch (Exception e) {
//				 e.printStackTrace();
//				 }finally {
//				 }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
