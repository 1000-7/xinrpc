package info.unclewang.service;

import info.unclewang.api.IHelloService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class TestServiceImpl implements IHelloService {
	@Override
	public String sayHi(String name) {
		log.info(name);
		return "Hello " + name;
	}
}
