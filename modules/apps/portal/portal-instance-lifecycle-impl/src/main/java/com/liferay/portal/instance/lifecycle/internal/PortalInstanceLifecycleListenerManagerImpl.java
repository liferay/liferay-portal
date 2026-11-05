/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.instance.lifecycle.internal;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.io.Deserializer;
import com.liferay.petra.io.Serializer;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.aop.AopService;
import com.liferay.portal.events.StartupHelperUtil;
import com.liferay.portal.instance.lifecycle.EveryNodeEveryStartup;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.Clusterable;
import com.liferay.portal.kernel.instance.lifecycle.PortalInstanceLifecycleManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.module.framework.service.IdentifiableOSGiService;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.service.CompanyLocalService;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.TransactionConfig;
import com.liferay.portal.kernel.transaction.TransactionInvokerUtil;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import java.nio.ByteBuffer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(service = AopService.class)
@Transactional(propagation = Propagation.REQUIRED)
public class PortalInstanceLifecycleListenerManagerImpl
	implements AopService, IdentifiableOSGiService,
			   PortalInstanceLifecycleManager {

	@Override
	public String getOSGiServiceIdentifier() {
		return PortalInstanceLifecycleListenerManagerImpl.class.getName();
	}

	@Clusterable
	@Override
	public void preregisterCompany(Company company) {
		for (PortalInstanceLifecycleListener portalInstanceLifecycleListener :
				_serviceTrackerList) {

			preregisterCompany(portalInstanceLifecycleListener, company);
		}
	}

	@Clusterable
	@Override
	public void preunregisterCompany(Company company) {
		for (PortalInstanceLifecycleListener portalInstanceLifecycleListener :
				_serviceTrackerList) {

			preunregisterCompany(portalInstanceLifecycleListener, company);
		}
	}

	@Clusterable
	@Override
	public void registerCompany(Company company) {
		_companies.add(company);

		for (PortalInstanceLifecycleListener portalInstanceLifecycleListener :
				_serviceTrackerList) {

			registerCompany(portalInstanceLifecycleListener, company);
		}
	}

	@Clusterable
	@Override
	public void unregisterCompany(Company company) {
		_companies.remove(company);

		for (PortalInstanceLifecycleListener portalInstanceLifecycleListener :
				_serviceTrackerList) {

			unregisterCompany(portalInstanceLifecycleListener, company);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_registryMap = _loadRegistryMap(bundleContext);

		_bundleContext = bundleContext;

		_serviceTrackerList = ServiceTrackerListFactory.open(
			bundleContext, PortalInstanceLifecycleListener.class, null,
			new PortalInstanceLifecycleListenerServiceTrackerCustomizer());
	}

	@Deactivate
	protected void deactivate() {
		_serviceTrackerList.close();

		_saveRegistryMap(_bundleContext, _refreshedRegistryMap);
	}

	protected void preregisterCompany(
		PortalInstanceLifecycleListener portalInstanceLifecycleListener,
		Company company) {

		_runIfNeeded(
			company.getCompanyId(), portalInstanceLifecycleListener, false,
			() -> {
				try {
					portalInstanceLifecycleListener.portalInstancePreregistered(
						company);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to preregister portal instance " + company,
							exception);
					}
				}
			});
	}

	protected void preunregisterCompany(
		PortalInstanceLifecycleListener portalInstanceLifecycleListener,
		Company company) {

		_runIfNeeded(
			company.getCompanyId(), portalInstanceLifecycleListener, false,
			() -> {
				try {
					portalInstanceLifecycleListener.
						portalInstancePreunregistered(company);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to preunregister portal instance " +
								company,
							exception);
					}
				}
			});
	}

	protected void registerCompany(
		PortalInstanceLifecycleListener portalInstanceLifecycleListener,
		Company company) {

		_runIfNeeded(
			company.getCompanyId(), portalInstanceLifecycleListener, true,
			() -> {
				Locale defaultLocale = LocaleThreadLocal.getDefaultLocale();
				Locale siteDefaultLocale =
					LocaleThreadLocal.getSiteDefaultLocale();

				try (SafeCloseable safeCloseable1 =
						CompanyThreadLocal.setCompanyIdWithSafeCloseable(
							company.getCompanyId());
					SafeCloseable safeCloseable2 =
						CompanyThreadLocal.
							setInitializingPortalInstanceWithSafeCloseable(
								true)) {

					LocaleThreadLocal.setDefaultLocale(company.getLocale());
					LocaleThreadLocal.setSiteDefaultLocale(null);

					portalInstanceLifecycleListener.portalInstanceRegistered(
						company);
				}
				catch (Exception exception) {
					if (_log.isWarnEnabled()) {
						_log.warn(
							"Unable to register portal instance " + company,
							exception);
					}
				}
				finally {
					LocaleThreadLocal.setDefaultLocale(defaultLocale);
					LocaleThreadLocal.setSiteDefaultLocale(siteDefaultLocale);
				}
			});
	}

	protected void unregisterCompany(
		PortalInstanceLifecycleListener portalInstanceLifecycleListener,
		Company company) {

		try {
			portalInstanceLifecycleListener.portalInstanceUnregistered(company);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn(
					"Unable to unregister portal instance " + company,
					exception);
			}
		}
	}

	private Map<Long, Map<String, Long>> _loadRegistryMap(
		BundleContext bundleContext) {

		Map<Long, Map<String, Long>> registryMap = new HashMap<>();

		File dataFile = bundleContext.getDataFile("registry.data");

		if (dataFile.exists() && !StartupHelperUtil.isDBNew()) {
			try {
				Deserializer deserializer = new Deserializer(
					ByteBuffer.wrap(FileUtil.getBytes(dataFile)));

				Bundle bundle = bundleContext.getBundle();

				if (deserializer.readLong() == bundle.getLastModified()) {
					int size = deserializer.readInt();

					for (int i = 0; i < size; i++) {
						long companyId = deserializer.readLong();

						int count = deserializer.readInt();

						Map<String, Long> registry = new HashMap<>();

						for (int j = 0; j < count; j++) {
							registry.put(
								deserializer.readString(),
								deserializer.readLong());
						}

						registryMap.put(companyId, registry);
					}
				}
			}
			catch (Exception exception) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to load registry data", exception);
				}
			}
		}

		return registryMap;
	}

	private void _runIfNeeded(
		long companyId,
		PortalInstanceLifecycleListener portalInstanceLifecycleListener,
		boolean addToRefreshedRegistry, Runnable runnable) {

		if (portalInstanceLifecycleListener instanceof EveryNodeEveryStartup) {
			runnable.run();

			return;
		}

		if (!ClusterInvokeThreadLocal.isEnabled()) {
			return;
		}

		long currentLastModifiedTime =
			portalInstanceLifecycleListener.getLastModifiedTime();

		String className = portalInstanceLifecycleListener.getName();

		try {
			Map<String, Long> registry = _registryMap.get(companyId);

			if ((registry == null) ||
				(currentLastModifiedTime != registry.getOrDefault(
					className, -1L))) {

				runnable.run();
			}
		}
		finally {
			if (addToRefreshedRegistry) {
				Map<String, Long> refreshedRegistry =
					_refreshedRegistryMap.computeIfAbsent(
						companyId, key -> new HashMap<>());

				refreshedRegistry.put(className, currentLastModifiedTime);
			}
		}
	}

	private void _saveRegistryMap(
		BundleContext bundleContext, Map<Long, Map<String, Long>> registryMap) {

		Bundle bundle = bundleContext.getBundle();

		Serializer serializer = new Serializer();

		serializer.writeLong(bundle.getLastModified());

		serializer.writeInt(registryMap.size());

		for (Map.Entry<Long, Map<String, Long>> companyEntry :
				registryMap.entrySet()) {

			Long companyId = companyEntry.getKey();

			serializer.writeLong(companyId);

			Map<String, Long> registry = companyEntry.getValue();

			serializer.writeInt(registry.size());

			for (Map.Entry<String, Long> registryEntry : registry.entrySet()) {
				serializer.writeString(registryEntry.getKey());
				serializer.writeLong(registryEntry.getValue());
			}
		}

		File dataFile = bundleContext.getDataFile("registry.data");

		try (OutputStream outputStream = new FileOutputStream(dataFile)) {
			serializer.writeTo(outputStream);
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to write JSON objects cache file", exception);
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalInstanceLifecycleListenerManagerImpl.class);

	private static final TransactionConfig _transactionConfig =
		TransactionConfig.Factory.create(
			Propagation.REQUIRED, new Class<?>[] {Exception.class});

	private BundleContext _bundleContext;
	private final Set<Company> _companies = new CopyOnWriteArraySet<>();

	@Reference
	private CompanyLocalService _companyLocalService;

	private final Map<Long, Map<String, Long>> _refreshedRegistryMap =
		new HashMap<>();
	private Map<Long, Map<String, Long>> _registryMap;
	private ServiceTrackerList<PortalInstanceLifecycleListener>
		_serviceTrackerList;

	private class PortalInstanceLifecycleListenerServiceTrackerCustomizer
		implements ServiceTrackerCustomizer
			<PortalInstanceLifecycleListener, PortalInstanceLifecycleListener> {

		@Override
		public PortalInstanceLifecycleListener addingService(
			ServiceReference<PortalInstanceLifecycleListener>
				serviceReference) {

			PortalInstanceLifecycleListener portalInstanceLifecycleListener =
				_bundleContext.getService(serviceReference);

			if (_companies.isEmpty()) {
				return portalInstanceLifecycleListener;
			}

			Iterator<Company> iterator = _companies.iterator();

			while (iterator.hasNext()) {
				Company company = iterator.next();

				Company fetchedCompany = _companyLocalService.fetchCompanyById(
					company.getCompanyId());

				if (fetchedCompany != null) {
					continue;
				}

				try {
					TransactionInvokerUtil.invoke(
						_transactionConfig,
						() -> {
							unregisterCompany(company);

							return null;
						});
				}
				catch (Throwable throwable) {
					_log.error(throwable.getMessage());

					return portalInstanceLifecycleListener;
				}
			}

			try {
				_companyLocalService.forEachCompany(
					company -> {
						try {
							TransactionInvokerUtil.invoke(
								_transactionConfig,
								() -> {
									preregisterCompany(
										portalInstanceLifecycleListener,
										company);

									registerCompany(
										portalInstanceLifecycleListener,
										company);

									return null;
								});
						}
						catch (Throwable throwable) {
							throw new Exception(throwable);
						}
					},
					new ArrayList<>(_companies));
			}
			catch (Exception exception) {
				_log.error(exception);

				return portalInstanceLifecycleListener;
			}

			return portalInstanceLifecycleListener;
		}

		@Override
		public void modifiedService(
			ServiceReference<PortalInstanceLifecycleListener> serviceReference,
			PortalInstanceLifecycleListener portalInstanceLifecycleListener) {
		}

		@Override
		public void removedService(
			ServiceReference<PortalInstanceLifecycleListener> serviceReference,
			PortalInstanceLifecycleListener portalInstanceLifecycleListener) {

			_bundleContext.ungetService(serviceReference);
		}

	}

}