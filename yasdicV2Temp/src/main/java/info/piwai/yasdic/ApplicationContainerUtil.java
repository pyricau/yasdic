package info.piwai.yasdic;

import java.util.HashMap;

import android.app.Application;
import android.content.Context;

/**
 * TODO : deal with destroy methods ?
 * 
 * @author Pierre-Yves Ricau
 * 
 */
public class ApplicationContainerUtil {

	/**
	 * Map between a subclass of context and a BeanFactoryHolder
	 */
	private static final HashMap<Class<? extends Context>, BeanFactoriesHolder>	beanFactoryHolders	= new HashMap<Class<? extends Context>, BeanFactoriesHolder>();

	private static Container													rootContainer;

	private static BeanFactoriesHolder											applicationBeanFactoryHolder;

	private ApplicationContainerUtil() {
		throw new UnsupportedOperationException("This class should not be instanciated");
	}

	public static void registerBeanFactoryHolder(Class<? extends Context> contextClass, BeanFactoriesHolder beanFactoryHolder) {
		beanFactoryHolders.put(contextClass, beanFactoryHolder);
	}

	public static void registerApplicationBeanFactoryHolder(BeanFactoriesHolder beanFactoryHolder) {
		applicationBeanFactoryHolder = beanFactoryHolder;
	}

	public static void manageContext(Context c) {
		if (rootContainer == null) {
			throw new IllegalStateException("A root container must already exist (by calling manageApplicationContext first)");
		}

		BeanFactoriesHolder beanFactoryHolder = beanFactoryHolders.get(c.getClass());

		// beanFactoryHolder may be null
		new Container(rootContainer, c, beanFactoryHolder);
	}

	public static void manageApplicationContext(Application c) {
		if (rootContainer != null) {
			throw new IllegalStateException("A root container has already been created");
		}

		if (applicationBeanFactoryHolder == null) {
			applicationBeanFactoryHolder = new BeanFactoriesHolder();
		}

		rootContainer = new Container(c, applicationBeanFactoryHolder);
	}

}
