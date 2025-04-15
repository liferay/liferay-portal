/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseSelectTag extends com.liferay.taglib.BaseValidatorTagSupport {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.Object getBean() {
		return _bean;
	}

	public boolean getChangesContext() {
		return _changesContext;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public java.lang.String getField() {
		return _field;
	}

	public boolean getFirst() {
		return _first;
	}

	public java.lang.String getHelpMessage() {
		return _helpMessage;
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

	public boolean getLast() {
		return _last;
	}

	public java.lang.String getListType() {
		return _listType;
	}

	public java.lang.String getListTypeFieldName() {
		return _listTypeFieldName;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
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

	public java.lang.String getPrefix() {
		return _prefix;
	}

	public boolean getRequired() {
		return _required;
	}

	public boolean getShowEmptyOption() {
		return _showEmptyOption;
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

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public java.lang.Object getValue() {
		return _value;
	}

	public java.lang.String getWrapperCssClass() {
		return _wrapperCssClass;
	}

	public void setBean(java.lang.Object bean) {
		_bean = bean;
	}

	public void setChangesContext(boolean changesContext) {
		_changesContext = changesContext;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.util.Map<java.lang.String, java.lang.Object> data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setField(java.lang.String field) {
		_field = field;
	}

	public void setFirst(boolean first) {
		_first = first;
	}

	public void setHelpMessage(java.lang.String helpMessage) {
		_helpMessage = helpMessage;
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

	public void setLast(boolean last) {
		_last = last;
	}

	public void setListType(java.lang.String listType) {
		_listType = listType;
	}

	public void setListTypeFieldName(java.lang.String listTypeFieldName) {
		_listTypeFieldName = listTypeFieldName;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
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

	public void setPrefix(java.lang.String prefix) {
		_prefix = prefix;
	}

	public void setRequired(boolean required) {
		_required = required;
	}

	public void setShowEmptyOption(boolean showEmptyOption) {
		_showEmptyOption = showEmptyOption;
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

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(java.lang.Object value) {
		_value = value;
	}

	public void setWrapperCssClass(java.lang.String wrapperCssClass) {
		_wrapperCssClass = wrapperCssClass;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_bean = null;
		_changesContext = false;
		_cssClass = null;
		_data = null;
		_disabled = false;
		_field = null;
		_first = false;
		_helpMessage = null;
		_id = null;
		_ignoreRequestValue = false;
		_inlineField = false;
		_inlineLabel = null;
		_label = null;
		_last = false;
		_listType = null;
		_listTypeFieldName = null;
		_localizeLabel = true;
		_model = null;
		_multiple = false;
		_name = null;
		_onChange = null;
		_onClick = null;
		_prefix = null;
		_required = false;
		_showEmptyOption = false;
		_showRequiredLabel = true;
		_suffix = null;
		_title = null;
		_useNamespace = true;
		_value = null;
		_wrapperCssClass = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "bean", _bean);
		setNamespacedAttribute(request, "changesContext", _changesContext);
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "disabled", _disabled);
		setNamespacedAttribute(request, "field", _field);
		setNamespacedAttribute(request, "first", _first);
		setNamespacedAttribute(request, "helpMessage", _helpMessage);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "ignoreRequestValue", _ignoreRequestValue);
		setNamespacedAttribute(request, "inlineField", _inlineField);
		setNamespacedAttribute(request, "inlineLabel", _inlineLabel);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "last", _last);
		setNamespacedAttribute(request, "listType", _listType);
		setNamespacedAttribute(request, "listTypeFieldName", _listTypeFieldName);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "model", _model);
		setNamespacedAttribute(request, "multiple", _multiple);
		setNamespacedAttribute(request, "name", _name);
		setNamespacedAttribute(request, "onChange", _onChange);
		setNamespacedAttribute(request, "onClick", _onClick);
		setNamespacedAttribute(request, "prefix", _prefix);
		setNamespacedAttribute(request, "required", _required);
		setNamespacedAttribute(request, "showEmptyOption", _showEmptyOption);
		setNamespacedAttribute(request, "showRequiredLabel", _showRequiredLabel);
		setNamespacedAttribute(request, "suffix", _suffix);
		setNamespacedAttribute(request, "title", _title);
		setNamespacedAttribute(request, "useNamespace", _useNamespace);
		setNamespacedAttribute(request, "value", _value);
		setNamespacedAttribute(request, "wrapperCssClass", _wrapperCssClass);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:select:";

	private static final String _END_PAGE =
		"/html/taglib/aui/select/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/select/start.jsp";

	private java.lang.Object _bean = null;
	private boolean _changesContext = false;
	private java.lang.String _cssClass = null;
	private java.util.Map<java.lang.String, java.lang.Object> _data = null;
	private boolean _disabled = false;
	private java.lang.String _field = null;
	private boolean _first = false;
	private java.lang.String _helpMessage = null;
	private java.lang.String _id = null;
	private boolean _ignoreRequestValue = false;
	private boolean _inlineField = false;
	private java.lang.String _inlineLabel = null;
	private java.lang.String _label = null;
	private boolean _last = false;
	private java.lang.String _listType = null;
	private java.lang.String _listTypeFieldName = null;
	private boolean _localizeLabel = true;
	private java.lang.Class<?> _model = null;
	private boolean _multiple = false;
	private java.lang.String _name = null;
	private java.lang.String _onChange = null;
	private java.lang.String _onClick = null;
	private java.lang.String _prefix = null;
	private boolean _required = false;
	private boolean _showEmptyOption = false;
	private boolean _showRequiredLabel = true;
	private java.lang.String _suffix = null;
	private java.lang.String _title = null;
	private boolean _useNamespace = true;
	private java.lang.Object _value = null;
	private java.lang.String _wrapperCssClass = null;

}