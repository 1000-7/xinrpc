package info.unclewang.service;

import info.unclewang.annotation.RpcProvider;
import info.unclewang.etcd.EtcdRegister;
import info.unclewang.server.NettyServer;
import info.unclewang.util.RpcUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Set;


/**
 * @author unclewang
 * @date 2019-07-22 20:13
 */
@Slf4j
@Service
public class RpcService {
	@Autowired
	private NettyServer nettyServer;
	@Autowired
	private ServerBootstrap serverBootstrap;
	@Autowired
	private EtcdRegister etcdRegister;

	private static Channel serverChannel = null;

	public void start(String ipAddress, int port) throws InterruptedException {
		serverChannel = nettyServer.bind(serverBootstrap, port);
		etcdRegister.initEtcd();
		Reflections reflections = new Reflections("info.unclewang");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcProvider.class);
		classes.forEach(clazz -> {
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces != null && interfaces.length > 0) {
				//只获取实现的第一个interface的name，实际上并不准确，可能有误。
				String clazzName = interfaces[0].getName();
				try {
					etcdRegister.register(clazzName, new InetSocketAddress(RpcUtils.parseAddress(ipAddress), port));
				} catch (UnknownHostException e) {
					log.error("UnknownHostException", e);
				}
			}

		});

	}

	@PreDestroy
	public void stop() {
		if (serverChannel != null) {
			serverChannel.close();
			serverChannel.parent().close();
		}
	}
}
