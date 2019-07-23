package info.unclewang.config;

import info.unclewang.annotation.RpcInterface;
import info.unclewang.proxy.RpcProxy;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Proxy;
import java.util.Set;

@Configuration
@Slf4j
public class RpcConfig implements ApplicationContextAware, InitializingBean {

	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Reflections reflections = new Reflections("info.unclewang.api");
		DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		Set<Class<?>> typesAnnotatedWith = reflections.getTypesAnnotatedWith(RpcInterface.class);
		for (Class<?> oneClass : typesAnnotatedWith) {
			beanFactory.registerSingleton(oneClass.getSimpleName(), create(oneClass));
		}
		log.info("afterPropertiesSet is {}", typesAnnotatedWith);
	}

	public static <T> T create(Class<T> interfaceClass) {
		return (T) Proxy.newProxyInstance(
				interfaceClass.getClassLoader(),
				new Class<?>[]{interfaceClass},
				new RpcProxy<>(interfaceClass)
		);
	}
}
