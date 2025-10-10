package com.liferay.portal.spring.hibernate;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.resource.beans.container.spi.BeanContainer;
import org.hibernate.resource.beans.container.spi.ContainedBean;
import org.hibernate.resource.beans.spi.BeanInstanceProducer;
import org.hibernate.type.spi.TypeBootstrapContext;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ConcurrentReferenceHashMap;

public final class SpringBeanContainer implements BeanContainer {
	private static final Log logger = LogFactory.getLog(
		SpringBeanContainer.class);
	private final ConfigurableListableBeanFactory beanFactory;
	private final Map<Object, SpringContainedBean<?>> beanCache = new ConcurrentReferenceHashMap();

	public SpringBeanContainer(ConfigurableListableBeanFactory beanFactory) {
		Assert.notNull(beanFactory, "ConfigurableListableBeanFactory is required");
		this.beanFactory = beanFactory;
	}

	public <B> ContainedBean<B> getBean(Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
		SpringContainedBean bean;
		if (lifecycleOptions.canUseCachedReferences()) {
			bean = (SpringContainedBean)this.beanCache.get(beanType);
			if (bean == null) {
				bean = this.createBean(beanType, lifecycleOptions, fallbackProducer);
				this.beanCache.put(beanType, bean);
			}
		} else {
			bean = this.createBean(beanType, lifecycleOptions, fallbackProducer);
		}

		return bean;
	}

	public <B> ContainedBean<B> getBean(String name, Class<B> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
		SpringContainedBean bean;
		if (lifecycleOptions.canUseCachedReferences()) {
			bean = (SpringContainedBean)this.beanCache.get(name);
			if (bean == null) {
				bean = this.createBean(name, beanType, lifecycleOptions, fallbackProducer);
				this.beanCache.put(name, bean);
			}
		} else {
			bean = this.createBean(name, beanType, lifecycleOptions, fallbackProducer);
		}

		return bean;
	}

	public void stop() {
		this.beanCache.values().forEach(SpringContainedBean::destroyIfNecessary);
		this.beanCache.clear();
	}

	private SpringContainedBean<?> createBean(Class<?> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
		try {
			if (lifecycleOptions.useJpaCompliantCreation()) {
				Object var10002 = this.beanFactory.createBean(beanType);
				ConfigurableListableBeanFactory var10003 = this.beanFactory;
				Objects.requireNonNull(var10003);
				return new SpringContainedBean(var10002, var10003::destroyBean);
			} else {
				return new SpringContainedBean(this.beanFactory.getBean(beanType));
			}
		} catch (BeansException var7) {
			Log var10000;
			String var10001;
			if (logger.isDebugEnabled()) {
				var10000 = logger;
				var10001 = String.valueOf(beanType);
				var10000.debug("Falling back to Hibernate's default producer after bean creation failure for " + var10001 + ": " + String.valueOf(var7));
			}

			try {
				return new SpringContainedBean(fallbackProducer.produceBeanInstance(beanType));
			} catch (RuntimeException var6) {
				if (var7 instanceof BeanCreationException) {
					if (logger.isDebugEnabled()) {
						var10000 = logger;
						var10001 = String.valueOf(beanType);
						var10000.debug("Fallback producer failed for " + var10001 + ": " + String.valueOf(var6));
					}

					throw var7;
				} else {
					throw var6;
				}
			}
		}
	}

	private SpringContainedBean<?> createBean(String name, Class<?> beanType, BeanContainer.LifecycleOptions lifecycleOptions, BeanInstanceProducer fallbackProducer) {
		try {
			if (lifecycleOptions.useJpaCompliantCreation()) {
				Object bean = null;
				if (fallbackProducer instanceof TypeBootstrapContext) {
					bean = fallbackProducer.produceBeanInstance(name, beanType);
				}

				if (this.beanFactory.containsBean(name)) {
					if (bean == null) {
						bean = this.beanFactory.autowire(beanType, 3, false);
					}

					this.beanFactory.autowireBeanProperties(bean, 0, false);
					this.beanFactory.applyBeanPropertyValues(bean, name);
					bean = this.beanFactory.initializeBean(bean, name);
					return new SpringContainedBean(bean, (beanInstance) -> {
						this.beanFactory.destroyBean(name, beanInstance);
					});
				} else {
					ConfigurableListableBeanFactory var10003;
					if (bean != null) {
						this.beanFactory.autowireBeanProperties(bean, 0, false);
						bean = this.beanFactory.initializeBean(bean, name);
						var10003 = this.beanFactory;
						Objects.requireNonNull(var10003);
						return new SpringContainedBean(bean, var10003::destroyBean);
					} else {
						Object var10002 = this.beanFactory.createBean(beanType);
						var10003 = this.beanFactory;
						Objects.requireNonNull(var10003);
						return new SpringContainedBean(var10002, var10003::destroyBean);
					}
				}
			} else {
				return this.beanFactory.containsBean(name) ? new SpringContainedBean(this.beanFactory.getBean(name, beanType)) : new SpringContainedBean(this.beanFactory.getBean(beanType));
			}
		} catch (BeansException var8) {
			Log var10000;
			String var10001;
			if (logger.isDebugEnabled()) {
				var10000 = logger;
				var10001 = String.valueOf(beanType);
				var10000.debug("Falling back to Hibernate's default producer after bean creation failure for " + var10001 + " with name '" + name + "': " + String.valueOf(var8));
			}

			try {
				return new SpringContainedBean(fallbackProducer.produceBeanInstance(name, beanType));
			} catch (RuntimeException var7) {
				if (var8 instanceof BeanCreationException) {
					if (logger.isDebugEnabled()) {
						var10000 = logger;
						var10001 = String.valueOf(beanType);
						var10000.debug("Fallback producer failed for " + var10001 + " with name '" + name + "': " + String.valueOf(var7));
					}

					throw var8;
				} else {
					throw var7;
				}
			}
		}
	}

	private static final class SpringContainedBean<B> implements ContainedBean<B> {
		private final B beanInstance;
		@Nullable
		private Consumer<B> destructionCallback;

		public SpringContainedBean(B beanInstance) {
			this.beanInstance = beanInstance;
		}

		public SpringContainedBean(B beanInstance, Consumer<B> destructionCallback) {
			this.beanInstance = beanInstance;
			this.destructionCallback = destructionCallback;
		}

		public B getBeanInstance() {
			return this.beanInstance;
		}

		public void destroyIfNecessary() {
			if (this.destructionCallback != null) {
				this.destructionCallback.accept(this.beanInstance);
			}

		}
	}
}

