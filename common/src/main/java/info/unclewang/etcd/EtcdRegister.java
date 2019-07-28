package info.unclewang.etcd;

import info.unclewang.util.RpcUtils;
import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.kv.DeleteResponse;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.kv.PutResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author unclewang
 * @date 2019-07-26 10:13
 */
@Slf4j
@Service
public class EtcdRegister implements Register {
	private static final String SERVICE_BASE_PATH = "xin_rpc";
	private static final String SERVICE_HOST_STATUS = "health";
	private static final String SPLIT_CHAR = "/";
	private static final String ETCD_ADDRESS = "http://127.0.0.1:2379";
	private KV kvClient;
	private Lease leaseClient;
	private static final int TTL = 60;
	private long leaseId;


	public void initEtcd() {
		initEtcd(ETCD_ADDRESS);
	}

	public void initEtcd(String address) {
		Client client = Client.builder().endpoints(address).build();
		this.leaseClient = client.getLeaseClient();
		this.kvClient = client.getKVClient();
		try {
			this.leaseId = leaseClient.grant(TTL).get().getID();
		} catch (InterruptedException | ExecutionException e) {
			log.error("init etcd error!!!", e);
		}

		keepAlive();
	}


	/**
	 * 发送心跳到ETCD，表明该host的存活状态
	 */
	@Override
	public void keepAlive() {
		ExecutorService executor = new ThreadPoolExecutor(1, 200, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(1024), new ThreadFactory() {
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r);
				t.setName("info.unclewang.etcd.keepAlive");
				t.setDaemon(true);
				return t;
			}
		}, new ThreadPoolExecutor.AbortPolicy());
		executor.submit(() -> {
			this.leaseClient.keepAlive(this.leaseId, new StreamObserver<LeaseKeepAliveResponse>() {
				@Override
				public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
					log.info("LeaseKeepAliveResponse value:{}/{}", leaseKeepAliveResponse.getTTL(), leaseKeepAliveResponse.getID());
				}

				@Override
				public void onError(Throwable throwable) {
					log.error("LeaseKeepAliveResponse error", throwable);
				}

				@Override
				public void onCompleted() {
					log.info("LeaseKeepAliveResponse completed");
				}
			});
		});
	}

	@Override
	public void register(String serviceName, InetSocketAddress address) {
		String key = MessageFormat.format("/{0}/{1}/{2}/{3}", SERVICE_BASE_PATH, serviceName, address.getHostName(), address.getPort());
		String value = MessageFormat.format("{0}/{1}/{2}/{3}", address.getHostName(), address.getAddress().getHostAddress(), address.getPort(), SERVICE_HOST_STATUS);
		ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());
		ByteSequence byteValue = ByteSequence.from(value, Charset.defaultCharset());

		try {
			PutResponse putResponse = this.kvClient.put(byteKey, byteValue, PutOption.newBuilder().withLeaseId(this.leaseId).build()).get();
			log.info("kv put success. key:value||{}:{}", key, value);
			log.info("register success. address:{}, prevKV;{}", address.toString(), putResponse.getPrevKv());
		} catch (InterruptedException | ExecutionException e) {
			log.error("kv put failed", e);
		}
	}

	@Override
	public void remove(String serviceName, InetSocketAddress address) {
		String key = MessageFormat.format("/{0}/{1}/{2}/{3}", SERVICE_BASE_PATH, serviceName, address.getHostName(), address.getPort());
		ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());
		try {
			CompletableFuture<DeleteResponse> delete = this.kvClient.delete(byteKey);
			DeleteResponse deleteResponse = delete.get();
			log.warn("remove service {} success. remove value is {}. delete num:{}", serviceName, key, deleteResponse.getDeleted());
		} catch (InterruptedException | ExecutionException e) {
			log.error("kv put failed", e);
		}
	}

	@Override
	public List<InetSocketAddress> discover(String serviceName) {
		String key = MessageFormat.format("/{0}/{1}", SERVICE_BASE_PATH, serviceName);
		log.info("start to find service, Name :{}", key);
		ByteSequence byteKey = ByteSequence.from(key, Charset.defaultCharset());
		List<InetSocketAddress> res = new ArrayList<>();
		try {
			GetResponse response = this.kvClient.get(byteKey, GetOption.newBuilder().withPrefix(byteKey).build()).get();
			response.getKvs().forEach(keyValue -> {
				String value = keyValue.getValue().toString(Charset.defaultCharset());
				String[] split = value.split(SPLIT_CHAR);
				try {
					if (StringUtils.isNotBlank(split[3]) && split[3].equals(SERVICE_HOST_STATUS)) {
						res.add(new InetSocketAddress(RpcUtils.parseAddress(split[1]), Integer.parseInt(split[2].replace(",", ""))));
						log.info("find service success, Name :{}", value);
					} else {
						log.error("find service failed, because this service hangs up. Name :{}", value);
					}
				} catch (UnknownHostException e) {
					log.error("UnknownHostException build socketAddress error!", e);
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			log.error("kv get failed", e);
		}
		return res;
	}


}
