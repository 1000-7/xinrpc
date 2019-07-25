package info.unclewang.handle;

import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author unclewang
 * @date 2019-07-22 19:54
 */
@Slf4j
@Component
@ChannelHandler.Sharable
public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> implements ApplicationContextAware {
	private ApplicationContext applicationContext;

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcRequest rpcRequest) throws Exception {
		log.info(new Date() + ": 收到客户端访问请求……");
		RpcResponse rpcResponse = new RpcResponse();
		rpcResponse.setRequestId(rpcRequest.getId());

		try {
			rpcResponse.setResult(handler(rpcRequest));
			rpcResponse.setSuccess(true);
		} catch (Throwable throwable) {
			rpcResponse.setThrowable(throwable);
			rpcResponse.setSuccess(false);
		}
		channelHandlerContext.writeAndFlush(rpcResponse);

	}

	private Object handler(RpcRequest request) throws Throwable {
		//获取request的内容
		String methodName = request.getMethodName();
		Class<?>[] parameterTypes = request.getParameterTypes();
		Object[] parameters = request.getParameters();

		//cglib处理request
		Class<?> clz = Class.forName(request.getClassName());
		Object serviceBean = applicationContext.getBean(clz);
		Class<?> serviceClass = serviceBean.getClass();
		FastClass fastClass = FastClass.create(serviceClass);

		FastMethod fastMethod = fastClass.getMethod(methodName, parameterTypes);
		return fastMethod.invoke(serviceBean, parameters);
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
