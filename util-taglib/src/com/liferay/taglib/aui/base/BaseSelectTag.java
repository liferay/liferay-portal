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
 */
public abstract class BaseSelectTag
	extends com.liferay.taglib.BaseValidatorTagSupport {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public Object getBean() {
		return _bean;
	}

	public boolean getChangesContext() {
		return _changesContext;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public java.util.Map<String, Object> getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public String getField() {
		return _field;
	}

	public boolean getFirst() {
		return _first;
	}

	public String getHelpMessage() {
		return _helpMessage;
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

	public boolean getLast() {
		return _last;
	}

	public String getListType() {
		return _listType;
	}

	public String getListTypeFieldName() {
		return _listTypeFieldName;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
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

	public String getPrefix() {
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

	public String getSuffix() {
		return _suffix;
	}

	public String getTitle() {
		return _title;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public Object getValue() {
		return _value;
	}

	public String getWrapperCssClass() {
		return _wrapperCssClass;
	}

	public void setBean(Object bean) {
		_bean = bean;
	}

	public void setChangesContext(boolean changesContext) {
		_changesContext = changesContext;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.util.Map<String, Object> data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setField(String field) {
		_field = field;
	}

	public void setFirst(boolean first) {
		_first = first;
	}

	public void setHelpMessage(String helpMessage) {
		_helpMessage = helpMessage;
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

	public void setLast(boolean last) {
		_last = last;
	}

	public void setListType(String listType) {
		_listType = listType;
	}

	public void setListTypeFieldName(String listTypeFieldName) {
		_listTypeFieldName = listTypeFieldName;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
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

	public void setPrefix(String prefix) {
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

	public void setSuffix(String suffix) {
		_suffix = suffix;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(Object value) {
		_value = value;
	}

	public void setWrapperCssClass(String wrapperCssClass) {
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
		setNamespacedAttribute(
			request, "ignoreRequestValue", _ignoreRequestValue);
		setNamespacedAttribute(request, "inlineField", _inlineField);
		setNamespacedAttribute(request, "inlineLabel", _inlineLabel);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "last", _last);
		setNamespacedAttribute(request, "listType", _listType);
		setNamespacedAttribute(
			request, "listTypeFieldName", _listTypeFieldName);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "model", _model);
		setNamespacedAttribute(request, "multiple", _multiple);
		setNamespacedAttribute(request, "name", _name);
		setNamespacedAttribute(request, "onChange", _onChange);
		setNamespacedAttribute(request, "onClick", _onClick);
		setNamespacedAttribute(request, "prefix", _prefix);
		setNamespacedAttribute(request, "required", _required);
		setNamespacedAttribute(request, "showEmptyOption", _showEmptyOption);
		setNamespacedAttribute(
			request, "showRequiredLabel", _showRequiredLabel);
		setNamespacedAttribute(request, "suffix", _suffix);
		setNamespacedAttribute(request, "title", _title);
		setNamespacedAttribute(request, "useNamespace", _useNamespace);
		setNamespacedAttribute(request, "value", _value);
		setNamespacedAttribute(request, "wrapperCssClass", _wrapperCssClass);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:select:";

	private static final String _END_PAGE = "/html/taglib/aui/select/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/select/start.jsp";

	private Object _bean;
	private boolean _changesContext;
	private String _cssClass;
	private java.util.Map<String, Object> _data;
	private boolean _disabled;
	private String _field;
	private boolean _first;
	private String _helpMessage;
	private String _id;
	private boolean _ignoreRequestValue;
	private boolean _inlineField;
	private String _inlineLabel;
	private String _label;
	private boolean _last;
	private String _listType;
	private String _listTypeFieldName;
	private boolean _localizeLabel = true;
	private Class<?> _model;
	private boolean _multiple;
	private String _name;
	private String _onChange;
	private String _onClick;
	private String _prefix;
	private boolean _required;
	private boolean _showEmptyOption;
	private boolean _showRequiredLabel = true;
	private String _suffix;
	private String _title;
	private boolean _useNamespace = true;
	private Object _value;
	private String _wrapperCssClass;

}