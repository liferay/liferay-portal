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
	description = "Represents the value of each field in structured content. Fields can contain different information types (e.g., documents, geolocation, etc.).",
	value = "ContentField"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "ContentField")
public class ContentField implements Serializable {

	public static ContentField toDTO(String json) {
		return ObjectMapperUtil.readValue(ContentField.class, json);
	}

	public static ContentField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(ContentField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's value."
	)
	@Valid
	public ContentFieldValue getContentFieldValue() {
		if (_contentFieldValueSupplier != null) {
			contentFieldValue = _contentFieldValueSupplier.get();

			_contentFieldValueSupplier = null;
		}

		return contentFieldValue;
	}

	public void setContentFieldValue(ContentFieldValue contentFieldValue) {
		this.contentFieldValue = contentFieldValue;

		_contentFieldValueSupplier = null;
	}

	@JsonIgnore
	public void setContentFieldValue(
		UnsafeSupplier<ContentFieldValue, Exception>
			contentFieldValueUnsafeSupplier) {

		_contentFieldValueSupplier = () -> {
			try {
				return contentFieldValueUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The field's value.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContentFieldValue contentFieldValue;

	@JsonIgnore
	private Supplier<ContentFieldValue> _contentFieldValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized field's values."
	)
	@Valid
	public Map<String, ContentFieldValue> getContentFieldValue_i18n() {
		if (_contentFieldValue_i18nSupplier != null) {
			contentFieldValue_i18n = _contentFieldValue_i18nSupplier.get();

			_contentFieldValue_i18nSupplier = null;
		}

		return contentFieldValue_i18n;
	}

	public void setContentFieldValue_i18n(
		Map<String, ContentFieldValue> contentFieldValue_i18n) {

		this.contentFieldValue_i18n = contentFieldValue_i18n;

		_contentFieldValue_i18nSupplier = null;
	}

	@JsonIgnore
	public void setContentFieldValue_i18n(
		UnsafeSupplier<Map<String, ContentFieldValue>, Exception>
			contentFieldValue_i18nUnsafeSupplier) {

		_contentFieldValue_i18nSupplier = () -> {
			try {
				return contentFieldValue_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized field's values.")
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Map<String, ContentFieldValue> contentFieldValue_i18n;

	@JsonIgnore
	private Supplier<Map<String, ContentFieldValue>>
		_contentFieldValue_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field type (e.g., image, text, etc.)."
	)
	public String getDataType() {
		if (_dataTypeSupplier != null) {
			dataType = _dataTypeSupplier.get();

			_dataTypeSupplier = null;
		}

		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;

		_dataTypeSupplier = null;
	}

	@JsonIgnore
	public void setDataType(
		UnsafeSupplier<String, Exception> dataTypeUnsafeSupplier) {

		_dataTypeSupplier = () -> {
			try {
				return dataTypeUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The field type (e.g., image, text, etc.).")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String dataType;

	@JsonIgnore
	private Supplier<String> _dataTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's control type (e.g., text, text area, etc.)."
	)
	public String getInputControl() {
		if (_inputControlSupplier != null) {
			inputControl = _inputControlSupplier.get();

			_inputControlSupplier = null;
		}

		return inputControl;
	}

	public void setInputControl(String inputControl) {
		this.inputControl = inputControl;

		_inputControlSupplier = null;
	}

	@JsonIgnore
	public void setInputControl(
		UnsafeSupplier<String, Exception> inputControlUnsafeSupplier) {

		_inputControlSupplier = () -> {
			try {
				return inputControlUnsafeSupplier.get();
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
		description = "The field's control type (e.g., text, text area, etc.)."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String inputControl;

	@JsonIgnore
	private Supplier<String> _inputControlSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's label."
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

	@GraphQLField(description = "The field's label.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected String label;

	@JsonIgnore
	private Supplier<String> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The localized field's labels."
	)
	@Valid
	public Map<String, String> getLabel_i18n() {
		if (_label_i18nSupplier != null) {
			label_i18n = _label_i18nSupplier.get();

			_label_i18nSupplier = null;
		}

		return label_i18n;
	}

	public void setLabel_i18n(Map<String, String> label_i18n) {
		this.label_i18n = label_i18n;

		_label_i18nSupplier = null;
	}

	@JsonIgnore
	public void setLabel_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			label_i18nUnsafeSupplier) {

		_label_i18nSupplier = () -> {
			try {
				return label_i18nUnsafeSupplier.get();
			}
			catch (RuntimeException runtimeException) {
				throw runtimeException;
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	@GraphQLField(description = "The localized field's labels.")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Map<String, String> label_i18n;

	@JsonIgnore
	private Supplier<Map<String, String>> _label_i18nSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "The field's internal name. This is valid for comparisons and unique in the structured content."
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

	@GraphQLField(
		description = "The field's internal name. This is valid for comparisons and unique in the structured content."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected String name;

	@JsonIgnore
	private Supplier<String> _nameSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of child content fields that depend on this resource."
	)
	@Valid
	public ContentField[] getNestedContentFields() {
		if (_nestedContentFieldsSupplier != null) {
			nestedContentFields = _nestedContentFieldsSupplier.get();

			_nestedContentFieldsSupplier = null;
		}

		return nestedContentFields;
	}

	public void setNestedContentFields(ContentField[] nestedContentFields) {
		this.nestedContentFields = nestedContentFields;

		_nestedContentFieldsSupplier = null;
	}

	@JsonIgnore
	public void setNestedContentFields(
		UnsafeSupplier<ContentField[], Exception>
			nestedContentFieldsUnsafeSupplier) {

		_nestedContentFieldsSupplier = () -> {
			try {
				return nestedContentFieldsUnsafeSupplier.get();
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
		description = "A list of child content fields that depend on this resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected ContentField[] nestedContentFields;

	@JsonIgnore
	private Supplier<ContentField[]> _nestedContentFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A flag that indicates whether this field can be rendered multiple times."
	)
	public Boolean getRepeatable() {
		if (_repeatableSupplier != null) {
			repeatable = _repeatableSupplier.get();

			_repeatableSupplier = null;
		}

		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;

		_repeatableSupplier = null;
	}

	@JsonIgnore
	public void setRepeatable(
		UnsafeSupplier<Boolean, Exception> repeatableUnsafeSupplier) {

		_repeatableSupplier = () -> {
			try {
				return repeatableUnsafeSupplier.get();
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
		description = "A flag that indicates whether this field can be rendered multiple times."
	)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	protected Boolean repeatable;

	@JsonIgnore
	private Supplier<Boolean> _repeatableSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof ContentField)) {
			return false;
		}

		ContentField contentField = (ContentField)object;

		return Objects.equals(toString(), contentField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		ContentFieldValue contentFieldValue = getContentFieldValue();

		if (contentFieldValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFieldValue\": ");

			sb.append(String.valueOf(contentFieldValue));
		}

		Map<String, ContentFieldValue> contentFieldValue_i18n =
			getContentFieldValue_i18n();

		if (contentFieldValue_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"contentFieldValue_i18n\": ");

			sb.append(_toJSON(contentFieldValue_i18n));
		}

		String dataType = getDataType();

		if (dataType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"dataType\": ");

			sb.append("\"");

			sb.append(_escape(dataType));

			sb.append("\"");
		}

		String inputControl = getInputControl();

		if (inputControl != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"inputControl\": ");

			sb.append("\"");

			sb.append(_escape(inputControl));

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

		Map<String, String> label_i18n = getLabel_i18n();

		if (label_i18n != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label_i18n\": ");

			sb.append(_toJSON(label_i18n));
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

		ContentField[] nestedContentFields = getNestedContentFields();

		if (nestedContentFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedContentFields\": ");

			sb.append("[");

			for (int i = 0; i < nestedContentFields.length; i++) {
				sb.append(String.valueOf(nestedContentFields[i]));

				if ((i + 1) < nestedContentFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean repeatable = getRepeatable();

		if (repeatable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(repeatable);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.headless.delivery.dto.v1_0.ContentField",
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