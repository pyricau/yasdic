/**
 * 
 */
package info.piwai.yasdic;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import info.piwai.yasdic.YasdicContainer.BeanDef;
import info.piwai.yasdic.exception.BeanNotFoundRuntimeException;
import info.piwai.yasdic.exception.CyclicDependencyRuntimeException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pricau
 * 
 */
public class YasdicContainerTest {

	YasdicContainer	container;

	public static class SimpleBean {
	}

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
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		container = new YasdicContainer();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		container.reset();
		container = null;
	}

	/**
	 * Test method for
	 * {@link info.piwai.yasdic.YasdicContainer#getBean(java.lang.String)}.
	 */
	@Test
	public void testGetSimpleBean() {
		container.define("simpleBeanSingleton1", new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		});

		container.define("simpleBeanSingleton2", true, new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		});

		container.define("simpleBeanPrototype", false, new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		});

		SimpleBean singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton1");
		SimpleBean singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		assertTrue("Getting twice a singleton should return the same instance", singleton1 == singleton2);

		singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton2");
		singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton2");

		assertTrue("Getting twice a singleton should return the same instance", singleton1 == singleton2);

		SimpleBean prototype1 = (SimpleBean) container.getBean("simpleBeanPrototype");
		SimpleBean prototype2 = (SimpleBean) container.getBean("simpleBeanPrototype");

		assertFalse("Getting twice a singleton should not return the same instance", prototype1 == prototype2);
	}

	@Test(expected = BeanNotFoundRuntimeException.class)
	public void testGetBeanNotFound() {
		container.getBean("someBean");
	}

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

		assertTrue("cycle1 should have cycle2 as a dependency", cycle1.getCycle() == cycle2);
		assertTrue("cycle2 should have cycle1 as a dependency", cycle2.getCycle() == cycle1);

	}

	// Cyclic dependency cannot be resolved in prototype scope
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

	@Test
	public void testSimpleDefine() {
		container.define("prop", "someBean");
		String prop = (String) container.getBean("prop");

		assertEquals("someBean", prop);
	}

	@Test
	public void testOverrideBeans() {

		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);

		SimpleBean singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton1");
		container.define("simpleBeanSingleton1", def);

		SimpleBean singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		assertFalse("Creating twice a singleton but with redefining it in between should create two instances", singleton1 == singleton2);

		container.define("simpleBeanSingleton1", false, def);

		SimpleBean prototype1 = (SimpleBean) container.getBean("simpleBeanSingleton1");
		SimpleBean prototype2 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		assertTrue("Overriding singleton definition with a prototype should work OK", singleton2 != prototype1 && prototype1 != prototype2);
	}

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

		assertTrue("Son container should give bean from parent if finding no definition",
				container.getBean("simpleBeanSingleton") == sonContainer.getBean("simpleBeanSingleton"));

		sonContainer.define("simpleBeanSingleton", def);

		assertFalse("Son container should give a different bean if having its own definition of the bean", container
				.getBean("simpleBeanSingleton") == sonContainer.getBean("simpleBeanSingleton"));

		container.reset();
		sonContainer.reset();

		try {
			sonContainer.getBean("simpleBeanSingleton");
			fail("sonContainer should not have any more simpleBeanSingleton definition or instance");
		} catch (BeanNotFoundRuntimeException e) {
			assertTrue("Exception should come from the parent", e.getCause() instanceof BeanNotFoundRuntimeException);
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
			assertTrue("Exception should come from the parent", e.getCause() instanceof CyclicDependencyRuntimeException);
		}

	}

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

	@Test
	public void testUndefineBeanSingletonCreated() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		SimpleBean singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		container.undefineBean("simpleBeanSingleton1");

		SimpleBean singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		assertTrue(singleton1 == singleton2);
	}

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

	@Test
	public void testUndefineAllBeansSingletonCreated() {
		BeanDef<SimpleBean> def = new BeanDef<SimpleBean>() {
			@Override
			protected SimpleBean newBean(YasdicContainer c) {
				return new SimpleBean();
			}
		};

		container.define("simpleBeanSingleton1", def);
		SimpleBean singleton1 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		container.undefineAllBeans();

		SimpleBean singleton2 = (SimpleBean) container.getBean("simpleBeanSingleton1");

		assertTrue(singleton1 == singleton2);
	}

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

}
