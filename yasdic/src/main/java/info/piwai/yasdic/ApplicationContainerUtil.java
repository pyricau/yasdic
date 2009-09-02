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

	private static final HashMap<Class<? extends Context>, BeanFactoryHolder>	beanFactoryHolders	= new HashMap<Class<? extends Context>, BeanFactoryHolder>();

	private static Container													rootContainer;

	private static BeanFactoryHolder											applicationBeanFactoryHolder;

	private ApplicationContainerUtil() {
		throw new UnsupportedOperationException("This class should not be instanciated");
	}

	public static void registerBeanFactoryHolder(Class<? extends Context> contextClass, BeanFactoryHolder beanFactoryHolder) {
		beanFactoryHolders.put(contextClass, beanFactoryHolder);
	}

	public static void registerApplicationBeanFactoryHolder(BeanFactoryHolder beanFactoryHolder) {
		applicationBeanFactoryHolder = beanFactoryHolder;
	}

	public static void manageContext(Context c) {
		if (rootContainer == null) {
			throw new IllegalStateException("A root container must already exist (by calling manageApplicationContext first)");
		}
		BeanFactoryHolder beanFactoryHolder = beanFactoryHolders.get(c.getClass());

		if (beanFactoryHolder == null) {
			beanFactoryHolder = new BeanFactoryHolder();
			registerBeanFactoryHolder(c.getClass(), beanFactoryHolder);
		}

		new Container(rootContainer, c, beanFactoryHolder);
	}

	public static void manageApplicationContext(Application c) {
		if (rootContainer != null) {
			throw new IllegalStateException("A root container has already been created");
		}

		if (applicationBeanFactoryHolder == null) {
			applicationBeanFactoryHolder = new BeanFactoryHolder();
		}

		rootContainer = new Container(c, applicationBeanFactoryHolder);
	}

}
