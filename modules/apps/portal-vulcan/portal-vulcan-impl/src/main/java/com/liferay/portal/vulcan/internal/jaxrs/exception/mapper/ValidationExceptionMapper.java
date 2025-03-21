/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.exception.mapper;

import com.liferay.portal.vulcan.jaxrs.exception.mapper.BaseExceptionMapper;
import com.liferay.portal.vulcan.jaxrs.exception.mapper.Problem;

import jakarta.validation.ValidationException;

/**
 * Converts any {@code ValidationException} to a {@code 400} error.
 *
 * @author Javier Gamarra
 * @review
 */
public class ValidationExceptionMapper
	extends BaseExceptionMapper<ValidationException> {

	@Override
	protected Problem getProblem(ValidationException validationException) {
		return new Problem(validationException);
	}

}