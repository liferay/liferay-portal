/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.BaseValidatorTagSupport;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseInputTag extends BaseValidatorTagSupport {

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

	public Object getBean() {
		return _bean;
	}

	public String getButtonIconOff() {
		return _buttonIconOff;
	}

	public String getButtonIconOn() {
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

	public String getCssClass() {
		return _cssClass;
	}

	public Object getData() {
		return _data;
	}

	public String getDateTogglerCheckboxLabel() {
		return _dateTogglerCheckboxLabel;
	}

	public String getDefaultLanguageId() {
		return _defaultLanguageId;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public String getField() {
		return _field;
	}

	public String getFieldParam() {
		return _fieldParam;
	}

	public boolean getFirst() {
		return _first;
	}

	public String getFormName() {
		return _formName;
	}

	public String getHelpMessage() {
		return _helpMessage;
	}

	public String getHelpTextCssClass() {
		return _helpTextCssClass;
	}

	public String getIconOff() {
		return _iconOff;
	}

	public String getIconOn() {
		return _iconOn;
	}

	public String getId() {
		return _id;
	}

	public boolean getIgnoreRequestValue() {
		return _ignoreRequestValue;
	}

	public boolean getInlineField() {
		return _inlineField;
	}

	public String getInlineLabel() {
		return _inlineLabel;
	}

	public String getLabel() {
		return _label;
	}

	public String getLabelCssClass() {
		return _labelCssClass;
	}

	public String getLanguageId() {
		return _languageId;
	}

	public String getLanguagesDropdownDirection() {
		return _languagesDropdownDirection;
	}

	public boolean getLast() {
		return _last;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public boolean getLocalized() {
		return _localized;
	}

	public Object getMax() {
		return _max;
	}

	public Object getMin() {
		return _min;
	}

	public Class<?> getModel() {
		return _model;
	}

	public boolean getMultiple() {
		return _multiple;
	}

	public String getName() {
		return _name;
	}

	public String getOnChange() {
		return _onChange;
	}

	public String getOnClick() {
		return _onClick;
	}

	public String getPlaceholder() {
		return _placeholder;
	}

	public String getPrefix() {
		return _prefix;
	}

	public boolean getRequired() {
		return _required;
	}

	public boolean getResizable() {
		return _resizable;
	}

	public String getSelectedLanguageId() {
		return _selectedLanguageId;
	}

	public boolean getShowRequiredLabel() {
		return _showRequiredLabel;
	}

	public String getSuffix() {
		return _suffix;
	}

	public String getTitle() {
		return _title;
	}

	public String getType() {
		return _type;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public Object getValue() {
		return _value;
	}

	public boolean getWrappedField() {
		return _wrappedField;
	}

	public String getWrapperCssClass() {
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

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setButtonIconOff(String buttonIconOff) {
		_buttonIconOff = buttonIconOff;
	}

	public void setButtonIconOn(String buttonIconOn) {
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

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Object data) {
		_data = data;
	}

	public void setDateTogglerCheckboxLabel(String dateTogglerCheckboxLabel) {
		_dateTogglerCheckboxLabel = dateTogglerCheckboxLabel;
	}

	public void setDefaultLanguageId(String defaultLanguageId) {
		_defaultLanguageId = defaultLanguageId;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setField(String field) {
		_field = field;
	}

	public void setFieldParam(String fieldParam) {
		_fieldParam = fieldParam;
	}

	public void setFirst(boolean first) {
		_first = first;
	}

	public void setFormName(String formName) {
		_formName = formName;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
	}

	public void setHelpTextCssClass(String helpTextCssClass) {
		_helpTextCssClass = helpTextCssClass;
	}

	public void setIconOff(String iconOff) {
		_iconOff = iconOff;
	}

	public void setIconOn(String iconOn) {
		_iconOn = iconOn;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setIgnoreRequestValue(boolean ignoreRequestValue) {
		_ignoreRequestValue = ignoreRequestValue;
	}

	public void setInlineField(boolean inlineField) {
		_inlineField = inlineField;
	}

	public void setInlineLabel(String inlineLabel) {
		_inlineLabel = inlineLabel;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLabelCssClass(String labelCssClass) {
		_labelCssClass = labelCssClass;
	}

	public void setLanguageId(String languageId) {
		_languageId = languageId;
	}

	public void setLanguagesDropdownDirection(
		String languagesDropdownDirection) {

		_languagesDropdownDirection = languagesDropdownDirection;
	}

	public void setLanguagesDropdownVisible(boolean languagesDropdownVisible) {
		_languagesDropdownVisible = languagesDropdownVisible;
	}

	public void setLast(boolean last) {
		_last = last;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setLocalized(boolean localized) {
		_localized = localized;
	}

	public void setMax(Object max) {
		_max = max;
	}

	public void setMin(Object min) {
		_min = min;
	}

	public void setModel(Class<?> model) {
		_model = model;
	}

	public void setMultiple(boolean multiple) {
		_multiple = multiple;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOnChange(String onChange) {
		_onChange = onChange;
	}

	public void setOnClick(String onClick) {
		_onClick = onClick;
	}

	public void setPlaceholder(String placeholder) {
		_placeholder = placeholder;
	}

	public void setPrefix(String prefix) {
		_prefix = prefix;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setResizable(boolean resizable) {
		_resizable = resizable;
	}

	public void setSelectedLanguageId(String selectedLanguageId) {
		_selectedLanguageId = selectedLanguageId;
	}

	public void setShowRequiredLabel(boolean showRequiredLabel) {
		_showRequiredLabel = showRequiredLabel;
	}

	public void setSuffix(String suffix) {
		_suffix = suffix;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(Object value) {
		_value = value;
	}

	public void setWrappedField(boolean wrappedField) {
		_wrappedField = wrappedField;
	}

	public void setWrapperCssClass(String wrapperCssClass) {
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
		_localizeLabel = true;
		_localized = false;
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
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(
			httpServletRequest, "activeLanguageIds", _activeLanguageIds);
		setNamespacedAttribute(httpServletRequest, "adminMode", _adminMode);
		setNamespacedAttribute(httpServletRequest, "autoFocus", _autoFocus);
		setNamespacedAttribute(httpServletRequest, "autoSize", _autoSize);
		setNamespacedAttribute(httpServletRequest, "bean", _bean);
		setNamespacedAttribute(
			httpServletRequest, "buttonIconOff", _buttonIconOff);
		setNamespacedAttribute(
			httpServletRequest, "buttonIconOn", _buttonIconOn);
		setNamespacedAttribute(
			httpServletRequest, "changesContext", _changesContext);
		setNamespacedAttribute(httpServletRequest, "checked", _checked);
		setNamespacedAttribute(httpServletRequest, "classPK", _classPK);
		setNamespacedAttribute(httpServletRequest, "classTypePK", _classTypePK);
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "data", _data);
		setNamespacedAttribute(
			httpServletRequest, "dateTogglerCheckboxLabel",
			_dateTogglerCheckboxLabel);
		setNamespacedAttribute(
			httpServletRequest, "defaultLanguageId", _defaultLanguageId);
		setNamespacedAttribute(httpServletRequest, "disabled", _disabled);
		setNamespacedAttribute(httpServletRequest, "field", _field);
		setNamespacedAttribute(httpServletRequest, "fieldParam", _fieldParam);
		setNamespacedAttribute(httpServletRequest, "first", _first);
		setNamespacedAttribute(httpServletRequest, "formName", _formName);
		setNamespacedAttribute(httpServletRequest, "helpMessage", _helpMessage);
		setNamespacedAttribute(
			httpServletRequest, "helpTextCssClass", _helpTextCssClass);
		setNamespacedAttribute(httpServletRequest, "iconOff", _iconOff);
		setNamespacedAttribute(httpServletRequest, "iconOn", _iconOn);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(
			httpServletRequest, "ignoreRequestValue", _ignoreRequestValue);
		setNamespacedAttribute(httpServletRequest, "inlineField", _inlineField);
		setNamespacedAttribute(httpServletRequest, "inlineLabel", _inlineLabel);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(
			httpServletRequest, "labelCssClass", _labelCssClass);
		setNamespacedAttribute(httpServletRequest, "languageId", _languageId);
		setNamespacedAttribute(
			httpServletRequest, "languagesDropdownDirection",
			_languagesDropdownDirection);
		setNamespacedAttribute(
			httpServletRequest, "languagesDropdownVisible",
			_languagesDropdownVisible);
		setNamespacedAttribute(httpServletRequest, "last", _last);
		setNamespacedAttribute(httpServletRequest, "localized", _localized);
		setNamespacedAttribute(
			httpServletRequest, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(httpServletRequest, "max", _max);
		setNamespacedAttribute(httpServletRequest, "min", _min);
		setNamespacedAttribute(httpServletRequest, "model", _model);
		setNamespacedAttribute(httpServletRequest, "multiple", _multiple);
		setNamespacedAttribute(httpServletRequest, "name", _name);
		setNamespacedAttribute(httpServletRequest, "onChange", _onChange);
		setNamespacedAttribute(httpServletRequest, "onClick", _onClick);
		setNamespacedAttribute(httpServletRequest, "placeholder", _placeholder);
		setNamespacedAttribute(httpServletRequest, "prefix", _prefix);
		setNamespacedAttribute(httpServletRequest, "required", _required);
		setNamespacedAttribute(httpServletRequest, "resizable", _resizable);
		setNamespacedAttribute(
			httpServletRequest, "selectedLanguageId", _selectedLanguageId);
		setNamespacedAttribute(
			httpServletRequest, "showRequiredLabel", _showRequiredLabel);
		setNamespacedAttribute(httpServletRequest, "suffix", _suffix);
		setNamespacedAttribute(httpServletRequest, "title", _title);
		setNamespacedAttribute(httpServletRequest, "type", _type);
		setNamespacedAttribute(
			httpServletRequest, "useNamespace", _useNamespace);
		setNamespacedAttribute(httpServletRequest, "value", _value);
		setNamespacedAttribute(
			httpServletRequest, "wrappedField", _wrappedField);
		setNamespacedAttribute(
			httpServletRequest, "wrapperCssClass", _wrapperCssClass);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:input:";

	private static final String _PAGE = "/html/taglib/aui/input/page.jsp";

	private List<String> _activeLanguageIds = new ArrayList<>();
	private boolean _adminMode;
	private boolean _autoFocus;
	private boolean _autoSize;
	private Object _bean;
	private String _buttonIconOff;
	private String _buttonIconOn;
	private boolean _changesContext;
	private boolean _checked;
	private long _classPK;
	private long _classTypePK = -1;
	private String _cssClass;
	private Object _data;
	private String _dateTogglerCheckboxLabel;
	private String _defaultLanguageId;
	private boolean _disabled;
	private String _field;
	private String _fieldParam;
	private boolean _first;
	private String _formName;
	private String _helpMessage;
	private String _helpTextCssClass = "input-group-addon";
	private String _iconOff;
	private String _iconOn;
	private String _id;
	private boolean _ignoreRequestValue;
	private boolean _inlineField;
	private String _inlineLabel;
	private String _label;
	private String _labelCssClass;
	private String _languageId;
	private String _languagesDropdownDirection;
	private boolean _languagesDropdownVisible = true;
	private boolean _last;
	private boolean _localizeLabel = true;
	private boolean _localized;
	private Object _max;
	private Object _min;
	private Class<?> _model;
	private boolean _multiple;
	private String _name;
	private String _onChange;
	private String _onClick;
	private String _placeholder;
	private String _prefix;
	private boolean _required;
	private boolean _resizable;
	private String _selectedLanguageId;
	private boolean _showRequiredLabel = true;
	private String _suffix;
	private String _title;
	private String _type;
	private boolean _useNamespace = true;
	private Object _value;
	private boolean _wrappedField;
	private String _wrapperCssClass;

}