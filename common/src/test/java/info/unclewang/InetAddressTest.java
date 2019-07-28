package info.unclewang;

import org.junit.jupiter.api.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressTest {
	@Test
	public void method() throws UnknownHostException {
		InetAddress localHost = InetAddress.getLocalHost();
		System.out.println(localHost.getAddress());
		System.out.println(localHost.getHostName());
		System.out.println(localHost.getHostAddress());

	}
}
