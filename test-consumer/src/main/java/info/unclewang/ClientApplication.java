package info.unclewang;

import info.unclewang.service.ConsumerServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author unclewang
 * @date 2019-07-22 21:18
 */
@SpringBootApplication
public class ClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientApplication.class);
		ConsumerServiceImpl.testXinRpc();
	}
}