package info.unclewang.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author unclewang
 * @date 2019-07-26 12:58
 */
public class RpcUtils {
	private static final String IP_SPLIT_CHAR = "\\.";
	public static final String RPC_SERVER_NAME = "test-xin-rpc";

	public static InetAddress parseAddress(String ipAddress) throws UnknownHostException {
		String[] ipStr = ipAddress.split(IP_SPLIT_CHAR);
		byte[] ipBuf = new byte[4];
		for (int i = 0; i < 4; i++) {
			ipBuf[i] = (byte) (Integer.parseInt(ipStr[i]) & 0xff);
		}

		return InetAddress.getByAddress(ipBuf);
	}
}
