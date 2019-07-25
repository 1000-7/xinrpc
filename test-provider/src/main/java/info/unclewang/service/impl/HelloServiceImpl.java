package info.unclewang.service.impl;

import info.unclewang.api.IHelloService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class HelloServiceImpl implements IHelloService {
	@Override
	public String sayHi(String name) {
		return "Hello " + name;
	}

	@Override
	public Map<String, String> map(String key, String value) {
		Map<String, String> map = new HashMap<>();
		map.put(key, key);
		map.put(value, value);

		return map;
	}
}
