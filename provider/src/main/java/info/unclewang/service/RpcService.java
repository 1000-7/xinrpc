package info.unclewang.service;

import info.unclewang.etcd.EtcdRegister;
import info.unclewang.server.NettyServer;
import info.unclewang.util.NettyProperties;
import info.unclewang.util.RpcUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;


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
	private NettyProperties nettyProperties;

	private Channel serverChannel;

	public void start(String ipAddress, int port) throws InterruptedException {
		serverChannel = nettyServer.bind(serverBootstrap, port);
		etcdRegister.initEtcd();
		try {
			etcdRegister.register(RpcUtils.RPC_SERVER_NAME, new InetSocketAddress(RpcUtils.parseAddress(ipAddress), port));
		} catch (UnknownHostException e) {
			log.error("UnknownHostException", e);
		}
	}

	@PreDestroy
	public void stop() {
		if (serverChannel != null) {
			serverChannel.close();
			serverChannel.parent().close();
		}
	}
}
