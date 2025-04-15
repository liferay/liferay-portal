/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.annotated.type;

import jakarta.enterprise.inject.spi.AnnotatedConstructor;
import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedMethod;
import jakarta.enterprise.inject.spi.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import java.util.Set;

/**
 * @author Shuyang Zhou
 */
public class ModifiedAnnotatedType<X> implements AnnotatedType<X> {

	public ModifiedAnnotatedType(
		AnnotatedType<X> annotatedType, Set<Annotation> annotations,
		Set<Type> types) {

		_annotatedType = annotatedType;
		_annotations = annotations;
		_types = types;
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
		return _annotatedType.getBaseType();
	}

	@Override
	public Set<AnnotatedConstructor<X>> getConstructors() {
		return _annotatedType.getConstructors();
	}

	@Override
	public Set<AnnotatedField<? super X>> getFields() {
		return _annotatedType.getFields();
	}

	@Override
	public Class<X> getJavaClass() {
		return _annotatedType.getJavaClass();
	}

	@Override
	public Set<AnnotatedMethod<? super X>> getMethods() {
		return _annotatedType.getMethods();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return _types;
	}

	@Override
	public boolean isAnnotationPresent(
		Class<? extends Annotation> annotationType) {

		for (Annotation annotation : _annotations) {
			Class<? extends Annotation> curAnnotationType =
				annotation.annotationType();

			if (curAnnotationType.equals(annotationType)) {
				return true;
			}
		}

		return false;
	}

	private final AnnotatedType<X> _annotatedType;
	private final Set<Annotation> _annotations;
	private final Set<Type> _types;

}