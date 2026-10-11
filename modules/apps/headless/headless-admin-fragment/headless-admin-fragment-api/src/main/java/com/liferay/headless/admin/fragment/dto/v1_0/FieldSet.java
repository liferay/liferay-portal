/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.fragment.dto.v1_0;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLField;
import com.liferay.portal.vulcan.graphql.annotation.GraphQLName;
import com.liferay.portal.vulcan.util.ObjectMapperUtil;

import jakarta.annotation.Generated;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Rubén Pulido
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "A group of related fields in a fragment's configuration.",
	value = "FieldSet"
)
@io.swagger.v3.oas.annotations.media.Schema(
	description = "A group of related fields in a fragment's configuration.",
	requiredProperties = {"fields"}
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "FieldSet")
public class FieldSet implements Serializable {

	public static FieldSet toDTO(String json) {
		return ObjectMapperUtil.readValue(FieldSet.class, json);
	}

	public static FieldSet unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(FieldSet.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The role of the field set in the page editor's configuration panel."
	)
	@JsonGetter("configurationRole")
	@Valid
	public ConfigurationRole getConfigurationRole() {
		if (_configurationRoleSupplier != null) {
			configurationRole = _configurationRoleSupplier.get();

			_configurationRoleSupplier = null;
		}

		return configurationRole;
	}

	@JsonIgnore
	public String getConfigurationRoleAsString() {
		ConfigurationRole configurationRole = getConfigurationRole();

		if (configurationRole == null) {
			return null;
		}

		return configurationRole.toString();
	}

	public void setConfigurationRole(ConfigurationRole configurationRole) {
		this.configurationRole = configurationRole;

		_configurationRoleSupplier = null;
	}

	@JsonIgnore
	public void setConfigurationRole(
		UnsafeSupplier<ConfigurationRole, Exception>
			configurationRoleUnsafeSupplier) {

		_configurationRoleSupplier = () -> {
			try {
				return configurationRoleUnsafeSupplier.get();
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
		description = "The role of the field set in the page editor's configuration panel."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ConfigurationRole configurationRole;

	@JsonIgnore
	private Supplier<ConfigurationRole> _configurationRoleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The JavaScript module loaded to render the field set with a custom component."
	)
	public String getCustomComponentModule() {
		if (_customComponentModuleSupplier != null) {
			customComponentModule = _customComponentModuleSupplier.get();

			_customComponentModuleSupplier = null;
		}

		return customComponentModule;
	}

	public void setCustomComponentModule(String customComponentModule) {
		this.customComponentModule = customComponentModule;

		_customComponentModuleSupplier = null;
	}

	@JsonIgnore
	public void setCustomComponentModule(
		UnsafeSupplier<String, Exception> customComponentModuleUnsafeSupplier) {

		_customComponentModuleSupplier = () -> {
			try {
				return customComponentModuleUnsafeSupplier.get();
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
		description = "The JavaScript module loaded to render the field set with a custom component."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String customComponentModule;

	@JsonIgnore
	private Supplier<String> _customComponentModuleSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field set's fields."
	)
	@Valid
	public Field[] getFields() {
		if (_fieldsSupplier != null) {
			fields = _fieldsSupplier.get();

			_fieldsSupplier = null;
		}

		return fields;
	}

	public void setFields(Field[] fields) {
		this.fields = fields;

		_fieldsSupplier = null;
	}

	@JsonIgnore
	public void setFields(
		UnsafeSupplier<Field[], Exception> fieldsUnsafeSupplier) {

		_fieldsSupplier = () -> {
			try {
				return fieldsUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The field set's fields.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	@NotNull
	protected Field[] fields;

	@JsonIgnore
	private Supplier<Field[]> _fieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field set's label."
	)
	public String getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(String label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		_labelSupplier = () -> {
			try {
				return labelUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The field set's label.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof FieldSet)) {
			return false;
		}

		FieldSet fieldSet = (FieldSet)object;

		return Objects.equals(toString(), fieldSet.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ConfigurationRole configurationRole = getConfigurationRole();

		if (configurationRole != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"configurationRole\": ");

			sb.append("\"");
			sb.append(configurationRole);
			sb.append("\"");
		}

		String customComponentModule = getCustomComponentModule();

		if (customComponentModule != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customComponentModule\": ");

			sb.append("\"");

			sb.append(_escape(customComponentModule));

			sb.append("\"");
		}

		Field[] fields = getFields();

		if (fields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fields\": ");

			sb.append("[");

			for (int i = 0; i < fields.length; i++) {
				sb.append(String.valueOf(fields[i]));

				if ((i + 1) < fields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append("\"");

			sb.append(_escape(label));

			sb.append("\"");
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.admin.fragment.dto.v1_0.FieldSet",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("ConfigurationRole")
	public static enum ConfigurationRole {

		ADVANCED("advanced"), STYLE("style");

		@JsonCreator
		public static ConfigurationRole create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (ConfigurationRole configurationRole : values()) {
				if (Objects.equals(configurationRole.getValue(), value)) {
					return configurationRole;
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

		private ConfigurationRole(String value) {
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

	private static String _toJSON(Object value) {
		if (value instanceof Collection) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray((Collection<?>)value));
		}
		else if (value instanceof Map) {
			return String.valueOf(
				JSONFactoryUtil.createJSONObject((Map<?, ?>)value));
		}
		else if (value instanceof Object[]) {
			return String.valueOf(
				JSONFactoryUtil.createJSONArray(
					Arrays.asList((Object[])value)));
		}
		else if (value instanceof String) {
			return StringBundler.concat("\"", _escape(value), "\"");
		}

		return String.valueOf(value);
	}

	private static final String[][] _JSON_ESCAPE_STRINGS = {
		{"\\", "\"", "\b", "\f", "\n", "\r", "\t"},
		{"\\\\", "\\\"", "\\b", "\\f", "\\n", "\\r", "\\t"}
	};

	private Map<String, Serializable> _extendedProperties;

}
// LIFERAY-REST-BUILDER-HASH:-1979280094