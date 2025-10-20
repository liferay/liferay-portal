/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.impl;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.cache.CacheRegistryItem;
import com.liferay.portal.kernel.change.tracking.CTAware;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.model.ModelHintsUtil;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.transaction.Transactional;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.model.impl.ClassNameImpl;
import com.liferay.portal.service.base.ClassNameLocalServiceBaseImpl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * @author Brian Wing Shun Chan
 */
@CTAware
public class ClassNameLocalServiceImpl
	extends ClassNameLocalServiceBaseImpl implements CacheRegistryItem {

	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public ClassName addClassName(String value) {
		ClassName className = classNamePersistence.fetchByValue(value);

		if (className == null) {
			long classNameId = counterLocalService.increment();

			className = classNamePersistence.create(classNameId);

			className.setValue(value);

			className = classNamePersistence.update(className);
		}

		ClassNamePool.add(className);

		return className;
	}

	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public void checkClassNames() {
		List<ClassName> classNames = classNamePersistence.findAll();

		for (ClassName className : classNames) {
			ClassNamePool.add(className);
		}

		List<String> models = ModelHintsUtil.getModels();

		for (String model : models) {
			getClassName(model);
		}
	}

	@Override
	public ClassName deleteClassName(ClassName className) {
		ClassName removedClassName = classNamePersistence.remove(className);

		ClassNamePool.remove(className);

		return removedClassName;
	}

	@Override
	public ClassName fetchByClassNameId(long classNameId) {
		ClassName className = ClassNamePool.fetchByClassNameId(classNameId);

		if (className == null) {
			className = classNamePersistence.fetchByPrimaryKey(classNameId);
		}

		ClassNamePool.add(className);

		return className;
	}

	@Override
	public ClassName fetchClassName(String value) {
		if (Validator.isNull(value)) {
			return _nullClassName;
		}

		ClassName className = ClassNamePool.fetchByValue(value);

		if (className == null) {
			className = classNamePersistence.fetchByValue(value);
		}

		if (className == null) {
			return _nullClassName;
		}

		ClassNamePool.add(className);

		return className;
	}

	@Override
	@Transactional(enabled = false)
	public ClassName getClassName(String value) {
		if (Validator.isNull(value)) {
			return _nullClassName;
		}

		// Always cache the class name. This table exists to improve
		// performance. Create the class name if one does not exist.

		ClassName className = ClassNamePool.fetchByValue(value);

		if (className != null) {
			return className;
		}

		try {
			return classNameLocalService.addClassName(value);
		}
		catch (Throwable throwable) {
			if (_log.isDebugEnabled()) {
				_log.debug(throwable);
			}

			return ClassNamePool.fetchByValue(value);
		}
	}

	@Override
	@Transactional(enabled = false)
	public long getClassNameId(Class<?> clazz) {
		return getClassNameId(clazz.getName());
	}

	@Override
	@Transactional(enabled = false)
	public long getClassNameId(String value) {
		ClassName className = getClassName(value);

		return className.getClassNameId();
	}

	@Override
	public Supplier<long[]> getClassNameIdsSupplier(String[] classNames) {
		Map<Long, long[]> classNameIds = new ConcurrentHashMap<>();

		return () -> classNameIds.computeIfAbsent(
			_getCompanyId(),
			key -> TransformUtil.transformToLongArray(
				ListUtil.fromArray(classNames),
				className -> getClassNameId(className)));
	}

	@Override
	public Supplier<Long> getClassNameIdSupplier(String className) {
		return () -> getClassNameId(className);
	}

	@Override
	public String getRegistryName() {
		return ClassNameLocalServiceImpl.class.getName();
	}

	@Override
	public void invalidate() {
		if (PropsValues.DATABASE_PARTITION_ENABLED &&
			(CompanyThreadLocal.getCompanyId() != CompanyConstants.SYSTEM)) {

			ClassNamePool.invalidate(CompanyThreadLocal.getCompanyId());

			return;
		}

		ClassNamePool.invalidate();
	}

	private static long _getCompanyId() {
		if (PropsValues.DATABASE_PARTITION_ENABLED) {
			return CompanyThreadLocal.getNonsystemCompanyId();
		}

		return CompanyConstants.SYSTEM;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		ClassNameLocalServiceImpl.class);

	private static final ClassName _nullClassName = new ClassNameImpl();

	private static class ClassNamePool {

		public static void add(ClassName className) {
			if (className == null) {
				return;
			}

			Map<String, Long> classNameIds = _getMap(_classNameIdsMap);

			classNameIds.put(className.getValue(), className.getClassNameId());

			Map<Long, ClassName> classNames = _getMap(_classNamesMap);

			classNames.put(className.getClassNameId(), className);
		}

		public static ClassName fetchByClassNameId(long classNameId) {
			Map<Long, ClassName> classNames = _getMap(_classNamesMap);

			return classNames.get(classNameId);
		}

		public static ClassName fetchByValue(String value) {
			Map<String, Long> classNameIds = _getMap(_classNameIdsMap);

			Long classNameId = classNameIds.get(value);

			if (classNameId == null) {
				return null;
			}

			Map<Long, ClassName> classNames = _getMap(_classNamesMap);

			return classNames.get(classNameId);
		}

		public static void invalidate() {
			for (Map<String, Long> map : _classNameIdsMap.values()) {
				map.clear();
			}

			for (Map<Long, ClassName> map : _classNamesMap.values()) {
				map.clear();
			}
		}

		public static void invalidate(long companyId) {
			_classNameIdsMap.remove(companyId);
			_classNamesMap.remove(companyId);
		}

		public static void remove(ClassName className) {
			_classNameIdsMap.computeIfPresent(
				_getCompanyId(),
				(key, classNameIds) -> {
					classNameIds.remove(className.getValue());

					return classNameIds;
				});

			_classNamesMap.computeIfPresent(
				_getCompanyId(),
				(key, classNames) -> {
					classNames.remove(className.getClassNameId());

					return classNames;
				});
		}

		private static <S, T> Map<S, T> _getMap(Map<Long, Map<S, T>> map) {
			return map.computeIfAbsent(
				_getCompanyId(), companyId -> new ConcurrentHashMap<>());
		}

		private static Map<Long, Map<String, Long>> _classNameIdsMap =
			new ConcurrentHashMap<>();
		private static Map<Long, Map<Long, ClassName>> _classNamesMap =
			new ConcurrentHashMap<>();

	}

}