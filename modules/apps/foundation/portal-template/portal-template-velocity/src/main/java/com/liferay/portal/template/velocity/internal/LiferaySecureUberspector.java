/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.portal.template.velocity.internal;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.AggregateClassLoader;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortalImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.util.introspection.SecureIntrospectorImpl;
import org.apache.velocity.util.introspection.SecureUberspector;

/**
 * @author Tomas Polesovsky
 */
public class LiferaySecureUberspector extends SecureUberspector {

	@Override
	public void init() {
		super.init();

		ExtendedProperties extendedProperties =
			_runtimeServices.getConfiguration();

		String[] restrictedClassNames = extendedProperties.getStringArray(
			RuntimeConstants.INTROSPECTOR_RESTRICT_CLASSES);

		_restrictedClasses = new ArrayList<>(restrictedClassNames.length);

		AggregateClassLoader aggregateClassLoader = new AggregateClassLoader(
			LiferaySecureUberspector.class.getClassLoader());

		aggregateClassLoader.addClassLoader(PortalImpl.class.getClassLoader());

		Thread thread = Thread.currentThread();

		if (thread.getContextClassLoader() != null) {
			aggregateClassLoader.addClassLoader(thread.getContextClassLoader());
		}

		for (String restrictedClassName : restrictedClassNames) {
			restrictedClassName = StringUtil.trim(restrictedClassName);

			if (Validator.isBlank(restrictedClassName)) {
				continue;
			}

			try {
				_restrictedClasses.add(
					aggregateClassLoader.loadClass(restrictedClassName));
			}
			catch (ClassNotFoundException cnfe) {
				super.log.error(
					"Unable to find restricted class " + restrictedClassName,
					cnfe);
			}
		}

		String[] restrictedPackageNames = extendedProperties.getStringArray(
			RuntimeConstants.INTROSPECTOR_RESTRICT_PACKAGES);

		if (restrictedPackageNames != null) {
			_restrictedPackageNames = new ArrayList<>(
				restrictedPackageNames.length);

			for (String restrictedPackageName : restrictedPackageNames) {
				restrictedPackageName = StringUtil.trim(restrictedPackageName);

				if (Validator.isBlank(restrictedPackageName)) {
					continue;
				}

				_restrictedPackageNames.add(restrictedPackageName);
			}
		}

		super.introspector = new LiferaySecureIntrospectorImpl();
	}

	@Override
	public void setRuntimeServices(RuntimeServices runtimeServices) {
		super.setRuntimeServices(runtimeServices);

		_runtimeServices = runtimeServices;
	}

	private void _checkClassIsRestricted(Class<?> clazz) {
		ClassRestrictionInformation classRestrictionInformation =
			_classRestrictionInformations.computeIfAbsent(
				clazz.getName(),
				className -> {
					for (Class<?> restrictedClass : _restrictedClasses) {
						if (!restrictedClass.isAssignableFrom(clazz)) {
							continue;
						}

						return new ClassRestrictionInformation(
							StringBundler.concat(
								"Denied resolving class ", className, " by ",
								restrictedClass.getName()));
					}

					Package pkg = clazz.getPackage();

					if (pkg == null) {
						return _nullInstance;
					}

					String packageName = pkg.getName();

					packageName = packageName.concat(StringPool.PERIOD);

					for (String restrictedPackageName :
							_restrictedPackageNames) {

						if (!packageName.startsWith(restrictedPackageName)) {
							continue;
						}

						return new ClassRestrictionInformation(
							StringBundler.concat(
								"Denied resolving class ", className, " by ",
								restrictedPackageName));
					}

					return _nullInstance;
				});

		if (classRestrictionInformation.isRestricted()) {
			throw new IllegalArgumentException(
				classRestrictionInformation.getDescription());
		}
	}

	private static final ClassRestrictionInformation _nullInstance =
		new ClassRestrictionInformation(null);

	private final Map<String, ClassRestrictionInformation>
		_classRestrictionInformations = new ConcurrentHashMap<>();
	private List<Class<?>> _restrictedClasses;
	private List<String> _restrictedPackageNames;
	private RuntimeServices _runtimeServices;

	private static class ClassRestrictionInformation {

		public String getDescription() {
			return _description;
		}

		public boolean isRestricted() {
			if (_description == null) {
				return false;
			}

			return true;
		}

		private ClassRestrictionInformation(String description) {
			_description = description;
		}

		private final String _description;

	}

	private class LiferaySecureIntrospectorImpl extends SecureIntrospectorImpl {

		@Override
		@SuppressWarnings("rawtypes")
		public boolean checkObjectExecutePermission(
			Class clazz, String methodName) {

			if ((methodName != null) && (methodName.equals("wait") ||
			 methodName.equals("notify")))
			{
				throw new IllegalArgumentException(
					"Executing method " + methodName + " is not allowed");
			}

			_checkClassIsRestricted(clazz);

			return true;
		}

		private LiferaySecureIntrospectorImpl() {
			super(
				new String[0], new String[0],
				LiferaySecureUberspector.this.log);
		}

	}

}