/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.util.ProxyFactory;

import java.io.Serializable;

/**
 * @author Brian Wing Shun Chan
 */
public class EntityCacheUtil {

	public static void clearCache() {
		EntityCache entityCache = getEntityCache();

		entityCache.clearCache();
	}

	public static void clearCache(Class<?> clazz) {
		EntityCache entityCache = getEntityCache();

		entityCache.clearCache(clazz);
	}

	public static void clearLocalCache() {
		EntityCache entityCache = getEntityCache();

		entityCache.clearLocalCache();
	}

	public static <T extends CacheModel<?>> T fetchCacheModel(
		Class<?> clazz, Serializable primaryKey, Class<T> cacheModelClass) {

		EntityCache entityCache = getEntityCache();

		return entityCache.fetchCacheModel(clazz, primaryKey, cacheModelClass);
	}

	public static EntityCache getEntityCache() {
		EntityCache entityCache = _entityCacheSnapshot.get();

		if (entityCache == null) {
			return DummyEntityCacheHolder._dummyEntityCache;
		}

		return entityCache;
	}

	public static Serializable getLocalCacheResult(
		Class<?> clazz, Serializable primaryKey) {

		EntityCache entityCache = getEntityCache();

		return entityCache.getLocalCacheResult(clazz, primaryKey);
	}

	public static PortalCache<Serializable, Serializable> getPortalCache(
		Class<?> clazz) {

		EntityCache entityCache = getEntityCache();

		return entityCache.getPortalCache(clazz);
	}

	public static Serializable getResult(
		Class<?> clazz, Serializable primaryKey) {

		EntityCache entityCache = getEntityCache();

		return entityCache.getResult(clazz, primaryKey);
	}

	public static void invalidate() {
		EntityCache entityCache = getEntityCache();

		entityCache.invalidate();
	}

	public static void putResult(
		Class<?> clazz, BaseModel<?> baseModel, boolean quiet,
		boolean updateFinderCache) {

		EntityCache entityCache = getEntityCache();

		entityCache.putResult(clazz, baseModel, quiet, updateFinderCache);
	}

	public static void putResult(
		Class<?> clazz, Serializable primaryKey, Serializable result) {

		EntityCache entityCache = getEntityCache();

		entityCache.putResult(clazz, primaryKey, result);
	}

	public static void removeCache(String className) {
		EntityCache entityCache = getEntityCache();

		entityCache.removeCache(className);
	}

	public static void removeResult(Class<?> clazz, BaseModel<?> baseModel) {
		EntityCache entityCache = getEntityCache();

		entityCache.removeResult(clazz, baseModel);
	}

	public static void removeResult(Class<?> clazz, Serializable primaryKey) {
		EntityCache entityCache = getEntityCache();

		entityCache.removeResult(clazz, primaryKey);
	}

	private static final Snapshot<EntityCache> _entityCacheSnapshot =
		new Snapshot<>(EntityCacheUtil.class, EntityCache.class);

	private static class DummyEntityCacheHolder {

		private static final EntityCache _dummyEntityCache =
			ProxyFactory.newDummyInstance(EntityCache.class);

	}

}