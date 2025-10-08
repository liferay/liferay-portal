/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.petra.lang.CentralizedThreadLocal;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationThreadLocal {

	public static ObjectDefinitionValidationContext
		getObjectDefinitionValidationContext() {

		return _objectDefinitionValidationContext.get();
	}

	public static <E extends Exception> void handleException(
			String className, E exception, String property, Object value)
		throws E {

		if (!_accumulateError.get()) {
			throw exception;
		}

		ObjectDefinitionValidationContext objectDefinitionValidationContext =
			_objectDefinitionValidationContext.get();

		if (objectDefinitionValidationContext == null) {
			return;
		}

		Class<?> clazz = exception.getClass();

		objectDefinitionValidationContext.addValidationError(
			className, exception.getMessage(), clazz.getName(), property,
			value);
	}

	public static boolean hasValidationError() {
		ObjectDefinitionValidationContext objectDefinitionValidationContext =
			_objectDefinitionValidationContext.get();

		if (objectDefinitionValidationContext == null) {
			return false;
		}

		return objectDefinitionValidationContext.hasValidationErrors();
	}

	public static boolean isAccumulateError() {
		return _accumulateError.get();
	}

	public static void setObjectDefinitionValidationContext(
		boolean accumulateError, ObjectDefinitionValidationContext context) {

		_accumulateError.set(accumulateError);
		_objectDefinitionValidationContext.set(context);
	}

	private static final CentralizedThreadLocal<Boolean> _accumulateError =
		new CentralizedThreadLocal<>(
			ObjectDefinitionValidationThreadLocal.class + "._accumulateError",
			() -> Boolean.FALSE);
	private static final CentralizedThreadLocal
		<ObjectDefinitionValidationContext> _objectDefinitionValidationContext =
			new CentralizedThreadLocal<>(
				ObjectDefinitionValidationThreadLocal.class +
					"._objectDefinitionValidationContext");

}