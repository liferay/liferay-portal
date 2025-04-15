/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.cdi.extension.internal.mvc;

import jakarta.enterprise.inject.spi.AnnotatedField;
import jakarta.enterprise.inject.spi.AnnotatedType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;

import java.util.Set;

/**
 * @author Neil Griffin
 */
public class ModifiedField<X> implements AnnotatedField<X> {

	public ModifiedField(
		AnnotatedField<X> annotatedField, Set<Annotation> annotations) {

		_annotatedField = annotatedField;
		_annotations = annotations;
	}

	@Override
	public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
		for (Annotation annotation : _annotations) {
			if (annotationClass.isAssignableFrom(annotation.getClass())) {
				return annotationClass.cast(annotation);
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
		return _annotatedField.getBaseType();
	}

	@Override
	public AnnotatedType<X> getDeclaringType() {
		return _annotatedField.getDeclaringType();
	}

	@Override
	public Field getJavaMember() {
		return _annotatedField.getJavaMember();
	}

	@Override
	public Set<Type> getTypeClosure() {
		return _annotatedField.getTypeClosure();
	}

	@Override
	public boolean isAnnotationPresent(
		Class<? extends Annotation> annotationClass) {

		if (getAnnotation(annotationClass) != null) {
			return true;
		}

		return false;
	}

	@Override
	public boolean isStatic() {
		return _annotatedField.isStatic();
	}

	private final AnnotatedField<X> _annotatedField;
	private final Set<Annotation> _annotations;

}