/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the definition of the user full name.",
	value = "UserAccountFullNameDefinition"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UserAccountFullNameDefinition")
public class UserAccountFullNameDefinition implements Serializable {

	public static UserAccountFullNameDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(
			UserAccountFullNameDefinition.class, json);
	}

	public static UserAccountFullNameDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			UserAccountFullNameDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of the user's account."
	)
	@Valid
	public UserAccountFullNameDefinitionField[]
		getUserAccountFullNameDefinitionFields() {

		if (_userAccountFullNameDefinitionFieldsSupplier != null) {
			userAccountFullNameDefinitionFields =
				_userAccountFullNameDefinitionFieldsSupplier.get();

			_userAccountFullNameDefinitionFieldsSupplier = null;
		}

		return userAccountFullNameDefinitionFields;
	}

	public void setUserAccountFullNameDefinitionFields(
		UserAccountFullNameDefinitionField[]
			userAccountFullNameDefinitionFields) {

		this.userAccountFullNameDefinitionFields =
			userAccountFullNameDefinitionFields;

		_userAccountFullNameDefinitionFieldsSupplier = null;
	}

	@JsonIgnore
	public void setUserAccountFullNameDefinitionFields(
		UnsafeSupplier<UserAccountFullNameDefinitionField[], Exception>
			userAccountFullNameDefinitionFieldsUnsafeSupplier) {

		_userAccountFullNameDefinitionFieldsSupplier = () -> {
			try {
				return userAccountFullNameDefinitionFieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "A list of the user's account.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected UserAccountFullNameDefinitionField[]
		userAccountFullNameDefinitionFields;

	@JsonIgnore
	private Supplier<UserAccountFullNameDefinitionField[]>
		_userAccountFullNameDefinitionFieldsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UserAccountFullNameDefinition)) {
			return false;
		}

		UserAccountFullNameDefinition userAccountFullNameDefinition =
			(UserAccountFullNameDefinition)object;

		return Objects.equals(
			toString(), userAccountFullNameDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		UserAccountFullNameDefinitionField[]
			userAccountFullNameDefinitionFields =
				getUserAccountFullNameDefinitionFields();

		if (userAccountFullNameDefinitionFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"userAccountFullNameDefinitionFields\": ");

			sb.append("[");

			for (int i = 0; i < userAccountFullNameDefinitionFields.length;
				 i++) {

				sb.append(
					String.valueOf(userAccountFullNameDefinitionFields[i]));

				if ((i + 1) < userAccountFullNameDefinitionFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.user.dto.v1_0.UserAccountFullNameDefinition",
		name = "x-class-name"
	)
	public String xClassName;

	private static String _escape(Object object) {
		return StringUtil.replace(
			String.valueOf(object), _JSON_ESCAPE_STRINGS[0],
			_JSON_ESCAPE_STRINGS[1]);
	}

	private static boolean _isArray(Object value) {
		if (value == null) {
			return false;
		}

		Class<?> clazz = value.getClass();

		return clazz.isArray();
	}

	private static String _toJSON(Map<String, ?> map) {
		StringBuilder sb = new StringBuilder("{");

		@SuppressWarnings("unchecked")
		Set set = map.entrySet();

		@SuppressWarnings("unchecked")
		Iterator<Map.Entry<String, ?>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, ?> entry = iterator.next();

			sb.append("\"");
			sb.append(_escape(entry.getKey()));
			sb.append("\": ");

			Object value = entry.getValue();

			if (_isArray(value)) {
				sb.append("[");

				Object[] valueArray = (Object[])value;

				for (int i = 0; i < valueArray.length; i++) {
					if (valueArray[i] instanceof Map) {
						sb.append(_toJSON((Map<String, ?>)valueArray[i]));
					}
					else if (valueArray[i] instanceof String) {
						sb.append("\"");
						sb.append(valueArray[i]);
						sb.append("\"");
					}
					else {
						sb.append(valueArray[i]);
					}

					if ((i + 1) < valueArray.length) {
						sb.append(", ");
					}
				}

				sb.append("]");
			}
			else if (value instanceof Map) {
				sb.append(_toJSON((Map<String, ?>)value));
			}
			else if (value instanceof String) {
				sb.append("\"");
				sb.append(_escape(value));
				sb.append("\"");
			}
			else {
				sb.append(value);
			}

			if (iterator.hasNext()) {
				sb.append(", ");
			}
		}

		sb.append("}");

		return sb.toString();
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}