/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.graphql.util;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

/**
 * @author Carlos Correa
 */
public class GraphQLUtil {

	public static String getGraphQLNameValue(
		AnnotatedElement annotatedElement) {

		GraphQLName graphQLName = annotatedElement.getAnnotation(
			GraphQLName.class);

		if (graphQLName != null) {
			return graphQLName.value();
		}

		Object value = _getAnnotationValue(
			annotatedElement,
			graphql.annotations.annotationTypes.GraphQLName.class);

		if (value == null) {
			return null;
		}

		return (String)value;
	}

	public static boolean isGraphQLFieldValue(
		AnnotatedElement annotatedElement) {

		GraphQLField graphQLField = annotatedElement.getAnnotation(
			GraphQLField.class);

		if (graphQLField != null) {
			return graphQLField.value();
		}

		Object value = _getAnnotationValue(
			annotatedElement,
			graphql.annotations.annotationTypes.GraphQLField.class);

		if (value == null) {
			return false;
		}

		return (boolean)value;
	}

	private static Object _getAnnotationValue(
		AnnotatedElement annotatedElement, Class<?> clazz) {

		for (Annotation annotation :
				annotatedElement.getDeclaredAnnotations()) {

			Class<? extends Annotation> typeClass = annotation.annotationType();

			if (!StringUtil.equals(typeClass.getName(), clazz.getName())) {
				continue;
			}

			try {
				Method method = typeClass.getMethod("value");

				return method.invoke(annotation);
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		}

		return null;
	}

}