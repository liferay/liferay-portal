/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.fips.rest.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Lucas Miranda
 * @generated
 */
@Generated("")
@GraphQLName("FIPSHealthVerification")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FIPSHealthVerification")
public class FIPSHealthVerification implements Serializable {

	public static FIPSHealthVerification toDTO(String json) {
		return ObjectMapperUtil.readValue(FIPSHealthVerification.class, json);
	}

	public static FIPSHealthVerification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			FIPSHealthVerification.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Date getDateVerified() {
		if (_dateVerifiedSupplier != null) {
			dateVerified = _dateVerifiedSupplier.get();

			_dateVerifiedSupplier = null;
		}

		return dateVerified;
	}

	public void setDateVerified(Date dateVerified) {
		this.dateVerified = dateVerified;

		_dateVerifiedSupplier = null;
	}

	@JsonIgnore
	public void setDateVerified(
		UnsafeSupplier<Date, Exception> dateVerifiedUnsafeSupplier) {

		_dateVerifiedSupplier = () -> {
			try {
				return dateVerifiedUnsafeSupplier.get();
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
	protected Date dateVerified;

	@JsonIgnore
	private Supplier<Date> _dateVerifiedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getErrorMessage() {
		if (_errorMessageSupplier != null) {
			errorMessage = _errorMessageSupplier.get();

			_errorMessageSupplier = null;
		}

		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;

		_errorMessageSupplier = null;
	}

	@JsonIgnore
	public void setErrorMessage(
		UnsafeSupplier<String, Exception> errorMessageUnsafeSupplier) {

		_errorMessageSupplier = () -> {
			try {
				return errorMessageUnsafeSupplier.get();
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
	protected String errorMessage;

	@JsonIgnore
	private Supplier<String> _errorMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("status")
	@Valid
	public Status getStatus() {
		if (_statusSupplier != null) {
			status = _statusSupplier.get();

			_statusSupplier = null;
		}

		return status;
	}

	@JsonIgnore
	public String getStatusAsString() {
		Status status = getStatus();

		if (status == null) {
			return null;
		}

		return status.toString();
	}

	public void setStatus(Status status) {
		this.status = status;

		_statusSupplier = null;
	}

	@JsonIgnore
	public void setStatus(
		UnsafeSupplier<Status, Exception> statusUnsafeSupplier) {

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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FIPSHealthVerification)) {
			return false;
		}

		FIPSHealthVerification fipsHealthVerification =
			(FIPSHealthVerification)object;

		return Objects.equals(toString(), fipsHealthVerification.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		DateFormat liferayToJSONDateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		Date dateVerified = getDateVerified();

		if (dateVerified != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dateVerified\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(dateVerified));

			sb.append("\"");
		}

		String errorMessage = getErrorMessage();

		if (errorMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"errorMessage\": ");

			sb.append("\"");

			sb.append(_escape(errorMessage));

			sb.append("\"");
		}

		Status status = getStatus();

		if (status != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"status\": ");

			sb.append("\"");
			sb.append(status);
			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.portal.security.fips.rest.dto.v1_0.FIPSHealthVerification",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Status")
	public static enum Status {

		ERROR("ERROR"), INITIALIZING("INITIALIZING"),
		OPERATIONAL("OPERATIONAL"), POWER_OFF("POWER_OFF"),
		SELF_TEST("SELF_TEST");

		@JsonCreator
		public static Status create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Status status : values()) {
				if (Objects.equals(status.getValue(), value)) {
					return status;
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

		private Status(String value) {
			_value = value;
		}

		private final String _value;

	}

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
// LIFERAY-REST-BUILDER-HASH:338063096