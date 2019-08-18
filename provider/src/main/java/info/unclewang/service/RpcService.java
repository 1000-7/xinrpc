package info.unclewang.service;

import info.unclewang.annotation.RpcProvider;
import info.unclewang.registry.Register;
import info.unclewang.registry.impl.EtcdRegister;
import info.unclewang.registry.impl.ZookeeperRegister;
import info.unclewang.server.NettyServer;
import info.unclewang.util.NettyProperties;
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
import java.util.Date;
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
	@Autowired
	private ZookeeperRegister zookeeperRegister;


	private static Channel serverChannel = null;
	private InetSocketAddress inetSocketAddress;
	private String serviceName;

	public void start(String ipAddress, int port) throws InterruptedException {
		serverChannel = nettyServer.bind(serverBootstrap, port);
		Register register;
		if (NettyProperties.useEtcd) {
			register = etcdRegister;
		} else {
			register = zookeeperRegister;
		}
		register.init();
		Reflections reflections = new Reflections("info.unclewang");
		Set<Class<?>> classes = reflections.getTypesAnnotatedWith(RpcProvider.class);
		classes.forEach(clazz -> {
			Class<?>[] interfaces = clazz.getInterfaces();
			if (interfaces != null && interfaces.length > 0) {
				//只获取实现的第一个interface的name，实际上并不准确，可能有误。
				String clazzName = interfaces[0].getName();
				this.serviceName = clazzName;
				try {
					log.info("register begin. service name :{}, address:{}:{}", serviceName, ipAddress, port);
					this.inetSocketAddress = new InetSocketAddress(RpcUtils.parseAddress(ipAddress), port);
					register.register(clazzName, this.inetSocketAddress);
					log.info("register end. service name :{}, address:{}:{}", serviceName, ipAddress, port);
				} catch (UnknownHostException e) {
					log.error("UnknownHostException", e);
				}
			}

		});
	}

	@PreDestroy
	public void stop() {
		etcdRegister.remove(this.serviceName, this.inetSocketAddress);
		log.warn("service is closed.{}", new Date(System.currentTimeMillis()));

		if (serverChannel != null) {
			serverChannel.close();
			serverChannel.parent().close();
		}
	}
}
