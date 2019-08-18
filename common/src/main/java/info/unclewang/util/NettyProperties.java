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

	private boolean keepAlive = true;

	private int backlog = 1024;

	private boolean tcpNodeLay = true;

	public static boolean sync = false;

	public static boolean useEtcd = false;
}
