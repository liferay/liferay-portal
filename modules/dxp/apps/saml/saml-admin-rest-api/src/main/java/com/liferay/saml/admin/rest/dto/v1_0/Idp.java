/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Configuration of the SAML provider's IDP capability.",
	value = "Idp"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Idp")
public class Idp implements Serializable {

	public static Idp toDTO(String json) {
		return ObjectMapperUtil.readValue(Idp.class, json);
	}

	public static Idp unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Idp.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAuthnRequestSignatureRequired() {
		if (_authnRequestSignatureRequiredSupplier != null) {
			authnRequestSignatureRequired =
				_authnRequestSignatureRequiredSupplier.get();

			_authnRequestSignatureRequiredSupplier = null;
		}

		return authnRequestSignatureRequired;
	}

	public void setAuthnRequestSignatureRequired(
		Boolean authnRequestSignatureRequired) {

		this.authnRequestSignatureRequired = authnRequestSignatureRequired;

		_authnRequestSignatureRequiredSupplier = null;
	}

	@JsonIgnore
	public void setAuthnRequestSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			authnRequestSignatureRequiredUnsafeSupplier) {

		_authnRequestSignatureRequiredSupplier = () -> {
			try {
				return authnRequestSignatureRequiredUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean authnRequestSignatureRequired;

	@JsonIgnore
	private Supplier<Boolean> _authnRequestSignatureRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Integer getDefaultAssertionLifetime() {
		if (_defaultAssertionLifetimeSupplier != null) {
			defaultAssertionLifetime = _defaultAssertionLifetimeSupplier.get();

			_defaultAssertionLifetimeSupplier = null;
		}

		return defaultAssertionLifetime;
	}

	public void setDefaultAssertionLifetime(Integer defaultAssertionLifetime) {
		this.defaultAssertionLifetime = defaultAssertionLifetime;

		_defaultAssertionLifetimeSupplier = null;
	}

	@JsonIgnore
	public void setDefaultAssertionLifetime(
		UnsafeSupplier<Integer, Exception>
			defaultAssertionLifetimeUnsafeSupplier) {

		_defaultAssertionLifetimeSupplier = () -> {
			try {
				return defaultAssertionLifetimeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Integer defaultAssertionLifetime;

	@JsonIgnore
	private Supplier<Integer> _defaultAssertionLifetimeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSessionMaximumAge() {
		if (_sessionMaximumAgeSupplier != null) {
			sessionMaximumAge = _sessionMaximumAgeSupplier.get();

			_sessionMaximumAgeSupplier = null;
		}

		return sessionMaximumAge;
	}

	public void setSessionMaximumAge(Long sessionMaximumAge) {
		this.sessionMaximumAge = sessionMaximumAge;

		_sessionMaximumAgeSupplier = null;
	}

	@JsonIgnore
	public void setSessionMaximumAge(
		UnsafeSupplier<Long, Exception> sessionMaximumAgeUnsafeSupplier) {

		_sessionMaximumAgeSupplier = () -> {
			try {
				return sessionMaximumAgeUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long sessionMaximumAge;

	@JsonIgnore
	private Supplier<Long> _sessionMaximumAgeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getSessionTimeout() {
		if (_sessionTimeoutSupplier != null) {
			sessionTimeout = _sessionTimeoutSupplier.get();

			_sessionTimeoutSupplier = null;
		}

		return sessionTimeout;
	}

	public void setSessionTimeout(Long sessionTimeout) {
		this.sessionTimeout = sessionTimeout;

		_sessionTimeoutSupplier = null;
	}

	@JsonIgnore
	public void setSessionTimeout(
		UnsafeSupplier<Long, Exception> sessionTimeoutUnsafeSupplier) {

		_sessionTimeoutSupplier = () -> {
			try {
				return sessionTimeoutUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long sessionTimeout;

	@JsonIgnore
	private Supplier<Long> _sessionTimeoutSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Idp)) {
			return false;
		}

		Idp idp = (Idp)object;

		return Objects.equals(toString(), idp.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean authnRequestSignatureRequired =
			getAuthnRequestSignatureRequired();

		if (authnRequestSignatureRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"authnRequestSignatureRequired\": ");

			sb.append(authnRequestSignatureRequired);
		}

		Integer defaultAssertionLifetime = getDefaultAssertionLifetime();

		if (defaultAssertionLifetime != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultAssertionLifetime\": ");

			sb.append(defaultAssertionLifetime);
		}

		Long sessionMaximumAge = getSessionMaximumAge();

		if (sessionMaximumAge != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionMaximumAge\": ");

			sb.append(sessionMaximumAge);
		}

		Long sessionTimeout = getSessionTimeout();

		if (sessionTimeout != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sessionTimeout\": ");

			sb.append(sessionTimeout);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.saml.admin.rest.dto.v1_0.Idp",
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