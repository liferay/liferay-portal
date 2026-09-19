/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.kernel;

import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Pablo Carvalho
 * @author Marcellus Tavares
 */
public class DDMFormField implements Serializable {

	public DDMFormField(String name, String type) {
		setName(name);
		setType(type);

		setDDMFormFieldOptions(new DDMFormFieldOptions());
		setLabel(new LocalizedValue());
		setPredefinedValue(new LocalizedValue());
		setStyle(new LocalizedValue());
		setTip(new LocalizedValue());
	}

	public void addNestedDDMFormField(DDMFormField nestedDDMFormField) {
		nestedDDMFormField.setDDMForm(_ddmForm);

		_nestedDDMFormFields.add(nestedDDMFormField);
	}

	public DDMForm getDDMForm() {
		return _ddmForm;
	}

	public DDMFormFieldOptions getDDMFormFieldOptions() {
		return (DDMFormFieldOptions)_properties.get("options");
	}

	public String getDataType() {
		return MapUtil.getString(_properties, "dataType");
	}

	public String getFieldNamespace() {
		return MapUtil.getString(_properties, "fieldNamespace");
	}

	public String getFieldReference() {
		return MapUtil.getString(_properties, "fieldReference");
	}

	public String getIndexType() {
		return MapUtil.getString(_properties, "indexType");
	}

	public LocalizedValue getLabel() {
		return (LocalizedValue)_properties.get("label");
	}

	public String getName() {
		return MapUtil.getString(_properties, "name");
	}

	public List<DDMFormField> getNestedDDMFormFields() {
		return _nestedDDMFormFields;
	}

	public Map<String, DDMFormField> getNestedDDMFormFieldsMap() {
		Map<String, DDMFormField> nestedDDMFormFieldsMap =
			new LinkedHashMap<>();

		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			nestedDDMFormFieldsMap.put(
				nestedDDMFormField.getName(), nestedDDMFormField);

			nestedDDMFormFieldsMap.putAll(
				nestedDDMFormField.getNestedDDMFormFieldsMap());
		}

		return nestedDDMFormFieldsMap;
	}

	public LocalizedValue getPredefinedValue() {
		return (LocalizedValue)_properties.get("predefinedValue");
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public Object getProperty(String name) {
		return _properties.get(name);
	}

	public LocalizedValue getStyle() {
		return (LocalizedValue)_properties.get("style");
	}

	public LocalizedValue getTip() {
		return (LocalizedValue)_properties.get("tip");
	}

	public String getType() {
		return MapUtil.getString(_properties, "type");
	}

	public boolean isLocalizable() {
		return MapUtil.getBoolean(_properties, "localizable");
	}

	public boolean isMultiple() {
		return MapUtil.getBoolean(_properties, "multiple");
	}

	public boolean isReadOnly() {
		return MapUtil.getBoolean(_properties, "readOnly");
	}

	public boolean isRepeatable() {
		return MapUtil.getBoolean(_properties, "repeatable");
	}

	public boolean isRequired() {
		return MapUtil.getBoolean(_properties, "required");
	}

	public boolean isShowLabel() {
		return MapUtil.getBoolean(_properties, "showLabel");
	}

	public boolean isTransient() {
		return Validator.isNull(getDataType());
	}

	public void setDDMForm(DDMForm ddmForm) {
		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			nestedDDMFormField.setDDMForm(ddmForm);
		}

		_ddmForm = ddmForm;
	}

	public void setDDMFormFieldOptions(
		DDMFormFieldOptions ddmFormFieldOptions) {

		_properties.put("options", ddmFormFieldOptions);
	}

	public void setDataType(String dataType) {
		_properties.put("dataType", dataType);
	}

	public void setFieldNamespace(String fieldNamespace) {
		_properties.put("fieldNamespace", fieldNamespace);
	}

	public void setFieldReference(String fieldReference) {
		_properties.put("fieldReference", fieldReference);
	}

	public void setIndexType(String indexType) {
		_properties.put("indexType", indexType);
	}

	public void setLabel(LocalizedValue label) {
		_properties.put("label", label);
	}

	public void setLocalizable(boolean localizable) {
		_properties.put("localizable", localizable);
	}

	public void setMultiple(boolean multiple) {
		_properties.put("multiple", multiple);
	}

	public void setName(String name) {
		_properties.put("name", name);
	}

	public void setNestedDDMFormFields(List<DDMFormField> nestedDDMFormFields) {
		_nestedDDMFormFields = nestedDDMFormFields;
	}

	public void setPredefinedValue(LocalizedValue predefinedValue) {
		_properties.put("predefinedValue", predefinedValue);
	}

	public void setProperty(String name, Object value) {
		_properties.put(name, value);
	}

	public void setReadOnly(boolean readOnly) {
		_properties.put("readOnly", readOnly);
	}

	public void setRepeatable(boolean repeatable) {
		_properties.put("repeatable", repeatable);
	}

	public void setRequired(boolean required) {
		_properties.put("required", required);
	}

	public void setShowLabel(boolean showLabel) {
		_properties.put("showLabel", showLabel);
	}

	public void setStyle(LocalizedValue style) {
		_properties.put("style", style);
	}

	public void setTip(LocalizedValue tip) {
		_properties.put("tip", tip);
	}

	public void setType(String type) {
		_properties.put("type", type);
	}

	private DDMForm _ddmForm;
	private List<DDMFormField> _nestedDDMFormFields = new ArrayList<>();
	private final Map<String, Object> _properties = new LinkedHashMap<>();

}