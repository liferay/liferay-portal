/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationThreadLocal {

	public static ObjectDefinitionValidationContext
		getObjectDefinitionValidationContext() {

		return _objectDefinitionValidationContext.get();
	}

	public static void handleAsValidationError(
			PortalException portalException, String className, String property,
			Object value)
		throws PortalException {

		ObjectDefinitionValidationContext objectDefinitionValidationContext =
			_objectDefinitionValidationContext.get();

		if (objectDefinitionValidationContext == null) {
			return;
		}

		if (!_accumulateError.get()) {
			throw portalException;
		}

		_handleAsValidationError(
			portalException.getMessage(),
			portalException.getClass(
			).getName(),
			className, property, value);
	}

	public static void handleAsValidationError(
		SystemException systemException, String className, String property,
		Object value) {

		ObjectDefinitionValidationContext objectDefinitionValidationContext =
			_objectDefinitionValidationContext.get();

		if (objectDefinitionValidationContext == null) {
			return;
		}

		if (!_accumulateError.get()) {
			throw systemException;
		}

		_handleAsValidationError(
			systemException.getMessage(),
			systemException.getClass(
			).getName(),
			className, property, value);
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
		Boolean accumulateError = _accumulateError.get();

		if (accumulateError == null) {
			return false;
		}

		return accumulateError;
	}

	public static void setObjectDefinitionValidationContext(
		boolean accumulateError, ObjectDefinitionValidationContext context) {

		_accumulateError.set(accumulateError);
		_objectDefinitionValidationContext.set(context);
	}

	private static void _handleAsValidationError(
		String errorMessage, String exceptionClassName, String className,
		String property, Object value) {

		ObjectDefinitionValidationContext objectDefinitionValidationContext =
			_objectDefinitionValidationContext.get();

		if (objectDefinitionValidationContext == null) {
			return;
		}

		objectDefinitionValidationContext.addValidationError(
			errorMessage, exceptionClassName, className, property, value);

		_objectDefinitionValidationContext.set(
			objectDefinitionValidationContext);
	}

	private static final CentralizedThreadLocal<Boolean> _accumulateError =
		new CentralizedThreadLocal<>(
			ObjectDefinitionValidationThreadLocal.class + "._accumulateError",
			() -> Boolean.FALSE);
	private static final CentralizedThreadLocal
		<ObjectDefinitionValidationContext> _objectDefinitionValidationContext =
			new CentralizedThreadLocal<>(
				ObjectDefinitionValidationThreadLocal.class +
					"._objectDefinitionValidationContext",
				() -> new ObjectDefinitionValidationContext(null, null));

}