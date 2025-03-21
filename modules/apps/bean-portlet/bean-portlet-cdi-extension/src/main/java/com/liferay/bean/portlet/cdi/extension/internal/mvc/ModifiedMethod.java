/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedParameter;
import jakarta.enterprise.inject.spi.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import java.util.List;
import java.util.Set;

/**
 * @author Neil Griffin
 */
public class ModifiedMethod<X> implements AnnotatedMethod<X> {

	public ModifiedMethod(
		AnnotatedMethod<X> annotatedMethod, Set<Annotation> annotations) {

		_annotatedMethod = annotatedMethod;
		_annotations = annotations;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
		for (Annotation annotation : _annotations) {
			Class<? extends Annotation> curAnnotationType =
				annotation.annotationType();

			if (curAnnotationType.equals(annotationType)) {
				return annotationType.cast(annotation);
			}
		}

		return null;
	}

	@Override
	public Set<Annotation> getAnnotations() {
		return _annotations;
	}

	@Override
	public Type getBaseType() {
		return _annotatedMethod.getBaseType();
	}

	@Override
	public AnnotatedType<X> getDeclaringType() {
		return _annotatedMethod.getDeclaringType();
	}

	@Override
	public Method getJavaMember() {
		return _annotatedMethod.getJavaMember();
	}

	@Override
	public List<AnnotatedParameter<X>> getParameters() {
		return _annotatedMethod.getParameters();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return _annotatedMethod.getTypeClosure();
	}

	@Override
	public boolean isAnnotationPresent(
		Class<? extends Annotation> annotationType) {

		Annotation annotation = getAnnotation(annotationType);

		if (annotation == null) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isStatic() {
		return _annotatedMethod.isStatic();
	}

	private final AnnotatedMethod<X> _annotatedMethod;
	private final Set<Annotation> _annotations;

}