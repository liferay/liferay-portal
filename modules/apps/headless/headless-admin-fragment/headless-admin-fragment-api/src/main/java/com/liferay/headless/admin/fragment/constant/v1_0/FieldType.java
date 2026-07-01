/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.constant.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import jakarta.annotation.Generated;

import java.util.Objects;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
public enum FieldType {

	BOOLEAN("Boolean"), CAPTCHA("CAPTCHA"), DATE("Date"), DATE_TIME("DateTime"),
	EMAIL("Email"), FILE("File"), FORM_BUTTON("FormButton"),
	FRIENDLY_URL("FriendlyURL"), HTML("HTML"),
	LOCALIZATION_SELECT("LocalizationSelect"), LONG_TEXT("LongText"),
	MULTISELECT("Multiselect"), NUMBER("Number"), PHONE_NUMBER("PhoneNumber"),
	RELATIONSHIP("Relationship"), SELECT("Select"), STEPPER("Stepper"),
	TEXT("Text");

	@JsonCreator
	public static FieldType fromString(String value) {
		if ((value == null) || value.equals("")) {
			return null;
		}

		for (FieldType fieldType : values()) {
			if (Objects.equals(fieldType.getValue(), value)) {
				return fieldType;
			}
		}

		throw new IllegalArgumentException("Invalid enum value: " + value);
	}

	@JsonValue
	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}

	private FieldType(String value) {
		_value = value;
	}

	private final String _value;

}
// LIFERAY-REST-BUILDER-HASH:-687990596