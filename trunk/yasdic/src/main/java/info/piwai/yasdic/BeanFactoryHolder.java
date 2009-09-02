package info.piwai.yasdic;

import info.piwai.yasdic.exception.YasdicException;

import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;

public class BeanFactoryHolder {

	public static final Integer						SINGLETON_EAGER		= 0;
	public static final Integer						SINGLETON_LAZY		= 1;
	public static final Integer						PROTOTYPE			= 2;

	private ContextInjector<? extends Context>		contextInjector;

	private OnNewBeanListener						onNewBeanListener;

	private final HashMap<String, BeanFactory<?>>	beanFactories		= new HashMap<String, BeanFactory<?>>();

	private boolean									frozen				= false;

	private final HashSet<String>					eagerSingletonIds	= new HashSet<String>();

	private final HashSet<String>					lazySingletonIds	= new HashSet<String>();

	public void register(String id, Integer scope, BeanFactory<?> factory) {
		if (frozen) {
			throw new UnsupportedOperationException(
					"The factory holder has been frozen, which means that it is actually used by the container. You cannot register any more BeanFactory");
		}
		if (scope < SINGLETON_EAGER || scope > PROTOTYPE) {
			throw new IllegalArgumentException("Scope parameter value should be one of SINGLETON_EAGER (" + SINGLETON_EAGER
					+ "), SINGLETON_LAZY (" + SINGLETON_LAZY + "), PROTOTYPE (" + PROTOTYPE + "). Actual value: " + scope);
		}

		if (beanFactories.containsKey(id)) {
			throw new IllegalArgumentException("Cannot override a bean factory in the same holder, this is a non sense!");
		}

		if (scope == SINGLETON_EAGER) {
			eagerSingletonIds.add(id);
		} else if (scope == SINGLETON_LAZY) {
			lazySingletonIds.add(id);
		}
		beanFactories.put(id, factory);
	}

	public void registerConstant(String id, final Object constant) {
		register(id, SINGLETON_LAZY, new BeanFactory<Object>() {
			@Override
			protected Object newBean(Container c) {
				return constant;
			}
		});
	}

	public void registerEagerSingleton(String id, BeanFactory<?> factory) {
		register(id, SINGLETON_EAGER, factory);
	}

	public void registerLazySingleton(String id, BeanFactory<?> factory) {
		register(id, SINGLETON_LAZY, factory);
	}

	public void registerPrototype(String id, BeanFactory<?> factory) {
		register(id, PROTOTYPE, factory);
	}

	public static abstract class BeanFactory<T> {

		protected abstract T newBean(Container c) throws YasdicException;

		protected void initBean(Container c, T bean) throws YasdicException {
		}
	}

	public interface ContextInjector<CONTEXT extends Context> {
		public abstract void inject(CONTEXT context, Container c) throws YasdicException;
	}

	public static abstract class OnNewBeanListener {
		public void onBeforeNewBean(String id) {

		}

		public void onAfterNewBean(String id, Object bean) {

		}

		public void onAfterInitBean(String id, Object bean) {

		}
	}

	public BeanFactoryHolder contextInjector(ContextInjector<? extends Context> contextInjector) {
		this.contextInjector = contextInjector;
		return this;
	}

	public BeanFactoryHolder onNewBeanListener(OnNewBeanListener onNewBeanListener) {
		this.onNewBeanListener = onNewBeanListener;
		return this;
	}

	/** Package protected methods, used by Container **/

	boolean hasPrototypeScope(String id) {
		if (eagerSingletonIds.contains(id)) {
			return false;
		}
		return !lazySingletonIds.contains(id);
	}

	/**
	 * Warning : do not modify the list. For performance concerns, this list is
	 * not locked.
	 * 
	 * @return
	 */
	HashSet<String> getEagerSingletonIds() {
		return eagerSingletonIds;
	}

	BeanFactory<?> getBeanFactory(String id) {
		return beanFactories.get(id);
	}

	ContextInjector<?> getContextInjector() {
		return contextInjector;
	}

	void freeze() {
		frozen = true;
	}

	OnNewBeanListener getOnNewBeanListener() {
		return onNewBeanListener;
	}

}
