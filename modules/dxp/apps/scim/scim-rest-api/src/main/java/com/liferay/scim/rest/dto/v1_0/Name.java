/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.scim.rest.dto.v1_0;

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

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Olivér Kecskeméty
 * @generated
 */
@Generated("")
@GraphQLName(description = "The components of the user's name.", value = "Name")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Name")
public class Name implements Serializable {

	public static Name toDTO(String json) {
		return ObjectMapperUtil.readValue(Name.class, json);
	}

	public static Name unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Name.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFamilyName() {
		if (_familyNameSupplier != null) {
			familyName = _familyNameSupplier.get();

			_familyNameSupplier = null;
		}

		return familyName;
	}

	public void setFamilyName(String familyName) {
		this.familyName = familyName;

		_familyNameSupplier = null;
	}

	@JsonIgnore
	public void setFamilyName(
		UnsafeSupplier<String, Exception> familyNameUnsafeSupplier) {

		_familyNameSupplier = () -> {
			try {
				return familyNameUnsafeSupplier.get();
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
	protected String familyName;

	@JsonIgnore
	private Supplier<String> _familyNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFormatted() {
		if (_formattedSupplier != null) {
			formatted = _formattedSupplier.get();

			_formattedSupplier = null;
		}

		return formatted;
	}

	public void setFormatted(String formatted) {
		this.formatted = formatted;

		_formattedSupplier = null;
	}

	@JsonIgnore
	public void setFormatted(
		UnsafeSupplier<String, Exception> formattedUnsafeSupplier) {

		_formattedSupplier = () -> {
			try {
				return formattedUnsafeSupplier.get();
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
	protected String formatted;

	@JsonIgnore
	private Supplier<String> _formattedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getGivenName() {
		if (_givenNameSupplier != null) {
			givenName = _givenNameSupplier.get();

			_givenNameSupplier = null;
		}

		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;

		_givenNameSupplier = null;
	}

	@JsonIgnore
	public void setGivenName(
		UnsafeSupplier<String, Exception> givenNameUnsafeSupplier) {

		_givenNameSupplier = () -> {
			try {
				return givenNameUnsafeSupplier.get();
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
	protected String givenName;

	@JsonIgnore
	private Supplier<String> _givenNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHonorificPrefix() {
		if (_honorificPrefixSupplier != null) {
			honorificPrefix = _honorificPrefixSupplier.get();

			_honorificPrefixSupplier = null;
		}

		return honorificPrefix;
	}

	public void setHonorificPrefix(String honorificPrefix) {
		this.honorificPrefix = honorificPrefix;

		_honorificPrefixSupplier = null;
	}

	@JsonIgnore
	public void setHonorificPrefix(
		UnsafeSupplier<String, Exception> honorificPrefixUnsafeSupplier) {

		_honorificPrefixSupplier = () -> {
			try {
				return honorificPrefixUnsafeSupplier.get();
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
	protected String honorificPrefix;

	@JsonIgnore
	private Supplier<String> _honorificPrefixSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHonorificSuffix() {
		if (_honorificSuffixSupplier != null) {
			honorificSuffix = _honorificSuffixSupplier.get();

			_honorificSuffixSupplier = null;
		}

		return honorificSuffix;
	}

	public void setHonorificSuffix(String honorificSuffix) {
		this.honorificSuffix = honorificSuffix;

		_honorificSuffixSupplier = null;
	}

	@JsonIgnore
	public void setHonorificSuffix(
		UnsafeSupplier<String, Exception> honorificSuffixUnsafeSupplier) {

		_honorificSuffixSupplier = () -> {
			try {
				return honorificSuffixUnsafeSupplier.get();
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
	protected String honorificSuffix;

	@JsonIgnore
	private Supplier<String> _honorificSuffixSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getMiddleName() {
		if (_middleNameSupplier != null) {
			middleName = _middleNameSupplier.get();

			_middleNameSupplier = null;
		}

		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;

		_middleNameSupplier = null;
	}

	@JsonIgnore
	public void setMiddleName(
		UnsafeSupplier<String, Exception> middleNameUnsafeSupplier) {

		_middleNameSupplier = () -> {
			try {
				return middleNameUnsafeSupplier.get();
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
	protected String middleName;

	@JsonIgnore
	private Supplier<String> _middleNameSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Name)) {
			return false;
		}

		Name name = (Name)object;

		return Objects.equals(toString(), name.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String familyName = getFamilyName();

		if (familyName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"familyName\": ");

			sb.append("\"");

			sb.append(_escape(familyName));

			sb.append("\"");
		}

		String formatted = getFormatted();

		if (formatted != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"formatted\": ");

			sb.append("\"");

			sb.append(_escape(formatted));

			sb.append("\"");
		}

		String givenName = getGivenName();

		if (givenName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"givenName\": ");

			sb.append("\"");

			sb.append(_escape(givenName));

			sb.append("\"");
		}

		String honorificPrefix = getHonorificPrefix();

		if (honorificPrefix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificPrefix\": ");

			sb.append("\"");

			sb.append(_escape(honorificPrefix));

			sb.append("\"");
		}

		String honorificSuffix = getHonorificSuffix();

		if (honorificSuffix != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"honorificSuffix\": ");

			sb.append("\"");

			sb.append(_escape(honorificSuffix));

			sb.append("\"");
		}

		String middleName = getMiddleName();

		if (middleName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"middleName\": ");

			sb.append("\"");

			sb.append(_escape(middleName));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.scim.rest.dto.v1_0.Name",
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