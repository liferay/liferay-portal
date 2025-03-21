/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.dto.v2_0;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
@GraphQLName("Error")
@io.swagger.v3.oas.annotations.media.Schema(
	requiredProperties = {"errorCode", "errorDescription", "message", "status"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Error")
public class Error implements Serializable {

	public static Error toDTO(String json) {
		return ObjectMapperUtil.readValue(Error.class, json);
	}

	public static Error unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Error.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Internal error code mapping", example = "996"
	)
	public Integer getErrorCode() {
		if (_errorCodeSupplier != null) {
			errorCode = _errorCodeSupplier.get();

			_errorCodeSupplier = null;
		}

		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;

		_errorCodeSupplier = null;
	}

	@JsonIgnore
	public void setErrorCode(
		UnsafeSupplier<Integer, Exception> errorCodeUnsafeSupplier) {

		_errorCodeSupplier = () -> {
			try {
				return errorCodeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "Internal error code mapping")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	protected Integer errorCode;

	@JsonIgnore
	private Supplier<Integer> _errorCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "Unable to find currency. Currency code should be expressed with 3-letter ISO 4217 format."
	)
	public String getErrorDescription() {
		if (_errorDescriptionSupplier != null) {
			errorDescription = _errorDescriptionSupplier.get();

			_errorDescriptionSupplier = null;
		}

		return errorDescription;
	}

	public void setErrorDescription(String errorDescription) {
		this.errorDescription = errorDescription;

		_errorDescriptionSupplier = null;
	}

	@JsonIgnore
	public void setErrorDescription(
		UnsafeSupplier<String, Exception> errorDescriptionUnsafeSupplier) {

		_errorDescriptionSupplier = () -> {
			try {
				return errorDescriptionUnsafeSupplier.get();
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
	@NotEmpty
	protected String errorDescription;

	@JsonIgnore
	private Supplier<String> _errorDescriptionSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		example = "No CommerceCurrency exists with the key {groupId=41811, code=US Dollar}"
	)
	public String getMessage() {
		if (_messageSupplier != null) {
			message = _messageSupplier.get();

			_messageSupplier = null;
		}

		return message;
	}

	public void setMessage(String message) {
		this.message = message;

		_messageSupplier = null;
	}

	@JsonIgnore
	public void setMessage(
		UnsafeSupplier<String, Exception> messageUnsafeSupplier) {

		_messageSupplier = () -> {
			try {
				return messageUnsafeSupplier.get();
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
	@NotEmpty
	protected String message;

	@JsonIgnore
	private Supplier<String> _messageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "HTTP Status code", example = "404"
	)
	public Integer getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Integer, Exception> statusUnsafeSupplier) {

		_statusSupplier = () -> {
			try {
				return statusUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "HTTP Status code")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@NotNull
	protected Integer status;

	@JsonIgnore
	private Supplier<Integer> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Error)) {
			return false;
		}

		Error error = (Error)object;

		return Objects.equals(toString(), error.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Integer errorCode = getErrorCode();

		if (errorCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorCode\": ");

			sb.append(errorCode);
		}

		String errorDescription = getErrorDescription();

		if (errorDescription != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorDescription\": ");

			sb.append("\"");

			sb.append(_escape(errorDescription));

			sb.append("\"");
		}

		String message = getMessage();

		if (message != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"message\": ");

			sb.append("\"");

			sb.append(_escape(message));

			sb.append("\"");
		}

		Integer status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append(status);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.commerce.admin.pricing.dto.v2_0.Error",
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