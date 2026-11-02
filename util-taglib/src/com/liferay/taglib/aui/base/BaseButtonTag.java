/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseButtonTag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Object getData() {
		return _data;
	}

	public boolean getDisabled() {
		return _disabled;
	}

	public String getHref() {
		return _href;
	}

	public String getIcon() {
		return _icon;
	}

	public String getIconAlign() {
		return _iconAlign;
	}

	public String getId() {
		return _id;
	}

	public String getName() {
		return _name;
	}

	public String getOnClick() {
		return _onClick;
	}

	public Object getPrimary() {
		return _primary;
	}

	public String getType() {
		return _type;
	}

	public boolean getUseDialog() {
		return _useDialog;
	}

	public boolean getUseNamespace() {
		return _useNamespace;
	}

	public String getValue() {
		return _value;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Object data) {
		_data = data;
	}

	public void setDisabled(boolean disabled) {
		_disabled = disabled;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setIconAlign(String iconAlign) {
		_iconAlign = iconAlign;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setOnClick(String onClick) {
		_onClick = onClick;
	}

	public void setPrimary(Object primary) {
		_primary = primary;
	}

	public void setType(String type) {
		_type = type;
	}

	public void setUseDialog(boolean useDialog) {
		_useDialog = useDialog;
	}

	public void setUseNamespace(boolean useNamespace) {
		_useNamespace = useNamespace;
	}

	public void setValue(String value) {
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
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "data", _data);
		setNamespacedAttribute(httpServletRequest, "disabled", _disabled);
		setNamespacedAttribute(httpServletRequest, "href", _href);
		setNamespacedAttribute(httpServletRequest, "icon", _icon);
		setNamespacedAttribute(httpServletRequest, "iconAlign", _iconAlign);
		setNamespacedAttribute(httpServletRequest, "id", _id);
		setNamespacedAttribute(httpServletRequest, "name", _name);
		setNamespacedAttribute(httpServletRequest, "onClick", _onClick);
		setNamespacedAttribute(httpServletRequest, "primary", _primary);
		setNamespacedAttribute(httpServletRequest, "type", _type);
		setNamespacedAttribute(httpServletRequest, "useDialog", _useDialog);
		setNamespacedAttribute(
			httpServletRequest, "useNamespace", _useNamespace);
		setNamespacedAttribute(httpServletRequest, "value", _value);
	}

	private static final String _ATTRIBUTE_NAMESPACE = "aui:button:";

	private static final String _END_PAGE = "/html/taglib/aui/button/end.jsp";

	private String _cssClass;
	private Object _data;
	private boolean _disabled;
	private String _href;
	private String _icon;
	private String _iconAlign = "left";
	private String _id;
	private String _name;
	private String _onClick;
	private Object _primary;
	private String _type = "button";
	private boolean _useDialog;
	private boolean _useNamespace = true;
	private String _value;

}