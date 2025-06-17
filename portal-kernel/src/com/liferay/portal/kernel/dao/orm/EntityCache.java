/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.orm;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.CacheModel;

import java.io.Serializable;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface EntityCache {

	public void clearCache();

	public void clearCache(Class<?> clazz);

	public void clearLocalCache();

	public <T extends CacheModel<?>> T fetchCacheModel(
		Class<?> clazz, Serializable primaryKey, Class<T> cacheModelClass);

	public Serializable getLocalCacheResult(
		Class<?> clazz, Serializable primaryKey);

	public PortalCache<Serializable, Serializable> getPortalCache(
		Class<?> clazz);

	public Serializable getResult(Class<?> clazz, Serializable primaryKey);

	public void invalidate();

	public void putResult(
		Class<?> clazz, BaseModel<?> baseModel, boolean quiet,
		boolean updateFinderCache);

	public void putResult(
		Class<?> clazz, Serializable primaryKey, Serializable result);

	public void removeCache(String className);

	public void removeResult(Class<?> clazz, BaseModel<?> baseModel);

	public void removeResult(Class<?> clazz, Serializable primaryKey);

}