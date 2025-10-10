/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.object.exception.ObjectDefinitionValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationContext {

	public ObjectDefinitionValidationContext(
		String objectDefinitionExternalReferenceCode) {

		_objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;
	}

	public void addValidationError(
		String className, String errorMessage, String exceptionClassName,
		String property, Object value) {

		_validationErrors.add(
			new ObjectDefinitionValidationException.ValidationError(
				className, errorMessage, exceptionClassName, property, value));
	}

	public String getObjectDefinitionExternalReferenceCode() {
		return _objectDefinitionExternalReferenceCode;
	}

	public List<ObjectDefinitionValidationException.ValidationError>
		getValidationErrors() {

		return _validationErrors;
	}

	public boolean hasValidationErrors() {
		return !_validationErrors.isEmpty();
	}

	private final String _objectDefinitionExternalReferenceCode;
	private final List<ObjectDefinitionValidationException.ValidationError>
		_validationErrors = new ArrayList<>();

}