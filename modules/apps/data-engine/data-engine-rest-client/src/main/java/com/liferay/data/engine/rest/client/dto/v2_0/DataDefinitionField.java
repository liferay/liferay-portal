/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.client.dto.v2_0;

import com.liferay.data.engine.rest.client.function.UnsafeSupplier;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataDefinitionFieldSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public class DataDefinitionField implements Cloneable, Serializable {

	public static DataDefinitionField toDTO(String json) {
		return DataDefinitionFieldSerDes.toDTO(json);
	}

	public Map<String, Object> getCustomProperties() {
		return customProperties;
	}

	public void setCustomProperties(Map<String, Object> customProperties) {
		this.customProperties = customProperties;
	}

	public void setCustomProperties(
		UnsafeSupplier<Map<String, Object>, Exception>
			customPropertiesUnsafeSupplier) {

		try {
			customProperties = customPropertiesUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> customProperties;

	public Map<String, Object> getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Map<String, Object> defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier<Map<String, Object>, Exception>
			defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> defaultValue;

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setFieldType(
		UnsafeSupplier<String, Exception> fieldTypeUnsafeSupplier) {

		try {
			fieldType = fieldTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String fieldType;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setId(UnsafeSupplier<Long, Exception> idUnsafeSupplier) {
		try {
			id = idUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Long id;

	public IndexType getIndexType() {
		return indexType;
	}

	public String getIndexTypeAsString() {
		if (indexType == null) {
			return null;
		}

		return indexType.toString();
	}

	public void setIndexType(IndexType indexType) {
		this.indexType = indexType;
	}

	public void setIndexType(
		UnsafeSupplier<IndexType, Exception> indexTypeUnsafeSupplier) {

		try {
			indexType = indexTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected IndexType indexType;

	public Boolean getIndexable() {
		return indexable;
	}

	public void setIndexable(Boolean indexable) {
		this.indexable = indexable;
	}

	public void setIndexable(
		UnsafeSupplier<Boolean, Exception> indexableUnsafeSupplier) {

		try {
			indexable = indexableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean indexable;

	public Map<String, Object> getLabel() {
		return label;
	}

	public void setLabel(Map<String, Object> label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<Map<String, Object>, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> label;

	public Boolean getLocalizable() {
		return localizable;
	}

	public void setLocalizable(Boolean localizable) {
		this.localizable = localizable;
	}

	public void setLocalizable(
		UnsafeSupplier<Boolean, Exception> localizableUnsafeSupplier) {

		try {
			localizable = localizableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean localizable;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setName(UnsafeSupplier<String, Exception> nameUnsafeSupplier) {
		try {
			name = nameUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String name;

	public DataDefinitionField[] getNestedDataDefinitionFields() {
		return nestedDataDefinitionFields;
	}

	public void setNestedDataDefinitionFields(
		DataDefinitionField[] nestedDataDefinitionFields) {

		this.nestedDataDefinitionFields = nestedDataDefinitionFields;
	}

	public void setNestedDataDefinitionFields(
		UnsafeSupplier<DataDefinitionField[], Exception>
			nestedDataDefinitionFieldsUnsafeSupplier) {

		try {
			nestedDataDefinitionFields =
				nestedDataDefinitionFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected DataDefinitionField[] nestedDataDefinitionFields;

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public void setReadOnly(
		UnsafeSupplier<Boolean, Exception> readOnlyUnsafeSupplier) {

		try {
			readOnly = readOnlyUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean readOnly;

	public Boolean getRepeatable() {
		return repeatable;
	}

	public void setRepeatable(Boolean repeatable) {
		this.repeatable = repeatable;
	}

	public void setRepeatable(
		UnsafeSupplier<Boolean, Exception> repeatableUnsafeSupplier) {

		try {
			repeatable = repeatableUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean repeatable;

	public Boolean getRequired() {
		return required;
	}

	public void setRequired(Boolean required) {
		this.required = required;
	}

	public void setRequired(
		UnsafeSupplier<Boolean, Exception> requiredUnsafeSupplier) {

		try {
			required = requiredUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean required;

	public Boolean getShowLabel() {
		return showLabel;
	}

	public void setShowLabel(Boolean showLabel) {
		this.showLabel = showLabel;
	}

	public void setShowLabel(
		UnsafeSupplier<Boolean, Exception> showLabelUnsafeSupplier) {

		try {
			showLabel = showLabelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean showLabel;

	public Map<String, Object> getTip() {
		return tip;
	}

	public void setTip(Map<String, Object> tip) {
		this.tip = tip;
	}

	public void setTip(
		UnsafeSupplier<Map<String, Object>, Exception> tipUnsafeSupplier) {

		try {
			tip = tipUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, Object> tip;

	public Boolean getVisible() {
		return visible;
	}

	public void setVisible(Boolean visible) {
		this.visible = visible;
	}

	public void setVisible(
		UnsafeSupplier<Boolean, Exception> visibleUnsafeSupplier) {

		try {
			visible = visibleUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Boolean visible;

	@Override
	public DataDefinitionField clone() throws CloneNotSupportedException {
		return (DataDefinitionField)super.clone();
	}

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
		return DataDefinitionFieldSerDes.toJSON(this);
	}

	public static enum IndexType {

		ALL("all"), KEYWORD("keyword"), NONE("none"), TEXT("text");

		public static IndexType create(String value) {
			for (IndexType indexType : values()) {
				if (Objects.equals(indexType.getValue(), value) ||
					Objects.equals(indexType.name(), value)) {

					return indexType;
				}
			}

			return null;
		}

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

}