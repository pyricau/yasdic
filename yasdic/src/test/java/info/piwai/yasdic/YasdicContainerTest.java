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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.piwai.yasdic.YasdicContainer.BeanDef;
import info.piwai.yasdic.exception.BeanNotFoundRuntimeException;
import info.piwai.yasdic.exception.CyclicDependencyRuntimeException;
import info.piwai.yasdic.exception.YasdicRuntimeException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
 * 
 */
public class YasdicContainerTest {

	YasdicContainer	container;

	/**
	 * Class used to test getting a simple bean from the container
	 * 
	 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
	 * 
	 */
	public static class SimpleBean {
	}

	/**
	 * Class used to test getting a
	 * 
	 * @author Pierre-Yves Ricau (py.ricau+yasdic@gmail.com)
	 * 
	 */
	public static class A {
		private SimpleBean	dep;

		public A(SimpleBean dep) {
			this.dep = dep;
		}

		public SimpleBean getDep() {
			return dep;
		}

		public void setDep(SimpleBean dep) {
			this.dep = dep;
		}
	}

	/**
	 * Class used to test cyclic dependency
	 * 
	 * @author Pierre-Yves Ricau
	 * 
	 */
	public static class Cycle {

		private Cycle	c;

		public Cycle(Cycle c) {
			this.c = c;
		}

		public Cycle() {
		}

		public void setCycle(Cycle c) {
			this.c = c;
		}

		public Cycle getCycle() {
			return c;
		}
	}

	/**
	 * Creating the container
	 * 
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		container = new YasdicContainer();
	}

	/**
	 * Cleaning the container
	 * 
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		container = null;
	}

	/**
	 * Testing that getting twice a singleton returns the same instance and that
	 * getting twice a prototype creates two instances
	 */
	@Test
	public void testGetSimpleBean() {
		container.define("simpleBeanSingleton1", new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		});

		container.define("simpleBeanSingleton2", true,
				new BeanDef<SimpleBean>() {
					@Override
					protected SimpleBean newBean(YasdicContainer c) {
						return new SimpleBean();
					}
				});

		container.define("simpleBeanPrototype", false,
				new BeanDef<SimpleBean>() {
					@Override
					protected SimpleBean newBean(YasdicContainer c) {
						return new SimpleBean();
					}
				});

		SimpleBean singleton1 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");
		SimpleBean singleton2 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		assertTrue("Getting twice a singleton should return the same instance",
				singleton1 == singleton2);

		singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton2");
		singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton2");

		assertTrue("Getting twice a singleton should return the same instance",
				singleton1 == singleton2);

		SimpleBean prototype1 = (SimpleBean) container
				.getBean("simpleBeanPrototype");
		SimpleBean prototype2 = (SimpleBean) container
				.getBean("simpleBeanPrototype");

		assertFalse(
				"Getting twice a prototype should not return the same instance",
				prototype1 == prototype2);
	}

	/**
	 * Testing that trying to get a bean not defined will throw an exception
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testGetBeanNotFound() {
		container.getBean("someBean");
	}

	/**
	 * Testing that trying to get a singleton bean with cyclic dependency will
	 * throw an exception
	 */
	@Test(expected = CyclicDependencyRuntimeException.class)
	public void testCyclicSingletonDependencyRuntimeException1() {
		container.define("cycle1", new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle2"));
			}
		});
		container.define("cycle2", new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle1"));
			}
		});
		container.getBean("cycle1");
	}

	/**
	 * Testing that trying to get a prototype bean with cyclic dependency will
	 * throw an exception
	 */
	@Test(expected = CyclicDependencyRuntimeException.class)
	public void testCyclicPrototypeDependencyRuntimeException1() {
		container.define("cycle1", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle2"));
			}
		});
		container.define("cycle2", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle1"));
			}
		});
		container.getBean("cycle1");
	}

	/**
	 * Testing that trying to get a singleton bean with cyclic dependency on
	 * itself
	 */
	@Test(expected = CyclicDependencyRuntimeException.class)
	public void testCyclicSingletonDependencyRuntimeException2() {
		container.define("cycle", new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle"));
			}
		});
		container.getBean("cycle");
	}

	/**
	 * Testing that trying to get a prototype bean with cyclic dependency on
	 * itself
	 */
	@Test(expected = CyclicDependencyRuntimeException.class)
	public void testCyclicPrototypeDependencyRuntimeException2() {
		container.define("cycle", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle"));
			}
		});
		container.getBean("cycle");
	}

	/**
	 * Testing that trying to get two singletons with cyclic dependency well
	 * implemented (using initBean) actually works
	 */
	@Test
	public void testCyclicSingletonDependency() {
		container.define("cycle1", new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle();
			}

			@Override
			protected void initBean(YasdicContainer c, Cycle bean) {
				bean.setCycle((Cycle) c.getBean("cycle2"));
			}
		});
		container.define("cycle2", new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle();
			}

			@Override
			protected void initBean(YasdicContainer c, Cycle bean) {
				bean.setCycle((Cycle) c.getBean("cycle1"));
			}
		});
		Cycle cycle1 = (Cycle) container.getBean("cycle1");
		Cycle cycle2 = (Cycle) container.getBean("cycle2");

		assertTrue("cycle1 should have cycle2 as a dependency", cycle1
				.getCycle() == cycle2);
		assertTrue("cycle2 should have cycle1 as a dependency", cycle2
				.getCycle() == cycle1);

	}

	/**
	 * Testing that trying to get two prototypes with cyclic dependency well
	 * implemented (using initBean), but in a way that only singletons should
	 * use throws an exception.
	 * 
	 * Cyclic dependency cannot be resolved in prototype scope. Should it be
	 * resolved in some way ? Any comment on this is welcome
	 */
	@Test(expected = CyclicDependencyRuntimeException.class)
	public void testCyclicPrototypeDependency() {
		container.define("cycle1", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle();
			}

			@Override
			protected void initBean(YasdicContainer c, Cycle bean) {
				bean.setCycle((Cycle) c.getBean("cycle2"));
			}
		});
		container.define("cycle2", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle();
			}

			@Override
			protected void initBean(YasdicContainer c, Cycle bean) {
				bean.setCycle((Cycle) c.getBean("cycle1"));
			}
		});
		container.getBean("cycle1");
	}

	/**
	 * Testing that injecting a singleton in the container and getting it back
	 * actually works
	 */
	@Test
	public void testSimpleDefine() {
		container.define("prop", "someBean");
		String prop = (String) container.getBean("prop");

		assertEquals("someBean", prop);
	}

	/**
	 * Testing overriding of bean definitions
	 */
	@Test
	public void testOverrideBeans() {

		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		SimpleBean singleton1 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");
		container.define("simpleBeanSingleton1", def);

		SimpleBean singleton2 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		assertFalse(
				"Creating twice a singleton but with redefining it in between should create two instances",
				singleton1 == singleton2);

		container.define("simpleBeanSingleton1", false, def);

		SimpleBean prototype1 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");
		SimpleBean prototype2 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		assertTrue(
				"Overriding singleton definition with a prototype should work OK",
				singleton2 != prototype1 && prototype1 != prototype2);
	}

	/**
	 * Testing that when no bean is found in the container, Yasdic looks in the
	 * parent container if any
	 */
	@Test
	public void testParentContainer() {
		YasdicContainer sonContainer = new YasdicContainer(container);

		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton", def);

		assertTrue(
				"Son container should give bean from parent if finding no definition",
				container.getBean("simpleBeanSingleton") == sonContainer
						.getBean("simpleBeanSingleton"));

		sonContainer.define("simpleBeanSingleton", def);

		assertFalse(
				"Son container should give a different bean if having its own definition of the bean",
				container.getBean("simpleBeanSingleton") == sonContainer
						.getBean("simpleBeanSingleton"));

		container.reset();
		sonContainer.reset();

		try {
			sonContainer.getBean("simpleBeanSingleton");
			fail("sonContainer should not have any more simpleBeanSingleton definition or instance");
		} catch (BeanNotFoundRuntimeException e) {
			assertTrue("Exception should come from the parent",
					e.getCause() instanceof BeanNotFoundRuntimeException);
		}

		container.define("cycle", false, new BeanDef<Cycle>() {
			@Override
			protected Cycle newBean(YasdicContainer c) {
				return new Cycle((Cycle) c.getBean("cycle"));
			}
		});
		try {
			sonContainer.getBean("cycle");
			fail("sonContainer should throw a cyclic dependency exception");
		} catch (CyclicDependencyRuntimeException e) {
			assertTrue("Exception should come from the parent",
					e.getCause() instanceof CyclicDependencyRuntimeException);
		}

	}

	/**
	 * Testing that undefining a bean and trying to get it throws an Exception
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testUndefineBeanNotFoundException() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		container.undefineBean("simpleBeanSingleton1");

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Testing that creating a defined singleton and undefining still enable
	 * caller to get the singleton
	 */
	@Test
	public void testUndefineBeanSingletonCreated() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		SimpleBean singleton1 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		container.undefineBean("simpleBeanSingleton1");

		SimpleBean singleton2 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		assertTrue(singleton1 == singleton2);
	}

	/**
	 * Testing that undefining all beans removes any definition
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testUndefineAllBeansNotFoundException() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		container.undefineAllBeans();

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Testing that undefining all beans with a singleton previously created
	 * still works
	 */
	@Test
	public void testUndefineAllBeansSingletonCreated() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		SimpleBean singleton1 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		container.undefineAllBeans();

		SimpleBean singleton2 = (SimpleBean) container
				.getBean("simpleBeanSingleton1");

		assertTrue(singleton1 == singleton2);
	}

	/**
	 * Testing that reset actually removes bean definition
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testResetNotFoundException() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		container.reset();

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Testing that reset actually removes the singleton
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testResetSingletonCreatedNotFoundException() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		container.getBean("simpleBeanSingleton1");

		container.reset();

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Testing that unstore singleton actually unstore it
	 */
	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testUnstoreSingletonNotFoundException() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		container.getBean("simpleBeanSingleton1");

		container.unstoreSingleton("simpleBeanSingleton1");

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Testing that unstore singleton has no effect on definitions
	 */
	@Test
	public void testUnstoreSingletonNotCreated() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		container.unstoreSingleton("simpleBeanSingleton1");

		container.getBean("simpleBeanSingleton1");
	}

	/**
	 * Test basic dependency injection behavior
	 */
	@Test
	public void testDependencyInjection() {
		container.define("simpleBean", new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		});

		container.define("a", false, new BeanDef<A>() {
			@Override
			protected A newBean(YasdicContainer c) {
				return new A((SimpleBean) c.getBean("simpleBean"));
			}
		});

		A a1 = (A) container.getBean("a");
		A a2 = (A) container.getBean("a");
		SimpleBean simpleBean = (SimpleBean) container.getBean("simpleBean");

		assertTrue(
				"Testing that a bean is really injected and that two prototype beans are injected the same singleton bean",
				a1 != a2 && a1.getDep() == simpleBean
						&& a1.getDep() == a2.getDep());
	}

	/**
	 * Testing that trying to update bean definitions when building beans throws
	 * an exception
	 */
	@Test
	public void testDefiningWhileNewBeanException() {
		container.define("simpleBean", new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {

				try {
					c.define("something", "toSomeValue");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.define("something", new BeanDef<SimpleBean>() {
						@Override
						protected SimpleBean newBean(YasdicContainer c) {
							return null;
						}

					});
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.undefineBean("someBean");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.unstoreSingleton("someBean");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.undefineAllBeans();
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.reset();
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				return new SimpleBean();
			}
		});
		container.getBean("simpleBean");
	}

	/**
	 * Testing that trying to update bean definitions when initializing beans
	 * throws an exception
	 */
	@Test
	public void testDefiningWhileInitBeanException() {
		container.define("simpleBean", new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}

			@Override
			protected void initBean(YasdicContainer c, SimpleBean bean) {
				try {
					c.define("something", "toSomeValue");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.define("something", new BeanDef<SimpleBean>() {
						@Override
						protected SimpleBean newBean(YasdicContainer c) {
							return null;
						}

					});
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.undefineBean("someBean");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.unstoreSingleton("someBean");
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.undefineAllBeans();
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

				try {
					c.reset();
					fail("Call should fail when creating a new bean");
				} catch (YasdicRuntimeException e) {
					assertEquals(YasdicRuntimeException.class, e.getClass());
				}

			}
		});
		container.getBean("simpleBean");
	}

}
