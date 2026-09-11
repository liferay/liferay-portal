/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
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
@GraphQLName("TranslationResponse")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "TranslationResponse")
public class TranslationResponse implements Serializable {

	public static TranslationResponse toDTO(String json) {
		return ObjectMapperUtil.readValue(TranslationResponse.class, json);
	}

	public static TranslationResponse unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			TranslationResponse.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getFailureMessagesJSON() {
		if (_failureMessagesJSONSupplier != null) {
			failureMessagesJSON = _failureMessagesJSONSupplier.get();

			_failureMessagesJSONSupplier = null;
		}

		return failureMessagesJSON;
	}

	public void setFailureMessagesJSON(String[] failureMessagesJSON) {
		this.failureMessagesJSON = failureMessagesJSON;

		_failureMessagesJSONSupplier = null;
	}

	@JsonIgnore
	public void setFailureMessagesJSON(
		UnsafeSupplier<String[], Exception> failureMessagesJSONUnsafeSupplier) {

		_failureMessagesJSONSupplier = () -> {
			try {
				return failureMessagesJSONUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] failureMessagesJSON;

	@JsonIgnore
	private Supplier<String[]> _failureMessagesJSONSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String[] getSuccessMessages() {
		if (_successMessagesSupplier != null) {
			successMessages = _successMessagesSupplier.get();

			_successMessagesSupplier = null;
		}

		return successMessages;
	}

	public void setSuccessMessages(String[] successMessages) {
		this.successMessages = successMessages;

		_successMessagesSupplier = null;
	}

	@JsonIgnore
	public void setSuccessMessages(
		UnsafeSupplier<String[], Exception> successMessagesUnsafeSupplier) {

		_successMessagesSupplier = () -> {
			try {
				return successMessagesUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String[] successMessages;

	@JsonIgnore
	private Supplier<String[]> _successMessagesSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof TranslationResponse)) {
			return false;
		}

		TranslationResponse translationResponse = (TranslationResponse)object;

		return Objects.equals(toString(), translationResponse.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String[] failureMessagesJSON = getFailureMessagesJSON();

		if (failureMessagesJSON != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"failureMessagesJSON\": ");

			sb.append("[");

			for (int i = 0; i < failureMessagesJSON.length; i++) {
				sb.append("\"");

				sb.append(_escape(failureMessagesJSON[i]));

				sb.append("\"");

				if ((i + 1) < failureMessagesJSON.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String[] successMessages = getSuccessMessages();

		if (successMessages != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"successMessages\": ");

			sb.append("[");

			for (int i = 0; i < successMessages.length; i++) {
				sb.append("\"");

				sb.append(_escape(successMessages[i]));

				sb.append("\"");

				if ((i + 1) < successMessages.length) {
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
		defaultValue = "com.liferay.object.rest.dto.v1_0.TranslationResponse",
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:1622898878