package info.unclewang.api;


import info.unclewang.annotation.RpcInterface;

import java.util.Map;

@RpcInterface
public interface IHelloService {
	String sayHi(String name);

	Map<String, String> map(String key, String value);
}
