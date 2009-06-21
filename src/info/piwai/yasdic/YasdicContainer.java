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
 * It let you define beans of scope singleton or prototype, add singletons to
 * the container, and get beans from the containers. Each bean has a unique
 * String id.
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

	// No interface : this is internal stuff, so we use virtual calls instead
	// interface calls
	private final HashMap<String, BeanDef<Object>>	beanDefinitions	= new HashMap<String, BeanDef<Object>>();
	private final HashSet<String>					singletonDefIds	= new HashSet<String>();
	private final HashMap<String, Object>			singletonBeans	= new HashMap<String, Object>();
	private final ArrayList<String>					dependencyStack	= new ArrayList<String>();

	private YasdicContainer							parent;

	public YasdicContainer() {
	}

	/**
	 * Build a container that has a parent container. The current container will
	 * looks in it's parent only if it cannot find any bean definition or
	 * singleton in it's own context.
	 * 
	 * @param parent
	 */
	public YasdicContainer(YasdicContainer parent) {
		this.parent = parent;
	}

	/**
	 * Get a bean that has previously defined
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 * @return
	 */
	public Object getBean(String id) {

		// Bean is in scope singleton and has already bean created
		if (singletonBeans.containsKey(id)) {
			return singletonBeans.get(id);
		}

		final BeanDef<Object> beanDefinition = beanDefinitions.get(id);

		if (beanDefinition == null) {

			if (parent != null) {
				Object beanFromParent;
				try {
					beanFromParent = parent.getBean(id);
				} catch (YasdicRuntimeException e) {
					throw new YasdicRuntimeException("Exception occured in parent container when getting bean [" + id + "]", e,
							dependencyStack);
				}
				if (beanFromParent != null) {
					return beanFromParent;
				}
			}
			throw new BeanNotFoundRuntimeException("Could not find bean  [" + id + "] singleton or definition in the container",
					dependencyStack);
		}

		if (dependencyStack.contains(id)) {
			throw new CyclicDependencyRuntimeException("Cyclic dependency on bean [" + id + "]", dependencyStack);
		}

		dependencyStack.add(id);
		Object bean = beanDefinition.callNewBean(this, id);

		if (singletonDefIds.contains(id)) {
			singletonBeans.put(id, bean);
			// Removing definition : we won't need to create this bean again
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
	 *            whether the container should create a singleton bean (as
	 *            opposed to a prototype)
	 * @param definition
	 *            an instance of a class extending BeanDef abstract class
	 */
	@SuppressWarnings("unchecked")
	public void define(String id, boolean singleton, BeanDef<?> definition) {
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
		beanDefinitions.remove(id);
		singletonDefIds.remove(id);
		singletonBeans.put(id, value);
	}

	/**
	 * Unstore bean definition, but keeps any already created singletons.
	 * 
	 * As its definition is cleaned anyway after a singleton is created, you
	 * should only use this to undefine singletons not created yet, or undefine
	 * prototype beans.
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 */
	public void undefineBean(String id) {
		beanDefinitions.remove(id);
		singletonDefIds.remove(id);
	}

	/**
	 * Unstore a singleton bean (whereas it as bean created in the container or
	 * given to it). Please be aware that since the definition is removed after
	 * having created a singleton, you will have no mean to get another instance
	 * of this bean.
	 * 
	 * @param id
	 *            the unique id of the bean for this container
	 */
	public void unstoreSingleton(String id) {
		singletonBeans.remove(id);
	}

	/**
	 * Unstore any bean definition, but keeps any already created singletons.
	 * 
	 * 
	 * Use with caution : no more prototype beans can be retrieved from
	 * getBean(String id) after calling this method, until you define new
	 * prototype beans. The same rule applies for singletons not created yet.
	 */
	public void undefineAllBeans() {
		beanDefinitions.clear();
		singletonDefIds.clear();
	}

	/**
	 * Totally reset the container, removing singletons and bean definitions
	 */
	public void reset() {
		beanDefinitions.clear();
		singletonDefIds.clear();
		singletonBeans.clear();
	}

	/**
	 * This class is used to define beans.
	 * 
	 * The bean(String id) method is available to get the dependencies your bean
	 * need.
	 * 
	 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
	 * 
	 */
	public static abstract class BeanDef<T> {

		/**
		 * You should instantiate your bean in this method, and return it. You
		 * can also set your dependencies in this method, but it may be a better
		 * option to use the initBean(Object bean) method to do so, in order to
		 * avoid cyclic dependency.
		 * 
		 * @return the instantiated bean
		 */
		protected abstract T newBean(YasdicContainer c);

		/**
		 * Could be overridden if needed by intermediate abstract class (for
		 * instance to enable loggin)
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
