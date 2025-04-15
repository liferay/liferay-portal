/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.rest.internal.jaxrs.feature;

import com.liferay.oauth2.provider.scope.RequiresScope;
import com.liferay.petra.reflect.AnnotationLocator;

import jakarta.ws.rs.HttpMethod;
import jakarta.ws.rs.Path;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Carlos Sierra Andrés
 */
public class RequiresScopeAnnotationFinder {

	public static Collection<String> find(Class<?> clazz) {
		Set<String> scopes = new HashSet<>();

		_find(new HashSet<>(), scopes, true, clazz);

		return scopes;
	}

	public static <T extends Annotation> T getScopeAnnotation(
		AnnotatedElement annotatedElement, Class<T> annotationClass) {

		T t = annotatedElement.getAnnotation(annotationClass);

		if (t != null) {
			return t;
		}

		List<Annotation> annotations = Collections.emptyList();

		if (annotatedElement instanceof Class<?>) {
			annotations = AnnotationLocator.locate((Class<?>)annotatedElement);
		}
		else if (annotatedElement instanceof Method) {
			annotations = AnnotationLocator.locate(
				(Method)annotatedElement, null);
		}

		for (Annotation annotation : annotations) {
			Class<? extends Annotation> curAnnotationClass =
				annotation.annotationType();

			if (curAnnotationClass.equals(annotationClass)) {
				return (T)annotation;
			}

			t = curAnnotationClass.getAnnotation(annotationClass);

			if (t != null) {
				return t;
			}
		}

		return null;
	}

	private static void _find(
		Set<Class<?>> classes, Set<String> scopes, boolean recurse,
		Class<?> clazz) {

		RequiresScope requiresScope = getScopeAnnotation(
			clazz, RequiresScope.class);

		if (requiresScope != null) {
			Collections.addAll(scopes, requiresScope.value());
		}

		for (Method method : clazz.getMethods()) {
			if (_isAnnotatedMethod(method)) {
				_find(classes, scopes, recurse, method);
			}
		}

		classes.remove(clazz);
	}

	private static void _find(
		Set<Class<?>> visited, Set<String> scopes, boolean recurse,
		Method method) {

		RequiresScope requiresScope = getScopeAnnotation(
			method, RequiresScope.class);

		if (requiresScope != null) {
			Collections.addAll(scopes, requiresScope.value());
		}

		if (_isSubresourceLocator(method) && recurse) {
			_find(visited, scopes, false, method.getReturnType());
		}
	}

	private static boolean _isAnnotatedMethod(Method method) {
		List<Annotation> annotations = AnnotationLocator.locate(
			method, Path.class);

		if (!annotations.isEmpty() || _isHttpMethod(method)) {
			return true;
		}

		return false;
	}

	private static boolean _isHttpMethod(Method method) {
		List<Annotation> annotations = AnnotationLocator.locate(method, null);

		for (Annotation annotation : annotations) {
			Class<? extends Annotation> annotationType =
				annotation.annotationType();

			if (annotationType.getAnnotationsByType(HttpMethod.class) != null) {
				return true;
			}
		}

		return false;
	}

	private static boolean _isSubresourceLocator(Method method) {
		if ((method.getDeclaredAnnotation(Path.class) != null) ||
			!_isHttpMethod(method)) {

			return true;
		}

		return false;
	}

}