/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.exception;

import com.liferay.portal.kernel.exception.PortalException;

import java.util.List;

/**
 * @author Caio Farias
 */
public class ObjectDefinitionValidationException extends PortalException {

	public ObjectDefinitionValidationException() {
	}

	public ObjectDefinitionValidationException(String msg) {
		super(msg);
	}

	public ObjectDefinitionValidationException(String msg, String details) {
		super(msg);

		_detail = details;
	}

	public ObjectDefinitionValidationException(
		String msg, Throwable throwable) {

		super(msg, throwable);
	}

	public ObjectDefinitionValidationException(Throwable throwable) {
		super(throwable);
	}

	public String getDetail() {
		return _detail;
	}

	public List<ObjectEntryValidationException.ValidationError>
		getValidationErrors() {

		return _validationErrors;
	}

	public void setValidationErrors(
		List<ObjectEntryValidationException.ValidationError> validationErrors) {

		_validationErrors = validationErrors;
	}

	public static class ValidationError {

		public ValidationError(
			String errorMessage, String exceptionClassName,
			String objectDefinitionName, String objectFieldName,
			String objectFieldValue) {

			_errorMessage = errorMessage;
			_exceptionClassName = exceptionClassName;
			_objectDefinitionName = objectDefinitionName;
			_objectFieldName = objectFieldName;
			_objectFieldValue = objectFieldValue;
		}

		public String getErrorMessage() {
			return _errorMessage;
		}

		public String getExceptionClassName() {
			return _exceptionClassName;
		}

		public String getObjectDefinitionName() {
			return _objectDefinitionName;
		}

		public String getObjectFieldName() {
			return _objectFieldName;
		}

		public String getObjectFieldValue() {
			return _objectFieldValue;
		}

		private final String _errorMessage;
		private final String _exceptionClassName;
		private final String _objectDefinitionName;
		private final String _objectFieldName;
		private final String _objectFieldValue;

	}

	private String _detail;
	private List<ObjectEntryValidationException.ValidationError>
		_validationErrors;

}