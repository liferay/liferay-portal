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
@GraphQLName(
	description = "Result of an on-demand cryptographic provider self-test re-verification.",
	value = "HealthVerification"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "Result of an on-demand cryptographic provider self-test re-verification."
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "HealthVerification")
public class HealthVerification implements Serializable {

	public static HealthVerification toDTO(String json) {
		return ObjectMapperUtil.readValue(HealthVerification.class, json);
	}

	public static HealthVerification unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(HealthVerification.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "When the verification ran."
	)
	public Date getDate() {
		if (_dateSupplier != null) {
			date = _dateSupplier.get();

			_dateSupplier = null;
		}

		return date;
	}

	public void setDate(Date date) {
		this.date = date;

		_dateSupplier = null;
	}

	@JsonIgnore
	public void setDate(UnsafeSupplier<Date, Exception> dateUnsafeSupplier) {
		_dateSupplier = () -> {
			try {
				return dateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "When the verification ran.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Date date;

	@JsonIgnore
	private Supplier<Date> _dateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The self-test that failed, if any."
	)
	public String getFailedTest() {
		if (_failedTestSupplier != null) {
			failedTest = _failedTestSupplier.get();

			_failedTestSupplier = null;
		}

		return failedTest;
	}

	public void setFailedTest(String failedTest) {
		this.failedTest = failedTest;

		_failedTestSupplier = null;
	}

	@JsonIgnore
	public void setFailedTest(
		UnsafeSupplier<String, Exception> failedTestUnsafeSupplier) {

		_failedTestSupplier = () -> {
			try {
				return failedTestUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The self-test that failed, if any.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String failedTest;

	@JsonIgnore
	private Supplier<String> _failedTestSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The provider FIPS state at the time of failure."
	)
	public String getFipsState() {
		if (_fipsStateSupplier != null) {
			fipsState = _fipsStateSupplier.get();

			_fipsStateSupplier = null;
		}

		return fipsState;
	}

	public void setFipsState(String fipsState) {
		this.fipsState = fipsState;

		_fipsStateSupplier = null;
	}

	@JsonIgnore
	public void setFipsState(
		UnsafeSupplier<String, Exception> fipsStateUnsafeSupplier) {

		_fipsStateSupplier = () -> {
			try {
				return fipsStateUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(
		description = "The provider FIPS state at the time of failure."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String fipsState;

	@JsonIgnore
	private Supplier<String> _fipsStateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The provider exception message on failure."
	)
	public String getProviderMessage() {
		if (_providerMessageSupplier != null) {
			providerMessage = _providerMessageSupplier.get();

			_providerMessageSupplier = null;
		}

		return providerMessage;
	}

	public void setProviderMessage(String providerMessage) {
		this.providerMessage = providerMessage;

		_providerMessageSupplier = null;
	}

	@JsonIgnore
	public void setProviderMessage(
		UnsafeSupplier<String, Exception> providerMessageUnsafeSupplier) {

		_providerMessageSupplier = () -> {
			try {
				return providerMessageUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The provider exception message on failure.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String providerMessage;

	@JsonIgnore
	private Supplier<String> _providerMessageSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The active FIPS provider name."
	)
	public String getProviderName() {
		if (_providerNameSupplier != null) {
			providerName = _providerNameSupplier.get();

			_providerNameSupplier = null;
		}

		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;

		_providerNameSupplier = null;
	}

	@JsonIgnore
	public void setProviderName(
		UnsafeSupplier<String, Exception> providerNameUnsafeSupplier) {

		_providerNameSupplier = () -> {
			try {
				return providerNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The active FIPS provider name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String providerName;

	@JsonIgnore
	private Supplier<String> _providerNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The verification outcome."
	)
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

	@GraphQLField(description = "The verification outcome.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Status status;

	@JsonIgnore
	private Supplier<Status> _statusSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof HealthVerification)) {
			return false;
		}

		HealthVerification healthVerification = (HealthVerification)object;

		return Objects.equals(toString(), healthVerification.toString());
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

		Date date = getDate();

		if (date != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"date\": ");

			sb.append("\"");

			sb.append(liferayToJSONDateFormat.format(date));

			sb.append("\"");
		}

		String failedTest = getFailedTest();

		if (failedTest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"failedTest\": ");

			sb.append("\"");

			sb.append(_escape(failedTest));

			sb.append("\"");
		}

		String fipsState = getFipsState();

		if (fipsState != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fipsState\": ");

			sb.append("\"");

			sb.append(_escape(fipsState));

			sb.append("\"");
		}

		String providerMessage = getProviderMessage();

		if (providerMessage != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerMessage\": ");

			sb.append("\"");

			sb.append(_escape(providerMessage));

			sb.append("\"");
		}

		String providerName = getProviderName();

		if (providerName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"providerName\": ");

			sb.append("\"");

			sb.append(_escape(providerName));

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
		defaultValue = "com.liferay.portal.security.fips.rest.dto.v1_0.HealthVerification",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Status")
	public static enum Status {

		HEALTHY("HEALTHY"), FAILED("FAILED"), NOT_APPLICABLE("NOT_APPLICABLE");

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
// LIFERAY-REST-BUILDER-HASH:272388356