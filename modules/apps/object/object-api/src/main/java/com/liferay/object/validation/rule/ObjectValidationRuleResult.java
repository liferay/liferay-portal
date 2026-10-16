/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.validation.rule;

/**
 * @author Carolina Barbosa
 */
public class ObjectValidationRuleResult {

	public ObjectValidationRuleResult(
		String errorMessage, String externalReferenceCode) {

		this(errorMessage, externalReferenceCode, null);
	}

	public ObjectValidationRuleResult(
		String errorMessage, String externalReferenceCode,
		String objectFieldName) {

		this(errorMessage, externalReferenceCode, null, objectFieldName);
	}

	public ObjectValidationRuleResult(
		String errorMessage, String externalReferenceCode,
		String objectEntryExternalReferenceCode, String objectFieldName) {

		_errorMessage = errorMessage;
		_externalReferenceCode = externalReferenceCode;
		_objectEntryExternalReferenceCode = objectEntryExternalReferenceCode;
		_objectFieldName = objectFieldName;
	}

	public String getErrorMessage() {
		return _errorMessage;
	}

	public String getExternalReferenceCode() {
		return _externalReferenceCode;
	}

	public String getObjectEntryExternalReferenceCode() {
		return _objectEntryExternalReferenceCode;
	}

	public String getObjectFieldName() {
		return _objectFieldName;
	}

	private String _errorMessage;
	private String _externalReferenceCode;
	private String _objectEntryExternalReferenceCode;
	private String _objectFieldName;

}