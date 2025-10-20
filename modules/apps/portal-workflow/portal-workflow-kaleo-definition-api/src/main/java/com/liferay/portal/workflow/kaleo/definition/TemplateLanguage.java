/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.definition;

import com.liferay.portal.workflow.kaleo.definition.exception.KaleoDefinitionValidationException;

import java.util.Objects;

/**
 * @author Michael C. Han
 */
public enum TemplateLanguage {

	FREEMARKER("freemarker"), TEXT("text");

	public static TemplateLanguage parse(String value)
		throws KaleoDefinitionValidationException {

		if (Objects.equals(FREEMARKER.getValue(), value)) {
			return FREEMARKER;
		}
		else if (Objects.equals(TEXT.getValue(), value)) {
			return TEXT;
		}

		throw new KaleoDefinitionValidationException.InvalidTemplateLanguage(
			value);
	}

	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}

	private TemplateLanguage(String value) {
		_value = value;
	}

	private final String _value;

}