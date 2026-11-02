/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseOptionTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public Object getLabel() {
		return _label;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public boolean getSelected() {
		return _selected;
	}

	public String getStyle() {
		return _style;
	}

	public boolean getUseModelValue() {
		return _useModelValue;
	}

	public Object getValue() {
		return _value;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setLabel(Object label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public void setStyle(String style) {
		_style = style;
	}

	public void setUseModelValue(boolean useModelValue) {
		_useModelValue = useModelValue;
	}

	public void setValue(Object value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_data = null;
		_disabled = false;
		_label = null;
		_localizeLabel = true;
		_selected = false;
		_style = null;
		_useModelValue = true;
		_value = null;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "data", _data);
		setNamespacedAttribute(httpServletRequest, "disabled", _disabled);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(
			httpServletRequest, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(httpServletRequest, "selected", _selected);
		setNamespacedAttribute(httpServletRequest, "style", _style);
		setNamespacedAttribute(
			httpServletRequest, "useModelValue", _useModelValue);
		setNamespacedAttribute(httpServletRequest, "value", _value);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:option:";

	private static final String _START_PAGE =
		"/html/taglib/aui/option/start.jsp";

	private String _cssClass;
	private Map<String, Object> _data;
	private boolean _disabled;
	private Object _label;
	private boolean _localizeLabel = true;
	private boolean _selected;
	private String _style;
	private boolean _useModelValue = true;
	private Object _value;

}