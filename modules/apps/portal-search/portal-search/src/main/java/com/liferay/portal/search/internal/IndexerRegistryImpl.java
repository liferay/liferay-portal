/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.internal;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.dependency.manager.DependencyManagerSyncUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerPostProcessor;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.dummy.DummyIndexer;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.search.configuration.IndexerRegistryConfiguration;
import com.liferay.portal.search.index.IndexStatusManager;
import com.liferay.portal.search.internal.buffer.BufferedIndexerInvocationHandler;
import com.liferay.portal.search.internal.buffer.IndexerRequestBuffer;
import com.liferay.portal.search.internal.buffer.IndexerRequestBufferOverflowHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Michael C. Han
 */
@Component(
	configurationPid = "com.liferay.portal.search.configuration.IndexerRegistryConfiguration",
	service = IndexerRegistry.class
)
public class IndexerRegistryImpl implements IndexerRegistry {

	@Override
	public <T> Indexer<T> getIndexer(Class<T> clazz) {
		return getIndexer(clazz.getName());
	}

	@Override
	public <T> Indexer<T> getIndexer(String className) {
		Indexer<T> indexer = _indexerServiceTrackerMap.getService(className);

		return _proxyIndexer(indexer);
	}

	@Override
	public List<IndexerPostProcessor> getIndexerPostProcessors(
		Indexer<?> indexer) {

		List<IndexerPostProcessor> indexerPostProcessors1 =
			_indexerPostProcessorsServiceTrackerMap.getService(
				indexer.getClassName());

		Class<?> clazz = indexer.getClass();

		List<IndexerPostProcessor> indexerPostProcessors2 =
			_indexerPostProcessorsServiceTrackerMap.getService(clazz.getName());

		if (indexerPostProcessors1 == null) {
			if (indexerPostProcessors2 == null) {
				return Collections.emptyList();
			}

			return indexerPostProcessors2;
		}

		if (indexerPostProcessors2 == null) {
			return indexerPostProcessors1;
		}

		return ListUtil.concat(indexerPostProcessors1, indexerPostProcessors2);
	}

	@Override
	public List<IndexerPostProcessor> getIndexerPostProcessors(
		String className) {

		List<IndexerPostProcessor> indexerPostProcessors =
			_indexerPostProcessorsServiceTrackerMap.getService(className);

		if (indexerPostProcessors == null) {
			return Collections.emptyList();
		}

		return indexerPostProcessors;
	}

	@Override
	public Set<Indexer<?>> getIndexers() {
		return new HashSet<>((Collection)_indexerServiceTrackerMap.values());
	}

	@Override
	public <T> Indexer<T> nullSafeGetIndexer(Class<T> clazz) {
		return nullSafeGetIndexer(clazz.getName());
	}

	@Override
	public <T> Indexer<T> nullSafeGetIndexer(String className) {
		Indexer<T> indexer = getIndexer(className);

		if (indexer != null) {
			return indexer;
		}

		if (_log.isInfoEnabled()) {
			_log.info("No indexer found for " + className);
		}

		return (Indexer<T>)ProxyUtil.newProxyInstance(
			Indexer.class.getClassLoader(), new Class<?>[] {Indexer.class},
			new StartupSafeReindexInvocationHandler(className));
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		_bundleContext = bundleContext;

		modified(properties);

		_indexerPostProcessorsServiceTrackerMap =
			ServiceTrackerMapFactory.openMultiValueMap(
				_bundleContext, IndexerPostProcessor.class,
				"indexer.class.name");

		_indexerServiceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			_bundleContext, Indexer.class, null,
			(serviceReference, emitter) -> {
				Indexer<?> indexer = _bundleContext.getService(
					serviceReference);

				emitter.emit(indexer.getClassName());

				Class<?> clazz = indexer.getClass();

				emitter.emit(clazz.getName());

				_bundleContext.ungetService(serviceReference);
			},
			new ServiceTrackerCustomizer<Indexer, Indexer>() {

				@Override
				public Indexer addingService(
					ServiceReference<Indexer> serviceReference) {

					return _bundleContext.getService(serviceReference);
				}

				@Override
				public void modifiedService(
					ServiceReference<Indexer> serviceReference,
					Indexer indexer) {
				}

				@Override
				public void removedService(
					ServiceReference<Indexer> serviceReference,
					Indexer indexer) {

					Class<?> clazz = indexer.getClass();

					_bufferedInvocationHandlers.remove(clazz.getName());
					_proxiedIndexers.remove(clazz.getName());

					_bufferedInvocationHandlers.remove(indexer.getClassName());
					_proxiedIndexers.remove(indexer.getClassName());
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_indexerServiceTrackerMap.close();

		_indexerPostProcessorsServiceTrackerMap.close();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_indexerRegistryConfiguration = ConfigurableUtil.createConfigurable(
			IndexerRegistryConfiguration.class, properties);

		for (BufferedIndexerInvocationHandler bufferedIndexerInvocationHandler :
				_bufferedInvocationHandlers.values()) {

			bufferedIndexerInvocationHandler.setIndexerRegistryConfiguration(
				_indexerRegistryConfiguration);
		}
	}

	private <T> Indexer<T> _proxyIndexer(Indexer<T> indexer) {
		if (indexer == null) {
			return null;
		}

		IndexerRequestBuffer indexerRequestBuffer = IndexerRequestBuffer.get();

		if ((indexerRequestBuffer == null) ||
			!_indexerRegistryConfiguration.buffered()) {

			return indexer;
		}

		Indexer<?> proxiedIndexer = _proxiedIndexers.get(
			indexer.getClassName());

		if (proxiedIndexer == null) {
			BufferedIndexerInvocationHandler bufferedIndexerInvocationHandler =
				new BufferedIndexerInvocationHandler(
					indexer, _indexStatusManager,
					_indexerRegistryConfiguration);

			bufferedIndexerInvocationHandler.
				setIndexerRequestBufferOverflowHandler(
					_indexerRequestBufferOverflowHandler);

			_bufferedInvocationHandlers.put(
				indexer.getClassName(), bufferedIndexerInvocationHandler);

			proxiedIndexer = (Indexer<?>)ProxyUtil.newProxyInstance(
				PortalClassLoaderUtil.getClassLoader(),
				ReflectionUtil.getInterfaces(indexer),
				bufferedIndexerInvocationHandler);

			_proxiedIndexers.put(indexer.getClassName(), proxiedIndexer);
		}

		return (Indexer<T>)proxiedIndexer;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		IndexerRegistryImpl.class);

	private final Map<String, BufferedIndexerInvocationHandler>
		_bufferedInvocationHandlers = new ConcurrentHashMap<>();
	private BundleContext _bundleContext;
	private final Indexer<?> _dummyIndexer = new DummyIndexer();
	private ServiceTrackerMap<String, List<IndexerPostProcessor>>
		_indexerPostProcessorsServiceTrackerMap;
	private volatile IndexerRegistryConfiguration _indexerRegistryConfiguration;

	@Reference
	private IndexerRequestBufferOverflowHandler
		_indexerRequestBufferOverflowHandler;

	private ServiceTrackerMap<String, Indexer> _indexerServiceTrackerMap;

	@Reference
	private IndexStatusManager _indexStatusManager;

	private final Map<String, Indexer<? extends Object>> _proxiedIndexers =
		new ConcurrentHashMap<>();

	private class StartupSafeReindexInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (Objects.equals(method.getName(), "reindex")) {
				DependencyManagerSyncUtil.registerSyncCallable(
					() -> {
						Indexer<?> indexer = getIndexer(_className);

						if (indexer == null) {
							indexer = _dummyIndexer;
						}

						return method.invoke(indexer, args);
					});

				return null;
			}

			return method.invoke(_dummyIndexer, args);
		}

		private StartupSafeReindexInvocationHandler(String className) {
			_className = className;
		}

		private final String _className;

	}

}