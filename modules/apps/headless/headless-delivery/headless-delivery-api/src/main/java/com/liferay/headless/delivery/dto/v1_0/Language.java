/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.dto.v1_0;

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

import jakarta.validation.Valid;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Describes a specific language in the platform.",
	value = "Language"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Language")
public class Language implements Serializable {

	public static Language toDTO(String json) {
		return ObjectMapperUtil.readValue(Language.class, json);
	}

	public static Language unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Language.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The language's contry name."
	)
	public String getCountryName() {
		if (_countryNameSupplier != null) {
			countryName = _countryNameSupplier.get();

			_countryNameSupplier = null;
		}

		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;

		_countryNameSupplier = null;
	}

	@JsonIgnore
	public void setCountryName(
		UnsafeSupplier<String, Exception> countryNameUnsafeSupplier) {

		_countryNameSupplier = () -> {
			try {
				return countryNameUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The language's contry name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String countryName;

	@JsonIgnore
	private Supplier<String> _countryNameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized language's country name values."
	)
	@Valid
	public Map<String, String> getCountryName_i18n() {
		if (_countryName_i18nSupplier != null) {
			countryName_i18n = _countryName_i18nSupplier.get();

			_countryName_i18nSupplier = null;
		}

		return countryName_i18n;
	}

	public void setCountryName_i18n(Map<String, String> countryName_i18n) {
		this.countryName_i18n = countryName_i18n;

		_countryName_i18nSupplier = null;
	}

	@JsonIgnore
	public void setCountryName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			countryName_i18nUnsafeSupplier) {

		_countryName_i18nSupplier = () -> {
			try {
				return countryName_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized language's country name values.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> countryName_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _countryName_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The language's ID."
	)
	public String getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(String id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<String, Exception> idUnsafeSupplier) {
		_idSupplier = () -> {
			try {
				return idUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The language's ID.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String id;

	@JsonIgnore
	private Supplier<String> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether the language has benn marked as default language."
	)
	public Boolean getMarkedAsDefault() {
		if (_markedAsDefaultSupplier != null) {
			markedAsDefault = _markedAsDefaultSupplier.get();

			_markedAsDefaultSupplier = null;
		}

		return markedAsDefault;
	}

	public void setMarkedAsDefault(Boolean markedAsDefault) {
		this.markedAsDefault = markedAsDefault;

		_markedAsDefaultSupplier = null;
	}

	@JsonIgnore
	public void setMarkedAsDefault(
		UnsafeSupplier<Boolean, Exception> markedAsDefaultUnsafeSupplier) {

		_markedAsDefaultSupplier = () -> {
			try {
				return markedAsDefaultUnsafeSupplier.get();
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
		description = "A flag that indicates whether the language has benn marked as default language."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean markedAsDefault;

	@JsonIgnore
	private Supplier<Boolean> _markedAsDefaultSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The language's name."
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

	@GraphQLField(description = "The language's name.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized language's name values."
	)
	@Valid
	public Map<String, String> getName_i18n() {
		if (_name_i18nSupplier != null) {
			name_i18n = _name_i18nSupplier.get();

			_name_i18nSupplier = null;
		}

		return name_i18n;
	}

	public void setName_i18n(Map<String, String> name_i18n) {
		this.name_i18n = name_i18n;

		_name_i18nSupplier = null;
	}

	@JsonIgnore
	public void setName_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			name_i18nUnsafeSupplier) {

		_name_i18nSupplier = () -> {
			try {
				return name_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized language's name values.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, String> name_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _name_i18nSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Language)) {
			return false;
		}

		Language language = (Language)object;

		return Objects.equals(toString(), language.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String countryName = getCountryName();

		if (countryName != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryName\": ");

			sb.append("\"");

			sb.append(_escape(countryName));

			sb.append("\"");
		}

		Map<String, String> countryName_i18n = getCountryName_i18n();

		if (countryName_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"countryName_i18n\": ");

			sb.append(_toJSON(countryName_i18n));
		}

		String id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append("\"");

			sb.append(_escape(id));

			sb.append("\"");
		}

		Boolean markedAsDefault = getMarkedAsDefault();

		if (markedAsDefault != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"markedAsDefault\": ");

			sb.append(markedAsDefault);
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

		Map<String, String> name_i18n = getName_i18n();

		if (name_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"name_i18n\": ");

			sb.append(_toJSON(name_i18n));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.Language",
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