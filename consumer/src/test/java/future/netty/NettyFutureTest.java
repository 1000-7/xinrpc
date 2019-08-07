package future.netty;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class NettyFutureTest {
	@Test
	public void method() {
		NioEventLoopGroup loopGroup = new NioEventLoopGroup();
		DefaultPromise<String> promise = new DefaultPromise<>(loopGroup.next());
		loopGroup.schedule(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					promise.setSuccess("99,99");
				} catch (InterruptedException e) {
					promise.setFailure(e);
					e.printStackTrace();
				}

			}
		}, 0, TimeUnit.SECONDS);

		promise.addListener(new GenericFutureListener<Future<? super String>>() {
			@Override
			public void operationComplete(Future<? super String> future) throws Exception {
				System.out.println("listener 1, price is " + future.getNow());
			}
		});

		promise.addListener(new GenericFutureListener<Future<? super String>>() {
			@Override
			public void operationComplete(Future<? super String> future) throws Exception {
				System.out.println("listener 2, price is " + future.get());
			}
		});

	}
}
