/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.dto.v1_0;

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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("ElementDefinition")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ElementDefinition")
public class ElementDefinition implements Serializable {

	public static ElementDefinition toDTO(String json) {
		return ObjectMapperUtil.readValue(ElementDefinition.class, json);
	}

	public static ElementDefinition unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ElementDefinition.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	public String getCategory() {
		if (_categorySupplier != null) {
			category = _categorySupplier.get();

			_categorySupplier = null;
		}

		return category;
	}

	public void setCategory(String category) {
		this.category = category;

		_categorySupplier = null;
	}

	@JsonIgnore
	public void setCategory(
		UnsafeSupplier<String, Exception> categoryUnsafeSupplier) {

		_categorySupplier = () -> {
			try {
				return categoryUnsafeSupplier.get();
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
	protected String category;

	@JsonIgnore
	private Supplier<String> _categorySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Configuration getConfiguration() {
		if (_configurationSupplier != null) {
			configuration = _configurationSupplier.get();

			_configurationSupplier = null;
		}

		return configuration;
	}

	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;

		_configurationSupplier = null;
	}

	@JsonIgnore
	public void setConfiguration(
		UnsafeSupplier<Configuration, Exception> configurationUnsafeSupplier) {

		_configurationSupplier = () -> {
			try {
				return configurationUnsafeSupplier.get();
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
	protected Configuration configuration;

	@JsonIgnore
	private Supplier<Configuration> _configurationSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getIcon() {
		if (_iconSupplier != null) {
			icon = _iconSupplier.get();

			_iconSupplier = null;
		}

		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;

		_iconSupplier = null;
	}

	@JsonIgnore
	public void setIcon(UnsafeSupplier<String, Exception> iconUnsafeSupplier) {
		_iconSupplier = () -> {
			try {
				return iconUnsafeSupplier.get();
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
	protected String icon;

	@JsonIgnore
	private Supplier<String> _iconSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public UiConfiguration getUiConfiguration() {
		if (_uiConfigurationSupplier != null) {
			uiConfiguration = _uiConfigurationSupplier.get();

			_uiConfigurationSupplier = null;
		}

		return uiConfiguration;
	}

	public void setUiConfiguration(UiConfiguration uiConfiguration) {
		this.uiConfiguration = uiConfiguration;

		_uiConfigurationSupplier = null;
	}

	@JsonIgnore
	public void setUiConfiguration(
		UnsafeSupplier<UiConfiguration, Exception>
			uiConfigurationUnsafeSupplier) {

		_uiConfigurationSupplier = () -> {
			try {
				return uiConfigurationUnsafeSupplier.get();
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
	protected UiConfiguration uiConfiguration;

	@JsonIgnore
	private Supplier<UiConfiguration> _uiConfigurationSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ElementDefinition)) {
			return false;
		}

		ElementDefinition elementDefinition = (ElementDefinition)object;

		return Objects.equals(toString(), elementDefinition.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		String category = getCategory();

		if (category != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"category\": ");

			sb.append("\"");

			sb.append(_escape(category));

			sb.append("\"");
		}

		Configuration configuration = getConfiguration();

		if (configuration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configuration\": ");

			sb.append(String.valueOf(configuration));
		}

		String icon = getIcon();

		if (icon != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"icon\": ");

			sb.append("\"");

			sb.append(_escape(icon));

			sb.append("\"");
		}

		UiConfiguration uiConfiguration = getUiConfiguration();

		if (uiConfiguration != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"uiConfiguration\": ");

			sb.append(String.valueOf(uiConfiguration));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.ElementDefinition",
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