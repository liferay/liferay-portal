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
import com.liferay.portal.kernel.model.impl.BaseModelImpl;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

import java.util.Map;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.property.access.spi.Getter;
import org.hibernate.property.access.spi.PropertyAccess;
import org.hibernate.property.access.spi.PropertyAccessStrategy;
import org.hibernate.property.access.spi.Setter;

/**
 * @author Shuyang Zhou
 */
public class PrivateFieldPropertyAccessor implements PropertyAccessStrategy {

	public PrivateFieldPropertyAccessor() {
		this(StringPool.UNDERLINE);
	}

	public PrivateFieldPropertyAccessor(String prefix) {
		_prefix = prefix;
	}

	@Override
	public PropertyAccess buildPropertyAccess(
		Class clazz, String propertyName) {

		String fieldName;

		if (_prefix.isEmpty()) {
			fieldName = propertyName;
		}
		else {
			fieldName = _prefix.concat(propertyName);
		}

		return _propertyAccesses.computeIfAbsent(
			StringBundler.concat(
				clazz.hashCode(), StringPool.POUND, clazz.getName(),
				StringPool.POUND, propertyName),
			key -> new VarHandlePropertyAccess(
				new VarHandleHolder(clazz, fieldName)));
	}

	private static final Map<String, PropertyAccess> _propertyAccesses =
		new ConcurrentReferenceValueHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);

	private final String _prefix;

	private static class FieldGetter implements Getter {

		@Override
		public Object get(Object target) {
			VarHandle varHandle = _varHandleHolder.getVarHandle();

			return varHandle.get(target);
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
			VarHandle verHandle = _varHandleHolder.getVarHandle();

			return verHandle.varType();
		}

		private FieldGetter(VarHandleHolder varHandleHolder) {
			_varHandleHolder = varHandleHolder;
		}

		private final VarHandleHolder _varHandleHolder;

	}

	private static class FieldSetter implements Setter {

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
			SessionFactoryImplementor sessionFactoryImplementor) {

			VarHandle varHandle = _varHandleHolder.getVarHandle();

			varHandle.set(target, value);
		}

		private FieldSetter(VarHandleHolder varHandleHolder) {
			_varHandleHolder = varHandleHolder;
		}

		private final VarHandleHolder _varHandleHolder;

	}

	private static class VarHandleHolder {

		public VarHandle getVarHandle() {
			if (_varHandle == null) {
				Class<?> modelClass = _containerJavaType;

				if (BaseModelImpl.class.isAssignableFrom(modelClass)) {
					Class<?> superClass = modelClass.getSuperclass();

					while (BaseModelImpl.class != superClass) {
						modelClass = superClass;

						superClass = modelClass.getSuperclass();
					}
				}

				MethodHandles.Lookup lookup = ReflectionUtil.getImplLookup();

				try {
					_varHandle = lookup.unreflectVarHandle(
						modelClass.getDeclaredField(_propertyName));
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					return ReflectionUtil.throwException(
						reflectiveOperationException);
				}
			}

			return _varHandle;
		}

		private VarHandleHolder(
			Class<?> containerJavaType, String propertyName) {

			_containerJavaType = containerJavaType;
			_propertyName = propertyName;
		}

		private final Class<?> _containerJavaType;
		private final String _propertyName;
		private VarHandle _varHandle;

	}

	private class VarHandlePropertyAccess implements PropertyAccess {

		@Override
		public Getter getGetter() {
			return _getter;
		}

		@Override
		public PropertyAccessStrategy getPropertyAccessStrategy() {
			return PrivateFieldPropertyAccessor.this;
		}

		@Override
		public Setter getSetter() {
			return _setter;
		}

		private VarHandlePropertyAccess(VarHandleHolder varHandleHolder) {
			_getter = new FieldGetter(varHandleHolder);
			_setter = new FieldSetter(varHandleHolder);
		}

		private final Getter _getter;
		private final Setter _setter;

	}

}