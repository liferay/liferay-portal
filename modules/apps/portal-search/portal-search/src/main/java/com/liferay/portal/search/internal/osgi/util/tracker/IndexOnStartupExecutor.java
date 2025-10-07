/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal.osgi.util.tracker;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.instance.lifecycle.PortalInstanceLifecycleListener;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.BaseSearcher;
import com.liferay.portal.kernel.search.IndexWriterHelper;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.search.configuration.ReindexConfiguration;
import com.liferay.portal.search.internal.instance.lifecycle.IndexOnStartupPortalInstanceLifecycleListener;

import java.io.Serializable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.ReindexConfiguration",
	service = {}
)
public class IndexOnStartupExecutor
	implements ServiceTrackerCustomizer<Indexer<?>, Indexer<?>> {

	@Override
	public Indexer<?> addingService(
		ServiceReference<Indexer<?>> serviceReference) {

		Indexer<?> indexer = _bundleContext.getService(serviceReference);

		boolean indexerIndexOnStartup = GetterUtil.getBoolean(
			serviceReference.getProperty(PropsKeys.INDEX_ON_STARTUP), true);

		String className = indexer.getClassName();

		if (!indexerIndexOnStartup || Validator.isNull(className) ||
			_isBaseSearcher(indexer.getClass())) {

			return indexer;
		}

		synchronized (_serviceRegistrations) {
			if (_serviceRegistrations.containsKey(className)) {
				if (_log.isInfoEnabled()) {
					_log.info(
						"Skip duplicate service registration for " + className);
				}

				return indexer;
			}

			PortalInstanceLifecycleListener portalInstanceLifecycleListener =
				new IndexOnStartupPortalInstanceLifecycleListener(
					_indexWriterHelper, className,
					HashMapBuilder.<String, Serializable>put(
						"executionMode",
						_reindexConfiguration.defaultReindexExecutionMode()
					).build());

			ServiceRegistration<PortalInstanceLifecycleListener>
				serviceRegistration = _bundleContext.registerService(
					PortalInstanceLifecycleListener.class,
					portalInstanceLifecycleListener, null);

			_serviceRegistrations.put(className, serviceRegistration);
		}

		return indexer;
	}

	@Override
	public void modifiedService(
		ServiceReference<Indexer<?>> serviceReference, Indexer<?> indexer) {
	}

	@Override
	public void removedService(
		ServiceReference<Indexer<?>> serviceReference, Indexer<?> indexer) {

		synchronized (_serviceRegistrations) {
			ServiceRegistration<PortalInstanceLifecycleListener>
				serviceRegistration = _serviceRegistrations.remove(
					indexer.getClassName());

			if (serviceRegistration != null) {
				serviceRegistration.unregister();
			}
		}
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		_reindexConfiguration = ConfigurableUtil.createConfigurable(
			ReindexConfiguration.class, properties);

		if (PropsValues.INDEX_ON_STARTUP) {
			ScheduledExecutorService scheduledExecutorService =
				Executors.newSingleThreadScheduledExecutor();

			scheduledExecutorService.schedule(
				() -> {
					if (_bundleContext != null) {
						_serviceTracker = new ServiceTracker<>(
							_bundleContext,
							(Class<Indexer<?>>)(Class<?>)Indexer.class, this);

						_serviceTracker.open();
					}
				},
				PropsValues.INDEX_ON_STARTUP_DELAY, TimeUnit.SECONDS);

			scheduledExecutorService.shutdown();
		}
	}

	@Deactivate
	protected void deactivate() {
		_bundleContext = null;

		Set<String> removedIndexerClassNames = new HashSet<>();

		synchronized (_serviceRegistrations) {
			for (Map.Entry
					<String,
					 ServiceRegistration<PortalInstanceLifecycleListener>>
						entry : _serviceRegistrations.entrySet()) {

				ServiceRegistration<?> serviceRegistration = entry.getValue();

				serviceRegistration.unregister();

				removedIndexerClassNames.add(entry.getKey());
			}

			for (String removedIndexerClassName : removedIndexerClassNames) {
				_serviceRegistrations.remove(removedIndexerClassName);
			}
		}

		if (_serviceTracker != null) {
			_serviceTracker.close();
		}
	}

	private boolean _isBaseSearcher(Class<?> indexerClass) {
		while ((indexerClass != null) && !Object.class.equals(indexerClass)) {
			if (indexerClass.equals(BaseSearcher.class)) {
				return true;
			}

			indexerClass = indexerClass.getSuperclass();
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexOnStartupExecutor.class);

	private BundleContext _bundleContext;

	@Reference
	private IndexWriterHelper _indexWriterHelper;

	private volatile ReindexConfiguration _reindexConfiguration;
	private final Map
		<String, ServiceRegistration<PortalInstanceLifecycleListener>>
			_serviceRegistrations = new HashMap<>();
	private ServiceTracker<Indexer<?>, Indexer<?>> _serviceTracker;

}