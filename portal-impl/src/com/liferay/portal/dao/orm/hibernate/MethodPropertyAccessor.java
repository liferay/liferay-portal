/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.dao.orm.hibernate;

import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import java.util.Map;

import org.hibernate.PropertyAccessException;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

/**
 * @author Shuyang Zhou
 */
public class MethodPropertyAccessor implements PropertyAccessStrategy {

	@Override
	public PropertyAccess buildPropertyAccess(
		Class clazz, String propertyName) {

		String key = StringBundler.concat(
			clazz.hashCode(), StringPool.POUND, clazz.getName(),
			StringPool.POUND, propertyName);

		PropertyAccess propertyAccess = _propertyAccesses.get(key);

		if (propertyAccess == null) {
			propertyAccess = new MethodPropertyAccess(
				this, clazz, propertyName);

			_propertyAccesses.put(key, propertyAccess);
		}

		return propertyAccess;
	}

	private static final Map<String, PropertyAccess> _propertyAccesses =
		new ConcurrentReferenceValueHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);

	private static class MethodHolder {

		public MethodHandle getGetterMethodHandle() {
			if (_getterMethodHandle == null) {
				_initialize();
			}

			return _getterMethodHandle;
		}

		public MethodHandle getSetterMethodHandle() {
			if (_setterMethodHandle == null) {
				_initialize();
			}

			return _setterMethodHandle;
		}

		private MethodHolder(Class<?> clazz, String propertyName) {
			_clazz = clazz;
			_propertyName = propertyName;
		}

		private String _getMethodName1() {
			StringBuilder sb = new StringBuilder(_propertyName);

			char c = sb.charAt(0);

			if ((c >= 'a') && (c <= 'z')) {
				sb.setCharAt(0, (char)(c - 32));
			}

			return sb.toString();
		}

		private String _getMethodName2() {
			StringBuilder sb = new StringBuilder(_propertyName);

			for (int i = 0; i < sb.length(); i++) {
				char c = sb.charAt(i);

				if ((c >= 'a') && (c <= 'z')) {
					sb.setCharAt(i, (char)(c - 32));
				}
				else {
					break;
				}
			}

			return sb.toString();
		}

		private void _initialize() {
			MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

			try {
				String methodName = _getMethodName1();

				Method getterMethod = ReflectionUtil.fetchMethod(
					_clazz, "get".concat(methodName));

				if (getterMethod == null) {
					methodName = _getMethodName2();

					getterMethod = ReflectionUtil.fetchMethod(
						_clazz, "get".concat(methodName));
				}

				if (getterMethod == null) {
					ReflectionUtil.throwException(
						new NoSuchMethodException(
							StringBundler.concat(
								"Unable to locate getter method for class ",
								_clazz.getName(), " and property ",
								_propertyName)));
				}

				_getterMethodHandle = lookup.unreflect(getterMethod);

				_setterMethodHandle = lookup.findVirtual(
					_clazz, "set".concat(methodName),
					MethodType.methodType(
						void.class, getterMethod.getReturnType()));
			}
			catch (ReflectiveOperationException reflectiveOperationException) {
				ReflectionUtil.throwException(reflectiveOperationException);
			}
		}

		private final Class<?> _clazz;
		private MethodHandle _getterMethodHandle;
		private final String _propertyName;
		private MethodHandle _setterMethodHandle;

	}

	private static class MethodPropertyAccess implements PropertyAccess {

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

		private MethodPropertyAccess(
			PropertyAccessStrategy propertyAccessStrategy, Class<?> clazz,
			String propertyName) {

			_propertyAccessStrategy = propertyAccessStrategy;

			MethodHolder methodHolder = new MethodHolder(clazz, propertyName);

			_getter = new MethodPropertyGetter(methodHolder);
			_setter = new MethodPropertySetter(methodHolder);
		}

		private final Getter _getter;
		private final PropertyAccessStrategy _propertyAccessStrategy;
		private final Setter _setter;

	}

	private static class MethodPropertyGetter implements Getter {

		@Override
		public Object get(Object target) {
			try {
				MethodHandle getterMethodHandle =
					_methodHolder.getGetterMethodHandle();

				return getterMethodHandle.invoke(target);
			}
			catch (Throwable throwable) {
				return ReflectionUtil.throwException(throwable);
			}
		}

		@Override
		public Object getForInsert(
			Object target, Map mergeMap,
			SharedSessionContractImplementor sharedSessionContractImplementor) {

			return get(target);
		}

		@Override
		public Member getMember() {
			return null;
		}

		@Override
		public Method getMethod() {
			return null;
		}

		@Override
		public String getMethodName() {
			return null;
		}

		@Override
		public Class getReturnType() {
			MethodHandle getterMethodHandle =
				_methodHolder.getGetterMethodHandle();

			MethodType methodType = getterMethodHandle.type();

			return methodType.returnType();
		}

		private MethodPropertyGetter(MethodHolder methodHolder) {
			_methodHolder = methodHolder;
		}

		private final MethodHolder _methodHolder;

	}

	private static class MethodPropertySetter implements Setter {

		@Override
		public Method getMethod() {
			return null;
		}

		@Override
		public String getMethodName() {
			return null;
		}

		@Override
		public void set(
				Object target, Object value,
				SessionFactoryImplementor sessionFactoryImplementor)
			throws PropertyAccessException {

			try {
				MethodHandle setterMethodHandle =
					_methodHolder.getSetterMethodHandle();

				setterMethodHandle.invoke(target, value);
			}
			catch (Throwable throwable) {
				ReflectionUtil.throwException(throwable);
			}
		}

		private MethodPropertySetter(MethodHolder methodHolder) {
			_methodHolder = methodHolder;
		}

		private final MethodHolder _methodHolder;

	}

}