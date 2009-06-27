/*
 * Copyright 2009 Pierre-Yves Ricau
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License. 
 */

package info.piwai.yasdic;

import info.piwai.yasdic.exception.BeanNotFoundRuntimeException;
import info.piwai.yasdic.exception.CyclicDependencyRuntimeException;
import info.piwai.yasdic.exception.YasdicRuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Yasdic stands for Yet Another Small Dependency Injection Container.
 * 
 * It may be used on a small mobile device, such as an Android phone, enabling
 * use of dependency injection together with low memory footprint and fast
 * response.
 * 
 * It lets you define beans of scope singleton or prototype, add singletons to
 * the container, and get beans from the containers. Each bean has a unique
 * String id.
 * 
 * A singleton bean is created once and then the container will always return
 * the same instance. A propotype bean has the opposite behavior : a new
 * instance is created every time the container is asked for a bean.
 * 
 * The defined beans are created whenever they are needed. If you want to force
 * the creation of a bean (and it's defined dependencies), just make a call to
 * the getBean() method.
 * 
 * See usage examples for more details on how to use it.
 * 
 * This small dependency injection container is inspired from Fabien Potencier's
 * one, written in PHP : http://github.com/fabpot/Pimple/tree/master
 * 
 * Some design choices has been made to optimise use on an Android plateform. I
 * applied some of the principles written here :
 * http://developer.android.com/guide/practices/design/performance.html
 * 
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
public final class YasdicContainer {

	/*
	 * No interface : this is internal stuff, so we use virtual calls instead of
	 * interface calls
	 */
	private final HashMap<String, BeanDef<Object>>	beanDefinitions	= new HashMap<String, BeanDef<Object>>();
	private final HashSet<String>					singletonDefIds	= new HashSet<String>();
	private final HashMap<String, Object>			singletonBeans	= new HashMap<String, Object>();
	private final ArrayList<String>					dependencyStack	= new ArrayList<String>();

	private YasdicContainer							parent;

	/**
	 * Constructor for a container with no parent container
	 */
	public YasdicContainer() {
	}

	/**
	 * Builds a container that has a parent container. The current container
	 * will looks in it's parent only if it cannot find any bean definition or
	 * singleton in it's own context.
	 * 
	 * @param parent
	 */
	public YasdicContainer(YasdicContainer parent) {
		this.parent = parent;
	}

	/**
	 * Tells whether a bean is available from this container
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @return true if a singleton or a definition is available for this bean in
	 *         this container or one of its parents
	 */
	public boolean hasBean(String id) {
		if (singletonBeans.containsKey(id)) {
			return true;
		}

		if (beanDefinitions.containsKey(id)) {
			return true;
		}

		if (parent != null) {
			return parent.hasBean(id);
		}

		return false;
	}

	/**
	 * Get a previously defined or previously injected bean
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @return a bean
	 */
	public Object getBean(String id) {

		// Bean is in scope singleton and has already bean created
		if (singletonBeans.containsKey(id)) {
			return singletonBeans.get(id);
		}

		// Getting the bean definition
		final BeanDef<Object> beanDefinition = beanDefinitions.get(id);

		// If we didn't find any definition, let's ask the parent container
		if (beanDefinition == null) {

			if (parent != null) {
				Object beanFromParent;
				try {
					beanFromParent = parent.getBean(id);
				} catch (CyclicDependencyRuntimeException e) {
					throw new CyclicDependencyRuntimeException(
							CyclicDependencyRuntimeException.class
									.getSimpleName()
									+ " occured in parent container when getting bean ["
									+ id + "]", e, dependencyStack);
				} catch (BeanNotFoundRuntimeException e) {
					throw new BeanNotFoundRuntimeException(
							BeanNotFoundRuntimeException.class.getSimpleName()
									+ " occured in parent container when getting bean ["
									+ id + "]", e, dependencyStack);
				}
				if (beanFromParent != null) {
					return beanFromParent;
				}
			}
			throw new BeanNotFoundRuntimeException("Could not find bean  ["
					+ id + "] singleton or definition in the container",
					dependencyStack);
		}

		if (dependencyStack.contains(id)) {
			throw new CyclicDependencyRuntimeException(
					"Cyclic dependency on bean [" + id + "]", dependencyStack);
		}

		dependencyStack.add(id);
		Object bean = beanDefinition.callNewBean(this, id);

		/*
		 * If it is a singleton, put the bean in the singleton map and remove
		 * its definition
		 */
		if (singletonDefIds.contains(id)) {
			singletonBeans.put(id, bean);
			beanDefinitions.remove(id);
			singletonDefIds.remove(id);
		}

		// Late initialization of the bean
		beanDefinition.initBean(this, bean);

		// Cleaning the stack
		dependencyStack.remove(id);

		return bean;
	}

	/**
	 * Stores a bean definition with singleton scope in the container
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @param definition
	 *            an instance of a class extending BeanDef abstract class
	 */
	public void define(String id, BeanDef<?> definition) {
		define(id, true, definition);
	}

	/**
	 * Stores a bean definition in the container
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @param singleton
	 *            whether the scope of the bean should be a singleton (as
	 *            opposed to a prototype)
	 * @param definition
	 *            an instance of a class extending BeanDef abstract class
	 */
	@SuppressWarnings("unchecked")
	public void define(String id, boolean singleton, BeanDef<?> definition) {
		checkDependencyStack();
		beanDefinitions.put(id, (BeanDef<Object>) definition);
		singletonBeans.remove(id);
		if (singleton) {
			singletonDefIds.add(id);
		} else {
			singletonDefIds.remove(id);
		}
	}

	/**
	 * Stores a singleton bean in the container (removing any previous bean
	 * definition for this id)
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @param value
	 */
	public void define(String id, Object value) {
		checkDependencyStack();
		beanDefinitions.remove(id);
		singletonDefIds.remove(id);
		singletonBeans.put(id, value);
	}

	/**
	 * Unstore bean definition, but keeps any already created singletons.
	 * 
	 * As its definition is cleaned anyway after a singleton is created, you
	 * should only use this to undefine singletons not created yet, or undefine
	 * prototype beans. It does not impact any parent container.
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 */
	public void undefineBean(String id) {
		checkDependencyStack();
		beanDefinitions.remove(id);
		singletonDefIds.remove(id);
	}

	/**
	 * Unstore a singleton bean (whereas it as bean created in the container or
	 * given to it). Please be aware that since the definition is removed after
	 * having created a singleton, you will have no mean to get another instance
	 * of this bean. It does not impact any parent container.
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 */
	public void unstoreSingleton(String id) {
		checkDependencyStack();
		singletonBeans.remove(id);
	}

	/**
	 * Unstore any bean definition, but keeps any already created singletons.
	 * 
	 * 
	 * Use with caution : no more prototype beans can be retrieved from
	 * getBean(String id) after calling this method, until you define new
	 * prototype beans. The same rule applies for singletons not created yet. It
	 * does not impact any parent container.
	 */
	public void undefineAllBeans() {
		checkDependencyStack();
		beanDefinitions.clear();
		singletonDefIds.clear();
	}

	/**
	 * Totally reset the container, removing singletons and bean definitions. It
	 * does not impact any parent container.
	 */
	public void reset() {
		checkDependencyStack();
		beanDefinitions.clear();
		singletonDefIds.clear();
		singletonBeans.clear();
	}

	private void checkDependencyStack() {
		if (dependencyStack.size() != 0) {
			throw new YasdicRuntimeException(
					"this method should not be called while creating beans",
					dependencyStack);
		}
	}

	/**
	 * This class is used to define beans.
	 * 
	 * The container is available as a parameter of the methods to get the
	 * dependencies your bean need. You should not try to redefine beans of the
	 * container inside this class methods.
	 * 
	 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
	 * 
	 */
	public static abstract class BeanDef<T> {

		/**
		 * You should instantiate your bean in this method, and return it. You
		 * can also set your dependencies in this method, but it may be a better
		 * option to use the initBean() method to do so, in order to avoid
		 * cyclic dependency.
		 * 
		 * @return the instantiated bean
		 */
		protected abstract T newBean(YasdicContainer c);

		/**
		 * Could be overridden if needed by intermediate abstract class (for
		 * instance to enable loggin). This method has to call newBean() to keep
		 * the defining working.
		 * 
		 * @param c
		 * @return
		 */
		protected T callNewBean(YasdicContainer c, String id) {
			return newBean(c);
		}

		/**
		 * Whenever you have a cyclic dependency, you should override this
		 * method to initialise your beans after having created them in
		 * newBean() method.
		 * 
		 * @param bean
		 */
		protected void initBean(YasdicContainer c, T bean) {
		}

	}

}
