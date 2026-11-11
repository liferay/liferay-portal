/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.aop;

import com.liferay.portal.dao.orm.hibernate.SessionFactoryImpl;
import com.liferay.portal.dao.orm.hibernate.VerifySessionFactoryWrapper;
import com.liferay.portal.kernel.aop.SkipAop;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.service.persistence.impl.BasePersistenceImpl;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.spring.bean.BeanReferenceAnnotationBeanPostProcessor;
import com.liferay.portal.spring.configurator.ConfigurableApplicationContextConfigurator;
import com.liferay.portal.spring.hibernate.PortalTransactionManager;
import com.liferay.portal.spring.hibernate.PortletHibernateConfiguration;
import com.liferay.portal.spring.hibernate.PortletTransactionManager;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionInvokerImpl;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Shuyang Zhou
 */
public class AopConfigurableApplicationContextConfigurator
	implements ConfigurableApplicationContextConfigurator {

	@Override
	public void configure(
		ConfigurableApplicationContext configurableApplicationContext) {

		configurableApplicationContext.addBeanFactoryPostProcessor(
			new AopBeanFactoryPostProcessor(
				configurableApplicationContext.getClassLoader()));
	}

	public static class ServiceBeanMatcher implements BeanMatcher {

		@Override
		public boolean match(Class<?> beanClass, String beanName) {
			if (beanName.endsWith(_SERVICE_SUFFIX) &&
				(beanClass.getAnnotation(SkipAop.class) == null)) {

				return true;
			}

			return false;
		}

		private static final String _SERVICE_SUFFIX = "Service";

	}

	private static class AopBeanFactoryPostProcessor
		implements BeanFactoryPostProcessor {

		@Override
		public void postProcessBeanFactory(
				ConfigurableListableBeanFactory configurableListableBeanFactory)
			throws BeansException {

			if (configurableListableBeanFactory.getBeanDefinitionCount() == 0) {

				// Protection for the those theme wars with no Spring XML files

				return;
			}

			configurableListableBeanFactory.addBeanPostProcessor(
				new BeanReferenceAnnotationBeanPostProcessor(
					configurableListableBeanFactory));

			DefaultSingletonBeanRegistry defaultSingletonBeanRegistry =
				(DefaultSingletonBeanRegistry)configurableListableBeanFactory;

			DefaultTransactionExecutor defaultTransactionExecutor =
				new DefaultTransactionExecutor(
					_getPlatformTransactionManager(
						configurableListableBeanFactory));

			configurableListableBeanFactory.registerSingleton(
				"transactionExecutor", defaultTransactionExecutor);

			// Portal Spring context only

			if (PortalClassLoaderUtil.isPortalClassLoader(_classLoader)) {
				TransactionInvokerImpl transactionInvokerImpl =
					new TransactionInvokerImpl();

				transactionInvokerImpl.setTransactionExecutor(
					defaultTransactionExecutor);

				TransactionInvokerUtil transactionInvokerUtil =
					new TransactionInvokerUtil();

				transactionInvokerUtil.setTransactionInvoker(
					transactionInvokerImpl);
			}

			// Service AOP

			ServiceBeanAutoProxyCreator serviceBeanAutoProxyCreator =
				new ServiceBeanAutoProxyCreator(
					_classLoader, defaultTransactionExecutor);

			defaultSingletonBeanRegistry.registerDisposableBean(
				"serviceBeanAutoProxyCreatorDestroyer",
				serviceBeanAutoProxyCreator::destroy);

			configurableListableBeanFactory.addBeanPostProcessor(
				serviceBeanAutoProxyCreator);
		}

		private AopBeanFactoryPostProcessor(ClassLoader classLoader) {
			_classLoader = classLoader;
		}

		private PlatformTransactionManager _getPlatformTransactionManager(
			ConfigurableListableBeanFactory configurableListableBeanFactory) {

			DataSource liferayDataSource =
				configurableListableBeanFactory.getBean(
					"liferayDataSource", DataSource.class);

			SessionFactoryImplementor liferayHibernateSessionFactory = null;

			DefaultSingletonBeanRegistry defaultSingletonBeanRegistry =
				(DefaultSingletonBeanRegistry)configurableListableBeanFactory;

			if (PortalClassLoaderUtil.isPortalClassLoader(_classLoader)) {
				liferayHibernateSessionFactory =
					configurableListableBeanFactory.getBean(
						"liferayHibernateSessionFactory",
						SessionFactoryImplementor.class);
			}
			else {
				PortletHibernateConfiguration portletHibernateConfiguration =
					new PortletHibernateConfiguration(
						_classLoader, liferayDataSource);

				portletHibernateConfiguration.afterPropertiesSet();

				liferayHibernateSessionFactory =
					(SessionFactoryImplementor)
						portletHibernateConfiguration.getObject();

				defaultSingletonBeanRegistry.registerDisposableBean(
					"liferayHibernateSessionFactoryDestroyer",
					liferayHibernateSessionFactory::close);
			}

			SessionFactoryImpl sessionFactoryImpl = new SessionFactoryImpl();

			sessionFactoryImpl.setSessionFactoryClassLoader(_classLoader);
			sessionFactoryImpl.setSessionFactoryImplementor(
				liferayHibernateSessionFactory);

			defaultSingletonBeanRegistry.registerDisposableBean(
				"liferaySessionFactoryDestroyer", sessionFactoryImpl::destroy);

			SessionFactory sessionFactory =
				VerifySessionFactoryWrapper.createVerifySessionFactoryWrapper(
					sessionFactoryImpl);

			configurableListableBeanFactory.addBeanPostProcessor(
				new BasePersistenceInjectionBeanPostProcessor(
					liferayDataSource, sessionFactory));

			if (PortalClassLoaderUtil.isPortalClassLoader(_classLoader)) {
				return configurableListableBeanFactory.getBean(
					"liferayTransactionManager",
					PlatformTransactionManager.class);
			}

			if (InfrastructureUtil.getDataSource() == liferayDataSource) {
				return new PortletTransactionManager(
					(PortalTransactionManager)
						InfrastructureUtil.getTransactionManager(),
					liferayHibernateSessionFactory);
			}

			return TransactionManagerFactory.createTransactionManager(
				liferayDataSource, liferayHibernateSessionFactory);
		}

		private final ClassLoader _classLoader;

	}

	private static class BasePersistenceInjectionBeanPostProcessor
		implements BeanPostProcessor {

		@Override
		public Object postProcessAfterInitialization(
			Object bean, String beanName) {

			return bean;
		}

		@Override
		public Object postProcessBeforeInitialization(
			Object bean, String beanName) {

			if (bean instanceof BasePersistenceImpl) {
				BasePersistenceImpl<?, ?> basePersistenceImpl =
					(BasePersistenceImpl<?, ?>)bean;

				basePersistenceImpl.setDataSource(_dataSource);
				basePersistenceImpl.setSessionFactory(_sessionFactory);
			}

			return bean;
		}

		private BasePersistenceInjectionBeanPostProcessor(
			DataSource dataSource, SessionFactory sessionFactory) {

			_dataSource = dataSource;
			_sessionFactory = sessionFactory;
		}

		private final DataSource _dataSource;
		private final SessionFactory _sessionFactory;

	}

	private static class ServiceBeanAutoProxyCreator
		extends BaseServiceBeanAutoProxyCreator {

		public void destroy() {
			for (AopInvocationHandler aopInvocationHandler :
					_aopInvocationHandlers) {

				AopCacheManager.destroy(aopInvocationHandler);
			}
		}

		@Override
		protected AopInvocationHandler createAopInvocationHandler(Object bean) {
			AopInvocationHandler aopInvocationHandler = AopCacheManager.create(
				bean, _transactionExecutor);

			_aopInvocationHandlers.add(aopInvocationHandler);

			return aopInvocationHandler;
		}

		private ServiceBeanAutoProxyCreator(
			ClassLoader classLoader, TransactionExecutor transactionExecutor) {

			super(new ServiceBeanMatcher(), classLoader);

			_transactionExecutor = transactionExecutor;
		}

		private final List<AopInvocationHandler> _aopInvocationHandlers =
			new ArrayList<>();
		private final TransactionExecutor _transactionExecutor;

	}

}