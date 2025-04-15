/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.client.dto.v1_0;

import com.liferay.search.experiences.rest.client.function.UnsafeSupplier;
import com.liferay.search.experiences.rest.client.serdes.v1_0.FieldSerDes;

import jakarta.annotation.Generated;

import java.io.Serializable;

import java.util.Objects;

/**
 * @author Brian Wing Shun Chan
 * @generated
 */
@Generated("")
public class Field implements Cloneable, Serializable {

	public static Field toDTO(String json) {
		return FieldSerDes.toDTO(json);
	}

	public Object getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(Object defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setDefaultValue(
		UnsafeSupplier<Object, Exception> defaultValueUnsafeSupplier) {

		try {
			defaultValue = defaultValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Object defaultValue;

	public FieldMapping[] getFieldMappings() {
		return fieldMappings;
	}

	public void setFieldMappings(FieldMapping[] fieldMappings) {
		this.fieldMappings = fieldMappings;
	}

	public void setFieldMappings(
		UnsafeSupplier<FieldMapping[], Exception> fieldMappingsUnsafeSupplier) {

		try {
			fieldMappings = fieldMappingsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected FieldMapping[] fieldMappings;

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public void setHelpText(
		UnsafeSupplier<String, Exception> helpTextUnsafeSupplier) {

		try {
			helpText = helpTextUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String helpText;

	public String getHelpTextLocalized() {
		return helpTextLocalized;
	}

	public void setHelpTextLocalized(String helpTextLocalized) {
		this.helpTextLocalized = helpTextLocalized;
	}

	public void setHelpTextLocalized(
		UnsafeSupplier<String, Exception> helpTextLocalizedUnsafeSupplier) {

		try {
			helpTextLocalized = helpTextLocalizedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String helpTextLocalized;

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabel(
		UnsafeSupplier<String, Exception> labelUnsafeSupplier) {

		try {
			label = labelUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String label;

	public String getLabelLocalized() {
		return labelLocalized;
	}

	public void setLabelLocalized(String labelLocalized) {
		this.labelLocalized = labelLocalized;
	}

	public void setLabelLocalized(
		UnsafeSupplier<String, Exception> labelLocalizedUnsafeSupplier) {

		try {
			labelLocalized = labelLocalizedUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String labelLocalized;

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setType(UnsafeSupplier<String, Exception> typeUnsafeSupplier) {
		try {
			type = typeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String type;

	public TypeOptions getTypeOptions() {
		return typeOptions;
	}

	public void setTypeOptions(TypeOptions typeOptions) {
		this.typeOptions = typeOptions;
	}

	public void setTypeOptions(
		UnsafeSupplier<TypeOptions, Exception> typeOptionsUnsafeSupplier) {

		try {
			typeOptions = typeOptionsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected TypeOptions typeOptions;

	@Override
	public Field clone() throws CloneNotSupportedException {
		return (Field)super.clone();
	}

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
		return FieldSerDes.toJSON(this);
	}

}