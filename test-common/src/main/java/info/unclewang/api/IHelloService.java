package info.unclewang.api;


import info.unclewang.annotation.RpcProvider;

import java.util.Map;

@RpcProvider(name = "info.unclewang.api.IHelloService")
public interface IHelloService {
	String sayHi(String name);

	Map<String, String> map(String key, String value);
}
