package info.unclewang.service;

import info.unclewang.api.IHelloService;
import info.unclewang.api.ConsumerService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

/**
 * @author unclewang
 * @date 2019-07-28 14:44
 */

@Service
public class ConsumerServiceImpl implements ConsumerService, ApplicationContextAware {

	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConsumerServiceImpl.applicationContext = applicationContext;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	/**
	 * 通过name获取 Bean.
	 *
	 * @param name
	 * @return
	 */
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	/**
	 * 通过class获取Bean.
	 *
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	/**
	 * 通过name,以及Clazz返回指定的Bean
	 *
	 * @param name
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(String name, Class<T> clazz) {
		return getApplicationContext().getBean(name, clazz);
	}

	public static void testXinRpc() {
		IHelloService helloService = getBean(IHelloService.class);
		System.out.println("aaasdada" + helloService.sayHi("asda"));
		System.out.println(helloService.sayHi("world"));
		System.out.println("helloService.map(\"aa\",\"bb\") = " + helloService.map("aa", "bb"));
	}

}
