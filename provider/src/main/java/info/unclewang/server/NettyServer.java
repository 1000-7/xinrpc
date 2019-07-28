package info.unclewang.server;

import info.unclewang.codec.RpcDecoder;
import info.unclewang.codec.RpcEncoder;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.handle.RpcHandler;
import info.unclewang.serialization.impl.FastJsonSerialization;
import info.unclewang.util.NettyProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author unclewang
 * @date 2019-07-22 19:26
 */
@Component
@EnableConfigurationProperties(NettyProperties.class)
public class NettyServer {
	@Autowired
	private NettyProperties nettyProperties;
	@Autowired
	private RpcHandler rpcHandler;

	@Bean
	public ServerBootstrap serverBootstrap() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup();
		NioEventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.DEBUG))
				.childOption(ChannelOption.SO_BACKLOG, nettyProperties.getBacklog())
				.childOption(ChannelOption.SO_KEEPALIVE, nettyProperties.isKeepAlive())
				.childOption(ChannelOption.TCP_NODELAY, nettyProperties.isTcpNodeLay())
				.childHandler(new ChannelInitializer<NioSocketChannel>() {
					@Override
					protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
						nioSocketChannel.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4));
						nioSocketChannel.pipeline().addLast(new RpcEncoder(RpcResponse.class, new FastJsonSerialization()));
						nioSocketChannel.pipeline().addLast(new RpcDecoder(RpcRequest.class, new FastJsonSerialization()));
						nioSocketChannel.pipeline().addLast(rpcHandler);
					}
				});

		return serverBootstrap;
	}

	public Channel bind(final ServerBootstrap serverBootstrap, int port) throws InterruptedException {
		return serverBootstrap.bind(port).addListener(future -> {
			if (future.isSuccess()) {
				System.out.println(new Date() + ": 端口[" + port + "]绑定成功!");
			} else {
				System.err.println("端口[" + port + "]绑定失败!");
			}
		}).sync().channel().closeFuture().channel();
	}
}

