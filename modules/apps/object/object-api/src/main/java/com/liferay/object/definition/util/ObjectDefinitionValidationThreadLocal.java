/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.object.exception.ObjectDefinitionValidationException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;

import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationThreadLocal {

	public static final String OBJECT_DEFINITION_KEY =
		ObjectDefinition.class.getName();

	public static final String OBJECT_FIELD_KEY = ObjectField.class.getName();

	public static String getObjectDefinitionERC() {
		return _objectDefinitionERC.get();
	}

	public static List<ObjectField> getObjectDefinitionObjectFields() {
		return _objectDefinitionObjectFields.get();
	}

	public static List<ObjectDefinitionValidationException.ValidationError>
		getValidationErrors() {

		return _validationErrors.get();
	}

	public static void handleAsValidationError(
			PortalException portalException, String objectDefinitionName,
			String objectFieldName, String objectFieldValue)
		throws PortalException {

		if (shouldThrowExceptionOnValidation()) {
			throw portalException;
		}

		_handleAsValidationError(
			portalException.getMessage(),
			portalException.getClass(
			).getName(),
			objectDefinitionName, objectFieldName, objectFieldValue);
	}

	public static void handleAsValidationError(
		SystemException systemException, String objectDefinitionName,
		String objectFieldName, String objectFieldValue) throws SystemException{

		if (shouldThrowExceptionOnValidation()) {
			throw systemException;
		}

		_handleAsValidationError(
			systemException.getMessage(),
			systemException.getClass(
			).getName(),
			objectDefinitionName, objectFieldName, objectFieldValue);
	}

	public static boolean hasValidationErrors() {
		List<ObjectDefinitionValidationException.ValidationError>
			validationErrors = _validationErrors.get();

		if (validationErrors == null) {
			return false;
		}

		return !_validationErrors.get(
		).isEmpty();
	}

	public static void setObjectDefinitionERC(String externalReferenceCode) {
		_objectDefinitionERC.set(externalReferenceCode);
	}

	public static void setObjectDefinitionObjectFields(
		List<ObjectField> objectFields) {

		_objectDefinitionObjectFields.set(objectFields);
	}

	public static void setShouldThrowExceptionOnValidation(
		boolean skipThrowException) {

		_shouldThrowExceptionOnValidation.set(skipThrowException);
	}

	public static void setValidationErrors(
		List<ObjectDefinitionValidationException.ValidationError>
			validationErrors) {

		_validationErrors.set(validationErrors);
	}

	public static boolean shouldThrowExceptionOnValidation() {
		return _shouldThrowExceptionOnValidation.get();
	}

	private static void _handleAsValidationError(
		String errorMessage, String exceptionClassName,
		String objectDefinitionName, String objectFieldName,
		String objectFieldValue) {

		List<ObjectDefinitionValidationException.ValidationError>
			validationErrors = _validationErrors.get();

		if (validationErrors == null) {
			return;
		}

		ObjectDefinitionValidationException.ValidationError validationError =
			new ObjectDefinitionValidationException.ValidationError(
				errorMessage, exceptionClassName, objectDefinitionName,
				objectFieldName, objectFieldValue);

		validationErrors.add(validationError);

		_validationErrors.set(validationErrors);
	}

	private static final CentralizedThreadLocal<String> _objectDefinitionERC =
		new CentralizedThreadLocal<>(
			ObjectDefinitionThreadLocal.class + "._objectDefinitionERC");
	private static final CentralizedThreadLocal<List<ObjectField>>
		_objectDefinitionObjectFields = new CentralizedThreadLocal<>(
			ObjectDefinitionThreadLocal.class +
				"._objectDefinitionObjectFields");
	private static final CentralizedThreadLocal<Boolean>
		_shouldThrowExceptionOnValidation = new CentralizedThreadLocal<>(
			ObjectDefinitionThreadLocal.class +
				"._shouldThrowExceptionOnValidation",
			() -> Boolean.TRUE);
	private static final CentralizedThreadLocal
		<List<ObjectDefinitionValidationException.ValidationError>>
			_validationErrors = new CentralizedThreadLocal<>(
				ObjectDefinitionThreadLocal.class + "._validationErrors");

}