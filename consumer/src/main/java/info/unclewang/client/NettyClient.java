package info.unclewang.client;

import info.unclewang.codec.RpcDecoder;
import info.unclewang.codec.RpcEncoder;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.handle.ClientFutureHandle;
import info.unclewang.handle.ClientHandler;
import info.unclewang.util.NettyProperties;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @author unclewang
 * @date 2019-07-22 20:41
 */
@Slf4j
public class NettyClient {
	private EventLoopGroup eventLoopGroup;
	private Channel channel;
	private ClientHandler clientHandler;
	private ClientFutureHandle clientFutureHandle;
	private ChannelFuture channelFuture;

	private String host;
	private int port;

	public NettyClient(String host, int port) {
		this.port = port;
		this.host = host;
	}


	public ChannelFuture connect(final InetSocketAddress inetSocketAddress) {
		clientHandler = new ClientHandler();
		clientFutureHandle = new ClientFutureHandle();
		eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();

		channelFuture = bootstrap.group(eventLoopGroup)
				.channel(NioSocketChannel.class)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4));
						pipeline.addLast(new RpcEncoder(RpcRequest.class));
						pipeline.addLast(new RpcDecoder(RpcResponse.class));
						if (NettyProperties.sync) {
							pipeline.addLast(clientHandler);
						} else {
							pipeline.addLast(clientFutureHandle);
						}
					}
				}).connect(inetSocketAddress);

		try {
			if (NettyProperties.sync) {
				channel = channelFuture.sync().channel();
			} else {
				channelFuture.addListener((ChannelFutureListener) this::operationComplete);
			}
		} catch (InterruptedException e) {
			log.error("channel error", e);
		}
		return channelFuture;
	}


	public InetSocketAddress getInetSocketAddress() {
		return new InetSocketAddress(host, port);
	}

	public void close() {
		eventLoopGroup.shutdownGracefully();
		channel.closeFuture().syncUninterruptibly();
	}

	public RpcResponse send(final RpcRequest request) {
		try {
			channel.writeAndFlush(request).await();
		} catch (InterruptedException e) {
			log.error("send rpc response error", e);
		}
		return clientHandler.getRpcResponse(request.getId());
	}

	private void operationComplete(ChannelFuture future) {
		if (future.isSuccess()) {
			channel = future.channel();
			log.info("start a client to " + host + ":" + port);
			channel.closeFuture().addListener((ChannelFutureListener) closeFuture -> {
				log.info("stop the client to " + host + ":" + port);
			});
		} else {
			log.error("start a Client failed", future.cause());
		}
	}
}
