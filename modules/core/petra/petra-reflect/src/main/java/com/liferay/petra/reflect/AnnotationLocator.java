/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.petra.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class AnnotationLocator {

	public static Map<Class<? extends Annotation>, Annotation> index(
		Class<?> targetClass) {

		Queue<Class<?>> queue = new LinkedList<>();

		queue.offer(targetClass);

		Map<Class<? extends Annotation>, Annotation> indexAnnotations =
			new LinkedHashMap<>();

		Class<?> clazz = null;

		while ((clazz = queue.poll()) != null) {
			if (!Proxy.isProxyClass(clazz)) {
				_mergeAnnotations(
					Collections.emptySet(), indexAnnotations, false,
					clazz.getAnnotations());
			}

			_queueSuperTypes(clazz, queue);
		}

		return indexAnnotations;
	}

	public static Map<Class<? extends Annotation>, Annotation> index(
		Method method, Class<?> targetClass) {

		Set<Class<? extends Annotation>> concludedAnnotationClasses =
			new HashSet<>();
		Map<Class<? extends Annotation>, Annotation> indexAnnotations =
			new LinkedHashMap<>();

		Class<?> clazz = null;

		Queue<Class<?>> queue = new LinkedList<>();

		if (targetClass == null) {
			queue.offer(method.getDeclaringClass());
		}
		else {
			queue.offer(targetClass);
		}

		while ((clazz = queue.poll()) != null) {
			if (!Proxy.isProxyClass(clazz)) {
				Method specificMethod = ReflectionUtil.fetchDeclaredMethod(
					false, clazz, method.getName(), method.getParameterTypes());

				if (specificMethod != null) {
					_mergeAnnotations(
						concludedAnnotationClasses, indexAnnotations, true,
						specificMethod.getAnnotations());
				}

				Method publicMethod = ReflectionUtil.fetchMethod(
					false, clazz, method.getName(), method.getParameterTypes());

				if (publicMethod != null) {

					// Ensure the class has a publicly inherited method

					_mergeAnnotations(
						concludedAnnotationClasses, indexAnnotations, false,
						clazz.getAnnotations());
				}
			}

			_queueSuperTypes(clazz, queue);
		}

		return indexAnnotations;
	}

	public static List<Annotation> locate(Class<?> targetClass) {
		Map<Class<? extends Annotation>, Annotation> indexAnnotations = index(
			targetClass);

		return new ArrayList<>(indexAnnotations.values());
	}

	public static <T extends Annotation> T locate(
		Class<?> targetClass, Class<T> annotationClass) {

		Queue<Class<?>> queue = new LinkedList<>();

		queue.offer(targetClass);

		Class<?> clazz = null;

		while ((clazz = queue.poll()) != null) {
			if (Proxy.isProxyClass(clazz)) {
				_queueSuperTypes(clazz, queue);

				continue;
			}

			T annotation = clazz.getAnnotation(annotationClass);

			if (annotation != null) {
				return annotation;
			}

			_queueSuperTypes(clazz, queue);
		}

		return null;
	}

	public static List<Annotation> locate(Method method, Class<?> targetClass) {
		Map<Class<? extends Annotation>, Annotation> indexAnnotations = index(
			method, targetClass);

		return new ArrayList<>(indexAnnotations.values());
	}

	public static <T extends Annotation> T locate(
		Method method, Class<?> targetClass, Class<T> annotationClass) {

		Queue<Class<?>> queue = new LinkedList<>();

		if (targetClass == null) {
			queue.offer(method.getDeclaringClass());
		}
		else {
			queue.offer(targetClass);
		}

		Class<?> clazz = null;

		while ((clazz = queue.poll()) != null) {
			if (Proxy.isProxyClass(clazz)) {
				_queueSuperTypes(clazz, queue);

				continue;
			}

			T annotation = null;

			try {
				Method specificMethod = clazz.getDeclaredMethod(
					method.getName(), method.getParameterTypes());

				annotation = specificMethod.getAnnotation(annotationClass);

				if (annotation != null) {
					return annotation;
				}
			}
			catch (Exception exception) {
			}

			try {

				// Ensure the class has a publicly inherited method

				clazz.getMethod(method.getName(), method.getParameterTypes());

				annotation = clazz.getAnnotation(annotationClass);
			}
			catch (Exception exception) {
			}

			if (annotation != null) {
				return annotation;
			}

			_queueSuperTypes(clazz, queue);
		}

		return null;
	}

	private static void _mergeAnnotations(
		Set<Class<? extends Annotation>> concludedAnnotationClasses,
		Map<Class<? extends Annotation>, Annotation> indexAnnotations,
		boolean fromMethod, Annotation[] sourceAnnotations) {

		for (Annotation sourceAnnotation : sourceAnnotations) {
			Class<? extends Annotation> annotationClass =
				sourceAnnotation.annotationType();

			if (concludedAnnotationClasses.contains(annotationClass)) {
				continue;
			}

			if (fromMethod) {
				indexAnnotations.put(annotationClass, sourceAnnotation);

				concludedAnnotationClasses.add(annotationClass);
			}
			else {
				indexAnnotations.putIfAbsent(annotationClass, sourceAnnotation);
			}
		}
	}

	private static void _queueSuperTypes(
		Class<?> clazz, Queue<Class<?>> queue) {

		Class<?> superClass = clazz.getSuperclass();

		if (superClass != null) {
			String name = superClass.getName();

			if (!name.startsWith("java")) {
				queue.offer(superClass);
			}
		}

		Class<?>[] interfaceClasses = clazz.getInterfaces();

		for (Class<?> interfaceClass : interfaceClasses) {
			if (queue.contains(interfaceClass)) {
				continue;
			}

			queue.offer(interfaceClass);
		}
	}

}