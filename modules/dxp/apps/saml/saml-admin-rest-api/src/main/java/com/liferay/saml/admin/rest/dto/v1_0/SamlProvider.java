/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.admin.rest.dto.v1_0;

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

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Stian Sigvartsen
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "The complete configuration of this SAML Provider.",
	value = "SamlProvider"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "SamlProvider")
public class SamlProvider implements Serializable {

	public static SamlProvider toDTO(String json) {
		return ObjectMapperUtil.readValue(SamlProvider.class, json);
	}

	public static SamlProvider unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(SamlProvider.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getEnabled() {
		if (_enabledSupplier != null) {
			enabled = _enabledSupplier.get();

			_enabledSupplier = null;
		}

		return enabled;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;

		_enabledSupplier = null;
	}

	@JsonIgnore
	public void setEnabled(
		UnsafeSupplier<Boolean, Exception> enabledUnsafeSupplier) {

		_enabledSupplier = () -> {
			try {
				return enabledUnsafeSupplier.get();
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
	protected Boolean enabled;

	@JsonIgnore
	private Supplier<Boolean> _enabledSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getEntityId() {
		if (_entityIdSupplier != null) {
			entityId = _entityIdSupplier.get();

			_entityIdSupplier = null;
		}

		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;

		_entityIdSupplier = null;
	}

	@JsonIgnore
	public void setEntityId(
		UnsafeSupplier<String, Exception> entityIdUnsafeSupplier) {

		_entityIdSupplier = () -> {
			try {
				return entityIdUnsafeSupplier.get();
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
	protected String entityId;

	@JsonIgnore
	private Supplier<String> _entityIdSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Idp getIdp() {
		if (_idpSupplier != null) {
			idp = _idpSupplier.get();

			_idpSupplier = null;
		}

		return idp;
	}

	public void setIdp(Idp idp) {
		this.idp = idp;

		_idpSupplier = null;
	}

	@JsonIgnore
	public void setIdp(UnsafeSupplier<Idp, Exception> idpUnsafeSupplier) {
		_idpSupplier = () -> {
			try {
				return idpUnsafeSupplier.get();
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
	protected Idp idp;

	@JsonIgnore
	private Supplier<Idp> _idpSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getKeyStoreCredentialPassword() {
		if (_keyStoreCredentialPasswordSupplier != null) {
			keyStoreCredentialPassword =
				_keyStoreCredentialPasswordSupplier.get();

			_keyStoreCredentialPasswordSupplier = null;
		}

		return keyStoreCredentialPassword;
	}

	public void setKeyStoreCredentialPassword(
		String keyStoreCredentialPassword) {

		this.keyStoreCredentialPassword = keyStoreCredentialPassword;

		_keyStoreCredentialPasswordSupplier = null;
	}

	@JsonIgnore
	public void setKeyStoreCredentialPassword(
		UnsafeSupplier<String, Exception>
			keyStoreCredentialPasswordUnsafeSupplier) {

		_keyStoreCredentialPasswordSupplier = () -> {
			try {
				return keyStoreCredentialPasswordUnsafeSupplier.get();
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
	protected String keyStoreCredentialPassword;

	@JsonIgnore
	private Supplier<String> _keyStoreCredentialPasswordSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("role")
	@Valid
	public Role getRole() {
		if (_roleSupplier != null) {
			role = _roleSupplier.get();

			_roleSupplier = null;
		}

		return role;
	}

	@JsonIgnore
	public String getRoleAsString() {
		Role role = getRole();

		if (role == null) {
			return null;
		}

		return role.toString();
	}

	public void setRole(Role role) {
		this.role = role;

		_roleSupplier = null;
	}

	@JsonIgnore
	public void setRole(UnsafeSupplier<Role, Exception> roleUnsafeSupplier) {
		_roleSupplier = () -> {
			try {
				return roleUnsafeSupplier.get();
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
	protected Role role;

	@JsonIgnore
	private Supplier<Role> _roleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSignMetadata() {
		if (_signMetadataSupplier != null) {
			signMetadata = _signMetadataSupplier.get();

			_signMetadataSupplier = null;
		}

		return signMetadata;
	}

	public void setSignMetadata(Boolean signMetadata) {
		this.signMetadata = signMetadata;

		_signMetadataSupplier = null;
	}

	@JsonIgnore
	public void setSignMetadata(
		UnsafeSupplier<Boolean, Exception> signMetadataUnsafeSupplier) {

		_signMetadataSupplier = () -> {
			try {
				return signMetadataUnsafeSupplier.get();
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
	protected Boolean signMetadata;

	@JsonIgnore
	private Supplier<Boolean> _signMetadataSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Sp getSp() {
		if (_spSupplier != null) {
			sp = _spSupplier.get();

			_spSupplier = null;
		}

		return sp;
	}

	public void setSp(Sp sp) {
		this.sp = sp;

		_spSupplier = null;
	}

	@JsonIgnore
	public void setSp(UnsafeSupplier<Sp, Exception> spUnsafeSupplier) {
		_spSupplier = () -> {
			try {
				return spUnsafeSupplier.get();
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
	protected Sp sp;

	@JsonIgnore
	private Supplier<Sp> _spSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getSslRequired() {
		if (_sslRequiredSupplier != null) {
			sslRequired = _sslRequiredSupplier.get();

			_sslRequiredSupplier = null;
		}

		return sslRequired;
	}

	public void setSslRequired(Boolean sslRequired) {
		this.sslRequired = sslRequired;

		_sslRequiredSupplier = null;
	}

	@JsonIgnore
	public void setSslRequired(
		UnsafeSupplier<Boolean, Exception> sslRequiredUnsafeSupplier) {

		_sslRequiredSupplier = () -> {
			try {
				return sslRequiredUnsafeSupplier.get();
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
	protected Boolean sslRequired;

	@JsonIgnore
	private Supplier<Boolean> _sslRequiredSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof SamlProvider)) {
			return false;
		}

		SamlProvider samlProvider = (SamlProvider)object;

		return Objects.equals(toString(), samlProvider.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean enabled = getEnabled();

		if (enabled != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"enabled\": ");

			sb.append(enabled);
		}

		String entityId = getEntityId();

		if (entityId != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"entityId\": ");

			sb.append("\"");

			sb.append(_escape(entityId));

			sb.append("\"");
		}

		Idp idp = getIdp();

		if (idp != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"idp\": ");

			sb.append(String.valueOf(idp));
		}

		String keyStoreCredentialPassword = getKeyStoreCredentialPassword();

		if (keyStoreCredentialPassword != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"keyStoreCredentialPassword\": ");

			sb.append("\"");

			sb.append(_escape(keyStoreCredentialPassword));

			sb.append("\"");
		}

		Role role = getRole();

		if (role != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"role\": ");

			sb.append("\"");

			sb.append(role);

			sb.append("\"");
		}

		Boolean signMetadata = getSignMetadata();

		if (signMetadata != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"signMetadata\": ");

			sb.append(signMetadata);
		}

		Sp sp = getSp();

		if (sp != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sp\": ");

			sb.append(String.valueOf(sp));
		}

		Boolean sslRequired = getSslRequired();

		if (sslRequired != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"sslRequired\": ");

			sb.append(sslRequired);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.saml.admin.rest.dto.v1_0.SamlProvider",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Role")
	public static enum Role {

		IDP("idp"), SP("sp");

		@JsonCreator
		public static Role create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Role role : values()) {
				if (Objects.equals(role.getValue(), value)) {
					return role;
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

		private Role(String value) {
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