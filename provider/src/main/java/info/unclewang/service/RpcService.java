package info.unclewang.service;

import info.unclewang.server.NettyServer;
import io.netty.channel.Channel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;


/**
 * @author unclewang
 * @date 2019-07-22 20:13
 */
@Service
public class RpcService {
	@Autowired
	private NettyServer nettyServer;
	private Channel serverChannel;

	public void start() throws InterruptedException {
		serverChannel = nettyServer.bind(nettyServer.serverBootstrap());
	}

	@PreDestroy
	public void stop() {
		if (serverChannel != null) {
			serverChannel.close();
			serverChannel.parent().close();
		}
	}
}
