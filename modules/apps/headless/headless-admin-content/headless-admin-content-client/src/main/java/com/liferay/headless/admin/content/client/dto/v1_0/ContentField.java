/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.content.client.dto.v1_0;

import com.liferay.headless.admin.content.client.function.UnsafeSupplier;
import com.liferay.headless.admin.content.client.serdes.v1_0.ContentFieldSerDes;

import java.io.Serializable;

import java.util.Map;
import java.util.Objects;

import jakarta.annotation.Generated;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public class ContentField implements Cloneable, Serializable {

	public static ContentField toDTO(String json) {
		return ContentFieldSerDes.toDTO(json);
	}

	public ContentFieldValue getContentFieldValue() {
		return contentFieldValue;
	}

	public void setContentFieldValue(ContentFieldValue contentFieldValue) {
		this.contentFieldValue = contentFieldValue;
	}

	public void setContentFieldValue(
		UnsafeSupplier<ContentFieldValue, Exception>
			contentFieldValueUnsafeSupplier) {

		try {
			contentFieldValue = contentFieldValueUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContentFieldValue contentFieldValue;

	public Map<String, ContentFieldValue> getContentFieldValue_i18n() {
		return contentFieldValue_i18n;
	}

	public void setContentFieldValue_i18n(
		Map<String, ContentFieldValue> contentFieldValue_i18n) {

		this.contentFieldValue_i18n = contentFieldValue_i18n;
	}

	public void setContentFieldValue_i18n(
		UnsafeSupplier<Map<String, ContentFieldValue>, Exception>
			contentFieldValue_i18nUnsafeSupplier) {

		try {
			contentFieldValue_i18n = contentFieldValue_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, ContentFieldValue> contentFieldValue_i18n;

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public void setDataType(
		UnsafeSupplier<String, Exception> dataTypeUnsafeSupplier) {

		try {
			dataType = dataTypeUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String dataType;

	public String getInputControl() {
		return inputControl;
	}

	public void setInputControl(String inputControl) {
		this.inputControl = inputControl;
	}

	public void setInputControl(
		UnsafeSupplier<String, Exception> inputControlUnsafeSupplier) {

		try {
			inputControl = inputControlUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String inputControl;

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

	public Map<String, String> getLabel_i18n() {
		return label_i18n;
	}

	public void setLabel_i18n(Map<String, String> label_i18n) {
		this.label_i18n = label_i18n;
	}

	public void setLabel_i18n(
		UnsafeSupplier<Map<String, String>, Exception>
			label_i18nUnsafeSupplier) {

		try {
			label_i18n = label_i18nUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Map<String, String> label_i18n;

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

	public ContentField[] getNestedContentFields() {
		return nestedContentFields;
	}

	public void setNestedContentFields(ContentField[] nestedContentFields) {
		this.nestedContentFields = nestedContentFields;
	}

	public void setNestedContentFields(
		UnsafeSupplier<ContentField[], Exception>
			nestedContentFieldsUnsafeSupplier) {

		try {
			nestedContentFields = nestedContentFieldsUnsafeSupplier.get();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected ContentField[] nestedContentFields;

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

	@Override
	public ContentField clone() throws CloneNotSupportedException {
		return (ContentField)super.clone();
	}

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
		return ContentFieldSerDes.toJSON(this);
	}

}