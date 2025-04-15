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
public abstract class BaseNavBarTag extends com.liferay.taglib.util.IncludeTag {

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

	public java.lang.String getId() {
		return _id;
	}

	public java.lang.String getMarkupView() {
		return _markupView;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setData(java.lang.Object data) {
		_data = data;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setMarkupView(java.lang.String markupView) {
		_markupView = markupView;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_data = null;
		_id = null;
		_markupView = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "data", _data);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "markupView", _markupView);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:nav-bar:";

	private static final String _PAGE =
		"/html/taglib/aui/nav_bar/page.jsp";

	private java.lang.String _cssClass = null;
	private java.lang.Object _data = null;
	private java.lang.String _id = null;
	private java.lang.String _markupView = null;

}