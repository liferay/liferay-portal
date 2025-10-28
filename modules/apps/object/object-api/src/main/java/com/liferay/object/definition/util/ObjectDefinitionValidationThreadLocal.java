/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.object.exception.ObjectDefinitionValidationException.ValidationError;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.lang.SafeCloseable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationThreadLocal {

	public static <E extends Exception> void addValidationError(
		String entryClassName, E exception, String propertyName,
		Object propertyValue) {

		List<ValidationError> validationErrors = _validationErrors.get();

		if (validationErrors == null) {
			validationErrors = new ArrayList<>();

			_validationErrors.set(validationErrors);
		}

		Class<?> clazz = exception.getClass();

		validationErrors.add(
			new ValidationError(
				entryClassName, exception.getMessage(), clazz.getName(),
				propertyName, propertyValue));
	}

	public static List<ValidationError> getValidationErrors() {
		return _validationErrors.get();
	}

	public static boolean isAccumulateError() {
		return _accumulateError.get();
	}

	public static SafeCloseable setAccumulateErrorWithSafeCloseable(
		boolean accumulateError) {

		return _accumulateError.setWithSafeCloseable(accumulateError);
	}

	public static SafeCloseable setValidationErrorsWithSafeCloseable(
		List<ValidationError> validationErrors) {

		return _validationErrors.setWithSafeCloseable(validationErrors);
	}

	private static final CentralizedThreadLocal<Boolean> _accumulateError =
		new CentralizedThreadLocal<>(
			ObjectDefinitionValidationThreadLocal.class + "._accumulateError",
			() -> Boolean.FALSE);
	private static final CentralizedThreadLocal<List<ValidationError>>
		_validationErrors = new CentralizedThreadLocal<>(
			ObjectDefinitionValidationThreadLocal.class + "._validationErrors");

}