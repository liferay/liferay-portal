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
public abstract class BaseButtonTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.lang.Object getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public java.lang.String getHref() {
		return _href;
	}

	public java.lang.String getIcon() {
		return _icon;
	}

	public java.lang.String getIconAlign() {
		return _iconAlign;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getName() {
		return _name;
	}

	public java.lang.String getOnClick() {
		return _onClick;
	}

	public java.lang.Object getPrimary() {
		return _primary;
	}

	public java.lang.String getType() {
		return _type;
	}

	public boolean getUseDialog() {
		return _useDialog;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public java.lang.String getValue() {
		return _value;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.lang.Object data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setHref(java.lang.String href) {
		_href = href;
	}

	public void setIcon(java.lang.String icon) {
		_icon = icon;
	}

	public void setIconAlign(java.lang.String iconAlign) {
		_iconAlign = iconAlign;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setName(java.lang.String name) {
		_name = name;
	}

	public void setOnClick(java.lang.String onClick) {
		_onClick = onClick;
	}

	public void setPrimary(java.lang.Object primary) {
		_primary = primary;
	}

	public void setType(java.lang.String type) {
		_type = type;
	}

	public void setUseDialog(boolean useDialog) {
		_useDialog = useDialog;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(java.lang.String value) {
		_value = value;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_data = null;
		_disabled = false;
		_href = null;
		_icon = null;
		_iconAlign = "left";
		_id = null;
		_name = null;
		_onClick = null;
		_primary = null;
		_type = "button";
		_useDialog = false;
		_useNamespace = true;
		_value = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "disabled", _disabled);
		setNamespacedAttribute(request, "href", _href);
		setNamespacedAttribute(request, "icon", _icon);
		setNamespacedAttribute(request, "iconAlign", _iconAlign);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "name", _name);
		setNamespacedAttribute(request, "onClick", _onClick);
		setNamespacedAttribute(request, "primary", _primary);
		setNamespacedAttribute(request, "type", _type);
		setNamespacedAttribute(request, "useDialog", _useDialog);
		setNamespacedAttribute(request, "useNamespace", _useNamespace);
		setNamespacedAttribute(request, "value", _value);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:button:";

	private static final String _END_PAGE =
		"/html/taglib/aui/button/end.jsp";

	private java.lang.String _cssClass = null;
	private java.lang.Object _data = null;
	private boolean _disabled = false;
	private java.lang.String _href = null;
	private java.lang.String _icon = null;
	private java.lang.String _iconAlign = "left";
	private java.lang.String _id = null;
	private java.lang.String _name = null;
	private java.lang.String _onClick = null;
	private java.lang.Object _primary = null;
	private java.lang.String _type = "button";
	private boolean _useDialog = false;
	private boolean _useNamespace = true;
	private java.lang.String _value = null;

}