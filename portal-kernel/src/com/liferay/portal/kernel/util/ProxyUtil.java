/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.concurrent.ConcurrentReferenceValueHashMap;
import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.petra.reflect.ReflectionUtil;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author Shuyang Zhou
 */
public class ProxyUtil {

	public static <T extends InvocationHandler> T fetchInvocationHandler(
		Object proxy, Class<T> clazz) {

		if (!isProxyClass(proxy.getClass())) {
			return null;
		}

		try {
			InvocationHandler invocationHandler =
				(InvocationHandler)_invocationHandlerField.get(proxy);

			if (clazz.isInstance(invocationHandler)) {
				return clazz.cast(invocationHandler);
			}

			return null;
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new IllegalArgumentException(illegalAccessException);
		}
	}

	public static InvocationHandler getInvocationHandler(Object proxy) {
		if (!isProxyClass(proxy.getClass())) {
			throw new IllegalArgumentException("Not a proxy instance");
		}

		try {
			return (InvocationHandler)_invocationHandlerField.get(proxy);
		}
		catch (IllegalAccessException illegalAccessException) {
			throw new IllegalArgumentException(illegalAccessException);
		}
	}

	public static Class<?> getProxyClass(
		ClassLoader classLoader, Class<?>... interfaceClasses) {

		ConcurrentMap<LookupKey, Class<?>> classReferences =
			_classReferences.get(classLoader);

		if (classReferences == null) {
			classReferences = new ConcurrentReferenceValueHashMap<>(
				FinalizeManager.WEAK_REFERENCE_FACTORY);

			ConcurrentMap<LookupKey, Class<?>> oldClassReferences =
				_classReferences.putIfAbsent(classLoader, classReferences);

			if (oldClassReferences != null) {
				classReferences = oldClassReferences;
			}
		}

		LookupKey lookupKey = new LookupKey(interfaceClasses);

		Class<?> clazz = classReferences.get(lookupKey);

		if (clazz == null) {
			synchronized (classReferences) {
				clazz = classReferences.get(lookupKey);

				if (clazz == null) {
					clazz = Proxy.getProxyClass(classLoader, interfaceClasses);

					classReferences.put(lookupKey, clazz);
				}
			}
		}

		ConstructorReference constructorReference = _constructorReferences.get(
			clazz);

		if (constructorReference == null) {
			_constructorReferences.putIfAbsent(
				clazz, new ConstructorReference());
		}

		return clazz;
	}

	public static <T> Function<InvocationHandler, T> getProxyProviderFunction(
		Class<?>... interfaceClasses) {

		ClassLoader classLoader = interfaceClasses[0].getClassLoader();

		if (classLoader == null) {
			classLoader = ClassLoader.getSystemClassLoader();
		}

		Class<?> proxyClass = getProxyClass(classLoader, interfaceClasses);

		try {
			Constructor<T> constructor =
				(Constructor<T>)proxyClass.getConstructor(_ARGUMENTS_CLASS);

			return invocationHandler -> {
				try {
					return constructor.newInstance(invocationHandler);
				}
				catch (ReflectiveOperationException
							reflectiveOperationException) {

					throw new InternalError(reflectiveOperationException);
				}
			};
		}
		catch (NoSuchMethodException noSuchMethodException) {
			throw new InternalError(noSuchMethodException);
		}
	}

	public static boolean isProxyClass(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException();
		}

		return _constructorReferences.containsKey(clazz);
	}

	public static <T> T newDelegateProxyInstance(
		ClassLoader classLoader, Class<T> interfaceClass, Object delegateObject,
		T defaultObject) {

		return (T)newProxyInstance(
			classLoader, new Class<?>[] {interfaceClass},
			new DelegateInvocationHandler(
				interfaceClass, delegateObject, defaultObject));
	}

	public static <T> T newLazyDelegateProxyInstance(
		ClassLoader classLoader, Class<T> interfaceClass,
		Supplier<T> delegateObjectSupplier) {

		return (T)newProxyInstance(
			classLoader, new Class<?>[] {interfaceClass},
			new LazyDelegateInvocationHandler(delegateObjectSupplier));
	}

	public static Object newProxyInstance(
		ClassLoader classLoader, Class<?>[] interfaces,
		InvocationHandler invocationHandler) {

		Class<?> proxyClass = getProxyClass(classLoader, interfaces);

		ConstructorReference constructorHolder = _constructorReferences.get(
			proxyClass);

		return constructorHolder.newInstance(proxyClass, invocationHandler);
	}

	private static final Class<?>[] _ARGUMENTS_CLASS = {
		InvocationHandler.class
	};

	private static final ConcurrentMap
		<ClassLoader, ConcurrentMap<LookupKey, Class<?>>> _classReferences =
			new ConcurrentReferenceKeyHashMap<>(
				FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final ConcurrentMap<Class<?>, ConstructorReference>
		_constructorReferences = new ConcurrentReferenceKeyHashMap<>(
			FinalizeManager.WEAK_REFERENCE_FACTORY);
	private static final Field _invocationHandlerField;

	static {
		try {
			_invocationHandlerField = ReflectionUtil.getDeclaredField(
				Proxy.class, "h");
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private static class ConstructorReference {

		public Object newInstance(
			Class<?> proxyClass, InvocationHandler invocationHandler) {

			Constructor<?> constructor = null;

			Reference<Constructor<?>> reference = _reference;

			try {
				if ((reference == null) ||
					((constructor = reference.get()) == null)) {

					constructor = proxyClass.getConstructor(_ARGUMENTS_CLASS);

					constructor.setAccessible(true);

					_reference = new WeakReference<>(constructor);
				}

				return constructor.newInstance(
					new Object[] {invocationHandler});
			}
			catch (ReflectiveOperationException reflectiveOperationException) {
				throw new InternalError(reflectiveOperationException);
			}
		}

		private volatile Reference<Constructor<?>> _reference;

	}

	private static class DelegateInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object object, Method method, Object[] args)
			throws Throwable {

			Method delegateMethod = _delegateMethods.get(method);

			try {
				if (delegateMethod != null) {
					return delegateMethod.invoke(_delegateObject, args);
				}

				return method.invoke(_defaultObject, args);
			}
			catch (InvocationTargetException invocationTargetException) {
				throw invocationTargetException.getTargetException();
			}
		}

		private DelegateInvocationHandler(
			Class<?> interfaceClass, Object delegateObject,
			Object defaultObject) {

			Map<Method, Method> delegateMethods = new HashMap<>();

			Class<?> delegateClass = delegateObject.getClass();

			for (Method delegateMethod : delegateClass.getDeclaredMethods()) {
				int modifiers = delegateMethod.getModifiers();

				if (!Modifier.isPublic(modifiers) ||
					Modifier.isStatic(modifiers)) {

					continue;
				}

				Method objectMethod = _toObjectMethod(delegateMethod);

				if (objectMethod == null) {
					try {
						Method interfaceMethod = interfaceClass.getMethod(
							delegateMethod.getName(),
							delegateMethod.getParameterTypes());

						delegateMethod.setAccessible(true);

						delegateMethods.put(interfaceMethod, delegateMethod);
					}
					catch (NoSuchMethodException noSuchMethodException) {
					}
				}
				else {
					delegateMethods.put(objectMethod, delegateMethod);
				}
			}

			_delegateMethods = delegateMethods;

			_delegateObject = delegateObject;
			_defaultObject = defaultObject;
		}

		private Method _toObjectMethod(Method method) {
			String name = method.getName();
			Class<?>[] parameterTypes = method.getParameterTypes();

			if (name.equals("equals") && (parameterTypes.length == 1) &&
				(parameterTypes[0] == Object.class)) {

				return _equalsMethod;
			}

			if (name.equals("hashCode") && (parameterTypes.length == 0)) {
				return _hashCodeMethod;
			}

			if (name.equals("toString") && (parameterTypes.length == 0)) {
				return _toStringMethod;
			}

			return null;
		}

		private static final Method _equalsMethod = ReflectionUtil.fetchMethod(
			Object.class, "equals", Object.class);
		private static final Method _hashCodeMethod =
			ReflectionUtil.fetchMethod(Object.class, "hashCode");
		private static final Method _toStringMethod =
			ReflectionUtil.fetchMethod(Object.class, "toString");

		private final Object _defaultObject;
		private final Map<Method, Method> _delegateMethods;
		private final Object _delegateObject;

	}

	private static class LazyDelegateInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

			if (_delegateObject == null) {
				_delegateObject = _delegateObjectSupplier.get();
			}

			return method.invoke(_delegateObject, args);
		}

		private LazyDelegateInvocationHandler(
			Supplier<?> delegateObjectSupplier) {

			_delegateObjectSupplier = delegateObjectSupplier;
		}

		private Object _delegateObject;
		private final Supplier<?> _delegateObjectSupplier;

	}

	private static class LookupKey {

		@Override
		public boolean equals(Object object) {
			if (object == this) {
				return true;
			}

			LookupKey lookupKey = (LookupKey)object;

			Reference<?>[] references = lookupKey._references;

			if (_references.length != references.length) {
				return false;
			}

			for (int i = 0; i < _references.length; i++) {
				Reference<?> reference = _references[i];
				Reference<?> otherReference = references[i];

				if (reference.get() != otherReference.get()) {
					return false;
				}
			}

			return true;
		}

		@Override
		public int hashCode() {
			return _hashCode;
		}

		private LookupKey(Class<?>[] interfaces) {
			int hashCode = 0;

			_references = new Reference<?>[interfaces.length];

			for (int i = 0; i < interfaces.length; i++) {
				Class<?> clazz = interfaces[i];

				hashCode = HashUtil.hash(hashCode, clazz.getName());

				_references[i] = new WeakReference<>(clazz);
			}

			_hashCode = hashCode;
		}

		private final int _hashCode;
		private final Reference<?>[] _references;

	}

}