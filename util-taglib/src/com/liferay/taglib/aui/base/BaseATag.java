/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import jakarta.servlet.jsp.JspException;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 * @generated
 */
public abstract class BaseATag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getAriaLabel() {
		return _ariaLabel;
	}

	public java.lang.String getAriaRole() {
		return _ariaRole;
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getData() {
		return _data;
	}

	public java.lang.String getHref() {
		return _href;
	}

	public java.lang.String getIconCssClass() {
		return _iconCssClass;
	}

	public java.lang.String getIcon() {
		return _icon;
	}

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public java.lang.String getLang() {
		return _lang;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public java.lang.String getOnClick() {
		return _onClick;
	}

	public java.lang.String getTarget() {
		return _target;
	}

	public java.lang.String getTitle() {
		return _title;
	}

	public void setAriaLabel(java.lang.String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setAriaRole(java.lang.String ariaRole) {
		_ariaRole = ariaRole;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.util.Map<java.lang.String, java.lang.Object> data) {
		_data = data;
	}

	public void setHref(java.lang.String href) {
		_href = href;
	}

	public void setIconCssClass(java.lang.String iconCssClass) {
		_iconCssClass = iconCssClass;
	}

	public void setIcon(java.lang.String icon) {
		_icon = icon;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setLang(java.lang.String lang) {
		_lang = lang;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setOnClick(java.lang.String onClick) {
		_onClick = onClick;
	}

	public void setTarget(java.lang.String target) {
		_target = target;
	}

	public void setTitle(java.lang.String title) {
		_title = title;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_ariaLabel = null;
		_ariaRole = null;
		_cssClass = null;
		_data = null;
		_href = null;
		_iconCssClass = null;
		_id = null;
		_label = null;
		_lang = null;
		_localizeLabel = true;
		_onClick = null;
		_target = null;
		_title = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:a:";

	private static final String _END_PAGE =
		"/html/taglib/aui/a/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/a/start.jsp";

	private java.lang.String _ariaLabel = null;
	private java.lang.String _ariaRole = null;
	private java.lang.String _cssClass = null;
	private java.util.Map<java.lang.String, java.lang.Object> _data = null;
	private java.lang.String _href = null;
	private java.lang.String _iconCssClass = null;
	private java.lang.String _icon = null;
	private java.lang.String _id = null;
	private java.lang.String _label = null;
	private java.lang.String _lang = null;
	private boolean _localizeLabel = true;
	private java.lang.String _onClick = null;
	private java.lang.String _target = null;
	private java.lang.String _title = null;

}