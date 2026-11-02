/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.aui.base;

import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.jsp.JspException;

import java.util.Map;

/**
 * @author Eduardo Lundgren
 * @author Bruno Basto
 * @author Nathan Cavanaugh
 * @author Julio Camarero
 */
public abstract class BaseATag extends IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public String getAriaLabel() {
		return _ariaLabel;
	}

	public String getAriaRole() {
		return _ariaRole;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public Map<String, Object> getData() {
		return _data;
	}

	public String getHref() {
		return _href;
	}

	public String getIcon() {
		return _icon;
	}

	public String getIconCssClass() {
		return _iconCssClass;
	}

	public String getId() {
		return _id;
	}

	public String getLabel() {
		return _label;
	}

	public String getLang() {
		return _lang;
	}

	public boolean getLocalizeLabel() {
		return _localizeLabel;
	}

	public String getOnClick() {
		return _onClick;
	}

	public String getTarget() {
		return _target;
	}

	public String getTitle() {
		return _title;
	}

	public void setAriaLabel(String ariaLabel) {
		_ariaLabel = ariaLabel;
	}

	public void setAriaRole(String ariaRole) {
		_ariaRole = ariaRole;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(Map<String, Object> data) {
		_data = data;
	}

	public void setHref(String href) {
		_href = href;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setIconCssClass(String iconCssClass) {
		_iconCssClass = iconCssClass;
	}

	public void setId(String id) {
		_id = id;
	}

	public void setLabel(String label) {
		_label = label;
	}

	public void setLang(String lang) {
		_lang = lang;
	}

	public void setLocalizeLabel(boolean localizeLabel) {
		_localizeLabel = localizeLabel;
	}

	public void setOnClick(String onClick) {
		_onClick = onClick;
	}

	public void setTarget(String target) {
		_target = target;
	}

	public void setTitle(String title) {
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
		_icon = null;
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

	private static final String _ATTRIBUTE_NAMESPACE = "aui:a:";

	private static final String _END_PAGE = "/html/taglib/aui/a/end.jsp";

	private static final String _START_PAGE = "/html/taglib/aui/a/start.jsp";

	private String _ariaLabel;
	private String _ariaRole;
	private String _cssClass;
	private Map<String, Object> _data;
	private String _href;
	private String _icon;
	private String _iconCssClass;
	private String _id;
	private String _label;
	private String _lang;
	private boolean _localizeLabel = true;
	private String _onClick;
	private String _target;
	private String _title;

}