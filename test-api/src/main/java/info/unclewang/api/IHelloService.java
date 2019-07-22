package info.unclewang.api;

import info.unclewang.annotation.RpcInterface;

/**
 * @author unclewang
 * @date 2019-07-22 21:13
 */
@RpcInterface
public interface IHelloService {

	String sayHi(String name);

}
