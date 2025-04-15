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
import com.liferay.portal.kernel.json.JSONFactoryUtil;
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
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
@GraphQLName("Field")
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "Field")
public class Field implements Serializable {

	public static Field toDTO(String json) {
		return ObjectMapperUtil.readValue(Field.class, json);
	}

	public static Field unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(Field.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Object getDefaultValue() {
		if (_defaultValueSupplier != null) {
			defaultValue = _defaultValueSupplier.get();

			_defaultValueSupplier = null;
		}

		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;

		_defaultValueSupplier = null;
	}

	@JsonIgnore
	public void setDefaultValue(
		UnsafeSupplier<Object, Exception> defaultValueUnsafeSupplier) {

		_defaultValueSupplier = () -> {
			try {
				return defaultValueUnsafeSupplier.get();
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
	protected Object defaultValue;

	@JsonIgnore
	private Supplier<Object> _defaultValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public FieldMapping[] getFieldMappings() {
		if (_fieldMappingsSupplier != null) {
			fieldMappings = _fieldMappingsSupplier.get();

			_fieldMappingsSupplier = null;
		}

		return fieldMappings;
	}

	public void setFieldMappings(FieldMapping[] fieldMappings) {
		this.fieldMappings = fieldMappings;

		_fieldMappingsSupplier = null;
	}

	@JsonIgnore
	public void setFieldMappings(
		UnsafeSupplier<FieldMapping[], Exception> fieldMappingsUnsafeSupplier) {

		_fieldMappingsSupplier = () -> {
			try {
				return fieldMappingsUnsafeSupplier.get();
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
	protected FieldMapping[] fieldMappings;

	@JsonIgnore
	private Supplier<FieldMapping[]> _fieldMappingsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHelpText() {
		if (_helpTextSupplier != null) {
			helpText = _helpTextSupplier.get();

			_helpTextSupplier = null;
		}

		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;

		_helpTextSupplier = null;
	}

	@JsonIgnore
	public void setHelpText(
		UnsafeSupplier<String, Exception> helpTextUnsafeSupplier) {

		_helpTextSupplier = () -> {
			try {
				return helpTextUnsafeSupplier.get();
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
	protected String helpText;

	@JsonIgnore
	private Supplier<String> _helpTextSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getHelpTextLocalized() {
		if (_helpTextLocalizedSupplier != null) {
			helpTextLocalized = _helpTextLocalizedSupplier.get();

			_helpTextLocalizedSupplier = null;
		}

		return helpTextLocalized;
	}

	public void setHelpTextLocalized(String helpTextLocalized) {
		this.helpTextLocalized = helpTextLocalized;

		_helpTextLocalizedSupplier = null;
	}

	@JsonIgnore
	public void setHelpTextLocalized(
		UnsafeSupplier<String, Exception> helpTextLocalizedUnsafeSupplier) {

		_helpTextLocalizedSupplier = () -> {
			try {
				return helpTextLocalizedUnsafeSupplier.get();
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
	protected String helpTextLocalized;

	@JsonIgnore
	private Supplier<String> _helpTextLocalizedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getLabelLocalized() {
		if (_labelLocalizedSupplier != null) {
			labelLocalized = _labelLocalizedSupplier.get();

			_labelLocalizedSupplier = null;
		}

		return labelLocalized;
	}

	public void setLabelLocalized(String labelLocalized) {
		this.labelLocalized = labelLocalized;

		_labelLocalizedSupplier = null;
	}

	@JsonIgnore
	public void setLabelLocalized(
		UnsafeSupplier<String, Exception> labelLocalizedUnsafeSupplier) {

		_labelLocalizedSupplier = () -> {
			try {
				return labelLocalizedUnsafeSupplier.get();
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
	protected String labelLocalized;

	@JsonIgnore
	private Supplier<String> _labelLocalizedSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getType() {
		if (_typeSupplier != null) {
			type = _typeSupplier.get();

			_typeSupplier = null;
		}

		return type;
	}

	public void setType(String type) {
		this.type = type;

		_typeSupplier = null;
	}

	@JsonIgnore
	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String type;

	@JsonIgnore
	private Supplier<String> _typeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public TypeOptions getTypeOptions() {
		if (_typeOptionsSupplier != null) {
			typeOptions = _typeOptionsSupplier.get();

			_typeOptionsSupplier = null;
		}

		return typeOptions;
	}

	public void setTypeOptions(TypeOptions typeOptions) {
		this.typeOptions = typeOptions;

		_typeOptionsSupplier = null;
	}

	@JsonIgnore
	public void setTypeOptions(
		UnsafeSupplier<TypeOptions, Exception> typeOptionsUnsafeSupplier) {

		_typeOptionsSupplier = () -> {
			try {
				return typeOptionsUnsafeSupplier.get();
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
	protected TypeOptions typeOptions;

	@JsonIgnore
	private Supplier<TypeOptions> _typeOptionsSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof Field)) {
			return false;
		}

		Field field = (Field)object;

		return Objects.equals(toString(), field.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Object defaultValue = getDefaultValue();

		if (defaultValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			if (defaultValue instanceof Map) {
				sb.append(
					JSONFactoryUtil.createJSONObject((Map<?, ?>)defaultValue));
			}
			else if (defaultValue instanceof String) {
				sb.append("\"");
				sb.append(_escape((String)defaultValue));
				sb.append("\"");
			}
			else {
				sb.append(defaultValue);
			}
		}

		FieldMapping[] fieldMappings = getFieldMappings();

		if (fieldMappings != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldMappings\": ");

			sb.append("[");

			for (int i = 0; i < fieldMappings.length; i++) {
				sb.append(String.valueOf(fieldMappings[i]));

				if ((i + 1) < fieldMappings.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		String helpText = getHelpText();

		if (helpText != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpText\": ");

			sb.append("\"");

			sb.append(_escape(helpText));

			sb.append("\"");
		}

		String helpTextLocalized = getHelpTextLocalized();

		if (helpTextLocalized != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"helpTextLocalized\": ");

			sb.append("\"");

			sb.append(_escape(helpTextLocalized));

			sb.append("\"");
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

		String labelLocalized = getLabelLocalized();

		if (labelLocalized != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"labelLocalized\": ");

			sb.append("\"");

			sb.append(_escape(labelLocalized));

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

		String type = getType();

		if (type != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"type\": ");

			sb.append("\"");

			sb.append(_escape(type));

			sb.append("\"");
		}

		TypeOptions typeOptions = getTypeOptions();

		if (typeOptions != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"typeOptions\": ");

			sb.append(String.valueOf(typeOptions));
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.search.experiences.rest.dto.v1_0.Field",
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