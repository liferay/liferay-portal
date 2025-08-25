/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.definition.util;

import com.liferay.object.exception.ObjectDefinitionValidationException;
import com.liferay.object.model.ObjectDefinition;
import com.liferay.object.model.ObjectField;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;

import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationContext {

	public static final String BUSINESS_TYPE_PROPERTY = "businessType";

	public static final String ENABLE_COMMENTS_PROPERTY = "enableComments";

	public static final String ENABLE_OBJECT_ENTRY_SCHEDULE_PROPERTY =
		"enableObjectEntrySchedule";

	public static final String ENABLE_OBJECT_ENTRY_VERSIONING_PROPERTY =
		"enableObjectEntryVersioning";

	public static final String EXTERNAL_REFERENCE_CODE_PROPERTY =
		"externalReferenceCode";

	public static final String INDEXED_PROPERTY = "indexed";

	public static final String NAME_PROPERTY = "name";

	public static final String OBJ_DEF_SETTINGS_VALUES_MAP_PROPERTY =
		"objectDefinitionSettingsValuesMap";

	public static final String OBJECT_DEFINITION_CLASS_NAME =
		ObjectDefinition.class.getName();

	public static final String OBJECT_FIELD_CLASS_NAME =
		ObjectField.class.getName();

	public static final String READ_ONLY_PROPERTY = "readOnly";

	public static final String SCOPE_PROPERTY = "scope";

	public static final String VERSION_PROPERTY = "version";

	public ObjectDefinitionValidationContext(
		String objectDefinitionExternalReferenceCode,
		List<ObjectDefinitionValidationException.ValidationError>
			validationErrors) {

		_objectDefinitionExternalReferenceCode =
			objectDefinitionExternalReferenceCode;
		_validationErrors = validationErrors;
	}

	public void addValidationError(
		String errorMessage, String exceptionClassName, String className,
		String property, Object value) {

		_validationErrors.add(
			new ObjectDefinitionValidationException.ValidationError(
				errorMessage, exceptionClassName, className, property, value));
	}

	public String getObjectDefinitionExternalReferenceCode() {
		return _objectDefinitionExternalReferenceCode;
	}

	public String getValidationErrorsAsJSON() {
		JSONArray jsonArray = JSONFactoryUtil.createJSONArray();

		for (ObjectDefinitionValidationException.ValidationError
				validationError : _validationErrors) {

			jsonArray.put(
				JSONUtil.put(
					"className", validationError.getClassName()
				).put(
					"errorMessage", validationError.getErrorMessage()
				).put(
					"exceptionClassName",
					validationError.getExceptionClassName()
				).put(
					"property", validationError.getProperty()
				).put(
					"value", validationError.getValue()
				));
		}

		return JSONUtil.put(
			"validationErrors", jsonArray
		).toString();
	}

	public boolean hasValidationErrors() {
		if (_validationErrors == null) {
			return false;
		}

		return !_validationErrors.isEmpty();
	}

	private final String _objectDefinitionExternalReferenceCode;
	private final List<ObjectDefinitionValidationException.ValidationError>
		_validationErrors;

}