/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseInputTag extends com.liferay.taglib.BaseValidatorTagSupport {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public List<String> getActiveLanguageIds() {
		return _activeLanguageIds;
	}

	public boolean getAutoFocus() {
		return _autoFocus;
	}

	public boolean getAutoSize() {
		return _autoSize;
	}

	public java.lang.Object getBean() {
		return _bean;
	}

	public java.lang.String getButtonIconOff() {
		return _buttonIconOff;
	}

	public java.lang.String getButtonIconOn() {
		return _buttonIconOn;
	}

	public boolean getChangesContext() {
		return _changesContext;
	}

	public boolean getChecked() {
		return _checked;
	}

	public long getClassPK() {
		return _classPK;
	}

	public long getClassTypePK() {
		return _classTypePK;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.lang.Object getData() {
		return _data;
	}

	public java.lang.String getDateTogglerCheckboxLabel() {
		return _dateTogglerCheckboxLabel;
	}

	public java.lang.String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public java.lang.String getField() {
		return _field;
	}

	public java.lang.String getFieldParam() {
		return _fieldParam;
	}

	public boolean getFirst() {
		return _first;
	}

	public java.lang.String getFormName() {
		return _formName;
	}

	public java.lang.String getHelpMessage() {
		return _helpMessage;
	}

	public java.lang.String getHelpTextCssClass() {
		return _helpTextCssClass;
	}

	public java.lang.String getIconOff() {
		return _iconOff;
	}

	public java.lang.String getIconOn() {
		return _iconOn;
	}

	public java.lang.String getId() {
		return _id;
	}

	public boolean getIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public boolean getInlineField() {
		return _inlineField;
	}

	public java.lang.String getInlineLabel() {
		return _inlineLabel;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public java.lang.String getLabelCssClass() {
		return _labelCssClass;
	}

	public java.lang.String getLanguageId() {
		return _languageId;
	}

	public java.lang.String getLanguagesDropdownDirection() {
		return _languagesDropdownDirection;
	}

	public boolean getLast() {
		return _last;
	}

	public boolean getLocalized() {
		return _localized;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public java.lang.Object getMax() {
		return _max;
	}

	public java.lang.Object getMin() {
		return _min;
	}

	public java.lang.Class<?> getModel() {
		return _model;
	}

	public boolean getMultiple() {
		return _multiple;
	}

	public java.lang.String getName() {
		return _name;
	}

	public java.lang.String getOnChange() {
		return _onChange;
	}

	public java.lang.String getOnClick() {
		return _onClick;
	}

	public java.lang.String getPlaceholder() {
		return _placeholder;
	}

	public java.lang.String getPrefix() {
		return _prefix;
	}

	public boolean getRequired() {
		return _required;
	}

	public boolean getResizable() {
		return _resizable;
	}

	public java.lang.String getSelectedLanguageId() {
		return _selectedLanguageId;
	}

	public boolean getShowRequiredLabel() {
		return _showRequiredLabel;
	}

	public java.lang.String getSuffix() {
		return _suffix;
	}

	public java.lang.String getTitle() {
		return _title;
	}

	public java.lang.String getType() {
		return _type;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public java.lang.Object getValue() {
		return _value;
	}

	public boolean getWrappedField() {
		return _wrappedField;
	}

	public java.lang.String getWrapperCssClass() {
		return _wrapperCssClass;
	}

	public boolean isAdminMode() {
		return _adminMode;
	}

	public boolean isLanguagesDropdownVisible() {
		return _languagesDropdownVisible;
	}

	public void setActiveLanguageIds(List<String> activeLanguageIds) {
		_activeLanguageIds = activeLanguageIds;
	}

	public void setAdminMode(boolean adminMode) {
		_adminMode = adminMode;
	}

	public void setAutoFocus(boolean autoFocus) {
		_autoFocus = autoFocus;
	}

	public void setAutoSize(boolean autoSize) {
		_autoSize = autoSize;
	}

	public void setBean(java.lang.Object bean) {
		_bean = bean;
	}

	public void setButtonIconOff(java.lang.String buttonIconOff) {
		_buttonIconOff = buttonIconOff;
	}

	public void setButtonIconOn(java.lang.String buttonIconOn) {
		_buttonIconOn = buttonIconOn;
	}

	public void setChangesContext(boolean changesContext) {
		_changesContext = changesContext;
	}

	public void setChecked(boolean checked) {
		_checked = checked;
	}

	public void setClassPK(long classPK) {
		_classPK = classPK;
	}

	public void setClassTypePK(long classTypePK) {
		_classTypePK = classTypePK;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.lang.Object data) {
		_data = data;
	}

	public void setDateTogglerCheckboxLabel(java.lang.String dateTogglerCheckboxLabel) {
		_dateTogglerCheckboxLabel = dateTogglerCheckboxLabel;
	}

	public void setDefaultLanguageId(java.lang.String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setField(java.lang.String field) {
		_field = field;
	}

	public void setFieldParam(java.lang.String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFirst(boolean first) {
		_first = first;
	}

	public void setFormName(java.lang.String formName) {
		_formName = formName;
	}

	public void setHelpMessage(java.lang.String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setHelpTextCssClass(java.lang.String helpTextCssClass) {
		_helpTextCssClass = helpTextCssClass;
	}

	public void setLanguagesDropdownVisible(boolean languagesDropdownVisible) {
		_languagesDropdownVisible = languagesDropdownVisible;
	}

	public void setIconOff(java.lang.String iconOff) {
		_iconOff = iconOff;
	}

	public void setIconOn(java.lang.String iconOn) {
		_iconOn = iconOn;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setInlineField(boolean inlineField) {
		_inlineField = inlineField;
	}

	public void setInlineLabel(java.lang.String inlineLabel) {
		_inlineLabel = inlineLabel;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setLabelCssClass(java.lang.String labelCssClass) {
		_labelCssClass = labelCssClass;
	}

	public void setLanguageId(java.lang.String languageId) {
		_languageId = languageId;
	}

	public void setLanguagesDropdownDirection(java.lang.String languagesDropdownDirection) {
		_languagesDropdownDirection = languagesDropdownDirection;
	}

	public void setLast(boolean last) {
		_last = last;
	}

	public void setLocalized(boolean localized) {
		_localized = localized;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setMax(java.lang.Object max) {
		_max = max;
	}

	public void setMin(java.lang.Object min) {
		_min = min;
	}

	public void setModel(java.lang.Class<?> model) {
		_model = model;
	}

	public void setMultiple(boolean multiple) {
		_multiple = multiple;
	}

	public void setName(java.lang.String name) {
		_name = name;
	}

	public void setOnChange(java.lang.String onChange) {
		_onChange = onChange;
	}

	public void setOnClick(java.lang.String onClick) {
		_onClick = onClick;
	}

	public void setPlaceholder(java.lang.String placeholder) {
		_placeholder = placeholder;
	}

	public void setPrefix(java.lang.String prefix) {
		_prefix = prefix;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setResizable(boolean resizable) {
		_resizable = resizable;
	}

	public void setSelectedLanguageId(java.lang.String selectedLanguageId) {
		_selectedLanguageId = selectedLanguageId;
	}

	public void setShowRequiredLabel(boolean showRequiredLabel) {
		_showRequiredLabel = showRequiredLabel;
	}

	public void setSuffix(java.lang.String suffix) {
		_suffix = suffix;
	}

	public void setTitle(java.lang.String title) {
		_title = title;
	}

	public void setType(java.lang.String type) {
		_type = type;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(java.lang.Object value) {
		_value = value;
	}

	public void setWrappedField(boolean wrappedField) {
		_wrappedField = wrappedField;
	}

	public void setWrapperCssClass(java.lang.String wrapperCssClass) {
		_wrapperCssClass = wrapperCssClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_activeLanguageIds = new ArrayList<>();
		_adminMode = false;
		_autoFocus = false;
		_autoSize = false;
		_bean = null;
		_buttonIconOff = null;
		_buttonIconOn = null;
		_changesContext = false;
		_checked = false;
		_classPK = 0;
		_classTypePK = -1;
		_cssClass = null;
		_data = null;
		_dateTogglerCheckboxLabel = null;
		_defaultLanguageId = null;
		_disabled = false;
		_field = null;
		_fieldParam = null;
		_first = false;
		_formName = null;
		_helpMessage = null;
		_helpTextCssClass = "input-group-addon";
		_iconOff = null;
		_iconOn = null;
		_id = null;
		_ignoreRequestValue = false;
		_inlineField = false;
		_inlineLabel = null;
		_label = null;
		_labelCssClass = null;
		_languageId = null;
		_languagesDropdownDirection = null;
		_languagesDropdownVisible = true;
		_last = false;
		_localized = false;
		_localizeLabel = true;
		_max = null;
		_min = null;
		_model = null;
		_multiple = false;
		_name = null;
		_onChange = null;
		_onClick = null;
		_placeholder = null;
		_prefix = null;
		_required = false;
		_resizable = false;
		_selectedLanguageId = null;
		_showRequiredLabel = true;
		_suffix = null;
		_title = null;
		_type = null;
		_useNamespace = true;
		_value = null;
		_wrappedField = false;
		_wrapperCssClass = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "activeLanguageIds", _activeLanguageIds);
		setNamespacedAttribute(request, "adminMode", _adminMode);
		setNamespacedAttribute(request, "autoFocus", _autoFocus);
		setNamespacedAttribute(request, "autoSize", _autoSize);
		setNamespacedAttribute(request, "bean", _bean);
		setNamespacedAttribute(request, "buttonIconOff", _buttonIconOff);
		setNamespacedAttribute(request, "buttonIconOn", _buttonIconOn);
		setNamespacedAttribute(request, "changesContext", _changesContext);
		setNamespacedAttribute(request, "checked", _checked);
		setNamespacedAttribute(request, "classPK", _classPK);
		setNamespacedAttribute(request, "classTypePK", _classTypePK);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "dateTogglerCheckboxLabel", _dateTogglerCheckboxLabel);
		setNamespacedAttribute(request, "defaultLanguageId", _defaultLanguageId);
		setNamespacedAttribute(request, "disabled", _disabled);
		setNamespacedAttribute(request, "field", _field);
		setNamespacedAttribute(request, "fieldParam", _fieldParam);
		setNamespacedAttribute(request, "first", _first);
		setNamespacedAttribute(request, "formName", _formName);
		setNamespacedAttribute(request, "helpMessage", _helpMessage);
		setNamespacedAttribute(request, "helpTextCssClass", _helpTextCssClass);
		setNamespacedAttribute(request, "languagesDropdownVisible", _languagesDropdownVisible);
		setNamespacedAttribute(request, "iconOff", _iconOff);
		setNamespacedAttribute(request, "iconOn", _iconOn);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "ignoreRequestValue", _ignoreRequestValue);
		setNamespacedAttribute(request, "inlineField", _inlineField);
		setNamespacedAttribute(request, "inlineLabel", _inlineLabel);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "labelCssClass", _labelCssClass);
		setNamespacedAttribute(request, "languageId", _languageId);
		setNamespacedAttribute(request, "languagesDropdownDirection", _languagesDropdownDirection);
		setNamespacedAttribute(request, "last", _last);
		setNamespacedAttribute(request, "localized", _localized);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "max", _max);
		setNamespacedAttribute(request, "min", _min);
		setNamespacedAttribute(request, "model", _model);
		setNamespacedAttribute(request, "multiple", _multiple);
		setNamespacedAttribute(request, "name", _name);
		setNamespacedAttribute(request, "onChange", _onChange);
		setNamespacedAttribute(request, "onClick", _onClick);
		setNamespacedAttribute(request, "placeholder", _placeholder);
		setNamespacedAttribute(request, "prefix", _prefix);
		setNamespacedAttribute(request, "required", _required);
		setNamespacedAttribute(request, "resizable", _resizable);
		setNamespacedAttribute(request, "selectedLanguageId", _selectedLanguageId);
		setNamespacedAttribute(request, "showRequiredLabel", _showRequiredLabel);
		setNamespacedAttribute(request, "suffix", _suffix);
		setNamespacedAttribute(request, "title", _title);
		setNamespacedAttribute(request, "type", _type);
		setNamespacedAttribute(request, "useNamespace", _useNamespace);
		setNamespacedAttribute(request, "value", _value);
		setNamespacedAttribute(request, "wrappedField", _wrappedField);
		setNamespacedAttribute(request, "wrapperCssClass", _wrapperCssClass);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:input:";

	private static final String _PAGE =
		"/html/taglib/aui/input/page.jsp";

	private List<String> _activeLanguageIds = new ArrayList<>();
	private boolean _adminMode = false;
	private boolean _autoFocus = false;
	private boolean _autoSize = false;
	private java.lang.Object _bean = null;
	private java.lang.String _buttonIconOff = null;
	private java.lang.String _buttonIconOn = null;
	private boolean _changesContext = false;
	private boolean _checked = false;
	private long _classPK = 0;
	private long _classTypePK = -1;
	private java.lang.String _cssClass = null;
	private java.lang.Object _data = null;
	private java.lang.String _dateTogglerCheckboxLabel = null;
	private java.lang.String _defaultLanguageId = null;
	private boolean _disabled = false;
	private java.lang.String _field = null;
	private java.lang.String _fieldParam = null;
	private boolean _first = false;
	private java.lang.String _formName = null;
	private java.lang.String _helpMessage = null;
	private java.lang.String _helpTextCssClass = "input-group-addon";
	private boolean _languagesDropdownVisible = true;
	private java.lang.String _iconOff = null;
	private java.lang.String _iconOn = null;
	private java.lang.String _id = null;
	private boolean _ignoreRequestValue = false;
	private boolean _inlineField = false;
	private java.lang.String _inlineLabel = null;
	private java.lang.String _label = null;
	private java.lang.String _labelCssClass = null;
	private java.lang.String _languageId = null;
	private java.lang.String _languagesDropdownDirection = null;
	private boolean _last = false;
	private boolean _localized = false;
	private boolean _localizeLabel = true;
	private java.lang.Object _max = null;
	private java.lang.Object _min = null;
	private java.lang.Class<?> _model = null;
	private boolean _multiple = false;
	private java.lang.String _name = null;
	private java.lang.String _onChange = null;
	private java.lang.String _onClick = null;
	private java.lang.String _placeholder = null;
	private java.lang.String _prefix = null;
	private boolean _required = false;
	private boolean _resizable = false;
	private java.lang.String _selectedLanguageId = null;
	private boolean _showRequiredLabel = true;
	private java.lang.String _suffix = null;
	private java.lang.String _title = null;
	private java.lang.String _type = null;
	private boolean _useNamespace = true;
	private java.lang.Object _value = null;
	private boolean _wrappedField = false;
	private java.lang.String _wrapperCssClass = null;

}