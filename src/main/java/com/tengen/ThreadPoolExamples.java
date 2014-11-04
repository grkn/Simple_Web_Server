package com.tengen;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPool examples
 * Executor class
 * @author grkn
 *
 */
public class ThreadPoolExamples {
	
	public static void loopTest(Executor executor){
		for(int i = 0; i< 1000;i++){
			executor.execute(new SimpleThread("Task"+i));
		}
	}
	
	public static void waitingPeriod(){
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("********************\n\n");
	}
	
	public static void main(String[] args) {
		//Cached Thread Pool : better to use short lived async tasks
		//Thread pool expands infinitly. If all threads are busy then new thread is created.
		ThreadFactory tFactory = new CustomThreadFactory();
		Executor executor = Executors.newCachedThreadPool(tFactory);
		executor.execute(new SimpleThread("Task1"));
	
		//Lets test with loop;
		loopTest(executor);
		
		//After loop test you can see that almost 60 thread is enough to execute 1000 short lived task.
		//I guess it is to short lived but at least we have tested.
		
		waitingPeriod();
		
		//Fixed size thread handling is good option when you have limited memory.
		//Also long lived tasks are handled with efficient way.
		//Example : Application Servers handles HTTP request with a limited thread size.(Configurable)
		executor = Executors.newFixedThreadPool(2, tFactory);
		executor.execute(new SimpleThread("Task1"));
		loopTest(executor);
		
		//After loop test you can see that 2 thread handled all 1000 tasks
		
		waitingPeriod();
		//Single Thread handles the execution of task but if it fails then new thread is created and continued to execute task
		executor = Executors.newSingleThreadExecutor(tFactory);
		executor.execute(new SimpleThread("Task1"));
		loopTest(executor);
		// As you can see single thread executed task one by one (sequential)
		// Not configurable
		
		waitingPeriod();
		
		ScheduledExecutorService service = Executors.newScheduledThreadPool(10, tFactory);
		//Executes task after 2 second
		service.schedule(new SimpleThread("Task1"), 2L, TimeUnit.SECONDS);
		
		//Execute task 1 delay and for each 2 second.
		ScheduledFuture<?> sFuture = service.scheduleAtFixedRate(new SimpleThread("Task2"),1 , 2, TimeUnit.SECONDS);
	
		FutureTask<?> fTask = new FutureTask<Void>(new SimpleThread("Task1"),null);
		
		
			
		justForTry();
		
		
	}
	
	public static void justForTry(){
		retry :
			for (int i = 0; i < 10; i++) {
				if(i == 9){
					continue retry;
				}
			}
	}

}

class SimpleThread implements Runnable{
	
	private String taskName;
	
	public SimpleThread(String taskName) {
		this.taskName = taskName;
	}
	
	@Override
	public void run() {
		StringBuffer buf = new StringBuffer(Thread.currentThread().getName())
		.append("  --  Executing Task1 : ").append(taskName);
		System.out.println(buf.toString());
	}
	
}

class CustomThreadFactory implements ThreadFactory{

	@Override
	public Thread newThread(Runnable r) {
		Thread th = new Thread(r);
		System.out.println("New Thread Created. Name : "+th.getName());
		return th;
	}
	
}