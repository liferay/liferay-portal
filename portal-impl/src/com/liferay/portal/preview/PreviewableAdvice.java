/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.preview;

import com.liferay.portal.kernel.aop.AopMethodInvocation;
import com.liferay.portal.kernel.aop.ChainableMethodAdvice;
import com.liferay.portal.kernel.model.BaseModel;
import com.liferay.portal.kernel.preview.Previewable;
import com.liferay.portal.kernel.service.PersistedModelLocalService;
import com.liferay.portal.kernel.service.persistence.BasePersistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class PreviewableAdvice extends ChainableMethodAdvice {

	@Override
	public Object createMethodContext(
		Object target, Method method,
		Map<Class<? extends Annotation>, Annotation> annotations) {

		Previewable previewable = (Previewable)annotations.get(
			Previewable.class);

		if ((previewable == null) || !previewable.enabled() ||
			!_isSupported(method.getName()) ||
			!(target instanceof
				PersistedModelLocalService persistedModelLocalService)) {

			return null;
		}

		BasePersistence<?> basePersistence =
			persistedModelLocalService.getBasePersistence();

		Class<?> modelClass = basePersistence.getModelClass();

		Type type = method.getGenericReturnType();

		if (modelClass == type) {
			return _baseModelResolver;
		}

		if (type instanceof TypeVariable) {
			return _runtimeResolver;
		}

		if (type instanceof ParameterizedType parameterizedType) {
			Type typeArgument = parameterizedType.getActualTypeArguments()[0];

			if (typeArgument instanceof TypeVariable) {
				return _runtimeResolver;
			}

			if (modelClass == typeArgument) {
				Class<?> rawType = (Class<?>)parameterizedType.getRawType();

				if (Collection.class.isAssignableFrom(rawType)) {
					if (rawType == List.class) {
						return _listBaseModelResolver;
					}

					if (rawType == Set.class) {
						return _setBaseModelResolver;
					}

					if (rawType == Queue.class) {
						return _queueBaseModelResolver;
					}
				}
			}
		}

		return null;
	}

	@Override
	protected Object afterReturning(
			AopMethodInvocation aopMethodInvocation, Object[] arguments,
			Object result)
		throws Throwable {

		if (result == null) {
			return null;
		}

		Resolver resolver = aopMethodInvocation.getAdviceMethodContext();

		return resolver.resolve(result);
	}

	private static Object _resolveRuntime(Object object) {
		if (object instanceof BaseModel<?> baseModel) {
			return PreviewableResolverUtil.resolve(baseModel);
		}

		if (!(object instanceof List<?> list) || list.isEmpty() ||
			!(list.get(0) instanceof BaseModel)) {

			return object;
		}

		return PreviewableResolverUtil.resolve(
			(Collection<BaseModel<?>>)object, new ArrayList<>());
	}

	private boolean _isSupported(String name) {
		for (String supportedMethodNamePrefix : _supportedMethodNamePrefixes) {
			if (name.startsWith(supportedMethodNamePrefix)) {
				return true;
			}
		}

		return false;
	}

	private static final Resolver _baseModelResolver =
		obj -> PreviewableResolverUtil.resolve((BaseModel<?>)obj);
	private static final Resolver _listBaseModelResolver =
		list -> PreviewableResolverUtil.resolve(
			(Collection<BaseModel<?>>)list, new ArrayList<>());
	private static final Resolver _queueBaseModelResolver =
		queue -> PreviewableResolverUtil.resolve(
			(Collection<BaseModel<?>>)queue, new ArrayDeque<>());
	private static final Resolver _runtimeResolver =
		PreviewableAdvice::_resolveRuntime;
	private static final Resolver _setBaseModelResolver =
		set -> PreviewableResolverUtil.resolve(
			(Collection<BaseModel<?>>)set, new HashSet<>());
	private static final Set<String> _supportedMethodNamePrefixes = Set.of(
		"dslQuery", "dynamicQuery", "fetch", "get", "load", "search");

	private interface Resolver {

		public Object resolve(Object object);

	}

}