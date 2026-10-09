/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.model.adapter;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.orm.QueryDefinition;
import com.liferay.portal.kernel.model.ModelWrapper;
import com.liferay.portal.kernel.util.ComparatorAdapter;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorAdapter;
import com.liferay.portal.kernel.util.ProxyUtil;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

/**
 * @author Shuyang Zhou
 */
public class ModelAdapterUtil {

	public static <T> List<T> adapt(Class<T> clazz, List<?> delegateObjects) {
		return TransformUtil.transform(
			delegateObjects, delegateObject -> adapt(clazz, delegateObject));
	}

	public static <T> T adapt(Class<T> clazz, Object delegateObject) {
		if (delegateObject == null) {
			return null;
		}

		return (T)ProxyUtil.newProxyInstance(
			clazz.getClassLoader(), new Class<?>[] {clazz, ModelWrapper.class},
			new DelegateInvocationHandler(delegateObject));
	}

	public static <T> T[] adapt(Class<T> clazz, Object[] delegateObjects) {
		T[] adaptedObjects = (T[])Array.newInstance(
			clazz, delegateObjects.length);

		for (int i = 0; i < delegateObjects.length; i++) {
			adaptedObjects[i] = adapt(clazz, delegateObjects[i]);
		}

		return adaptedObjects;
	}

	public static <T, V> Comparator<T> adapt(
		Class<V> clazz, Comparator<V> comparator) {

		if (comparator == null) {
			return null;
		}

		return new ComparatorAdapter<T, V>(comparator) {

			@Override
			public V adapt(T t) {
				return ModelAdapterUtil.adapt(clazz, t);
			}

		};
	}

	public static <T, V> OrderByComparator<T> adapt(
		Class<V> clazz, OrderByComparator<V> orderByComparator) {

		if (orderByComparator == null) {
			return null;
		}

		return new OrderByComparatorAdapter<T, V>(orderByComparator) {

			@Override
			public V adapt(T t) {
				return ModelAdapterUtil.adapt(clazz, t);
			}

		};
	}

	public static <T, V> QueryDefinition<T> adapt(
		Class<V> clazz, QueryDefinition<V> queryDefinition) {

		if (queryDefinition == null) {
			return null;
		}

		QueryDefinition<T> adaptedQueryDefinition = new QueryDefinition<>(
			queryDefinition.getStatus(), queryDefinition.isExcludeStatus(),
			queryDefinition.getOwnerUserId(), queryDefinition.isIncludeOwner(),
			queryDefinition.getStart(), queryDefinition.getEnd(),
			adapt(clazz, queryDefinition.getOrderByComparator()));

		adaptedQueryDefinition.setAttributes(queryDefinition.getAttributes());

		return adaptedQueryDefinition;
	}

	public static <T> T adapt(
		Function<InvocationHandler, T> proxyProviderFunction,
		Object delegateObject) {

		if (delegateObject == null) {
			return null;
		}

		return proxyProviderFunction.apply(
			new DelegateInvocationHandler(delegateObject));
	}

	private static class DelegateInvocationHandler
		implements InvocationHandler {

		@Override
		public Object invoke(Object proxy, Method method, Object[] args)
			throws ReflectiveOperationException {

			String methodName = method.getName();

			if (methodName.equals("getWrappedModel")) {
				return _delegateObject;
			}

			Class<?> delegateClass = _delegateObject.getClass();

			method = delegateClass.getMethod(
				method.getName(), method.getParameterTypes());

			if (args == null) {
				return method.invoke(_delegateObject);
			}

			for (int i = 0; i < args.length; i++) {
				if (!ProxyUtil.isProxyClass(args[i].getClass())) {
					continue;
				}

				InvocationHandler invocationHandler =
					ProxyUtil.getInvocationHandler(args[i]);

				if (!(invocationHandler instanceof DelegateInvocationHandler)) {
					continue;
				}

				DelegateInvocationHandler delegateInvocationHandler =
					(DelegateInvocationHandler)invocationHandler;

				args[i] = delegateInvocationHandler._delegateObject;
			}

			return method.invoke(_delegateObject, args);
		}

		private DelegateInvocationHandler(Object delegateObject) {
			_delegateObject = delegateObject;
		}

		private final Object _delegateObject;

	}

}