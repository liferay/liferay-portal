/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.metadata.ConstraintDescriptor;

/**
 * @author Neil Griffin
 */
public class MessageInterpolatorContextImpl
	implements MessageInterpolator.Context {

	public MessageInterpolatorContextImpl(
		ConstraintViolation<?> constraintViolation) {

		_constraintViolation = constraintViolation;
	}

	@Override
	public ConstraintDescriptor<?> getConstraintDescriptor() {
		return _constraintViolation.getConstraintDescriptor();
	}

	@Override
	public Object getValidatedValue() {
		return _constraintViolation.getInvalidValue();
	}

	@Override
	public <T> T unwrap(Class<T> aClass) {
		throw new UnsupportedOperationException();
	}

	private final ConstraintViolation<?> _constraintViolation;

}