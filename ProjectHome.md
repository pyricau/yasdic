29/10/2009 : I will not improve this project anymore. If you are looking for a good Dependency Injection framework on Android, I suggest you to try out Guice no-aop. I am currently using the excellent robo-guice (http://code.google.com/p/robo-guice/) framework, which still has no stable release as of today.

**Yasdic 1.0 release is available!** Feel free to [download](http://code.google.com/p/yasdic/downloads/list) it!

---

Yasdic stands for _Yet Another Small Dependency Injection Container_. And that's what it is: nothing more then one class, one inner class, and three exceptions.

It was first designed to enable dependency injection on Android applications without impacting performances, but it actually may be used anywhere.

**What is dependency injection ?** [Here is a good explanation](http://martinfowler.com/articles/injection.html).

Yasdic uses a String unique **id** to identify each bean.

It can easily deal with **singleton** and **prototype** scopes, **cyclic dependency**, and **container hierarchy**.

The beans are **lazily created**, i.e. a bean and its dependencies are created only when you call the getBean("myBean") method on the container.

Yasdic provides dependency injection **without using any reflection mechanism**. Reflection should be avoided when targeting performances on mobile devices.

I created two example projects on the svn to help you getting started with Yasdic: smallmvc (Java basic application following the MVC pattern), and **countadroid** (**Android** counter application). Right [there](http://code.google.com/p/yasdic/source/browse/#svn/trunk/examples).

**You are free to checkout the svn and start using Yasdic** [here](http://code.google.com/p/yasdic/source/checkout).

I am already using Yasdic in some Android applications (have a look at [SugaDroid](http://code.google.com/p/sugadroid/source/browse/#svn/trunk/code)).

You are welcome to review the code and give any feedback on this project. If you use Yasdic in your project, please send me a mail and I will create a dedicated page with the projects list.

This project was started on the 20th of June 2009.

---

## Quick usage ##

Definition of a "myController" bean that has two constructor dependencies ("myService" and "myView"):
```
        container.define("myController", new BeanDef<IController>() {
            public IController newBean(YasdicContainer c) {
                return new ControllerImpl((IService) c.getBean("myService"), (IView) c.getBean("myView"));
            }
        });
```
And to actually get the bean created and use it:
```
        //The "myService", "myView" and "myController" beans are created and injected.
        IController controller = (IController) container.getBean("myController"); 
```

---

## Pimple & Yasdic ##
Yasdic is actually quite inspired from Pimple, written in PHP: http://github.com/fabpot/Pimple

Here is a quick comparison on how to do about the same thing:
### In PHP using Pimple ###
```
$container = new Pimple();

$container->cookie_name = 'SESSION_ID';
$container->storage_class = 'SessionStorage';

$container->storage = function ($c)
{
  return new $c->storage_class($c->cookie_name);
};

$container->user = $c->asShared(function ($c)
{
  return new User($c->storage);
});

$user = $container->user; 
```
### In Java using Yasdic ###
```
YasdicContainer container = new YasdicContainer();

container.define("cookie_name", "SESSION_ID");
container.define("storage_class", SessionStorage.class);

container.define("storage", false, new BeanDef<IStorage>() {
  protected IStorage newBean(YasdicContainer c) {
    try { IStorage storage = (Class<IStorage>) c.getBean("storage_class").newInstance();
          storage.setCookieName((String) c.getBean("cookie_name"));
          return storage;
    } catch (Exception e) {throw new RuntimeException(e);}
  }
});

container.define("user", new BeanDef<User>() {
  protected User newBean(YasdicContainer c) {
    return new User((IStorage) c.getBean("storage"));
  }
});

User user = (User) container.getBean("user"); 
```

---

## Some more examples ##

What about cyclic dependency ? Let say we have a "myCycle1" bean that needs a "myCycle2", and a "myCycle2" bean that needs a "myCycle1". You might try to define it this way:
```
        container.define("myCycle1", new BeanDef<Cycle>() {
            public Cycle newBean(YasdicContainer c) {
                return new Cycle((Cycle)c.getBean("myCycle2");
            }
        });

        container.define("myCycle2", new BeanDef<Cycle>() {
            public Cycle newBean(YasdicContainer c) {
                return new Cycle((Cycle)c.getBean("myCycle1"));
            }
        });

        container.getBean("myCycle1"); // Throws a CyclicDependencyRuntimeException
```
This would lead to a CyclicDependencyRuntimeException (if Yasdic didn't detect cyclic dependencies, you would get a StackOverflowError).


To avoid cyclic dependencies, you may use setter injection instead of constructor injection, combined with using the initBean method, to implement late injection:
```
        container.define("myCycle1", new BeanDef<Cycle>() {
            public Cycle newBean(YasdicContainer c) {
                return new Cycle();
            }
            public void initBean(YasdicContainer c, Cycle bean) {
                bean.setCycle((Cycle)c.getBean("myCycle2"));
            }
        });

        container.define("myCycle2", new BeanDef<Cycle>() {
            public Cycle newBean(YasdicContainer c) {
                return new Cycle();
            }
            public void initBean(YasdicContainer c, Cycle bean) {
                bean.setCycle((Cycle)c.getBean("myCycle1"));
            }
        });

        container.getBean("myCycle1"); // No problem
```

Bean are defined at compile time (since it is done using anonymous classes), but you can still make the dependency injection dynamic.

Let say we have a myController bean that requires a IView dependency, but the actual implementation (ContactView or CrazyView) that should be used depends on a runtime condition.

You could define one or the other depending on the condition:
```
        container.define("myController", new BeanDef<IController>() {
            public IController newBean(YasdicContainer c) {
                return new ControllerImpl((IView) c.getBean("myView"));
            }
        });

        if(someCondition) {
          container.define("myView", new BeanDef<IView>() {
              public IView newBean(YasdicContainer c) {
                  return new ContactView();
              }
          });
        } else {
          container.define("myView", new BeanDef<IView>() {
              public IView newBean(YasdicContainer c) {
                  return new CrazyView();
              }
          });
        }

        IController controller = (IController) container.getBean("myController");
```

You could also define both, and require one or the other in the controller, depending on a viewName parameter:
```
        container.define("myController", new BeanDef<IController>() {
            public IController newBean(YasdicContainer c) {
                return new ControllerImpl((IView) c.getBean((String)c.getBean("viewName")));
            }
        });

        container.define("contactView", new BeanDef<IView>() {
            public IView newBean(YasdicContainer c) {
                return new ContactView();
            }
        });

        container.define("crazyView", new BeanDef<IView>() {
            public IView newBean(YasdicContainer c) {
                return new CrazyView();
            }
        });

        if(someCondition) {
          //This is how we inject a singleton bean already created in the container
          //It can easily be used to define property values
          container.define("viewName", "contactView");
        } else {
          container.define("viewName", "crazyView");
        }

        IController controller = (IController) container.getBean("myController");
```

You could finally define a myView bean with the implementation depending on a condition parameter:
```
        container.define("myController", new BeanDef<IController>() {
            public IController newBean(YasdicContainer c) {
                return new ControllerImpl((IView) c.getBean("myView"));
            }
        });

        container.define("myView", new BeanDef<IView>() {
            public IView newBean(YasdicContainer c) {
                if ((Boolean)c.getBean("someCondition")) {
                  return new ContactView();
                } else {
                  return new CrazyView();
                }                
            }
        });

        container.define("someCondition", (Boolean) someCondition);

        IController controller = (IController) container.getBean("myController");
```