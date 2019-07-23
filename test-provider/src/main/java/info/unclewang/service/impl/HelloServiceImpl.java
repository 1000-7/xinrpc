package info.unclewang.service.impl;

import info.unclewang.api.IHelloService;
import org.springframework.stereotype.Service;

@Service
public class HelloServiceImpl implements IHelloService {
	@Override
	public Object sayHi(String name) {
		return "Hello " + name;
	}
}
