package info.unclewang.api;


import info.unclewang.annotation.RpcInterface;

@RpcInterface
public interface IHelloService {
	Object sayHi(String name);
}
