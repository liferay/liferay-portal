/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.validation;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ValidationException;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;

import java.lang.reflect.Method;

import java.util.Set;

/**
 * @author Javier Gamarra
 */
public class ValidationUtil {

	public static void validate(Object value) {
		Validator validator = ValidatorFactory.getValidator();

		Set<ConstraintViolation<Object>> constraintViolations =
			validator.validate(value);

		if (!constraintViolations.isEmpty()) {
			_throwValidationException(constraintViolations);
		}
	}

	public static void validateArguments(
		Object resource, Method method, Object[] arguments) {

		Validator validator = ValidatorFactory.getValidator();

		ExecutableValidator executableValidator = validator.forExecutables();

		Set<ConstraintViolation<Object>> constraintViolations =
			executableValidator.validateParameters(resource, method, arguments);

		if (!constraintViolations.isEmpty()) {
			_throwValidationException(constraintViolations);
		}
	}

	private static void _throwValidationException(
		Set<ConstraintViolation<Object>> constraintViolations) {

		StringBundler sb = new StringBundler(constraintViolations.size() * 4);

		for (ConstraintViolation<Object> constraintViolation :
				constraintViolations) {

			sb.append(constraintViolation.getPropertyPath());
			sb.append(StringPool.SPACE);
			sb.append(constraintViolation.getMessage());
			sb.append(StringPool.NEW_LINE);
		}

		throw new ValidationException(sb.toString());
	}

}