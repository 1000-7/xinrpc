package info.unclewang.client;

import info.unclewang.codec.RpcDecoder;
import info.unclewang.codec.RpcEncoder;
import info.unclewang.entity.RpcRequest;
import info.unclewang.entity.RpcResponse;
import info.unclewang.handle.ClientHandler;
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

	private String host;
	private int port;

	public NettyClient(String host, int port) {
		this.port = port;
		this.host = host;
	}


	public void connect(final InetSocketAddress inetSocketAddress) {
		clientHandler = new ClientHandler();
		eventLoopGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();

		bootstrap.group(eventLoopGroup)
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
						pipeline.addLast(clientHandler);
					}
				});

		try {
			channel = bootstrap.connect(inetSocketAddress).sync().channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
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

}
