package info.unclewang.proxy;

import info.unclewang.annotation.RpcConsumer;
import info.unclewang.proxy.cglib.RpcMethodInterceptor;
import info.unclewang.proxy.jdk.RpcInvocationHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.Enhancer;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;
import java.util.Set;

@Component
@Slf4j
public class RpcProxy implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Reflections reflections = new Reflections("info.unclewang.api");
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		Set<Class<?>> rpcConsumerAnnotations = reflections.getTypesAnnotatedWith(RpcConsumer.class);
		for (Class<?> oneClass : rpcConsumerAnnotations) {
			RpcConsumer annotation = oneClass.getAnnotation(RpcConsumer.class);
			if (annotation == null) {
				continue;
			}
//			beanFactory.registerSingleton(oneClass.getSimpleName(), create(Class.forName(annotation.call())));
			beanFactory.registerSingleton(oneClass.getSimpleName(), createByCglib(Class.forName(annotation.call())));
		}
		log.warn("afterPropertiesSet rpcConsumerAnnotations is {}", rpcConsumerAnnotations);
	}

	public static <T> T create(Class<T> interfaceClass) {
		return (T) Proxy.newProxyInstance(
				interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass},
				new RpcInvocationHandler<>(interfaceClass)
		);
	}

	public static <T> T createByCglib(Class<T> clazz) {
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(clazz);
		enhancer.setCallback(new RpcMethodInterceptor(clazz));
		return (T) enhancer.create();
	}
}
