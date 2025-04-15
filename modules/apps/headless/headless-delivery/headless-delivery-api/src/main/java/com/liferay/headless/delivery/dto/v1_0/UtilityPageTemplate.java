/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents a Utility Page Template.",
	value = "UtilityPageTemplate"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "UtilityPageTemplate")
public class UtilityPageTemplate implements Serializable {

	public static UtilityPageTemplate toDTO(String json) {
		return ObjectMapperUtil.readValue(UtilityPageTemplate.class, json);
	}

	public static UtilityPageTemplate unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			UtilityPageTemplate.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "Specifies if the utility page template should be the default for the given type."
	)
	public Boolean getDefaultTemplate() {
		if (_defaultTemplateSupplier != null) {
			defaultTemplate = _defaultTemplateSupplier.get();

			_defaultTemplateSupplier = null;
		}

		return defaultTemplate;
	}

	public void setDefaultTemplate(Boolean defaultTemplate) {
		this.defaultTemplate = defaultTemplate;

		_defaultTemplateSupplier = null;
	}

	@JsonIgnore
	public void setDefaultTemplate(
		UnsafeSupplier<Boolean, Exception> defaultTemplateUnsafeSupplier) {

		_defaultTemplateSupplier = () -> {
			try {
				return defaultTemplateUnsafeSupplier.get();
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
		description = "Specifies if the utility page template should be the default for the given type."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean defaultTemplate;

	@JsonIgnore
	private Supplier<Boolean> _defaultTemplateSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The utility page template external reference code."
	)
	public String getExternalReferenceCode() {
		if (_externalReferenceCodeSupplier != null) {
			externalReferenceCode = _externalReferenceCodeSupplier.get();

			_externalReferenceCodeSupplier = null;
		}

		return externalReferenceCode;
	}

	public void setExternalReferenceCode(String externalReferenceCode) {
		this.externalReferenceCode = externalReferenceCode;

		_externalReferenceCodeSupplier = null;
	}

	@JsonIgnore
	public void setExternalReferenceCode(
		UnsafeSupplier<String, Exception> externalReferenceCodeUnsafeSupplier) {

		_externalReferenceCodeSupplier = () -> {
			try {
				return externalReferenceCodeUnsafeSupplier.get();
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
		description = "The utility page template external reference code."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String externalReferenceCode;

	@JsonIgnore
	private Supplier<String> _externalReferenceCodeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The utility page template name."
	)
	public String getName() {
		if (_nameSupplier != null) {
			name = _nameSupplier.get();

			_nameSupplier = null;
		}

		return name;
	}

	public void setName(String name) {
		this.name = name;

		_nameSupplier = null;
	}

	@JsonIgnore
	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		_nameSupplier = () -> {
			try {
				return nameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The utility page template name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The utility page template type."
	)
	@JsonGetter("type")
	@Valid
	public Type getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	@JsonIgnore
	public String getTypeAsString() {
		Type type = getType();

		if (type == null) {
			return null;
		}

		return type.toString();
	}

	public void setType(Type type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<Type, Exception> typeUnsafeSupplier) {
		_typeSupplier = () -> {
			try {
				return typeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The utility page template type.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Type type;

	@JsonIgnore
	private Supplier<Type> _typeSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof UtilityPageTemplate)) {
			return false;
		}

		UtilityPageTemplate utilityPageTemplate = (UtilityPageTemplate)object;

		return Objects.equals(toString(), utilityPageTemplate.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Boolean defaultTemplate = getDefaultTemplate();

		if (defaultTemplate != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultTemplate\": ");

			sb.append(defaultTemplate);
		}

		String externalReferenceCode = getExternalReferenceCode();

		if (externalReferenceCode != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"externalReferenceCode\": ");

			sb.append("\"");

			sb.append(_escape(externalReferenceCode));

			sb.append("\"");
		}

		String name = getName();

		if (name != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name\": ");

			sb.append("\"");

			sb.append(_escape(name));

			sb.append("\"");
		}

		Type type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(type);

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.UtilityPageTemplate",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("Type")
	public static enum Type {

		COOKIE_POLICY("CookiePolicy"), CREATE_ACCOUNT("CreateAccount"),
		ERROR("Error"), ERROR_CODE404("ErrorCode404"),
		ERROR_CODE500("ErrorCode500"), FORGOT_PASSWORD("ForgotPassword"),
		LOGIN("Login"), TERMS_OF_USE("TermsOfUse");

		@JsonCreator
		public static Type create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (Type type : values()) {
				if (Objects.equals(type.getValue(), value)) {
					return type;
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

		private Type(String value) {
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