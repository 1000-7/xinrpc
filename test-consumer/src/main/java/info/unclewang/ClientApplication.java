package info.unclewang;

import info.unclewang.api.IHelloService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author unclewang
 * @date 2019-07-22 21:18
 */
@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		ConfigurableApplicationContext context = SpringApplication.run(ClientApplication.class);
		IHelloService helloService = context.getBean(IHelloService.class);
		System.out.println(helloService.sayHi("world"));
	}

}
