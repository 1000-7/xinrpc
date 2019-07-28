package info.unclewang.call;

import info.unclewang.annotation.RpcConsumer;
/**
 * @author unclewang
 * @date 2019-07-28 16:09
 */
@RpcConsumer(call = "info.unclewang.api.IHelloService")
public interface ConsumerService {
}
