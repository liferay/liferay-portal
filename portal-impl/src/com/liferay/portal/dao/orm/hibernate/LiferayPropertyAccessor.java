/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.TextFormatter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.Map;

import org.hibernate.PropertyAccessException;
import org.hibernate.PropertyNotFoundException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.ReflectHelper;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.GetterMethodImpl;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;
import org.hibernate.property.access.spi.SetterMethodImpl;

/**
 * @author Dante Wang
 */
public class LiferayPropertyAccessor implements PropertyAccessStrategy {

	@Override
	public PropertyAccess buildPropertyAccess(
		Class containerJavaType, String propertyName, boolean setterRequired) {

		return new LiferayPropertyAccess(this, containerJavaType, propertyName);
	}

	public static class LiferayPropertyAccess implements PropertyAccess {

		public LiferayPropertyAccess(
			PropertyAccessStrategy propertyAccessStrategy, Class<?> clazz,
			String propertyName) {

			_propertyAccessStrategy = propertyAccessStrategy;

			_getter = _createGetter(clazz, propertyName);
			_setter = _createSetter(clazz, propertyName);
		}

		@Override
		public Getter getGetter() {
			return _getter;
		}

		@Override
		public PropertyAccessStrategy getPropertyAccessStrategy() {
			return _propertyAccessStrategy;
		}

		@Override
		public Setter getSetter() {
			return _setter;
		}

		protected String formatPropertyName(String propertyName) {
			return TextFormatter.format(propertyName, TextFormatter.G);
		}

		@SuppressWarnings("unchecked")
		private Getter _createGetter(Class<?> clazz, String propertyName)
			throws PropertyNotFoundException {

			String methodNameSuffix = formatPropertyName(propertyName);

			String getterMethodName = "get".concat(methodNameSuffix);

			try {
				Method getterMethod = clazz.getMethod(getterMethodName);

				return new LiferayPropertyGetter(getterMethod, propertyName);
			}
			catch (NoSuchMethodException noSuchMethodException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Getter not found for ", clazz.getName(),
							StringPool.POUND, propertyName),
						noSuchMethodException);
				}

				Method getterMethod = ReflectHelper.findGetterMethod(
					clazz, propertyName);

				return new GetterMethodImpl(clazz, propertyName, getterMethod);
			}
		}

		@SuppressWarnings("unchecked")
		private Setter _createSetter(Class<?> clazz, String propertyName)
			throws PropertyNotFoundException {

			String methodNameSuffix = formatPropertyName(propertyName);

			String getterMethodName = "get".concat(methodNameSuffix);
			String setterMethodName = "set".concat(methodNameSuffix);

			try {
				Method getterMethod = clazz.getMethod(getterMethodName);

				Method setterMethod = clazz.getMethod(
					setterMethodName, getterMethod.getReturnType());

				return new LiferayPropertySetter(setterMethod, propertyName);
			}
			catch (NoSuchMethodException noSuchMethodException) {
				if (_log.isWarnEnabled()) {
					_log.warn(
						StringBundler.concat(
							"Setter not found for ", clazz.getName(),
							StringPool.POUND, propertyName),
						noSuchMethodException);
				}

				Method getterMethod = ReflectHelper.findGetterMethod(
					clazz, propertyName);

				Method setterMethod = ReflectHelper.findSetterMethod(
					clazz, propertyName, getterMethod.getReturnType());

				return new SetterMethodImpl(clazz, propertyName, setterMethod);
			}
		}

		private static final Log _log = LogFactoryUtil.getLog(
			LiferayPropertyAccess.class);

		private final Getter _getter;
		private final PropertyAccessStrategy _propertyAccessStrategy;
		private final Setter _setter;

		private static class LiferayPropertyGetter implements Getter {

			@Override
			public Object get(Object target) throws PropertyAccessException {
				try {
					return _method.invoke(target);
				}
				catch (IllegalAccessException | IllegalArgumentException |
					   InvocationTargetException exception) {

					throw new PropertyAccessException(
						exception, exception.getMessage(), false,
						_method.getDeclaringClass(), _propertyName);
				}
			}

			@Override
			public Object getForInsert(
					Object target, Map mergeMap,
					SharedSessionContractImplementor
						sharedSessionContractImplementor)
				throws PropertyAccessException {

				return get(target);
			}

			@Override
			public Member getMember() {
				return _method;
			}

			@Override
			public Method getMethod() {
				return _method;
			}

			@Override
			public String getMethodName() {
				return _method.getName();
			}

			@Override
			public Type getReturnType() {
				return _method.getReturnType();
			}

			@Override
			public Class<?> getReturnTypeClass() {
				return _method.getReturnType();
			}

			private LiferayPropertyGetter(Method method, String propertyName) {
				_method = method;
				_propertyName = propertyName;
			}

			private final Method _method;
			private final String _propertyName;

		}

		private static class LiferayPropertySetter implements Setter {

			@Override
			public Method getMethod() {
				return _method;
			}

			@Override
			public String getMethodName() {
				return _method.getName();
			}

			@Override
			public void set(Object target, Object value)
				throws PropertyAccessException {

				try {
					_method.invoke(target, value);
				}
				catch (IllegalAccessException | IllegalArgumentException |
					   InvocationTargetException | NullPointerException
						   exception) {

					throw new PropertyAccessException(
						exception, exception.getMessage(), true,
						_method.getDeclaringClass(), _propertyName);
				}
			}

			private LiferayPropertySetter(Method method, String propertyName) {
				_method = method;
				_propertyName = propertyName;
			}

			private final Method _method;
			private final String _propertyName;

		}

	}

}