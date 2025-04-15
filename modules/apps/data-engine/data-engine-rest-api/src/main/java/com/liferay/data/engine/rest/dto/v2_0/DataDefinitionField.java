/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.dto.v2_0;

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
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
@GraphQLName(
	description = "Represents the value of each field in data definition.",
	value = "DataDefinitionField"
)
@JsonFilter("Liferay.Vulcan")
@XmlRootElement(name = "DataDefinitionField")
public class DataDefinitionField implements Serializable {

	public static DataDefinitionField toDTO(String json) {
		return ObjectMapperUtil.readValue(DataDefinitionField.class, json);
	}

	public static DataDefinitionField unsafeToDTO(String json) {
		return ObjectMapperUtil.unsafeReadValue(
			DataDefinitionField.class, json);
	}

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getCustomProperties() {
		if (_customPropertiesSupplier != null) {
			customProperties = _customPropertiesSupplier.get();

			_customPropertiesSupplier = null;
		}

		return customProperties;
	}

	public void setCustomProperties(Map<String, Object> customProperties) {
		this.customProperties = customProperties;

		_customPropertiesSupplier = null;
	}

	@JsonIgnore
	public void setCustomProperties(
		UnsafeSupplier<Map<String, Object>, Exception>
			customPropertiesUnsafeSupplier) {

		_customPropertiesSupplier = () -> {
			try {
				return customPropertiesUnsafeSupplier.get();
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
	protected Map<String, Object> customProperties;

	@JsonIgnore
	private Supplier<Map<String, Object>> _customPropertiesSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getDefaultValue() {
		if (_defaultValueSupplier != null) {
			defaultValue = _defaultValueSupplier.get();

			_defaultValueSupplier = null;
		}

		return defaultValue;
	}

	public void setDefaultValue(Map<String, Object> defaultValue) {
		this.defaultValue = defaultValue;

		_defaultValueSupplier = null;
	}

	@JsonIgnore
	public void setDefaultValue(
		UnsafeSupplier<Map<String, Object>, Exception>
			defaultValueUnsafeSupplier) {

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
	protected Map<String, Object> defaultValue;

	@JsonIgnore
	private Supplier<Map<String, Object>> _defaultValueSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public String getFieldType() {
		if (_fieldTypeSupplier != null) {
			fieldType = _fieldTypeSupplier.get();

			_fieldTypeSupplier = null;
		}

		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;

		_fieldTypeSupplier = null;
	}

	@JsonIgnore
	public void setFieldType(
		UnsafeSupplier<String, Exception> fieldTypeUnsafeSupplier) {

		_fieldTypeSupplier = () -> {
			try {
				return fieldTypeUnsafeSupplier.get();
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
	protected String fieldType;

	@JsonIgnore
	private Supplier<String> _fieldTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Long getId() {
		if (_idSupplier != null) {
			id = _idSupplier.get();

			_idSupplier = null;
		}

		return id;
	}

	public void setId(Long id) {
		this.id = id;

		_idSupplier = null;
	}

	@JsonIgnore
	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Long id;

	@JsonIgnore
	private Supplier<Long> _idSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@JsonGetter("indexType")
	@Valid
	public IndexType getIndexType() {
		if (_indexTypeSupplier != null) {
			indexType = _indexTypeSupplier.get();

			_indexTypeSupplier = null;
		}

		return indexType;
	}

	@JsonIgnore
	public String getIndexTypeAsString() {
		IndexType indexType = getIndexType();

		if (indexType == null) {
			return null;
		}

		return indexType.toString();
	}

	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;

		_indexTypeSupplier = null;
	}

	@JsonIgnore
	public void setIndexType(
		UnsafeSupplier<IndexType, Exception> indexTypeUnsafeSupplier) {

		_indexTypeSupplier = () -> {
			try {
				return indexTypeUnsafeSupplier.get();
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
	protected IndexType indexType;

	@JsonIgnore
	private Supplier<IndexType> _indexTypeSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getIndexable() {
		if (_indexableSupplier != null) {
			indexable = _indexableSupplier.get();

			_indexableSupplier = null;
		}

		return indexable;
	}

	public void setIndexable(Boolean indexable) {
		this.indexable = indexable;

		_indexableSupplier = null;
	}

	@JsonIgnore
	public void setIndexable(
		UnsafeSupplier<Boolean, Exception> indexableUnsafeSupplier) {

		_indexableSupplier = () -> {
			try {
				return indexableUnsafeSupplier.get();
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
	protected Boolean indexable;

	@JsonIgnore
	private Supplier<Boolean> _indexableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getLabel() {
		if (_labelSupplier != null) {
			label = _labelSupplier.get();

			_labelSupplier = null;
		}

		return label;
	}

	public void setLabel(Map<String, Object> label) {
		this.label = label;

		_labelSupplier = null;
	}

	@JsonIgnore
	public void setLabel(
		UnsafeSupplier<Map<String, Object>, Exception> labelUnsafeSupplier) {

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
	protected Map<String, Object> label;

	@JsonIgnore
	private Supplier<Map<String, Object>> _labelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getLocalizable() {
		if (_localizableSupplier != null) {
			localizable = _localizableSupplier.get();

			_localizableSupplier = null;
		}

		return localizable;
	}

	public void setLocalizable(Boolean localizable) {
		this.localizable = localizable;

		_localizableSupplier = null;
	}

	@JsonIgnore
	public void setLocalizable(
		UnsafeSupplier<Boolean, Exception> localizableUnsafeSupplier) {

		_localizableSupplier = () -> {
			try {
				return localizableUnsafeSupplier.get();
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
	protected Boolean localizable;

	@JsonIgnore
	private Supplier<Boolean> _localizableSupplier;

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

	@io.swagger.v3.oas.annotations.media.Schema(
		description = "A list of child data definition fields that depend on this resource."
	)
	@Valid
	public DataDefinitionField[] getNestedDataDefinitionFields() {
		if (_nestedDataDefinitionFieldsSupplier != null) {
			nestedDataDefinitionFields =
				_nestedDataDefinitionFieldsSupplier.get();

			_nestedDataDefinitionFieldsSupplier = null;
		}

		return nestedDataDefinitionFields;
	}

	public void setNestedDataDefinitionFields(
		DataDefinitionField[] nestedDataDefinitionFields) {

		this.nestedDataDefinitionFields = nestedDataDefinitionFields;

		_nestedDataDefinitionFieldsSupplier = null;
	}

	@JsonIgnore
	public void setNestedDataDefinitionFields(
		UnsafeSupplier<DataDefinitionField[], Exception>
			nestedDataDefinitionFieldsUnsafeSupplier) {

		_nestedDataDefinitionFieldsSupplier = () -> {
			try {
				return nestedDataDefinitionFieldsUnsafeSupplier.get();
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
		description = "A list of child data definition fields that depend on this resource."
	)
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected DataDefinitionField[] nestedDataDefinitionFields;

	@JsonIgnore
	private Supplier<DataDefinitionField[]> _nestedDataDefinitionFieldsSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getReadOnly() {
		if (_readOnlySupplier != null) {
			readOnly = _readOnlySupplier.get();

			_readOnlySupplier = null;
		}

		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;

		_readOnlySupplier = null;
	}

	@JsonIgnore
	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		_readOnlySupplier = () -> {
			try {
				return readOnlyUnsafeSupplier.get();
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
	protected Boolean readOnly;

	@JsonIgnore
	private Supplier<Boolean> _readOnlySupplier;

	@io.swagger.v3.oas.annotations.media.Schema
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

	@GraphQLField
	@JsonProperty(access = JsonProperty.Access.READ_WRITE)
	protected Boolean repeatable;

	@JsonIgnore
	private Supplier<Boolean> _repeatableSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getRequired() {
		if (_requiredSupplier != null) {
			required = _requiredSupplier.get();

			_requiredSupplier = null;
		}

		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;

		_requiredSupplier = null;
	}

	@JsonIgnore
	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		_requiredSupplier = () -> {
			try {
				return requiredUnsafeSupplier.get();
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
	protected Boolean required;

	@JsonIgnore
	private Supplier<Boolean> _requiredSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getShowLabel() {
		if (_showLabelSupplier != null) {
			showLabel = _showLabelSupplier.get();

			_showLabelSupplier = null;
		}

		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;

		_showLabelSupplier = null;
	}

	@JsonIgnore
	public void setShowLabel(
		UnsafeSupplier<Boolean, Exception> showLabelUnsafeSupplier) {

		_showLabelSupplier = () -> {
			try {
				return showLabelUnsafeSupplier.get();
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
	protected Boolean showLabel;

	@JsonIgnore
	private Supplier<Boolean> _showLabelSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	@Valid
	public Map<String, Object> getTip() {
		if (_tipSupplier != null) {
			tip = _tipSupplier.get();

			_tipSupplier = null;
		}

		return tip;
	}

	public void setTip(Map<String, Object> tip) {
		this.tip = tip;

		_tipSupplier = null;
	}

	@JsonIgnore
	public void setTip(
		UnsafeSupplier<Map<String, Object>, Exception> tipUnsafeSupplier) {

		_tipSupplier = () -> {
			try {
				return tipUnsafeSupplier.get();
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
	protected Map<String, Object> tip;

	@JsonIgnore
	private Supplier<Map<String, Object>> _tipSupplier;

	@io.swagger.v3.oas.annotations.media.Schema
	public Boolean getVisible() {
		if (_visibleSupplier != null) {
			visible = _visibleSupplier.get();

			_visibleSupplier = null;
		}

		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;

		_visibleSupplier = null;
	}

	@JsonIgnore
	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		_visibleSupplier = () -> {
			try {
				return visibleUnsafeSupplier.get();
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
	protected Boolean visible;

	@JsonIgnore
	private Supplier<Boolean> _visibleSupplier;

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DataDefinitionField)) {
			return false;
		}

		DataDefinitionField dataDefinitionField = (DataDefinitionField)object;

		return Objects.equals(toString(), dataDefinitionField.toString());
	}

	@Override
	public int hashCode() {
		String string = toString();

		return string.hashCode();
	}

	public String toString() {
		StringBundler sb = new StringBundler();

		sb.append("{");

		Map<String, Object> customProperties = getCustomProperties();

		if (customProperties != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"customProperties\": ");

			sb.append(_toJSON(customProperties));
		}

		Map<String, Object> defaultValue = getDefaultValue();

		if (defaultValue != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"defaultValue\": ");

			sb.append(_toJSON(defaultValue));
		}

		String fieldType = getFieldType();

		if (fieldType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"fieldType\": ");

			sb.append("\"");

			sb.append(_escape(fieldType));

			sb.append("\"");
		}

		Long id = getId();

		if (id != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"id\": ");

			sb.append(id);
		}

		IndexType indexType = getIndexType();

		if (indexType != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexType\": ");

			sb.append("\"");

			sb.append(indexType);

			sb.append("\"");
		}

		Boolean indexable = getIndexable();

		if (indexable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"indexable\": ");

			sb.append(indexable);
		}

		Map<String, Object> label = getLabel();

		if (label != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"label\": ");

			sb.append(_toJSON(label));
		}

		Boolean localizable = getLocalizable();

		if (localizable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"localizable\": ");

			sb.append(localizable);
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

		DataDefinitionField[] nestedDataDefinitionFields =
			getNestedDataDefinitionFields();

		if (nestedDataDefinitionFields != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"nestedDataDefinitionFields\": ");

			sb.append("[");

			for (int i = 0; i < nestedDataDefinitionFields.length; i++) {
				sb.append(String.valueOf(nestedDataDefinitionFields[i]));

				if ((i + 1) < nestedDataDefinitionFields.length) {
					sb.append(", ");
				}
			}

			sb.append("]");
		}

		Boolean readOnly = getReadOnly();

		if (readOnly != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"readOnly\": ");

			sb.append(readOnly);
		}

		Boolean repeatable = getRepeatable();

		if (repeatable != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"repeatable\": ");

			sb.append(repeatable);
		}

		Boolean required = getRequired();

		if (required != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"required\": ");

			sb.append(required);
		}

		Boolean showLabel = getShowLabel();

		if (showLabel != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"showLabel\": ");

			sb.append(showLabel);
		}

		Map<String, Object> tip = getTip();

		if (tip != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"tip\": ");

			sb.append(_toJSON(tip));
		}

		Boolean visible = getVisible();

		if (visible != null) {
			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append("\"visible\": ");

			sb.append(visible);
		}

		sb.append("}");

		return sb.toString();
	}

	@io.swagger.v3.oas.annotations.media.Schema(
		accessMode = io.swagger.v3.oas.annotations.media.Schema.AccessMode.READ_ONLY,
		defaultValue = "com.liferay.data.engine.rest.dto.v2_0.DataDefinitionField",
		name = "x-class-name"
	)
	public String xClassName;

	@GraphQLName("IndexType")
	public static enum IndexType {

		ALL("all"), KEYWORD("keyword"), NONE("none"), TEXT("text");

		@JsonCreator
		public static IndexType create(String value) {
			if ((value == null) || value.equals("")) {
				return null;
			}

			for (IndexType indexType : values()) {
				if (Objects.equals(indexType.getValue(), value)) {
					return indexType;
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

		private IndexType(String value) {
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