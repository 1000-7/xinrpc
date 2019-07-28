package info.unclewang.etcd;

import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author unclewang
 * @date 2019-07-26 10:11
 */
public interface Register {
	/**
	 * @param serviceName
	 * @param address
	 * @throws Exception
	 * 服务注册
	 */
	void register(String serviceName, InetSocketAddress address);

	/**
	 * @param serviceName
	 * @return
	 * 服务发现
	 */
	List<InetSocketAddress> discover(String serviceName);

	/**
	 * 保活
	 */
	void keepAlive();
}
