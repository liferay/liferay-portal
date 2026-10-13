/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.petra.function.UnsafeSupplierValue;
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
		Map<Class<?>, Map<Serializable, Object>> previewableMap) {

		Long previewId = _previewIdGenerator.getAndIncrement();

		_previewableMaps.put(previewId, previewableMap);

		return previewId;
	}

	public static Long getPreviewId() {
		return _previewId.get();
	}

	public static Set<Long> getPreviewIds() {
		return _previewableMaps.keySet();
	}

	public static Map<Serializable, Object> getPreviewableMap(
		Class<?> modelClass) {

		Long previewId = _previewId.get();

		if (previewId == null) {
			return null;
		}

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			_previewableMaps.get(previewId);

		if (previewableMap == null) {
			return null;
		}

		return previewableMap.get(modelClass);
	}

	public static Map<Class<?>, Map<Serializable, Object>> removePreviewableMap(
		Long previewId) {

		return _previewableMaps.remove(previewId);
	}

	public static BaseModel<?> resolve(BaseModel<?> baseModel) {
		Long previewId = _previewId.get();

		if (previewId == null) {
			return baseModel;
		}

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			_previewableMaps.get(previewId);

		if (MapUtil.isEmpty(previewableMap)) {
			return baseModel;
		}

		Class<?> modelClass = baseModel.getModelClass();

		Map<Serializable, Object> pkMap = previewableMap.get(modelClass);

		if (MapUtil.isEmpty(pkMap)) {
			return baseModel;
		}

		Object to = pkMap.get(baseModel.getPrimaryKeyObj());

		if (to == null) {
			return baseModel;
		}

		try {
			if (to instanceof UnsafeSupplierValue<?, ?> unsafeSupplierValue) {
				return (BaseModel<?>)unsafeSupplierValue.getValue();
			}

			PersistedModelLocalService persistedModelLocalService =
				PersistedModelLocalServiceRegistryUtil.
					getPersistedModelLocalService(modelClass.getName());

			return (BaseModel<?>)persistedModelLocalService.getPersistedModel(
				(Serializable)to);
		}
		catch (Throwable throwable) {
			return ReflectionUtil.throwException(throwable);
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

		Map<Class<?>, Map<Serializable, Object>> previewableMap =
			_previewableMaps.get(previewId);

		if (MapUtil.isEmpty(previewableMap)) {
			return fromBaseModels;
		}

		Iterator<BaseModel<?>> iterator = fromBaseModels.iterator();

		BaseModel<?> baseModel = iterator.next();

		Class<?> modelClass = baseModel.getModelClass();

		Map<Serializable, Object> pkMap = previewableMap.get(modelClass);

		if (MapUtil.isEmpty(pkMap)) {
			return fromBaseModels;
		}

		PersistedModelLocalService persistedModelLocalService =
			PersistedModelLocalServiceRegistryUtil.
				getPersistedModelLocalService(modelClass.getName());

		Set<Serializable> toPKs = new TreeSet<>();

		for (BaseModel<?> fromBaseModel : fromBaseModels) {
			Object to = pkMap.get(fromBaseModel.getPrimaryKeyObj());

			if ((to != null) && !(to instanceof UnsafeSupplierValue)) {
				toPKs.add((Serializable)to);
			}
		}

		Map<Serializable, PersistedModel> toPersistedModels =
			persistedModelLocalService.fetchPersistedModels(toPKs);

		Throwable throwable1 = null;

		for (BaseModel<?> fromBaseModel : fromBaseModels) {
			Object to = pkMap.get(fromBaseModel.getPrimaryKeyObj());

			if (to == null) {
				toBaseModels.add(fromBaseModel);
			}
			else if (to instanceof
						UnsafeSupplierValue<?, ?> unsafeSupplierValue) {

				try {
					toBaseModels.add(
						(BaseModel<?>)unsafeSupplierValue.getValue());
				}
				catch (Throwable throwable2) {
					throwable1 = _addSuppressed(throwable1, throwable2);
				}
			}
			else {
				Serializable toPK = (Serializable)to;

				PersistedModel toPersistedModel = toPersistedModels.get(toPK);

				if (toPersistedModel == null) {
					try {
						persistedModelLocalService.getPersistedModel(toPK);
					}
					catch (PortalException portalException) {
						throwable1 = _addSuppressed(
							throwable1, portalException);
					}
				}

				if (toPersistedModel instanceof BaseModel<?> toBaseModel) {
					toBaseModels.add(toBaseModel);
				}
			}
		}

		if (throwable1 != null) {
			ReflectionUtil.throwException(throwable1);
		}

		return toBaseModels;
	}

	public static SafeCloseable setPreviewIdWithSafeCloseable(Long previewId) {
		return _previewId.setWithSafeCloseable(previewId);
	}

	private static Throwable _addSuppressed(
		Throwable throwable1, Throwable throwable2) {

		if (throwable1 == null) {
			return throwable2;
		}

		throwable1.addSuppressed(throwable2);

		return throwable1;
	}

	private static final CentralizedThreadLocal<Long> _previewId =
		new CentralizedThreadLocal<>(
			PreviewableResolverUtil.class.getName() + "._previewId");
	private static final AtomicLong _previewIdGenerator = new AtomicLong();
	private static final Map<Long, Map<Class<?>, Map<Serializable, Object>>>
		_previewableMaps = new ConcurrentHashMap<>();

}