package info.piwai.yasdic;

import info.piwai.yasdic.BeanFactoryHolder.BeanFactory;
import info.piwai.yasdic.BeanFactoryHolder.ContextInjector;
import info.piwai.yasdic.BeanFactoryHolder.OnNewBeanListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;

public class Container {

	private final Container					parent;

	private final Context					currentContext;

	private final BeanFactoryHolder			factoryHolder;

	private final HashMap<String, Object>	singletonBeans	= new HashMap<String, Object>();

	/*
	 * This List works as a stack (push / pull + contains). Should we change
	 * from ArrayList to something else ?
	 */
	private final ArrayList<String>			dependencyStack	= new ArrayList<String>();

	public Container(Container parent, Context currentContext, BeanFactoryHolder factoryHolder) {
		this.parent = parent;
		this.currentContext = currentContext;
		this.factoryHolder = factoryHolder;
		factoryHolder.freeze();
		refresh();
	}

	public Container(Context currentContext, BeanFactoryHolder factoryHolder) {
		this(null, currentContext, factoryHolder);
	}

	public <T extends Context> T getCurrentContext() {
		return (T) currentContext;
	}

	public <T extends Context> T getCurrentContext(Class<T> clazz) {
		return getCurrentContext();
	}

	public Context getGlobalContext() {
		if (parent != null) {
			return parent.getGlobalContext();
		} else {
			return currentContext;
		}
	}

	private void refresh() {
		singletonBeans.clear();
		HashSet<String> eagerSingletonIds = factoryHolder.getEagerSingletonIds();
		for (String eagerSingletonId : eagerSingletonIds) {
			getBean(eagerSingletonId);
		}
		ContextInjector contextInjector = factoryHolder.getContextInjector();

		if (contextInjector != null) {
			contextInjector.inject(currentContext, this);
		}
	}

	public <T> T getBean(String id, Class<T> clazz) {
		return getBean(id);
	}

	public void storeSingleton(String id, Object constant) {
		singletonBeans.put(id, constant);
	}

	public boolean hasStoredSingleton(String id) {
		if (singletonBeans.containsKey(id)) {
			return true;
		}
		if (parent != null) {
			return parent.hasStoredSingleton(id);
		}
		return false;
	}

	public <T> T getBean(String id) {

		T bean = (T) singletonBeans.get(id);
		if (bean != null) {
			return bean;
		}

		BeanFactoryHolder factoryHolder = this.factoryHolder;

		BeanFactory<T> factory = (BeanFactory<T>) factoryHolder.getBeanFactory(id);

		if (factory == null) {
			if (parent != null) {
				// This will throw an exception if bean is not found in parent.
				return parent.getBean(id);
			}
			throw new IllegalArgumentException("Bean not found: " + id);
		}

		OnNewBeanListener listener = factoryHolder.getOnNewBeanListener();

		if (listener != null) {
			listener.onBeforeNewBean(id);
		}

		if (dependencyStack.contains(id)) {
			throw new IllegalStateException("Cyclic dependency on bean: " + id);
		}

		dependencyStack.add(id);

		bean = factory.newBean(this);

		if (!factoryHolder.hasPrototypeScope(id)) {
			singletonBeans.put(id, bean);
		}

		if (listener != null) {
			listener.onAfterNewBean(id, bean);
		}

		factory.initBean(this, bean);

		if (listener != null) {
			listener.onAfterInitBean(id, bean);
		}

		dependencyStack.remove(id);

		return bean;
	}

}
