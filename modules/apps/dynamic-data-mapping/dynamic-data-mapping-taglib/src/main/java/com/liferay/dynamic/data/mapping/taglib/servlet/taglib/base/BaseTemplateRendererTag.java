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
public abstract class BaseTemplateRendererTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getClassName() {
		return _className;
	}

	public java.util.Map<java.lang.String, java.lang.Object> getContextObjects() {
		return _contextObjects;
	}

	public java.lang.String getDisplayStyle() {
		return _displayStyle;
	}

	public long getDisplayStyleGroupId() {
		return _displayStyleGroupId;
	}

	public java.util.List<?> getEntries() {
		return _entries;
	}

	public void setClassName(java.lang.String className) {
		_className = className;
	}

	public void setContextObjects(java.util.Map<java.lang.String, java.lang.Object> contextObjects) {
		_contextObjects = contextObjects;
	}

	public void setDisplayStyle(java.lang.String displayStyle) {
		_displayStyle = displayStyle;
	}

	public void setDisplayStyleGroupId(long displayStyleGroupId) {
		_displayStyleGroupId = displayStyleGroupId;
	}

	public void setEntries(java.util.List<?> entries) {
		_entries = entries;
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
		_contextObjects = new java.util.HashMap<java.lang.String, java.lang.Object>();
		_displayStyle = null;
		_displayStyleGroupId = 0;
		_entries = null;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "className", _className);
		setNamespacedAttribute(request, "contextObjects", _contextObjects);
		setNamespacedAttribute(request, "displayStyle", _displayStyle);
		setNamespacedAttribute(request, "displayStyleGroupId", _displayStyleGroupId);
		setNamespacedAttribute(request, "entries", _entries);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "liferay-ddm:template-renderer:";

	private static final String _START_PAGE =
		"/template_renderer/start.jsp";

	private java.lang.String _className = null;
	private java.util.Map<java.lang.String, java.lang.Object> _contextObjects = new java.util.HashMap<java.lang.String, java.lang.Object>();
	private java.lang.String _displayStyle = null;
	private long _displayStyleGroupId = 0;
	private java.util.List<?> _entries = null;

}