/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.spring.extender.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.concurrent.DefaultNoticeableFuture;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.dao.orm.hibernate.SessionFactoryImpl;
import com.liferay.portal.dao.orm.hibernate.VerifySessionFactoryWrapper;
import com.liferay.portal.kernel.concurrent.SystemExecutorServiceUtil;
import com.liferay.portal.kernel.dao.jdbc.DataSourceFactoryUtil;
import com.liferay.portal.kernel.dao.orm.SessionFactory;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.InfrastructureUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.spring.extender.internal.jdbc.DataSourceUtil;
import com.liferay.portal.spring.extender.internal.loader.ModuleAggregareClassLoader;
import com.liferay.portal.spring.extender.internal.upgrade.release.SchemaCreatorImpl;
import com.liferay.portal.spring.hibernate.PortalTransactionManager;
import com.liferay.portal.spring.hibernate.PortletHibernateConfiguration;
import com.liferay.portal.spring.hibernate.PortletTransactionManager;
import com.liferay.portal.spring.transaction.DefaultTransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionExecutor;
import com.liferay.portal.spring.transaction.TransactionManagerFactory;
import com.liferay.portal.upgrade.release.SchemaCreator;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.function.Supplier;

import javax.sql.DataSource;

import org.hibernate.engine.spi.SessionFactoryImplementor;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Preston Crary
 */
@Component(service = {})
public class LiferayServiceExtender
	implements BundleTrackerCustomizer
		<Supplier<LiferayServiceExtender.LiferayServiceExtension>> {

	@Override
	public Supplier<LiferayServiceExtension> addingBundle(
		Bundle bundle, BundleEvent bundleEvent) {

		Dictionary<String, String> headers = bundle.getHeaders(
			StringPool.BLANK);

		if ((headers.get("Liferay-Service") == null) ||
			(headers.get("Liferay-Spring-Context") != null)) {

			return null;
		}

		ExecutorService executorService =
			SystemExecutorServiceUtil.getExecutorService();

		DefaultNoticeableFuture<LiferayServiceExtension>
			defaultNoticeableFuture = new DefaultNoticeableFuture<>(
				() -> {
					LiferayServiceExtension liferayServiceExtension =
						new LiferayServiceExtension(bundle);

					liferayServiceExtension.start();

					return liferayServiceExtension;
				});

		executorService.submit(defaultNoticeableFuture);

		return () -> {
			try {
				return defaultNoticeableFuture.get();
			}
			catch (InterruptedException interruptedException) {
				_log.error(interruptedException);
			}
			catch (ExecutionException executionException) {
				_log.error(executionException.getCause());
			}

			return null;
		};
	}

	@Override
	public void modifiedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		Supplier<LiferayServiceExtension> supplier) {
	}

	@Override
	public void removedBundle(
		Bundle bundle, BundleEvent bundleEvent,
		Supplier<LiferayServiceExtension> supplier) {

		LiferayServiceExtension liferayServiceExtension = supplier.get();

		if (liferayServiceExtension != null) {
			liferayServiceExtension.destroy();
		}
	}

	public class LiferayServiceExtension {

		public void destroy() {
			for (ServiceRegistration<?> serviceRegistration :
					_serviceRegistrations) {

				serviceRegistration.unregister();
			}

			_sessionFactoryImplementorDCLSingleton.destroy(
				SessionFactoryImplementor::close);

			if (InfrastructureUtil.getDataSource() != _dataSource) {
				try {
					DataSourceFactoryUtil.destroyDataSource(_dataSource);
				}
				catch (Exception exception) {
					_log.error(
						"Unable to destroy external data source " + _dataSource,
						exception);
				}
			}
		}

		public void start() throws Exception {
			BundleWiring extendeeBundleWiring = _extendeeBundle.adapt(
				BundleWiring.class);

			ClassLoader extendeeClassLoader =
				extendeeBundleWiring.getClassLoader();

			_dataSource = DataSourceUtil.getDataSource(extendeeClassLoader);

			BundleContext extendeeBundleContext =
				_extendeeBundle.getBundleContext();

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					SchemaCreator.class,
					new SchemaCreatorImpl(_extendeeBundle, _dataSource), null));

			ClassLoader classLoader = new ModuleAggregareClassLoader(
				extendeeClassLoader, _extendeeBundle.getSymbolicName());

			Supplier<SessionFactoryImplementor>
				sessionFactoryImplementorSupplier =
					() -> _sessionFactoryImplementorDCLSingleton.getSingleton(
						() -> {
							PortletHibernateConfiguration
								portletHibernateConfiguration =
									new PortletHibernateConfiguration(
										classLoader, _dataSource);

							portletHibernateConfiguration.afterPropertiesSet();

							return (SessionFactoryImplementor)
								portletHibernateConfiguration.getObject();
						});

			DefaultTransactionExecutor defaultTransactionExecutor =
				_getTransactionExecutor(
					_dataSource, sessionFactoryImplementorSupplier);

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					TransactionExecutor.class, defaultTransactionExecutor,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					DataSource.class, _dataSource,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));

			SessionFactoryImpl sessionFactoryImpl = new SessionFactoryImpl();

			sessionFactoryImpl.setSessionFactoryClassLoader(classLoader);
			sessionFactoryImpl.setSessionFactoryImplementorSupplier(
				sessionFactoryImplementorSupplier);

			SessionFactory sessionFactory =
				VerifySessionFactoryWrapper.createVerifySessionFactoryWrapper(
					sessionFactoryImpl);

			_serviceRegistrations.add(
				extendeeBundleContext.registerService(
					SessionFactory.class, sessionFactory,
					MapUtil.singletonDictionary(
						"origin.bundle.symbolic.name",
						_extendeeBundle.getSymbolicName())));
		}

		private LiferayServiceExtension(Bundle extendeeBundle) {
			_extendeeBundle = extendeeBundle;
		}

		private DefaultTransactionExecutor _getTransactionExecutor(
			DataSource liferayDataSource,
			Supplier<SessionFactoryImplementor>
				sessionFactoryImplementorSupplier) {

			PlatformTransactionManager platformTransactionManager = null;

			if (InfrastructureUtil.getDataSource() == liferayDataSource) {
				platformTransactionManager = new PortletTransactionManager(
					(PortalTransactionManager)
						InfrastructureUtil.getTransactionManager(),
					sessionFactoryImplementorSupplier);
			}
			else {
				platformTransactionManager =
					TransactionManagerFactory.createTransactionManager(
						liferayDataSource,
						sessionFactoryImplementorSupplier.get());
			}

			return new DefaultTransactionExecutor(platformTransactionManager);
		}

		private DataSource _dataSource;
		private final Bundle _extendeeBundle;
		private final List<ServiceRegistration<?>> _serviceRegistrations =
			new ArrayList<>();
		private final DCLSingleton<SessionFactoryImplementor>
			_sessionFactoryImplementorDCLSingleton = new DCLSingleton<>();

	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleTracker = new BundleTracker<>(
			bundleContext, Bundle.ACTIVE, this);

		DependencyManagerSyncUtil.registerSyncFutureTask(
			new FutureTask<>(
				() -> {
					_bundleTracker.open();

					Map<Bundle, Supplier<LiferayServiceExtension>> map =
						_bundleTracker.getTracked();

					for (Supplier<LiferayServiceExtension> supplier :
							map.values()) {

						supplier.get();
					}

					return null;
				}),
			LiferayServiceExtender.class.getName() + "-BundleTrackerOpener");
	}

	@Deactivate
	protected void deactivate() {
		_bundleTracker.close();
	}

	private static final Log _log = LogFactoryUtil.getLog(
		LiferayServiceExtender.class);

	private BundleTracker<Supplier<LiferayServiceExtension>> _bundleTracker;

}