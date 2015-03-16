import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;


public class Test {

	CyclicBarrier barrier;
	
	static class Worker implements Runnable{

		private Test t;
		boolean isDone = false;
		
		public Worker(Test t) {
			this.t = t;
		}
		
		@Override
		public void run() {
			while(!done()){
				
				process();
				
				try {
					t.barrier.await();
				} catch (InterruptedException | BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("All Threads worked");
			}
		}

		private void process() {
			try {
				System.out.println(Thread.currentThread().getName());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		private boolean done() {
			return isDone;
		}
	}
	
	
	public static void main(String[] args) {
		Test t = new Test();
		final Worker w = new Test.Worker(t);
		t.barrier = new CyclicBarrier(100, new Runnable() {
			
			@Override
			public void run() {
				System.out.println("Cyclic Barier Worked");
				w.isDone = true;
			}
		});
		for (int i = 0 ; i < 100 ; i++) {
			new Thread(w).start();
		}
	}
}
