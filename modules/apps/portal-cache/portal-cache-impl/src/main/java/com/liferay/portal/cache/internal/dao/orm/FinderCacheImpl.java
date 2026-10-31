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
import com.liferay.portal.cache.PortalCacheWrapper;
import com.liferay.portal.cache.TransactionalPortalCache;
import com.liferay.portal.dao.init.DBInitUtil;
import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.cache.CacheRegistryUtil;
import com.liferay.portal.kernel.cache.MultiVMPool;
import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManager;
import com.liferay.portal.kernel.cache.PortalCacheManagerListener;
import com.liferay.portal.kernel.cache.key.CacheKeyGenerator;
import com.liferay.portal.kernel.cache.key.CacheKeyGeneratorUtil;
import com.liferay.portal.kernel.cache.transactional.TransactionalPortalCacheUtil;
import com.liferay.portal.kernel.change.tracking.CTCollectionThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterExecutor;
import com.liferay.portal.kernel.cluster.ClusterInvokeThreadLocal;
import com.liferay.portal.kernel.cluster.ClusterRequest;
import com.liferay.portal.kernel.dao.orm.ArgumentsResolver;
import com.liferay.portal.kernel.dao.orm.CountFinderPathRegistry;
import com.liferay.portal.kernel.dao.orm.FinderCache;
import com.liferay.portal.kernel.dao.orm.FinderCacheUtil;
import com.liferay.portal.kernel.dao.orm.FinderPath;
import com.liferay.portal.kernel.dao.orm.ModelRemovalThreadLocal;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.db.partition.DBPartition;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.MVCCModel;
import com.liferay.portal.kernel.model.change.tracking.CTModel;
import com.liferay.portal.kernel.service.persistence.BasePersistence;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LRUMap;
import com.liferay.portal.kernel.util.MethodHandler;
import com.liferay.portal.kernel.util.MethodKey;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;
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
import java.util.concurrent.atomic.AtomicLong;

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

		_removePrivateCounts();

		_clearCache(className);
		_clearCache(_getCacheNameWithPagination(className));
		_clearCache(_getCacheNameWithoutPagination(className));
		_clearCache(_getCountCacheName(className));

		_clearDSLQueryCache(className);
	}

	@Override
	public void clearCache() {
		clearLocalCache();

		_removePrivateCounts();

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
		PortalCache<Serializable, Serializable> portalCache = null;

		boolean productionMode = CTCollectionThreadLocal.isProductionMode();

		if (_isLocalCacheEnabled(productionMode)) {
			localCache = _localCache.get();

			localCacheKey = new LocalCacheKey(
				finderPath.getCacheName(), cacheKey);

			cacheValue = localCache.get(localCacheKey);
		}

		if (cacheValue == null) {
			finderPath.touch();

			portalCache = _getCTPortalCache(
				finderPath.getCacheName(), productionMode);

			boolean transactionalCount = false;

			if (_isMaintainedCountFinderPath(finderPath) &&
				TransactionalPortalCacheUtil.isEnabled()) {

				transactionalCount = true;
			}

			CountKey countKey = null;

			if (transactionalCount) {
				countKey = _createCountKey(
					finderPath, cacheKey, productionMode);
			}

			cacheValue = _getPrivateCount(countKey);

			if (cacheValue == null) {
				cacheValue = portalCache.get(cacheKey);

				if (cacheValue instanceof AtomicLong atomicLong) {
					cacheValue = atomicLong.get() + _getPendingDelta(countKey);
				}
			}

			if (cacheValue != null) {
				if (transactionalCount) {
					_flushPendingWrites(
						finderPath.getEntityClassName(), basePersistence);
				}

				if (localCache != null) {
					localCache.put(localCacheKey, cacheValue);
				}
			}
		}

		Object result = _getResult(
			finderPath, args, basePersistence, cacheValue);

		if (result == null) {
			if (portalCache == null) {
				portalCache = _getCTPortalCache(
					finderPath.getCacheName(), productionMode);
			}

			TransactionalPortalCacheUtil.preparePut(portalCache, cacheKey);
		}

		return result;
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
		boolean productionMode = CTCollectionThreadLocal.isProductionMode();

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

				_removeResult(finderPath, args, true, productionMode);

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

		if (!finderPath.isCountResult()) {
			_addFinderPath(finderPath);
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);

		Serializable portalCacheValue = cacheValue;
		int timeToLive = PortalCache.DEFAULT_TIME_TO_LIVE;

		if ((result instanceof Long count) &&
			_isMaintainedCountFinderPath(finderPath)) {

			if (TransactionalPortalCacheUtil.isEnabled()) {
				CountKey countKey = _createCountKey(
					finderPath, cacheKey, productionMode);

				if (countKey != null) {
					TransactionalPortalCacheUtil.put(
						_privateCountPortalCache, countKey,
						new PrivateCount(
							count,
							TransactionalPortalCacheUtil.getStartSequence()),
						PortalCache.DEFAULT_TIME_TO_LIVE, true);

					_putLocalCache(
						finderPath, cacheKey, cacheValue, productionMode);

					return;
				}
			}

			portalCacheValue = new AtomicLong(count);
			timeToLive = _countTimeToLive;
		}

		if (!TransactionalPortalCacheUtil.completePut(
				_getCTPortalCache(finderPath.getCacheName(), productionMode),
				cacheKey, portalCacheValue, timeToLive)) {

			if (_isLocalCacheEnabled(productionMode)) {
				Map<LocalCacheKey, Serializable> localCache = _localCache.get();

				localCache.remove(
					new LocalCacheKey(finderPath.getCacheName(), cacheKey));
			}

			return;
		}

		_putLocalCache(finderPath, cacheKey, cacheValue, productionMode);
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
		boolean productionMode = CTCollectionThreadLocal.isProductionMode();

		for (FinderPath finderPath : _getFinderPaths(className)) {
			_removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, false, false),
				false, productionMode);
			_removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, true, true),
				false, productionMode);
		}

		if (!_countMaintenanceEnabled || !(baseModel instanceof MVCCModel)) {
			_removePrivateCounts();

			_clearCache(_getCountCacheName(className));

			return;
		}

		_markPendingFlush(className, argumentsResolver.getTableName());

		PortalCache<Serializable, Serializable> portalCache = _portalCaches.get(
			_getCountCacheName(className));

		if (productionMode &&
			(portalCache instanceof CTAwarePortalCache ctAwarePortalCache)) {

			ctAwarePortalCache.removeAllFromCTPortalCaches();
		}

		boolean removing = ModelRemovalThreadLocal.isRemoving(baseModel);

		for (FinderPath finderPath :
				CountFinderPathRegistry.getCountFinderPaths(className)) {

			if (removing) {
				_adjustResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, false, true),
					-1, false, productionMode);

				continue;
			}

			_removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, false, false),
				false, productionMode);
			_removeResult(
				finderPath,
				argumentsResolver.getArguments(
					finderPath, baseModel, true, true),
				false, productionMode);
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
		removeCache(_getCountCacheName(cacheName));

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

		_removeResult(
			finderPath, args, true, CTCollectionThreadLocal.isProductionMode());
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
		finderPaths.addAll(
			CountFinderPathRegistry.getCountFinderPaths(className));
		finderPaths.addAll(_getFinderPaths(className));

		ArgumentsResolver argumentsResolver =
			argumentsResolverHolder.getArgumentsResolver();

		if (_countMaintenanceEnabled && (baseModel instanceof MVCCModel)) {
			_markPendingFlush(className, argumentsResolver.getTableName());
		}

		boolean productionMode = CTCollectionThreadLocal.isProductionMode();

		for (FinderPath finderPath : finderPaths) {
			if (_isMaintainedCountFinderPath(finderPath) &&
				(baseModel instanceof MVCCModel)) {

				if (baseModel.isNew()) {
					_adjustResult(
						finderPath,
						argumentsResolver.getArguments(
							finderPath, baseModel, false, false),
						1, true, productionMode);
				}
				else {
					_adjustResult(
						finderPath,
						argumentsResolver.getArguments(
							finderPath, baseModel, true, false),
						1, true, productionMode);
					_adjustResult(
						finderPath,
						argumentsResolver.getArguments(
							finderPath, baseModel, true, true),
						-1, true, productionMode);
				}

				continue;
			}

			if (baseModel.isNew()) {
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, false, false),
					false, productionMode);
			}
			else {
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, true, false),
					false, productionMode);
				_removeResult(
					finderPath,
					argumentsResolver.getArguments(
						finderPath, baseModel, true, true),
					false, productionMode);
			}
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_bundleContext = bundleContext;

		_countMaintenanceEnabled =
			!DBInitUtil.isReadWriteDataSource() &&
			GetterUtil.getBoolean(
				PropsUtil.get(
					PropsKeys.
						VALUE_OBJECT_FINDER_CACHE_COUNT_MAINTENANCE_ENABLED));

		if (PropsValues.CLUSTER_LINK_ENABLED) {
			_countTimeToLive = GetterUtil.getInteger(
				PropsUtil.get(
					PropsKeys.
						VALUE_OBJECT_FINDER_CACHE_COUNT_CLUSTER_TIME_TO_LIVE));
		}
		else {
			_countTimeToLive = PortalCache.DEFAULT_TIME_TO_LIVE;
		}

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

		if (!PropsValues.DATABASE_PARTITION_ENABLED &&
			(localCacheMaxSize > 0)) {

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

	private void _addFinderPath(FinderPath finderPath) {
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
	}

	private void _adjustResult(
		FinderPath finderPath, Object[] args, long delta,
		boolean removeFromCTPortalCaches, boolean productionMode) {

		if (args == null) {
			return;
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);

		PortalCache<Serializable, Serializable> portalCache = _getPortalCache(
			finderPath.getCacheName());

		if (!removeFromCTPortalCaches &&
			(portalCache instanceof CTAwarePortalCache ctAwarePortalCache)) {

			portalCache = ctAwarePortalCache.getCTPortalCache();
		}

		CountKey countKey = null;

		if (TransactionalPortalCacheUtil.isEnabled()) {
			countKey = _createCountKey(finderPath, cacheKey, productionMode);
		}

		if (countKey == null) {
			portalCache.remove(cacheKey);

			return;
		}

		Long privateCount = _getPrivateCount(countKey);

		if (privateCount != null) {
			TransactionalPortalCacheUtil.put(
				_privateCountPortalCache, countKey, privateCount + delta,
				PortalCache.DEFAULT_TIME_TO_LIVE, true);
		}

		Serializable cacheValue = countKey._portalCache.get(cacheKey);

		if (cacheValue instanceof AtomicLong atomicLong) {
			_putPendingCount(countKey, atomicLong, delta);

			if (portalCache instanceof CTAwarePortalCache ctAwarePortalCache) {
				ctAwarePortalCache.removeFromCTPortalCaches(cacheKey);
			}
		}
		else {
			portalCache.remove(cacheKey);
		}
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

	private CountKey _createCountKey(
		FinderPath finderPath, Serializable cacheKey, boolean productionMode) {

		PortalCache<Serializable, Serializable> portalCache = _getPortalCache(
			finderPath.getCacheName());

		if (portalCache instanceof CTAwarePortalCache ctAwarePortalCache) {
			if (!productionMode) {
				return null;
			}

			portalCache = ctAwarePortalCache.getProductionPortalCache();
		}

		if (portalCache instanceof
				TransactionalPortalCache<Serializable, Serializable>
					transactionalPortalCache) {

			return new CountKey(transactionalPortalCache, cacheKey);
		}

		return null;
	}

	private Serializable _encodeCacheKey(
		FinderPath finderPath, Object[] arguments) {

		CacheKeyGenerator cacheKeyGenerator = _getCacheKeyGenerator(
			finderPath.isBaseModelResult());

		String[] keys = new String[(arguments.length * 2) + 1];

		keys[0] = finderPath.getCacheKeyPrefix();

		for (int i = 0; i < arguments.length; i++) {
			int index = (i * 2) + 1;

			keys[index] = StringPool.PERIOD;
			keys[index + 1] = StringUtil.toHexString(arguments[i]);
		}

		return cacheKeyGenerator.getCacheKey(keys);
	}

	private void _flushPendingCount(
		CountKey countKey, PendingCount pendingCount) {

		if (pendingCount._delta == 0) {
			return;
		}

		TransactionalPortalCacheUtil.invalidate(
			countKey._portalCache, countKey._cacheKey);

		PortalCache<Serializable, Serializable> portalCache =
			countKey._portalCache.getWrappedPortalCache();

		Serializable cacheValue = portalCache.get(countKey._cacheKey);

		if (cacheValue == null) {
			return;
		}

		if (cacheValue == pendingCount._atomicLong) {
			pendingCount._atomicLong.addAndGet(pendingCount._delta);
		}
		else {
			PortalCacheHelperUtil.removeWithoutReplicator(
				portalCache, countKey._cacheKey);
		}
	}

	private void _flushPendingWrites(
		String cacheName, BasePersistence<?> basePersistence) {

		String tableName = TransactionalPortalCacheUtil.get(
			_pendingFlushPortalCache, cacheName);

		if (tableName == null) {
			return;
		}

		Session session = basePersistence.getCurrentSession();

		session.autoFlushIfRequired(Collections.singleton(tableName));

		TransactionalPortalCacheUtil.put(
			_pendingFlushPortalCache, cacheName, null,
			PortalCache.DEFAULT_TIME_TO_LIVE, true);
	}

	private PortalCache<Serializable, Serializable> _getCTPortalCache(
		String cacheName, boolean productionMode) {

		PortalCache<Serializable, Serializable> portalCache = _getPortalCache(
			cacheName);

		if (portalCache instanceof CTAwarePortalCache ctAwarePortalCache) {
			if (productionMode) {
				return ctAwarePortalCache.getProductionPortalCache();
			}

			return ctAwarePortalCache.getCTPortalCache();
		}

		return portalCache;
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

	private String _getCacheNameWithPagination(String cacheName) {
		return cacheName.concat(".List1");
	}

	private String _getCacheNameWithoutPagination(String cacheName) {
		return cacheName.concat(".List2");
	}

	private String _getCountCacheName(String className) {
		return className.concat(".Count");
	}

	private Collection<FinderPath> _getFinderPaths(String cacheName) {
		Map<String, FinderPath> finderPaths = _finderPathsMap.get(cacheName);

		if (finderPaths == null) {
			return Collections.emptySet();
		}

		return finderPaths.values();
	}

	private long _getPendingDelta(CountKey countKey) {
		if (countKey == null) {
			return 0;
		}

		PendingCount pendingCount = TransactionalPortalCacheUtil.get(
			_pendingCountPortalCache, countKey);

		if (pendingCount == null) {
			return 0;
		}

		return pendingCount._delta;
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

		if (className.endsWith(".Count") || className.endsWith(".List1") ||
			className.endsWith(".List2")) {

			modelImplClassName = className.substring(0, className.length() - 6);
		}

		boolean ctAware = false;
		boolean sharded = PropsValues.DATABASE_PARTITION_ENABLED;

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

					if (PropsValues.DATABASE_PARTITION_ENABLED) {
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

					if (PropsValues.DATABASE_PARTITION_ENABLED) {
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

	private Long _getPrivateCount(CountKey countKey) {
		if (countKey == null) {
			return null;
		}

		Object value = TransactionalPortalCacheUtil.get(
			_privateCountPortalCache, countKey);

		if (value instanceof PrivateCount privateCount) {
			return privateCount._count;
		}

		if (value instanceof Long count) {
			return count;
		}

		return null;
	}

	private Object _getResult(
		FinderPath finderPath, Object[] args,
		BasePersistence<?> basePersistence, Serializable cacheValue) {

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

	private boolean _isLocalCacheEnabled(boolean productionMode) {
		if ((_localCache == null) || !productionMode) {
			return false;
		}

		return ThreadLocalFilterThreadLocal.isFilterInvoked();
	}

	private boolean _isMaintainedCountFinderPath(FinderPath finderPath) {
		if (_countMaintenanceEnabled && finderPath.isCountResult()) {
			return true;
		}

		return false;
	}

	private void _markPendingFlush(String cacheName, String tableName) {
		if (TransactionalPortalCacheUtil.isEnabled()) {
			TransactionalPortalCacheUtil.put(
				_pendingFlushPortalCache, cacheName, tableName,
				PortalCache.DEFAULT_TIME_TO_LIVE, true);
		}
	}

	private void _publishPrivateCount(CountKey countKey, Object value) {
		if (value instanceof PrivateCount privateCount) {
			TransactionalPortalCacheUtil.completePut(
				countKey._portalCache.getWrappedPortalCache(),
				countKey._cacheKey, new AtomicLong(privateCount._count),
				privateCount._startSequence, _countTimeToLive);
		}
	}

	private void _putLocalCache(
		FinderPath finderPath, Serializable cacheKey, Serializable cacheValue,
		boolean productionMode) {

		if (_isLocalCacheEnabled(productionMode)) {
			Map<LocalCacheKey, Serializable> localCache = _localCache.get();

			localCache.put(
				new LocalCacheKey(finderPath.getCacheName(), cacheKey),
				cacheValue);
		}
	}

	private void _putPendingCount(
		CountKey countKey, AtomicLong atomicLong, long delta) {

		PendingCount pendingCount = TransactionalPortalCacheUtil.get(
			_pendingCountPortalCache, countKey);

		if (pendingCount == null) {
			pendingCount = new PendingCount(atomicLong, delta);
		}
		else {
			pendingCount = pendingCount.add(atomicLong, delta);
		}

		TransactionalPortalCacheUtil.put(
			_pendingCountPortalCache, countKey, pendingCount,
			PortalCache.DEFAULT_TIME_TO_LIVE, true);
	}

	private void _removePrivateCounts() {
		if (_countMaintenanceEnabled &&
			TransactionalPortalCacheUtil.isEnabled()) {

			TransactionalPortalCacheUtil.removeAll(
				_privateCountPortalCache, true);
		}
	}

	private void _removeResult(
		FinderPath finderPath, Object[] args, boolean removeFromLocalCache,
		boolean productionMode) {

		if (args == null) {
			return;
		}

		Serializable cacheKey = _encodeCacheKey(finderPath, args);

		if (removeFromLocalCache && _isLocalCacheEnabled(productionMode)) {
			Map<LocalCacheKey, Serializable> localCache = _localCache.get();

			localCache.remove(
				new LocalCacheKey(finderPath.getCacheName(), cacheKey));
		}

		PortalCache<Serializable, Serializable> portalCache = _getPortalCache(
			finderPath.getCacheName());

		portalCache.remove(cacheKey);

		if (TransactionalPortalCacheUtil.isEnabled() &&
			_isMaintainedCountFinderPath(finderPath)) {

			CountKey countKey = _createCountKey(
				finderPath, cacheKey, productionMode);

			if (countKey != null) {
				TransactionalPortalCacheUtil.put(
					_privateCountPortalCache, countKey, null,
					PortalCache.DEFAULT_TIME_TO_LIVE, true);
			}
		}
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

	private boolean _countMaintenanceEnabled;
	private int _countTimeToLive;
	private final Map<String, Set<String>> _dslQueryCacheNamesMap =
		new ConcurrentHashMap<>();
	private final Map<String, Map<String, FinderPath>> _finderPathsMap =
		new ConcurrentHashMap<>();
	private ThreadLocal<LRUMap<LocalCacheKey, Serializable>> _localCache;

	@Reference
	private MultiVMPool _multiVMPool;

	private final PortalCache<Serializable, PendingCount>
		_pendingCountPortalCache =
			new PortalCacheWrapper<Serializable, PendingCount>(null) {

				@Override
				public boolean isSharded() {
					return PropsValues.DATABASE_PARTITION_ENABLED;
				}

				@Override
				public void put(
					Serializable key, PendingCount pendingCount,
					int timeToLive) {

					_flushPendingCount((CountKey)key, pendingCount);
				}

			};

	private final PortalCache<Serializable, String> _pendingFlushPortalCache =
		new PortalCacheWrapper<Serializable, String>(null) {

			@Override
			public boolean isSharded() {
				return PropsValues.DATABASE_PARTITION_ENABLED;
			}

			@Override
			public void put(Serializable key, String value, int timeToLive) {
			}

		};

	private final ConcurrentMap<String, PortalCache<Serializable, Serializable>>
		_portalCaches = new ConcurrentHashMap<>();

	private final PortalCache<Serializable, Object> _privateCountPortalCache =
		new PortalCacheWrapper<Serializable, Object>(null) {

			@Override
			public boolean isSharded() {
				return PropsValues.DATABASE_PARTITION_ENABLED;
			}

			@Override
			public void put(Serializable key, Object value, int timeToLive) {
				_publishPrivateCount((CountKey)key, value);
			}

			@Override
			public void removeAll() {
			}

		};

	private ServiceRegistration<CacheRegistryItem> _serviceRegistration;
	private ServiceTrackerMap<String, ArgumentsResolverHolder>
		_serviceTrackerMap;
	private boolean _valueObjectFinderCacheEnabled;
	private int _valueObjectFinderCacheListThreshold;

	private static class CountKey implements Serializable {

		@Override
		public boolean equals(Object object) {
			CountKey countKey = (CountKey)object;

			if ((_portalCache == countKey._portalCache) &&
				_cacheKey.equals(countKey._cacheKey)) {

				return true;
			}

			return false;
		}

		@Override
		public int hashCode() {
			return HashUtil.hash(
				System.identityHashCode(_portalCache), _cacheKey.hashCode());
		}

		private CountKey(
			TransactionalPortalCache<Serializable, Serializable> portalCache,
			Serializable cacheKey) {

			_portalCache = portalCache;
			_cacheKey = cacheKey;
		}

		private final Serializable _cacheKey;
		private final transient TransactionalPortalCache
			<Serializable, Serializable> _portalCache;

	}

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

	private static class PendingCount {

		public PendingCount add(AtomicLong atomicLong, long delta) {
			if (atomicLong == _atomicLong) {
				return new PendingCount(atomicLong, _delta + delta);
			}

			return new PendingCount(null, _delta + delta);
		}

		private PendingCount(AtomicLong atomicLong, long delta) {
			_atomicLong = atomicLong;
			_delta = delta;
		}

		private final AtomicLong _atomicLong;
		private final long _delta;

	}

	private static class PrivateCount {

		private PrivateCount(long count, long startSequence) {
			_count = count;
			_startSequence = startSequence;
		}

		private final long _count;
		private final long _startSequence;

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