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
public abstract class BaseOptionTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
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

	public java.lang.Object getLabel() {
		return _label;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public boolean getSelected() {
		return _selected;
	}

	public java.lang.String getStyle() {
		return _style;
	}

	public boolean getUseModelValue() {
		return _useModelValue;
	}

	public java.lang.Object getValue() {
		return _value;
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

	public void setLabel(java.lang.Object label) {
		_label = label;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setSelected(boolean selected) {
		_selected = selected;
	}

	public void setStyle(java.lang.String style) {
		_style = style;
	}

	public void setUseModelValue(boolean useModelValue) {
		_useModelValue = useModelValue;
	}

	public void setValue(java.lang.Object value) {
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
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "disabled", _disabled);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "localizeLabel", _localizeLabel);
		setNamespacedAttribute(request, "selected", _selected);
		setNamespacedAttribute(request, "style", _style);
		setNamespacedAttribute(request, "useModelValue", _useModelValue);
		setNamespacedAttribute(request, "value", _value);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:option:";

	private static final String _START_PAGE =
		"/html/taglib/aui/option/start.jsp";

	private java.lang.String _cssClass = null;
	private java.util.Map<java.lang.String, java.lang.Object> _data = null;
	private boolean _disabled = false;
	private java.lang.Object _label = null;
	private boolean _localizeLabel = true;
	private boolean _selected = false;
	private java.lang.String _style = null;
	private boolean _useModelValue = true;
	private java.lang.Object _value = null;

}