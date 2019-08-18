package info.unclewang.registry;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author unclewang
 * @date 2019-07-26 10:11
 */
public interface Register {

	void init();
	/**
	 * @param serviceName
	 * @param address
	 * @throws Exception 将服务注册到注册中心中
	 */
	void register(String serviceName, InetSocketAddress address);

	/**
	 * @param serviceName
	 * @return 从注册中心进行服务发现
	 */
	List<InetSocketAddress> discover(String serviceName);

	/**
	 * @param serviceName
	 * @param address     从注册中心中剔除某个服务的地址
	 */
	void remove(String serviceName, InetSocketAddress address);

	/**
	 * 保活
	 */
	void keepAlive();
}
