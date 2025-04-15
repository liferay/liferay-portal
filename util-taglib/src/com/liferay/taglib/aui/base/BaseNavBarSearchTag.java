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
public abstract class BaseNavBarSearchTag extends com.liferay.taglib.util.IncludeTag {

	@Override
	public int doStartTag() throws JspException {
		setAttributeNamespace(_ATTRIBUTE_NAMESPACE);

		return super.doStartTag();
	}

	public java.lang.String getCssClass() {
		return _cssClass;
	}

	public java.lang.String getFile() {
		return _file;
	}

	public java.lang.String getId() {
		return _id;
	}

	public com.liferay.portal.kernel.dao.search.SearchContainer<?> getSearchContainer() {
		return _searchContainer;
	}

	public void setCssClass(java.lang.String cssClass) {
		_cssClass = cssClass;
	}

	public void setFile(java.lang.String file) {
		_file = file;
	}

	public void setId(java.lang.String id) {
		_id = id;
	}

	public void setSearchContainer(com.liferay.portal.kernel.dao.search.SearchContainer<?> searchContainer) {
		_searchContainer = searchContainer;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_cssClass = null;
		_file = null;
		_id = null;
		_searchContainer = null;
	}

	@Override
	protected String getEndPage() {
		return _END_PAGE;
	}

	@Override
	protected String getStartPage() {
		return _START_PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest request) {
		setNamespacedAttribute(request, "cssClass", _cssClass);
		setNamespacedAttribute(request, "file", _file);
		setNamespacedAttribute(request, "id", _id);
		setNamespacedAttribute(request, "searchContainer", _searchContainer);
	}

	protected static final String _ATTRIBUTE_NAMESPACE = "aui:nav-bar-search:";

	private static final String _END_PAGE =
		"/html/taglib/aui/nav_bar_search/end.jsp";

	private static final String _START_PAGE =
		"/html/taglib/aui/nav_bar_search/start.jsp";

	private java.lang.String _cssClass = null;
	private java.lang.String _file = null;
	private java.lang.String _id = null;
	private com.liferay.portal.kernel.dao.search.SearchContainer<?> _searchContainer = null;

}