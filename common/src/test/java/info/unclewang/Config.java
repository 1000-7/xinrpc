package info.unclewang;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
	private Map<String, String> cache = new HashMap<>();
	private CuratorFramework client;
	private static final String CONFIG_PREFIX = "/CONFIG";

	// 初始化zk连接
	public Config() {
		this.client = CuratorFrameworkFactory.newClient("localhost:2181", new RetryNTimes(3, 1000));
		this.client.start();
		this.init();
	}

	public void init() {
		try {
			// 从zk中获取配置项并保存到缓存中
			List<String> childrenNames = client.getChildren().forPath(CONFIG_PREFIX);
			for (String name : childrenNames) {
				String value = new String(client.getData().forPath(CONFIG_PREFIX + "/" + name));
				cache.put(name, value);
			}

			// 绑定一个监听器 cacheData设为true，事件发生后可以拿到节点发送的内容。
			// 使用该配置文件的每个应用机器都需要监听，这里只是用于演示
			PathChildrenCache watcher = new PathChildrenCache(client, CONFIG_PREFIX, true);
			watcher.getListenable().addListener(new PathChildrenCacheListener() {
				@Override
				public void childEvent(CuratorFramework curatorFramework, PathChildrenCacheEvent event) throws Exception {

					String path = event.getData().getPath();
					if (path.startsWith(CONFIG_PREFIX)) {
						String key = path.replace(CONFIG_PREFIX + "/", "");
						// 子节点新增或变更时 更新缓存信息
						if (PathChildrenCacheEvent.Type.CHILD_ADDED.equals(event.getType()) ||
								PathChildrenCacheEvent.Type.CHILD_UPDATED.equals(event.getType())) {
							cache.put(key, new String(event.getData().getData()));
						}
						// 子节点被删除时 从缓存中删除
						if (PathChildrenCacheEvent.Type.CHILD_REMOVED.equals(event.getType())) {
							cache.remove(key);
						}
					}
				}
			});
			watcher.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// 保存配置信息
	public void save(String name, String value) {
		String configFullName = CONFIG_PREFIX + "/" + name;
		try {
			Stat stat = client.checkExists().forPath(configFullName);
			if (stat == null) {
				client.create().creatingParentsIfNeeded().withMode(CreateMode.PERSISTENT).forPath(configFullName, value.getBytes());
			} else {
				client.setData().forPath(configFullName, value.getBytes());
			}
			cache.put(name, value);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 获取配置信息
	public String get(String name) {
		return cache.get(name);
	}
}

