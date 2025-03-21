/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.servlet.taglib;

import com.liferay.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Eudaldo Alonso
 */
public class SidebarTogglerButtonTag extends IncludeTag {

	public String getCssClass() {
		return _cssClass;
	}

	public String getIcon() {
		return _icon;
	}

	public String getLabel() {
		return _label;
	}

	public String getSidenavId() {
		return _sidenavId;
	}

	public String getTypeMobile() {
		return _typeMobile;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	public void setIcon(String icon) {
		_icon = icon;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSidenavId(String sidenavId) {
		_sidenavId = sidenavId;
	}

	public void setTypeMobile(String typeMobile) {
		_typeMobile = typeMobile;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_icon = null;
		_label = null;
		_sidenavId = null;
		_typeMobile = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		setNamespacedAttribute(httpServletRequest, "cssClass", _cssClass);
		setNamespacedAttribute(httpServletRequest, "icon", _icon);
		setNamespacedAttribute(httpServletRequest, "label", _label);
		setNamespacedAttribute(httpServletRequest, "sidenavId", _sidenavId);
		setNamespacedAttribute(httpServletRequest, "typeMobile", _typeMobile);

		super.setAttributes(httpServletRequest);
	}

	private static final String _ATTRIBUTE_NAMESPACE =
		"liferay-frontend:sidebar-toggler-button:";

	private static final String _PAGE = "/sidebar_toggler_button/page.jsp";

	private String _cssClass;
	private String _icon;
	private String _label;
	private String _sidenavId;
	private String _typeMobile;

}