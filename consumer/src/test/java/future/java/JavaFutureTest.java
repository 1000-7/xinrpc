package future.java;

import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.concurrent.*;

public class JavaFutureTest {
	private FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
		@Override
		public String call() throws Exception {
			Thread.sleep(3000);
			return "get future result " + new Date();
		}
	});

	@Test
	public void method() {
		new Thread(futureTask).start();
		System.out.println("task begin execute");
		try {
			System.out.println(futureTask.get(2000, TimeUnit.MILLISECONDS));
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		}

	}
}
