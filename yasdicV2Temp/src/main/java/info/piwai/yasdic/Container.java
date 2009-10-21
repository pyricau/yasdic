package info.piwai.yasdic;

import info.piwai.yasdic.BeanFactoriesHolder.BeanFactory;
import info.piwai.yasdic.BeanFactoriesHolder.ContextInjector;
import info.piwai.yasdic.BeanFactoriesHolder.OnNewBeanListener;
import info.piwai.yasdic.exception.YasdicException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import junit.framework.Assert;
import android.content.Context;

/**
 * This class is not thread safe.
 * 
 * @author Pierre-Yves Ricau
 * 
 */
public class Container {

	private final Container					parent;

	private final Context					containerContext;

	private final BeanFactoriesHolder		factoriesHolder;

	private final HashMap<String, Object>	singletonBeans	= new HashMap<String, Object>();

	/*
	 * This List works as a stack (push / pull + contains). Should we change
	 * from ArrayList to something else ?
	 */
	private final ArrayList<String>			dependencyStack	= new ArrayList<String>();

	/**
	 * currentContext must not be null. Parent may be null, factoryHolder may be
	 * null, but not both.
	 * 
	 * @param parent
	 * @param currentContext
	 * @param factoryHolder
	 */
	public Container(Container parent, Context currentContext, BeanFactoriesHolder factoryHolder) {
		Assert.assertNotNull(currentContext);
		if (parent == null) {
			Assert.assertNotNull(factoryHolder);
		}

		this.parent = parent;
		containerContext = currentContext;
		factoriesHolder = factoryHolder;

		if (factoryHolder != null) {
			factoryHolder.freeze();
		}
		refresh();
	}

	public Container(Context currentContext, BeanFactoriesHolder factoryHolder) {
		this(null, currentContext, factoryHolder);
	}

	public Context getRootContainerContext() {
		if (parent != null) {
			return parent.getRootContainerContext();
		} else {
			return containerContext;
		}
	}

	@SuppressWarnings("unchecked")
	private void refresh() {
		singletonBeans.clear();
		if (factoriesHolder == null) {
			return;
		}
		Set<String> eagerSingletonIds = factoriesHolder.getEagerSingletonIds();
		try {
			for (String eagerSingletonId : eagerSingletonIds) {
				getBean(eagerSingletonId);
			}

			ContextInjector contextInjector = factoriesHolder.getContextInjector();

			if (contextInjector != null) {
				contextInjector.inject(containerContext, this);
			}
		} catch (YasdicException e) {
			throw new RuntimeException("A checked YasdicException occured", e);
		}
	}

	/**
	 * @see #getBeanUnchecked(String)
	 */
	@SuppressWarnings("unchecked")
	public <T> T getBeanUnchecked(String id, Class<T> requiredType) {
		return (T) getBeanUnchecked(id);
	}

	public Context getContainerContext() {
		return containerContext;
	}

	@SuppressWarnings("unchecked")
	public <T extends Context> T getContainerContext(Class<T> requiredType) {
		return (T) getContainerContext();
	}

	@SuppressWarnings("unchecked")
	public <T> T getBean(String id, Class<T> requiredType) throws YasdicException {
		return (T) getBean(id);
	}

	/**
	 * To be used for late calls to the container, for instance to get prototype
	 * beans. The bean which will make this call. This method is provided to
	 * avoid having to deal with checked exceptions. It should not be called
	 * from a BeanFactory.
	 * 
	 * @see #getBean(String)
	 */
	public Object getBeanUnchecked(String id) {
		try {
			return getBean(id);
		} catch (YasdicException e) {
			throw new RuntimeException("A checked YasdicException occured", e);
		}
	}

	public boolean containsBean(String id) {
		if (factoriesHolder.contains(id)) {
			return true;
		}
		if (parent != null) {
			return parent.containsBean(id);
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public Object getBean(String id) throws YasdicException {

		Object bean = singletonBeans.get(id);
		if (bean != null) {
			return bean;
		}

		BeanFactoriesHolder factoryHolder = factoriesHolder;

		BeanFactory factory = null;

		if (factoryHolder != null) {
			factory = factoryHolder.getBeanFactory(id);
		}

		if (factory == null) {
			if (parent != null) {
				// This will throw an exception if bean is not found in parent.
				try {
					return parent.getBean(id);
				} catch (YasdicException e) {
					throw new YasdicException("YasdicException on getBean() in parent" + stackToString(id), e);
				}
			}
			throw new YasdicException("Bean not found" + stackToString(id));
		}

		OnNewBeanListener listener = factoryHolder.getOnNewBeanListener();

		if (listener != null) {
			listener.onBeforeNewBean(id);
		}

		synchronized (dependencyStack) {

			if (dependencyStack.contains(id)) {
				throw new YasdicException("Cyclic dependency" + stackToString(id));
			}

			dependencyStack.add(id);

			try {
				bean = factory.newBean(this);
			} catch (RuntimeException e) {
				throw new YasdicException("RuntimeException on newBean() " + stackToString(id), e);
			}

			if (!factoryHolder.hasPrototypeScope(id)) {
				singletonBeans.put(id, bean);
			}

			if (listener != null) {
				listener.onAfterNewBean(id, bean);
			}

			try {

				factory.initBean(this, bean);
			} catch (RuntimeException e) {
				throw new YasdicException("RuntimeException on initBean()" + stackToString(id), e);
			}

			if (listener != null) {
				listener.onAfterInitBean(id, bean);
			}

			dependencyStack.remove(id);
		}

		return bean;
	}

	private String stackToString(String id) {
		synchronized (dependencyStack) {
			String message = "\nBean: [" + id + "]\nDependency stack: " + dependencyStack.toString();
			dependencyStack.clear();
			return message;
		}
	}
}
