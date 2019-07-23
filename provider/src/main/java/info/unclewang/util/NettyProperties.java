package info.unclewang.util;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author unclewang
 * @date 2019-07-22 19:26
 */
@Configuration
@Data
@ConfigurationProperties(prefix = "netty")
public class NettyProperties {

	private int tcpPort = 8000;

	private int bossCount = 4;

	private int workerCount = 16;

	private boolean keepAlive = true;

	private int backlog = 1024;

	private boolean tcpNodeLay = true;
}
