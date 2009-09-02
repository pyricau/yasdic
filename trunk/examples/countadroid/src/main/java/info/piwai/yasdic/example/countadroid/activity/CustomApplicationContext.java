package info.piwai.yasdic.example.countadroid.activity;

import info.piwai.yasdic.ApplicationContainerUtil;
import info.piwai.yasdic.BeanFactoryHolder;
import info.piwai.yasdic.Container;
import info.piwai.yasdic.BeanFactoryHolder.BeanFactory;
import info.piwai.yasdic.BeanFactoryHolder.ContextInjector;
import info.piwai.yasdic.BeanFactoryHolder.OnNewBeanListener;
import info.piwai.yasdic.example.countadroid.service.ICounter;
import info.piwai.yasdic.example.countadroid.service.LocalCounter;
import info.piwai.yasdic.example.countadroid.service.RemoteCounter;
import info.piwai.yasdic.example.countadroid.tasks.CountTask;
import info.piwai.yasdic.example.countadroid.tasks.IncrementTask;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Application;
import android.util.Log;

public class CustomApplicationContext extends Application {

	@Override
	public void onCreate() {

		OnNewBeanListener onNewBeanListener = new OnNewBeanListener() {
			@Override
			public void onAfterNewBean(String id, Object bean) {
				StringBuilder sb = new StringBuilder("******** Bean [");
				sb.append(id).append("] created, instance of [").append(bean.getClass().getSimpleName()).append("]");
				Log.d("OnNewBeanListener", sb.toString());
			}
		};

		BeanFactoryHolder beanFactoryHolder = new BeanFactoryHolder().onNewBeanListener(onNewBeanListener);

		registerApplicationBeanFactories(beanFactoryHolder);
		ApplicationContainerUtil.registerApplicationBeanFactoryHolder(beanFactoryHolder);

		BeanFactoryHolder beanFactoryHolder2 = new BeanFactoryHolder().onNewBeanListener(onNewBeanListener).contextInjector(
				new ContextInjector<StartActivity>() {
					@Override
					public void inject(StartActivity context, Container c) {
						context.setContainer2(c);
					}
				});
		registerStartActivityBeanFactories(beanFactoryHolder2);
		ApplicationContainerUtil.registerBeanFactoryHolder(StartActivity.class, beanFactoryHolder2);

		ApplicationContainerUtil.manageApplicationContext(this);

	}

	private void registerApplicationBeanFactories(BeanFactoryHolder holder) {
		/*
		 * Defining the counter url for the remoteCounter. You may change this
		 * value if your remote counting script is not on the same path.
		 * 
		 * Please note that 10.0.2.2 is a virtual IP to represent the localhost
		 * on your computer.
		 */
		holder.registerConstant("counterUrl", "http://10.0.2.2/countadroid/count.php");

		holder.registerLazySingleton("localCounter", new BeanFactory<LocalCounter>() {
			@Override
			protected LocalCounter newBean(Container c) {
				return new LocalCounter();
			}
		});

		holder.registerLazySingleton("remoteCounter", new BeanFactory<RemoteCounter>() {
			@Override
			protected RemoteCounter newBean(Container c) {
				return new RemoteCounter();
			}

			@Override
			protected void initBean(Container c, RemoteCounter bean) {
				bean.setCounterURL(c.getBean("counterUrl", String.class));
				bean.setHttpClient((HttpClient) c.getBean("counterHttpClient"));
			}
		});

		holder.registerLazySingleton("counterHttpClient", new BeanFactory<HttpClient>() {
			@Override
			protected HttpClient newBean(Container c) {
				return new DefaultHttpClient();
			}
		});
	}

	private void registerStartActivityBeanFactories(BeanFactoryHolder holder) {
		holder.registerPrototype("incrementTask", new BeanFactory<IncrementTask>() {
			@Override
			protected IncrementTask newBean(Container c) {
				return new IncrementTask();
			}

			@Override
			protected void initBean(Container c, IncrementTask bean) {
				bean.setCounter((ICounter) c.getBean((String) c.getBean("counterId")));
				bean.setActivity(c.getCurrentContext(StartActivity.class));
			}
		});
		holder.registerPrototype("countTask", new BeanFactory<CountTask>() {
			@Override
			protected CountTask newBean(Container c) {
				return new CountTask();
			}

			@Override
			protected void initBean(Container c, CountTask bean) {
				bean.setCounter((ICounter) c.getBean((String) c.getBean("counterId")));
				bean.setActivity(c.getCurrentContext(StartActivity.class));
			}
		});
	}

}
