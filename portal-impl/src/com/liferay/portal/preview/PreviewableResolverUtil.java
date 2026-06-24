/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.model.PersistedModel;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.service.PersistedModelLocalServiceRegistryUtil;

import java.io.Serializable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Shuyang Zhou
 */
public class PreviewableResolverUtil {

	public static Long addPreviewableMap(
		Map<Class<?>, Map<Serializable, Serializable>> previewableMap) {

		Long previewId = _previewIdGenerator.getAndIncrement();

		_previewableMaps.put(previewId, previewableMap);

		return previewId;
	}

	public static Map<Serializable, Serializable> getPreviewableMap(
		Class<?> modelClass) {

		Long previewId = _previewId.get();

		if (previewId == null) {
			return null;
		}

		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
			_previewableMaps.get(previewId);

		if (previewableMap == null) {
			return null;
		}

		return previewableMap.get(modelClass);
	}

	public static Long getPreviewId() {
		return _previewId.get();
	}

	public static Set<Long> getPreviewIds() {
		return _previewableMaps.keySet();
	}

	public static Map<Class<?>, Map<Serializable, Serializable>>
		removePreviewableMap(Long previewId) {

		return _previewableMaps.remove(previewId);
	}

	public static BaseModel<?> resolve(BaseModel<?> baseModel) {
		Long previewId = _previewId.get();

		if (previewId == null) {
			return baseModel;
		}

		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
			_previewableMaps.get(previewId);

		if (MapUtil.isEmpty(previewableMap)) {
			return baseModel;
		}

		Class<?> modelClass = baseModel.getModelClass();

		Map<Serializable, Serializable> pkMap = previewableMap.get(modelClass);

		if (MapUtil.isEmpty(pkMap)) {
			return baseModel;
		}

		Serializable toPK = pkMap.get(baseModel.getPrimaryKeyObj());

		if (toPK == null) {
			return baseModel;
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(modelClass.getName());

		try {
			return (BaseModel<?>)persistedModelLocalService.getPersistedModel(
				toPK);
		}
		catch (PortalException portalException) {
			return ReflectionUtil.throwException(portalException);
		}
	}

	public static Collection<BaseModel<?>> resolve(
		Collection<BaseModel<?>> fromBaseModels,
		Collection<BaseModel<?>> toBaseModels) {

		if (fromBaseModels.isEmpty()) {
			return fromBaseModels;
		}

		Long previewId = _previewId.get();

		if (previewId == null) {
			return fromBaseModels;
		}

		Map<Class<?>, Map<Serializable, Serializable>> previewableMap =
			_previewableMaps.get(previewId);

		if (MapUtil.isEmpty(previewableMap)) {
			return fromBaseModels;
		}

		Iterator<BaseModel<?>> iterator = fromBaseModels.iterator();

		BaseModel<?> baseModel = iterator.next();

		Class<?> modelClass = baseModel.getModelClass();

		Map<Serializable, Serializable> pkMap = previewableMap.get(modelClass);

		if (MapUtil.isEmpty(pkMap)) {
			return fromBaseModels;
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(modelClass.getName());

		Set<Serializable> toPKs = new TreeSet<>();

		for (BaseModel<?> fromBaseModel : fromBaseModels) {
			Serializable toPK = pkMap.get(fromBaseModel.getPrimaryKeyObj());

			if (toPK != null) {
				toPKs.add(toPK);
			}
		}

		Map<Serializable, PersistedModel> toPersistedModels =
			persistedModelLocalService.fetchPersistedModels(toPKs);

		Exception exception = null;

		for (BaseModel<?> fromBaseModel : fromBaseModels) {
			Serializable toPK = pkMap.get(fromBaseModel.getPrimaryKeyObj());

			if (toPK == null) {
				toBaseModels.add(fromBaseModel);
			}
			else {
				PersistedModel toPersistedModel = toPersistedModels.get(toPK);

				if (toPersistedModel == null) {

					// Missing preview target is not allowed, collect exceptions
					// for rethrowing.

					try {
						persistedModelLocalService.getPersistedModel(toPK);
					}
					catch (PortalException portalException) {
						if (exception == null) {
							exception = portalException;
						}
						else {
							exception.addSuppressed(portalException);
						}
					}
				}

				if (toPersistedModel instanceof BaseModel<?> toBaseModel) {
					toBaseModels.add(toBaseModel);
				}
			}
		}

		if (exception != null) {
			ReflectionUtil.throwException(exception);
		}

		return toBaseModels;
	}

	public static SafeCloseable setPreviewIdWithSafeCloseable(Long previewId) {
		return _previewId.setWithSafeCloseable(previewId);
	}

	private static final Map
		<Long, Map<Class<?>, Map<Serializable, Serializable>>>
			_previewableMaps = new ConcurrentHashMap<>();
	private static final CentralizedThreadLocal<Long> _previewId =
		new CentralizedThreadLocal<>(
			PreviewableResolverUtil.class.getName() + "._previewId");
	private static final AtomicLong _previewIdGenerator = new AtomicLong();

}