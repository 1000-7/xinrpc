package info.unclewang.registry.impl;

import info.unclewang.registry.Register;
import info.unclewang.util.RpcUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.GetChildrenBuilder;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.Stat;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author unclewang
 * @date 2019-08-12 15:34
 */
@Slf4j
@Service
public class ZookeeperRegister implements Register {
	private static final String SERVICE_BASE_PATH = "xin_rpc";
	private static final String SERVICE_HOST_STATUS = "health";
	private static final String SPLIT_CHAR = "/";
	private static final String ZOOKEEPER_ADDRESS = "127.0.0.1:2181";
	private CuratorFramework client;

	private static final int TTL = 60;


	@Override
	public void init() {
		initZookeeper(ZOOKEEPER_ADDRESS);
	}

	public void initZookeeper(String address) {
		/**
		 * 同步创建zk示例，原生api是异步的
		 * 这一步是设置重连策略
		 *
		 * ExponentialBackoffRetry构造器参数：
		 *  curator链接zookeeper的策略:ExponentialBackoffRetry
		 *  baseSleepTimeMs：初始sleep的时间
		 *  maxRetries：最大重试次数
		 *  maxSleepMs：最大重试时间
		 */
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 5);
		client = CuratorFrameworkFactory.builder()
				.connectString(address)
				.sessionTimeoutMs(15000)
				.connectionTimeoutMs(6000)
				.retryPolicy(retryPolicy)
				.build();
		client.start();
		keepAlive();
	}

	@Override
	public void register(String serviceName, InetSocketAddress address) {
		String key = MessageFormat.format("/{0}/{1}/{2}:{3}", SERVICE_BASE_PATH, serviceName, address.getHostName(), address.getPort());
		String value = MessageFormat.format("{0}/{1}/{2}/{3}", address.getHostName(), address.getAddress().getHostAddress(), address.getPort(), SERVICE_HOST_STATUS);
		try {
			log.info("ZookeeperRegister begin register, key{},value{}", key, value);
			String s = "";
			if (!checkExists(key)) {

				s = this.client.create().creatingParentsIfNeeded()
						.withMode(CreateMode.EPHEMERAL)
						.withACL(ZooDefs.Ids.OPEN_ACL_UNSAFE)
						.forPath(key, value.getBytes());
			}

			log.info("register success. address:{}, result;{}", address.toString(), s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<InetSocketAddress> discover(String serviceName) {
		String key = MessageFormat.format("/{0}/{1}", SERVICE_BASE_PATH, serviceName);
		List<InetSocketAddress> res = new ArrayList<>();
		try {
			GetChildrenBuilder children = this.client.getChildren();
			List<String> kvs = children.forPath(key);
			kvs.forEach(k -> {
				try {
					byte[] bytes = this.client.getData().forPath(key + "/" + k);
					String value = new String(bytes);
					String[] split = value.split(SPLIT_CHAR);
					try {
						if (StringUtils.isNotBlank(split[3]) && split[3].equals(SERVICE_HOST_STATUS)) {
							res.add(new InetSocketAddress(RpcUtils.parseAddress(split[1]), Integer.parseInt(split[2].replace(",", ""))));
							log.info("find service success, Name :{}", value);
						} else {
							log.error("find service failed, because this service hangs up. Name :{}", value);
						}
					} catch (UnknownHostException e) {
						log.error("UnknownHostException build socketAddress error!", e);
					}
				} catch (Exception e) {
					log.error("discover one address fail. address:{}", k);
				}
			});

		} catch (Exception e) {
			log.error("discover fail. address:{}", key);
			e.printStackTrace();
		}
		return res;
	}

	@Override
	public void remove(String serviceName, InetSocketAddress address) {
		String key = MessageFormat.format("/{0}/{1}/{2}/{3}", SERVICE_BASE_PATH, serviceName, address.getHostName(), address.getPort());
		try {
			this.client.delete().guaranteed().forPath(key);
		} catch (Exception e) {
			log.error("remove fail. address:{}", key);
			e.printStackTrace();
		}
	}

	public boolean checkExists(String nodePath) {
		try {
			Stat stat = client.checkExists().forPath(nodePath);
			return stat != null;
		} catch (Exception e) {
			log.error(MessageFormat.format("检查Zookeeper节点是否存在出现异常,nodePath:{0}", nodePath), e);
		}
		return false;
	}

	@Override
	public void keepAlive() {

	}
}
