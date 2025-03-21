/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.bean.portlet.spring.extension.internal.mvc;

import jakarta.mvc.binding.ValidationError;

import jakarta.validation.ConstraintViolation;

/**
 * @author Neil Griffin
 */
public class ValidationErrorImpl
	extends ParamErrorImpl implements ValidationError {

	public ValidationErrorImpl(
		ConstraintViolation<?> constraintViolation, String message,
		String paramName) {

		super(message, paramName);

		_constraintViolation = constraintViolation;
	}

	@Override
	public ConstraintViolation<?> getViolation() {
		return _constraintViolation;
	}

	private final ConstraintViolation<?> _constraintViolation;

}