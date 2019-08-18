package info.unclewang;

import org.junit.jupiter.api.Test;

public class ZookeeperTest {
	@Test
	public void method() {
		Config config = new Config();
		// 模拟一个配置项，实际生产中会在系统初始化时从配置文件中加载进来
		config.save("timeout", "1000");

		// 每3S打印一次获取到的配置项
		for (int i = 0; i < 10; i++) {
			System.out.println(config.get("timeout"));
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
