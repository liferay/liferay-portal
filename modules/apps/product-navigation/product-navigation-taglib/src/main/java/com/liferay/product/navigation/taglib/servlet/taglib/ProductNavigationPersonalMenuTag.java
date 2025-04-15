/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.taglib.servlet.taglib;

import com.liferay.portal.kernel.model.User;
import com.liferay.product.navigation.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Pei-Jung Lan
 */
public class ProductNavigationPersonalMenuTag extends IncludeTag {

	public String getLabel() {
		return _label;
	}

	public String getSize() {
		return _size;
	}

	public User getUSer() {
		return _user;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public boolean isExpanded() {
		return _expanded;
	}

	/**
	 * @deprecated As of Athanasius (7.3.x), with no direct replacement
	 */
	@Deprecated
	public void setExpanded(boolean expanded) {
		_expanded = expanded;
	}

	public void setLabel(String label) {
		_label = label;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	public void setSize(String size) {
		_size = size;
	}

	public void setUser(User user) {
		_user = user;
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_expanded = false;
		_label = null;
		_size = null;
		_user = null;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		long color = 0;

		if (_user != null) {
			color = _user.getUserId() % 10;
		}

		httpServletRequest.setAttribute(
			"liferay-product-navigation:personal-menu:color", color);
		httpServletRequest.setAttribute(
			"liferay-product-navigation:personal-menu:label", _label);
		httpServletRequest.setAttribute(
			"liferay-product-navigation:personal-menu:size", _size);
		httpServletRequest.setAttribute(
			"liferay-product-navigation:personal-menu:user", _user);
	}

	private static final String _PAGE = "/personal_menu/page.jsp";

	private boolean _expanded;
	private String _label;
	private String _size;
	private User _user;

}