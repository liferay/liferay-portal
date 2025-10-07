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

	public List<ValidationError> getValidationErrors() {
		return _validationErrors;
	}

	public void setValidationErrors(List<ValidationError> validationErrors) {
		_validationErrors = validationErrors;
	}

	public static class ValidationError {

		public ValidationError(
			String errorMessage, String exceptionClassName, String className,
			String property, Object value) {

			_errorMessage = errorMessage;
			_exceptionClassName = exceptionClassName;
			_className = className;
			_property = property;
			_value = value;
		}

		public String getClassName() {
			return _className;
		}

		public String getErrorMessage() {
			return _errorMessage;
		}

		public String getExceptionClassName() {
			return _exceptionClassName;
		}

		public String getProperty() {
			return _property;
		}

		public Object getValue() {
			return _value;
		}

		private final String _className;
		private final String _errorMessage;
		private final String _exceptionClassName;
		private final String _property;
		private final Object _value;

	}

	private List<ValidationError> _validationErrors;

}