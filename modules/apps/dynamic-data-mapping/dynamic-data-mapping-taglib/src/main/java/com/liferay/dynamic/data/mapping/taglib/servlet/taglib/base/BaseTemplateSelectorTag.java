/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.taglib.servlet.taglib.base;

import com.liferay.dynamic.data.mapping.taglib.internal.servlet.ServletContextUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Bruno Basto
 * @generated
 */
public abstract class BaseTemplateSelectorTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getClassName() {
		return _className;
	}

	public java.lang.String getDefaultDisplayStyle() {
		return _defaultDisplayStyle;
	}

	public java.lang.String getDisplayStyle() {
		return _displayStyle;
	}

	public long getDisplayStyleGroupId() {
		return _displayStyleGroupId;
	}

	public java.util.List<java.lang.String> getDisplayStyles() {
		return _displayStyles;
	}

	public java.lang.String getIcon() {
		return _icon;
	}

	public java.lang.String getLabel() {
		return _label;
	}

	public java.lang.String getRefreshURL() {
		return _refreshURL;
	}

	public boolean getShowEmptyOption() {
		return _showEmptyOption;
	}

	public void setClassName(java.lang.String className) {
		_className = className;
	}

	public void setDefaultDisplayStyle(java.lang.String defaultDisplayStyle) {
		_defaultDisplayStyle = defaultDisplayStyle;
	}

	public void setDisplayStyle(java.lang.String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setDisplayStyleGroupId(long displayStyleGroupId) {
		_displayStyleGroupId = displayStyleGroupId;
	}

	public void setDisplayStyles(java.util.List<java.lang.String> displayStyles) {
		_displayStyles = displayStyles;
	}

	public void setIcon(java.lang.String icon) {
		_icon = icon;
	}

	public void setLabel(java.lang.String label) {
		_label = label;
	}

	public void setRefreshURL(java.lang.String refreshURL) {
		_refreshURL = refreshURL;
	}

	public void setShowEmptyOption(boolean showEmptyOption) {
		_showEmptyOption = showEmptyOption;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_className = null;
		_defaultDisplayStyle = com.liferay.petra.string.StringPool.BLANK;
		_displayStyle = null;
		_displayStyleGroupId = 0;
		_displayStyles = null;
		_icon = null;
		_label = "display-template";
		_refreshURL = null;
		_showEmptyOption = false;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "className", _className);
		setNamespacedAttribute(request, "defaultDisplayStyle", _defaultDisplayStyle);
		setNamespacedAttribute(request, "displayStyle", _displayStyle);
		setNamespacedAttribute(request, "displayStyleGroupId", _displayStyleGroupId);
		setNamespacedAttribute(request, "displayStyles", _displayStyles);
		setNamespacedAttribute(request, "icon", _icon);
		setNamespacedAttribute(request, "label", _label);
		setNamespacedAttribute(request, "refreshURL", _refreshURL);
		setNamespacedAttribute(request, "showEmptyOption", _showEmptyOption);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-ddm:template-selector:";

	private static final String _PAGE =
		"/template_selector/page.jsp";

	private java.lang.String _className = null;
	private java.lang.String _defaultDisplayStyle = com.liferay.petra.string.StringPool.BLANK;
	private java.lang.String _displayStyle = null;
	private long _displayStyleGroupId = 0;
	private java.util.List<java.lang.String> _displayStyles = null;
	private java.lang.String _icon = null;
	private java.lang.String _label = "display-template";
	private java.lang.String _refreshURL = null;
	private boolean _showEmptyOption = false;

}