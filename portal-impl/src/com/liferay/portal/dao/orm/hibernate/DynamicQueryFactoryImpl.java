/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.portal.kernel.annotation.ImplementationClassName;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * @author Brian Wing Shun Chan
 */
public class DynamicQueryFactoryImpl implements DynamicQueryFactory {

	@Override
	public DynamicQuery forClass(Class<?> clazz, ClassLoader classLoader) {
		return new DynamicQueryImpl();
	}

	@Override
	public DynamicQuery forClass(
		Class<?> clazz, String alias, ClassLoader classLoader) {

		return new DynamicQueryImpl();
	}

	protected Class<?> getImplClass(Class<?> clazz, ClassLoader classLoader) {
		ImplementationClassName implementationClassName = clazz.getAnnotation(
			ImplementationClassName.class);

		if (implementationClassName == null) {
			String className = clazz.getName();

			if (!className.endsWith("Impl")) {
				_log.error("Unable find model for " + clazz);
			}

			return clazz;
		}

		Class<?> implClass = clazz;

		String implClassName = implementationClassName.value();

		try {
			implClass = getImplClass(implClassName, classLoader);
		}
		catch (Exception exception1) {
			if (classLoader != _portalClassLoader) {
				try {
					implClass = getImplClass(implClassName, _portalClassLoader);
				}
				catch (Exception exception2) {
					_log.error(
						"Unable find model " + implClassName, exception2);
				}
			}
			else {
				_log.error("Unable find model " + implClassName, exception1);
			}
		}

		return implClass;
	}

	protected Class<?> getImplClass(
			String implClassName, ClassLoader classLoader)
		throws ClassNotFoundException {

		return classLoader.loadClass(implClassName);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicQueryFactoryImpl.class);

	private final ClassLoader _portalClassLoader =
		DynamicQueryFactoryImpl.class.getClassLoader();

}