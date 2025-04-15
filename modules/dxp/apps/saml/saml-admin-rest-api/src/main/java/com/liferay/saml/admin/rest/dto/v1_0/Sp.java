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
	description = "Configuration of the SAML provider's SP capability.",
	value = "Sp"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Sp")
public class Sp implements Serializable {

	public static Sp toDTO(String json) {
		return ObjectMapperUtil.readValue(Sp.class, json);
	}

	public static Sp unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Sp.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAllowShowingTheLoginPortlet() {
		if (_allowShowingTheLoginPortletSupplier != null) {
			allowShowingTheLoginPortlet =
				_allowShowingTheLoginPortletSupplier.get();

			_allowShowingTheLoginPortletSupplier = null;
		}

		return allowShowingTheLoginPortlet;
	}

	public void setAllowShowingTheLoginPortlet(
		Boolean allowShowingTheLoginPortlet) {

		this.allowShowingTheLoginPortlet = allowShowingTheLoginPortlet;

		_allowShowingTheLoginPortletSupplier = null;
	}

	@JsonIgnore
	public void setAllowShowingTheLoginPortlet(
		UnsafeSupplier<Boolean, Exception>
			allowShowingTheLoginPortletUnsafeSupplier) {

		_allowShowingTheLoginPortletSupplier = () -> {
			try {
				return allowShowingTheLoginPortletUnsafeSupplier.get();
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
	protected Boolean allowShowingTheLoginPortlet;

	@JsonIgnore
	private Supplier<Boolean> _allowShowingTheLoginPortletSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getAssertionSignatureRequired() {
		if (_assertionSignatureRequiredSupplier != null) {
			assertionSignatureRequired =
				_assertionSignatureRequiredSupplier.get();

			_assertionSignatureRequiredSupplier = null;
		}

		return assertionSignatureRequired;
	}

	public void setAssertionSignatureRequired(
		Boolean assertionSignatureRequired) {

		this.assertionSignatureRequired = assertionSignatureRequired;

		_assertionSignatureRequiredSupplier = null;
	}

	@JsonIgnore
	public void setAssertionSignatureRequired(
		UnsafeSupplier<Boolean, Exception>
			assertionSignatureRequiredUnsafeSupplier) {

		_assertionSignatureRequiredSupplier = () -> {
			try {
				return assertionSignatureRequiredUnsafeSupplier.get();
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
	protected Boolean assertionSignatureRequired;

	@JsonIgnore
	private Supplier<Boolean> _assertionSignatureRequiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getClockSkew() {
		if (_clockSkewSupplier != null) {
			clockSkew = _clockSkewSupplier.get();

			_clockSkewSupplier = null;
		}

		return clockSkew;
	}

	public void setClockSkew(Long clockSkew) {
		this.clockSkew = clockSkew;

		_clockSkewSupplier = null;
	}

	@JsonIgnore
	public void setClockSkew(
		UnsafeSupplier<Long, Exception> clockSkewUnsafeSupplier) {

		_clockSkewSupplier = () -> {
			try {
				return clockSkewUnsafeSupplier.get();
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
	protected Long clockSkew;

	@JsonIgnore
	private Supplier<Long> _clockSkewSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getKeyStoreEncryptionCredentialPassword() {
		if (_keyStoreEncryptionCredentialPasswordSupplier != null) {
			keyStoreEncryptionCredentialPassword =
				_keyStoreEncryptionCredentialPasswordSupplier.get();

			_keyStoreEncryptionCredentialPasswordSupplier = null;
		}

		return keyStoreEncryptionCredentialPassword;
	}

	public void setKeyStoreEncryptionCredentialPassword(
		String keyStoreEncryptionCredentialPassword) {

		this.keyStoreEncryptionCredentialPassword =
			keyStoreEncryptionCredentialPassword;

		_keyStoreEncryptionCredentialPasswordSupplier = null;
	}

	@JsonIgnore
	public void setKeyStoreEncryptionCredentialPassword(
		UnsafeSupplier<String, Exception>
			keyStoreEncryptionCredentialPasswordUnsafeSupplier) {

		_keyStoreEncryptionCredentialPasswordSupplier = () -> {
			try {
				return keyStoreEncryptionCredentialPasswordUnsafeSupplier.get();
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
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	protected String keyStoreEncryptionCredentialPassword;

	@JsonIgnore
	private Supplier<String> _keyStoreEncryptionCredentialPasswordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLdapImportEnabled() {
		if (_ldapImportEnabledSupplier != null) {
			ldapImportEnabled = _ldapImportEnabledSupplier.get();

			_ldapImportEnabledSupplier = null;
		}

		return ldapImportEnabled;
	}

	public void setLdapImportEnabled(Boolean ldapImportEnabled) {
		this.ldapImportEnabled = ldapImportEnabled;

		_ldapImportEnabledSupplier = null;
	}

	@JsonIgnore
	public void setLdapImportEnabled(
		UnsafeSupplier<Boolean, Exception> ldapImportEnabledUnsafeSupplier) {

		_ldapImportEnabledSupplier = () -> {
			try {
				return ldapImportEnabledUnsafeSupplier.get();
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
	protected Boolean ldapImportEnabled;

	@JsonIgnore
	private Supplier<Boolean> _ldapImportEnabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSignAuthnRequest() {
		if (_signAuthnRequestSupplier != null) {
			signAuthnRequest = _signAuthnRequestSupplier.get();

			_signAuthnRequestSupplier = null;
		}

		return signAuthnRequest;
	}

	public void setSignAuthnRequest(Boolean signAuthnRequest) {
		this.signAuthnRequest = signAuthnRequest;

		_signAuthnRequestSupplier = null;
	}

	@JsonIgnore
	public void setSignAuthnRequest(
		UnsafeSupplier<Boolean, Exception> signAuthnRequestUnsafeSupplier) {

		_signAuthnRequestSupplier = () -> {
			try {
				return signAuthnRequestUnsafeSupplier.get();
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
	protected Boolean signAuthnRequest;

	@JsonIgnore
	private Supplier<Boolean> _signAuthnRequestSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Sp)) {
			return false;
		}

		Sp sp = (Sp)object;

		return Objects.equals(toString(), sp.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean allowShowingTheLoginPortlet = getAllowShowingTheLoginPortlet();

		if (allowShowingTheLoginPortlet != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"allowShowingTheLoginPortlet\": ");

			sb.append(allowShowingTheLoginPortlet);
		}

		Boolean assertionSignatureRequired = getAssertionSignatureRequired();

		if (assertionSignatureRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"assertionSignatureRequired\": ");

			sb.append(assertionSignatureRequired);
		}

		Long clockSkew = getClockSkew();

		if (clockSkew != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"clockSkew\": ");

			sb.append(clockSkew);
		}

		String keyStoreEncryptionCredentialPassword =
			getKeyStoreEncryptionCredentialPassword();

		if (keyStoreEncryptionCredentialPassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreEncryptionCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(keyStoreEncryptionCredentialPassword));

			sb.append("\"");
		}

		Boolean ldapImportEnabled = getLdapImportEnabled();

		if (ldapImportEnabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"ldapImportEnabled\": ");

			sb.append(ldapImportEnabled);
		}

		Boolean signAuthnRequest = getSignAuthnRequest();

		if (signAuthnRequest != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signAuthnRequest\": ");

			sb.append(signAuthnRequest);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.saml.admin.rest.dto.v1_0.Sp",
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