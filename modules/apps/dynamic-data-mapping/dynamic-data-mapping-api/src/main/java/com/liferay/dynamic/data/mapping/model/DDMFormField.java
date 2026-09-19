/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.model;

import com.liferay.petra.lang.HashUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Validator;

import java.io.Serializable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

/**
 * @author Pablo Carvalho
 * @author Marcellus Tavares
 */
public class DDMFormField implements Serializable {

	public DDMFormField() {
		_ddmFormFieldRules = new ArrayList<>();
		_nestedDDMFormFields = new ArrayList<>();
		_properties = new LinkedHashMap<>();
	}

	public DDMFormField(DDMFormField ddmFormField) {
		_properties = new LinkedHashMap<>(ddmFormField._properties);

		_dataType = ddmFormField._dataType;
		_fieldNamespace = ddmFormField._fieldNamespace;
		_fieldReference = ddmFormField._fieldReference;
		_indexType = ddmFormField._indexType;
		_localizable = ddmFormField._localizable;
		_multiple = ddmFormField._multiple;
		_name = ddmFormField._name;
		_readOnly = ddmFormField._readOnly;
		_repeatable = ddmFormField._repeatable;
		_required = ddmFormField._required;
		_showLabel = ddmFormField._showLabel;
		_type = ddmFormField._type;
		_visibilityExpression = ddmFormField._visibilityExpression;
		_visualProperty = ddmFormField._visualProperty;

		DDMFormFieldOptions ddmFormFieldOptions =
			ddmFormField.getDDMFormFieldOptions();

		if (ddmFormFieldOptions != null) {
			setDDMFormFieldOptions(
				new DDMFormFieldOptions(ddmFormFieldOptions));
		}

		_ddmFormFieldRules = new ArrayList<>(
			ddmFormField._ddmFormFieldRules.size());

		for (DDMFormFieldRule ddmFormFieldRule :
				ddmFormField._ddmFormFieldRules) {

			addDDMFormFieldRule(new DDMFormFieldRule(ddmFormFieldRule));
		}

		DDMFormFieldValidation ddmFormFieldValidation =
			ddmFormField.getDDMFormFieldValidation();

		if (ddmFormFieldValidation != null) {
			setDDMFormFieldValidation(
				new DDMFormFieldValidation(ddmFormFieldValidation));
		}

		LocalizedValue label = ddmFormField.getLabel();

		if (label != null) {
			setLabel(new LocalizedValue(label));
		}

		LocalizedValue predefinedValue = ddmFormField.getPredefinedValue();

		if (predefinedValue != null) {
			setPredefinedValue(new LocalizedValue(predefinedValue));
		}

		LocalizedValue requiredErrorMessage =
			ddmFormField.getRequiredErrorMessage();

		if (requiredErrorMessage != null) {
			setRequiredErrorMessage(new LocalizedValue(requiredErrorMessage));
		}

		LocalizedValue style = ddmFormField.getStyle();

		if (style != null) {
			setStyle(new LocalizedValue(style));
		}

		LocalizedValue tip = ddmFormField.getTip();

		if (tip != null) {
			setTip(new LocalizedValue(tip));
		}

		_nestedDDMFormFields = new ArrayList<>(
			ddmFormField._nestedDDMFormFields.size());

		for (DDMFormField nestedDDMFormField :
				ddmFormField._nestedDDMFormFields) {

			addNestedDDMFormField(new DDMFormField(nestedDDMFormField));
		}
	}

	public DDMFormField(String name, String type) {
		_ddmFormFieldRules = new ArrayList<>();
		_nestedDDMFormFields = new ArrayList<>();
		_properties = new LinkedHashMap<>();

		setName(name);
		setType(type);

		Locale locale = LocaleUtil.getDefault();

		setDDMFormFieldOptions(new DDMFormFieldOptions(locale));

		setFieldReference(name);

		setLabel(new LocalizedValue(locale));
		setPredefinedValue(new LocalizedValue(locale));
		setStyle(new LocalizedValue(locale));
		setTip(new LocalizedValue(locale));
	}

	/**
	 * @deprecated As of Judson (7.1.x), with no direct replacement
	 */
	@Deprecated
	public void addDDMFormFieldRule(DDMFormFieldRule ddmFormFieldRule) {
		_ddmFormFieldRules.add(ddmFormFieldRule);
	}

	public void addNestedDDMFormField(DDMFormField nestedDDMFormField) {
		nestedDDMFormField.setDDMForm(_ddmForm);

		_nestedDDMFormFields.add(nestedDDMFormField);

		_nestedDDMFormFieldsMap.put(
			nestedDDMFormField.getName(), nestedDDMFormField);
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}

		if (!(object instanceof DDMFormField)) {
			return false;
		}

		DDMFormField ddmFormField = (DDMFormField)object;

		if (Objects.equals(_properties, ddmFormField._properties) &&
			Objects.equals(
				_nestedDDMFormFields, ddmFormField._nestedDDMFormFields)) {

			return true;
		}

		return false;
	}

	public DDMForm getDDMForm() {
		return _ddmForm;
	}

	public DDMFormFieldOptions getDDMFormFieldOptions() {
		DDMFormFieldOptions ddmFormFieldOptions = _ddmFormFieldOptions;

		String dataSourceType = getDataSourceType();

		if ((ddmFormFieldOptions != null) &&
			Validator.isNotNull(dataSourceType) &&
			!dataSourceType.equals(_DATA_SOURCE_TYPE_MANUAL)) {

			Locale defaultLocale = ddmFormFieldOptions.getDefaultLocale();

			ddmFormFieldOptions = new DDMFormFieldOptions();

			ddmFormFieldOptions.setDefaultLocale(defaultLocale);
		}

		return ddmFormFieldOptions;
	}

	public DDMFormFieldValidation getDDMFormFieldValidation() {
		return _ddmFormFieldValidation;
	}

	public DDMFormLayout getDDMFormLayout() {
		return _ddmFormLayout;
	}

	public String getDataSourceType() {
		Object propertyDataSourceType = _properties.get("dataSourceType");

		if (propertyDataSourceType == null) {
			return _DATA_SOURCE_TYPE_MANUAL;
		}

		String dataSourceType = StringPool.BLANK;

		if (propertyDataSourceType instanceof JSONArray) {
			JSONArray jsonArray = (JSONArray)propertyDataSourceType;

			return GetterUtil.getString(
				jsonArray.get(0), _DATA_SOURCE_TYPE_MANUAL);
		}
		else if (propertyDataSourceType instanceof String) {
			dataSourceType = (String)propertyDataSourceType;

			if (dataSourceType.startsWith(StringPool.OPEN_BRACKET) &&
				dataSourceType.endsWith(StringPool.CLOSE_BRACKET)) {

				try {
					JSONArray jsonArray = JSONFactoryUtil.createJSONArray(
						dataSourceType);

					return GetterUtil.getString(
						jsonArray.get(0), _DATA_SOURCE_TYPE_MANUAL);
				}
				catch (JSONException jsonException) {
					if (_log.isDebugEnabled()) {
						_log.debug(jsonException);
					}

					return dataSourceType;
				}
			}
		}

		return dataSourceType;
	}

	public String getDataType() {
		return _dataType;
	}

	public String getFieldNamespace() {
		return _fieldNamespace;
	}

	public String getFieldReference() {
		return _fieldReference;
	}

	public String getIndexType() {
		return _indexType;
	}

	public LocalizedValue getLabel() {
		return _label;
	}

	public String getName() {
		return _name;
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

	public DDMFormField getNestedDDMFormFieldsMap(String name) {
		DDMFormField nestedDDMFormField = _nestedDDMFormFieldsMap.get(name);

		if (nestedDDMFormField != null) {
			return nestedDDMFormField;
		}

		for (DDMFormField curNestedDDMFormField : _nestedDDMFormFields) {
			DDMFormField nestedNestedDDMFormField =
				curNestedDDMFormField.getNestedDDMFormFieldsMap(name);

			if (nestedNestedDDMFormField != null) {
				return nestedNestedDDMFormField;
			}
		}

		return null;
	}

	public Map<String, DDMFormField> getNestedDDMFormFieldsReferencesMap() {
		Map<String, DDMFormField> nestedDDMFormFieldsReferencesMap =
			new LinkedHashMap<>();

		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			nestedDDMFormFieldsReferencesMap.put(
				nestedDDMFormField.getFieldReference(), nestedDDMFormField);

			nestedDDMFormFieldsReferencesMap.putAll(
				nestedDDMFormField.getNestedDDMFormFieldsReferencesMap());
		}

		return nestedDDMFormFieldsReferencesMap;
	}

	public Map<String, DDMFormField> getNontransientNestedDDMFormFieldsMap() {
		Map<String, DDMFormField> nestedDDMFormFieldsMap =
			new LinkedHashMap<>();

		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			if (!nestedDDMFormField.isTransient()) {
				nestedDDMFormFieldsMap.put(
					nestedDDMFormField.getName(), nestedDDMFormField);
			}

			nestedDDMFormFieldsMap.putAll(
				nestedDDMFormField.getNontransientNestedDDMFormFieldsMap());
		}

		return nestedDDMFormFieldsMap;
	}

	public Map<String, DDMFormField>
		getNontransientNestedDDMFormFieldsReferencesMap() {

		Map<String, DDMFormField> nestedDDMFormFieldsReferencesMap =
			new LinkedHashMap<>();

		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			if (!nestedDDMFormField.isTransient()) {
				nestedDDMFormFieldsReferencesMap.put(
					nestedDDMFormField.getFieldReference(), nestedDDMFormField);
			}

			nestedDDMFormFieldsReferencesMap.putAll(
				nestedDDMFormField.
					getNontransientNestedDDMFormFieldsReferencesMap());
		}

		return nestedDDMFormFieldsReferencesMap;
	}

	public LocalizedValue getPredefinedValue() {
		return _predefinedValue;
	}

	public Map<String, Object> getProperties() {
		return _properties;
	}

	public Object getProperty(String name) {
		return _properties.get(name);
	}

	public LocalizedValue getRequiredErrorMessage() {
		return _requiredErrorMessage;
	}

	public LocalizedValue getStyle() {
		return _style;
	}

	public LocalizedValue getTip() {
		return _tip;
	}

	public String getType() {
		return _type;
	}

	public String getVisibilityExpression() {
		return _visibilityExpression;
	}

	public boolean hasProperty(String propertyKey) {
		return _properties.containsKey(propertyKey);
	}

	@Override
	public int hashCode() {
		int hash = HashUtil.hash(0, _properties);

		return HashUtil.hash(hash, _nestedDDMFormFields);
	}

	public boolean isLocalizable() {
		return _localizable;
	}

	public boolean isMultiple() {
		return _multiple;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}

	public boolean isRepeatable() {
		return _repeatable;
	}

	public boolean isRequired() {
		return _required;
	}

	public boolean isShowLabel() {
		return _showLabel;
	}

	/**
	 * This method returns <code>true</code> if the DDMFormField is not supposed
	 * to hold value/data, i.e. its "dataType" property is blank or
	 * <code>null</code>. Transient fields can be considered structural fields
	 * like Liferay's native separator or fieldset fields.
	 *
	 * @return boolean
	 * @review
	 */
	public boolean isTransient() {
		return Validator.isNull(getDataType());
	}

	public boolean isVisualProperty() {
		return _visualProperty;
	}

	public void removeProperty(String propertyKey) {
		_properties.remove(propertyKey);
	}

	public void setDDMForm(DDMForm ddmForm) {
		for (DDMFormField nestedDDMFormField : _nestedDDMFormFields) {
			nestedDDMFormField.setDDMForm(ddmForm);

			_nestedDDMFormFieldsMap.put(
				nestedDDMFormField.getName(), nestedDDMFormField);
		}

		_ddmForm = ddmForm;
	}

	public void setDDMFormFieldOptions(
		DDMFormFieldOptions ddmFormFieldOptions) {

		_properties.put("options", ddmFormFieldOptions);

		_ddmFormFieldOptions = ddmFormFieldOptions;
	}

	public void setDDMFormFieldValidation(
		DDMFormFieldValidation ddmFormFieldValidation) {

		_properties.put("validation", ddmFormFieldValidation);

		_ddmFormFieldValidation = ddmFormFieldValidation;
	}

	public void setDDMFormLayout(DDMFormLayout ddmFormLayout) {
		_ddmFormLayout = ddmFormLayout;
	}

	public void setDataType(String dataType) {
		dataType = GetterUtil.getString(dataType);

		_properties.put("dataType", dataType);

		_dataType = dataType;
	}

	public void setFieldNamespace(String fieldNamespace) {
		fieldNamespace = GetterUtil.getString(fieldNamespace);

		_properties.put("fieldNamespace", fieldNamespace);

		_fieldNamespace = fieldNamespace;
	}

	public void setFieldReference(String fieldReference) {
		fieldReference = GetterUtil.getString(fieldReference);

		_properties.put("fieldReference", fieldReference);

		_fieldReference = fieldReference;
	}

	public void setIndexType(String indexType) {
		indexType = GetterUtil.getString(indexType);

		_properties.put("indexType", indexType);

		_indexType = indexType;
	}

	public void setLabel(LocalizedValue label) {
		_properties.put("label", label);

		_label = label;
	}

	public void setLocalizable(boolean localizable) {
		_properties.put("localizable", localizable);

		_localizable = localizable;
	}

	public void setMultiple(boolean multiple) {
		_properties.put("multiple", multiple);

		_multiple = multiple;
	}

	public void setName(String name) {
		name = GetterUtil.getString(name);

		_properties.put("name", name);

		_name = name;
	}

	public void setNestedDDMFormFields(List<DDMFormField> nestedDDMFormFields) {
		for (DDMFormField nestedDDMFormField : nestedDDMFormFields) {
			_nestedDDMFormFieldsMap.put(
				nestedDDMFormField.getName(), nestedDDMFormField);
		}

		_nestedDDMFormFields = nestedDDMFormFields;
	}

	public void setPredefinedValue(LocalizedValue predefinedValue) {
		_properties.put("predefinedValue", predefinedValue);

		_predefinedValue = predefinedValue;
	}

	public void setProperty(String name, Object value) {
		_properties.put(name, value);

		VarHandle varHandle = _varHandles.get(name);

		if (varHandle != null) {
			if (varHandle.varType() == boolean.class) {
				varHandle.set(this, GetterUtil.getBoolean(value));
			}
			else if (varHandle.varType() == String.class) {
				varHandle.set(this, GetterUtil.getString(value));
			}
			else {
				if (Validator.isNull(value)) {
					value = null;
				}

				Class<?> clazz = varHandle.varType();

				if (clazz.isInstance(value)) {
					varHandle.set(this, value);
				}
			}
		}
	}

	public void setReadOnly(boolean readOnly) {
		_properties.put("readOnly", readOnly);

		_readOnly = readOnly;
	}

	public void setRepeatable(boolean repeatable) {
		_properties.put("repeatable", repeatable);

		_repeatable = repeatable;
	}

	public void setRequired(boolean required) {
		_properties.put("required", required);

		_required = required;
	}

	public void setRequiredErrorMessage(LocalizedValue requiredErrorMessage) {
		_properties.put("requiredErrorMessage", requiredErrorMessage);

		_requiredErrorMessage = requiredErrorMessage;
	}

	public void setShowLabel(boolean showLabel) {
		_properties.put("showLabel", showLabel);

		_showLabel = showLabel;
	}

	public void setStyle(LocalizedValue style) {
		_properties.put("style", style);

		_style = style;
	}

	public void setTip(LocalizedValue tip) {
		_properties.put("tip", tip);

		_tip = tip;
	}

	public void setType(String type) {
		type = GetterUtil.getString(type);

		_properties.put("type", type);

		_type = type;
	}

	public void setVisibilityExpression(String visibilityExpression) {
		visibilityExpression = GetterUtil.getString(visibilityExpression);

		_properties.put("visibilityExpression", visibilityExpression);

		_visibilityExpression = visibilityExpression;
	}

	public void setVisualProperty(boolean visualProperty) {
		_properties.put("visualProperty", visualProperty);

		_visualProperty = visualProperty;
	}

	private static final String _DATA_SOURCE_TYPE_MANUAL = "manual";

	private static final Log _log = LogFactoryUtil.getLog(DDMFormField.class);

	private static final Map<String, VarHandle> _varHandles = new HashMap<>();

	static {
		try {
			MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(
				DDMFormField.class, MethodHandles.lookup());

			for (Field field : DDMFormField.class.getDeclaredFields()) {
				Property property = field.getAnnotation(Property.class);

				if (property == null) {
					continue;
				}

				String fieldName = field.getName();

				String name = property.value();

				if (name.isEmpty()) {
					name = fieldName;

					name = name.substring(1);
				}

				_varHandles.put(
					name,
					lookup.findVarHandle(
						DDMFormField.class, fieldName, field.getType()));
			}
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new ExceptionInInitializerError(reflectiveOperationException);
		}
	}

	@Property
	private String _dataType = StringPool.BLANK;

	private DDMForm _ddmForm;

	@Property("options")
	private DDMFormFieldOptions _ddmFormFieldOptions;

	private final List<DDMFormFieldRule> _ddmFormFieldRules;

	@Property("validation")
	private DDMFormFieldValidation _ddmFormFieldValidation;

	private DDMFormLayout _ddmFormLayout;

	@Property
	private String _fieldNamespace = StringPool.BLANK;

	@Property
	private String _fieldReference = StringPool.BLANK;

	@Property
	private String _indexType = StringPool.BLANK;

	@Property
	private LocalizedValue _label;

	@Property
	private boolean _localizable;

	@Property
	private boolean _multiple;

	@Property
	private String _name = StringPool.BLANK;

	private List<DDMFormField> _nestedDDMFormFields;
	private final Map<String, DDMFormField> _nestedDDMFormFieldsMap =
		new HashMap<>();

	@Property
	private LocalizedValue _predefinedValue;

	private final Map<String, Object> _properties;

	@Property
	private boolean _readOnly;

	@Property
	private boolean _repeatable;

	@Property
	private boolean _required;

	@Property
	private LocalizedValue _requiredErrorMessage;

	@Property
	private boolean _showLabel = true;

	@Property
	private LocalizedValue _style;

	@Property
	private LocalizedValue _tip;

	@Property
	private String _type = StringPool.BLANK;

	@Property
	private String _visibilityExpression = StringPool.BLANK;

	@Property
	private boolean _visualProperty;

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	private @interface Property {

		public String value() default "";

	}

}