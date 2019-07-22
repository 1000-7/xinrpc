package info.unclewang;

import info.unclewang.service.RpcService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
/**
 * @author unclewang
 * @date 2019-07-22 21:16
 */
@SpringBootApplication
public class Application {
	public static void main(String[] args) throws InterruptedException {
		ConfigurableApplicationContext context = SpringApplication.run(Application.class);
		RpcService service = context.getBean(RpcService.class);
		service.start();
	}
}