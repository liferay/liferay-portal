/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.cache.internal.dao.orm;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LRUMap;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.servlet.filters.threadlocal.ThreadLocalFilterThreadLocal;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Brian Wing Shun Chan
 * @author Shuyang Zhou
 */
@Component(service = FinderCache.class)
public class FinderCacheImpl
	implements FinderCache, PortalCacheManagerListener {

	public void clearByEntityCache(String className) {
		clearLocalCache();

		_clearCache(className);
		_clearCache(_getCacheNameWithPagination(className));
		_clearCache(_getCacheNameWithoutPagination(className));

		_clearDSLQueryCache(className);
	}

	@Override
	public void clearCache() {
		clearLocalCache();

		for (PortalCache<?, ?> portalCache : _portalCaches.values()) {
			portalCache.removeAll();
		}
	}

	@Override
	public void clearCache(Class<?> clazz) {
		clearByEntityCache(clazz.getName());
	}

	@Override
	public void clearDSLQueryCache(String tableName) {
		_clearDSLQueryCache(tableName);

		if (_clusterExecutor.isEnabled() &&
			ClusterInvokeThreadLocal.isEnabled()) {

			try {
				ClusterRequest clusterRequest =
					ClusterRequest.createMulticastRequest(
						new MethodHandler(
							_clearDSLQueryCacheMethodKey, tableName),
						true);

				clusterRequest.setFireAndForget(true);

				_clusterExecutor.execute(clusterRequest);
			}
			catch (Throwable throwable) {
				_log.error(throwable, throwable);
			}
		}
	}

	@Override
	public void clearLocalCache() {
		if (_localCache != null) {
			_localCache.remove();
		}
	}

	@Override
	public void dispose() {
		_portalCaches.clear();
	}

	@Override
	public Object getResult(
		FinderPath finderPath, Object[] args,
		BasePersistence<?> basePersistence) {

		if (!_valueObjectFinderCacheEnabled || !CacheRegistryUtil.isActive()) {
			return null;
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);
		Serializable cacheValue = null;
		Map<LocalCacheKey, Serializable> localCache = null;
		LocalCacheKey localCacheKey = null;

		if (_isLocalCacheEnabled()) {
			localCache = _localCache.get();

			localCacheKey = new LocalCacheKey(
				finderPath.getCacheName(), cacheKey);

			cacheValue = localCache.get(localCacheKey);
		}

		if (cacheValue == null) {
			finderPath.touch();

			PortalCache<Serializable, Serializable> portalCache =
				_getPortalCache(finderPath.getCacheName());

			cacheValue = portalCache.get(cacheKey);

			if ((cacheValue != null) && (localCache != null)) {
				localCache.put(localCacheKey, cacheValue);
			}
		}

		if (cacheValue == null) {
			return null;
		}

		if (cacheValue instanceof EmptyResult) {
			EmptyResult emptyResult = (EmptyResult)cacheValue;

			if (emptyResult.matches(args)) {
				return Collections.emptyList();
			}

			return null;
		}

		if (!finderPath.isBaseModelResult()) {
			return cacheValue;
		}

		if (cacheValue instanceof Serializable[]) {
			Serializable[] primaryKeys = (Serializable[])cacheValue;

			if (primaryKeys.length == 1) {
				Serializable result = basePersistence.fetchByPrimaryKey(
					primaryKeys[0]);

				if (result == null) {
					return null;
				}

				return Arrays.asList(result);
			}

			Set<Serializable> primaryKeysSet = SetUtil.fromArray(primaryKeys);

			Map<Serializable, ? extends BaseModel<?>> map =
				basePersistence.fetchByPrimaryKeys(primaryKeysSet);

			if (map.size() < primaryKeysSet.size()) {
				return null;
			}

			List<Serializable> list = new ArrayList<>(primaryKeys.length);

			for (Serializable curPrimaryKey : primaryKeys) {
				list.add(map.get(curPrimaryKey));
			}

			return Collections.unmodifiableList(list);
		}

		return basePersistence.fetchByPrimaryKey(cacheValue);
	}

	@Override
	public void init() {
	}

	@Override
	public void invalidate() {
		clearCache();
	}

	@Override
	public void notifyPortalCacheAdded(String portalCacheName) {
	}

	@Override
	public void notifyPortalCacheRemoved(String portalCacheName) {
		if (portalCacheName.startsWith(_GROUP_KEY_PREFIX)) {
			_portalCaches.remove(
				portalCacheName.substring(_GROUP_KEY_PREFIX.length()));
		}
	}

	@Override
	public void putResult(FinderPath finderPath, Object[] args, Object result) {
		if (!_valueObjectFinderCacheEnabled || !CacheRegistryUtil.isActive() ||
			(result == null) || !finderPath.isTouched()) {

			return;
		}

		Serializable cacheValue = (Serializable)result;

		if (result instanceof BaseModel<?>) {
			BaseModel<?> model = (BaseModel<?>)result;

			cacheValue = model.getPrimaryKeyObj();
		}
		else if (result instanceof List<?>) {
			List<?> objects = (List<?>)result;

			if (objects.isEmpty()) {
				cacheValue = new EmptyResult(args);
			}
			else if ((objects.size() > _valueObjectFinderCacheListThreshold) &&
					 (_valueObjectFinderCacheListThreshold > 0)) {

				_removeResult(finderPath, args);

				return;
			}
			else if (finderPath.isBaseModelResult()) {
				Serializable[] primaryKeys = new Serializable[objects.size()];

				for (int i = 0; i < objects.size(); i++) {
					BaseModel<?> baseModel = (BaseModel<?>)objects.get(i);

					primaryKeys[i] = baseModel.getPrimaryKeyObj();
				}

				cacheValue = primaryKeys;
			}
		}

		String cacheName = finderPath.getCacheName();
		String cacheKeyPrefix = finderPath.getCacheKeyPrefix();

		Map<String, FinderPath> finderPaths = _finderPathsMap.get(cacheName);

		if (finderPaths == null) {
			finderPaths = new ConcurrentHashMap<>();

			Map<String, FinderPath> originalFinderPaths =
				_finderPathsMap.putIfAbsent(cacheName, finderPaths);

			if (originalFinderPaths != null) {
				finderPaths = originalFinderPaths;
			}
		}

		if (!finderPaths.containsKey(cacheKeyPrefix)) {
			if (cacheKeyPrefix.startsWith("dslQuery")) {
				for (String tableName :
						FinderPath.decodeDSLQueryCacheName(cacheName)) {

					Set<String> dslQueryCacheNames =
						_dslQueryCacheNamesMap.computeIfAbsent(
							tableName,
							key -> Collections.newSetFromMap(
								new ConcurrentHashMap<>()));

					dslQueryCacheNames.add(cacheName);
				}
			}

			finderPaths.putIfAbsent(cacheKeyPrefix, finderPath);
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);

		if (_isLocalCacheEnabled()) {
			Map<LocalCacheKey, Serializable> localCache = _localCache.get();

			localCache.put(
				new LocalCacheKey(finderPath.getCacheName(), cacheKey),
				cacheValue);
		}

		PortalCacheHelperUtil.putWithoutReplicator(
			_getPortalCache(finderPath.getCacheName()), cacheKey, cacheValue);
	}

	public void removeByEntityCache(String className, BaseModel<?> baseModel) {
		ArgumentsResolverHolder argumentsResolverHolder =
			_serviceTrackerMap.getService(className);

		if (argumentsResolverHolder == null) {
			clearByEntityCache(className);

			return;
		}

		clearLocalCache();

		_clearCache(_getCacheNameWithPagination(className));
		_clearCache(_getCacheNameWithoutPagination(className));

		_clearDSLQueryCache(className);

		ArgumentsResolver argumentsResolver =
			argumentsResolverHolder.getArgumentsResolver();

		for (FinderPath finderPath : _getFinderPaths(className)) {
			removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, false, false));
			removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, true, true));
		}
	}

	@Override
	public void removeCache(String className) {
		PortalCache<Serializable, Serializable> portalCache =
			_portalCaches.remove(className);

		if (portalCache instanceof CTAwarePortalCache) {
			CTAwarePortalCache ctAwarePortalCache =
				(CTAwarePortalCache)portalCache;

			ctAwarePortalCache.destroy();
		}
		else {
			String groupKey = _GROUP_KEY_PREFIX.concat(className);

			_multiVMPool.removePortalCache(groupKey);
		}

		_finderPathsMap.remove(className);
	}

	public void removeCacheByEntityCache(String cacheName) {
		removeCache(cacheName);
		removeCache(_getCacheNameWithPagination(cacheName));
		removeCache(_getCacheNameWithoutPagination(cacheName));

		String tableName = null;

		ArgumentsResolverHolder argumentsResolverHolder =
			_serviceTrackerMap.getService(cacheName);

		if (argumentsResolverHolder == null) {
			tableName = cacheName;
		}
		else {
			tableName = argumentsResolverHolder.getTableName();
		}

		Set<String> dslQueryCacheNames = _dslQueryCacheNamesMap.remove(
			tableName);

		if (dslQueryCacheNames != null) {
			for (String dslQueryCacheName : dslQueryCacheNames) {
				removeCache(dslQueryCacheName);
			}
		}
	}

	@Override
	public void removeResult(FinderPath finderPath, Object[] args) {
		if (!_valueObjectFinderCacheEnabled || !CacheRegistryUtil.isActive()) {
			return;
		}

		_removeResult(finderPath, args);
	}

	public void updateByEntityCache(String className, BaseModel<?> baseModel) {
		if (!_valueObjectFinderCacheEnabled) {
			return;
		}

		ArgumentsResolverHolder argumentsResolverHolder =
			_serviceTrackerMap.getService(className);

		if (argumentsResolverHolder == null) {
			clearByEntityCache(className);

			return;
		}

		clearLocalCache();

		_clearCache(_getCacheNameWithPagination(className));

		_clearDSLQueryCache(className);

		Set<FinderPath> finderPaths = new HashSet<>();

		finderPaths.addAll(
			_getFinderPaths(_getCacheNameWithoutPagination(className)));
		finderPaths.addAll(_getFinderPaths(className));

		ArgumentsResolver argumentsResolver =
			argumentsResolverHolder.getArgumentsResolver();

		for (FinderPath finderPath : finderPaths) {
			if (baseModel.isNew()) {
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, false, false));
			}
			else {
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, true, false));
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, true, true));
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_valueObjectFinderCacheEnabled = GetterUtil.getBoolean(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_ENABLED));
		_valueObjectFinderCacheListThreshold = GetterUtil.getInteger(
			PropsUtil.get(PropsKeys.VALUE_OBJECT_FINDER_CACHE_LIST_THRESHOLD));

		if (_valueObjectFinderCacheListThreshold == 0) {
			_valueObjectFinderCacheEnabled = false;
		}

		int localCacheMaxSize = GetterUtil.getInteger(
			PropsUtil.get(
				PropsKeys.VALUE_OBJECT_FINDER_THREAD_LOCAL_CACHE_MAX_SIZE));

		if (!DBPartition.isPartitionEnabled() && (localCacheMaxSize > 0)) {
			_localCache = new CentralizedThreadLocal<>(
				FinderCacheImpl.class + "._localCache",
				() -> new LRUMap<>(localCacheMaxSize));
		}
		else {
			_localCache = null;
		}

		PortalCacheManager<? extends Serializable, ? extends Serializable>
			portalCacheManager = _multiVMPool.getPortalCacheManager();

		portalCacheManager.registerPortalCacheManagerListener(this);

		_serviceRegistration = bundleContext.registerService(
			CacheRegistryItem.class, new FinderCacheCacheRegistryItem(), null);
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, ArgumentsResolver.class, "class.name",
			new ServiceTrackerCustomizer
				<ArgumentsResolver, ArgumentsResolverHolder>() {

				@Override
				public ArgumentsResolverHolder addingService(
					ServiceReference<ArgumentsResolver> serviceReference) {

					ArgumentsResolverHolder argumentsResolverHolder =
						new ArgumentsResolverHolder(serviceReference);

					_argumentsResolverHolderMap.put(
						argumentsResolverHolder.getTableName(),
						argumentsResolverHolder);

					return argumentsResolverHolder;
				}

				@Override
				public void modifiedService(
					ServiceReference<ArgumentsResolver> serviceReference,
					ArgumentsResolverHolder argumentsResolverHolder) {
				}

				@Override
				public void removedService(
					ServiceReference<ArgumentsResolver> serviceReference,
					ArgumentsResolverHolder argumentsResolverHolder) {

					_argumentsResolverHolderMap.remove(
						argumentsResolverHolder.getTableName());

					argumentsResolverHolder.ungetArgumentsResolver();
				}

			});
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	private void _clearCache(String cacheName) {
		PortalCache<?, ?> portalCache = _getPortalCache(cacheName);

		portalCache.removeAll();
	}

	private void _clearDSLQueryCache(String className) {
		ArgumentsResolverHolder argumentsResolverHolder =
			_serviceTrackerMap.getService(className);

		String tableName = null;

		if (argumentsResolverHolder == null) {
			tableName = className;
		}
		else {
			tableName = argumentsResolverHolder.getTableName();
		}

		Set<String> dslQueryCacheNames = _dslQueryCacheNamesMap.get(tableName);

		if (dslQueryCacheNames != null) {
			clearLocalCache();

			for (String dslQueryCacheName : dslQueryCacheNames) {
				_clearCache(dslQueryCacheName);
			}
		}
	}

	private Serializable _encodeCacheKey(
		FinderPath finderPath, Object[] arguments) {

		CacheKeyGenerator cacheKeyGenerator = _getCacheKeyGenerator(
			finderPath.isBaseModelResult());

		String[] keys = new String[arguments.length * 2];

		for (int i = 0; i < arguments.length; i++) {
			int index = i * 2;

			keys[index] = StringPool.PERIOD;
			keys[index + 1] = StringUtil.toHexString(arguments[i]);
		}

		return cacheKeyGenerator.getCacheKey(
			new String[] {
				finderPath.getCacheKeyPrefix(),
				StringUtil.toHexString(cacheKeyGenerator.getCacheKey(keys))
			});
	}

	private CacheKeyGenerator _getCacheKeyGenerator(boolean baseModel) {
		if (baseModel) {
			CacheKeyGenerator cacheKeyGenerator = _baseModelCacheKeyGenerator;

			if (cacheKeyGenerator == null) {
				cacheKeyGenerator = CacheKeyGeneratorUtil.getCacheKeyGenerator(
					FinderCache.class.getName() + "#BaseModel");

				_baseModelCacheKeyGenerator = cacheKeyGenerator;
			}

			return cacheKeyGenerator;
		}

		CacheKeyGenerator cacheKeyGenerator = _cacheKeyGenerator;

		if (cacheKeyGenerator == null) {
			cacheKeyGenerator = CacheKeyGeneratorUtil.getCacheKeyGenerator(
				FinderCache.class.getName());

			_cacheKeyGenerator = cacheKeyGenerator;
		}

		return cacheKeyGenerator;
	}

	private String _getCacheNameWithoutPagination(String cacheName) {
		return cacheName.concat(".List2");
	}

	private String _getCacheNameWithPagination(String cacheName) {
		return cacheName.concat(".List1");
	}

	private Collection<FinderPath> _getFinderPaths(String cacheName) {
		Map<String, FinderPath> finderPaths = _finderPathsMap.get(cacheName);

		if (finderPaths == null) {
			return Collections.emptySet();
		}

		return finderPaths.values();
	}

	private PortalCache<Serializable, Serializable> _getPortalCache(
		String className) {

		PortalCache<Serializable, Serializable> portalCache = _portalCaches.get(
			className);

		if (portalCache != null) {
			return portalCache;
		}

		String groupKey = _GROUP_KEY_PREFIX.concat(className);

		String modelImplClassName = className;

		if (className.endsWith(".List1") || className.endsWith(".List2")) {
			modelImplClassName = className.substring(0, className.length() - 6);
		}

		boolean ctAware = false;
		boolean sharded = DBPartition.isPartitionEnabled();

		ArgumentsResolverHolder argumentsResolverHolder =
			_serviceTrackerMap.getService(modelImplClassName);

		if (argumentsResolverHolder != null) {
			ArgumentsResolver argumentsResolver =
				argumentsResolverHolder.getArgumentsResolver();

			if (!Objects.equals(
					argumentsResolver.getClassName(),
					argumentsResolver.getTableName())) {

				Class<?> clazz = argumentsResolver.getClass();

				ClassLoader classLoader = clazz.getClassLoader();

				try {
					Class<?> modelImplClass = classLoader.loadClass(
						argumentsResolver.getClassName());

					if (DBPartition.isPartitionEnabled()) {
						sharded = DBPartition.isPartitionedModel(
							modelImplClass);
					}

					ctAware = CTModel.class.isAssignableFrom(modelImplClass);
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isWarnEnabled()) {
						_log.warn(classNotFoundException);
					}
				}
			}
		}
		else {
			String[] tableNames = FinderPath.decodeDSLQueryCacheName(className);

			for (String tableName : tableNames) {
				argumentsResolverHolder = _argumentsResolverHolderMap.get(
					tableName);

				if (argumentsResolverHolder == null) {
					continue;
				}

				ArgumentsResolver argumentsResolver =
					argumentsResolverHolder.getArgumentsResolver();

				if (Objects.equals(
						argumentsResolver.getClassName(),
						argumentsResolver.getTableName())) {

					continue;
				}

				Class<?> clazz = argumentsResolver.getClass();

				ClassLoader classLoader = clazz.getClassLoader();

				try {
					Class<?> modelImplClass = classLoader.loadClass(
						argumentsResolver.getClassName());

					ctAware = CTModel.class.isAssignableFrom(modelImplClass);

					if (DBPartition.isPartitionEnabled()) {
						sharded = DBPartition.isPartitionedModel(
							modelImplClass);
					}

					if (ctAware) {
						break;
					}
				}
				catch (ClassNotFoundException classNotFoundException) {
					if (_log.isWarnEnabled()) {
						_log.warn(classNotFoundException);
					}
				}
			}
		}

		if (ctAware) {
			portalCache = new CTAwarePortalCache(
				_multiVMPool, groupKey, false, sharded);
		}
		else {
			portalCache =
				(PortalCache<Serializable, Serializable>)
					_multiVMPool.getPortalCache(groupKey, false, sharded);
		}

		PortalCache<Serializable, Serializable> previousPortalCache =
			_portalCaches.putIfAbsent(className, portalCache);

		if (previousPortalCache != null) {
			return previousPortalCache;
		}

		return portalCache;
	}

	private boolean _isLocalCacheEnabled() {
		if ((_localCache == null) ||
			!CTCollectionThreadLocal.isProductionMode()) {

			return false;
		}

		return ThreadLocalFilterThreadLocal.isFilterInvoked();
	}

	private void _removeResult(FinderPath finderPath, Object[] args) {
		if (args == null) {
			return;
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);

		if (_isLocalCacheEnabled()) {
			Map<LocalCacheKey, Serializable> localCache = _localCache.get();

			localCache.remove(
				new LocalCacheKey(finderPath.getCacheName(), cacheKey));
		}

		PortalCache<Serializable, Serializable> portalCache = _getPortalCache(
			finderPath.getCacheName());

		portalCache.remove(cacheKey);
	}

	private static final String _GROUP_KEY_PREFIX =
		FinderCache.class.getName() + StringPool.PERIOD;

	private static final Log _log = LogFactoryUtil.getLog(
		FinderCacheImpl.class);

	private static final MethodKey _clearDSLQueryCacheMethodKey = new MethodKey(
		FinderCacheUtil.class, "clearDSLQueryCache", String.class);

	private final Map<String, ArgumentsResolverHolder>
		_argumentsResolverHolderMap = new ConcurrentHashMap<>();
	private volatile CacheKeyGenerator _baseModelCacheKeyGenerator;
	private BundleContext _bundleContext;
	private volatile CacheKeyGenerator _cacheKeyGenerator;

	@Reference
	private ClusterExecutor _clusterExecutor;

	private final Map<String, Set<String>> _dslQueryCacheNamesMap =
		new ConcurrentHashMap<>();
	private final Map<String, Map<String, FinderPath>> _finderPathsMap =
		new ConcurrentHashMap<>();
	private ThreadLocal<LRUMap<LocalCacheKey, Serializable>> _localCache;

	@Reference
	private MultiVMPool _multiVMPool;

	private final ConcurrentMap<String, PortalCache<Serializable, Serializable>>
		_portalCaches = new ConcurrentHashMap<>();
	private ServiceRegistration<CacheRegistryItem> _serviceRegistration;
	private ServiceTrackerMap<String, ArgumentsResolverHolder>
		_serviceTrackerMap;
	private boolean _valueObjectFinderCacheEnabled;
	private int _valueObjectFinderCacheListThreshold;

	private static class LocalCacheKey {

		@Override
		public boolean equals(Object object) {
			LocalCacheKey localCacheKey = (LocalCacheKey)object;

			if (_className.equals(localCacheKey._className) &&
				_cacheKey.equals(localCacheKey._cacheKey)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return HashUtil.hash(_className.hashCode(), _cacheKey.hashCode());
		}

		private LocalCacheKey(String className, Serializable cacheKey) {
			_className = className;
			_cacheKey = cacheKey;
		}

		private final Serializable _cacheKey;
		private final String _className;

	}

	private class ArgumentsResolverHolder {

		public ArgumentsResolver getArgumentsResolver() {
			return _argumentsResolverDCLSingleton.getSingleton(
				() -> _bundleContext.getService(_serviceReference));
		}

		public String getTableName() {
			return (String)_serviceReference.getProperty("table.name");
		}

		public void ungetArgumentsResolver() {
			_argumentsResolverDCLSingleton.destroy(
				argumentsResolver -> _bundleContext.ungetService(
					_serviceReference));
		}

		private ArgumentsResolverHolder(
			ServiceReference<ArgumentsResolver> serviceReference) {

			_serviceReference = serviceReference;
		}

		private final DCLSingleton<ArgumentsResolver>
			_argumentsResolverDCLSingleton = new DCLSingleton<>();
		private final ServiceReference<ArgumentsResolver> _serviceReference;

	}

	private class FinderCacheCacheRegistryItem implements CacheRegistryItem {

		@Override
		public String getRegistryName() {
			return FinderCache.class.getName();
		}

		@Override
		public void invalidate() {
			clearCache();
		}

	}

}