/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.taglib.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntry;
import com.liferay.portal.kernel.servlet.taglib.ui.BreadcrumbEntryContributorUtil;
import com.liferay.site.navigation.taglib.internal.servlet.ServletContextUtil;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eudaldo Alonso
 */
public class BreadcrumbTag extends IncludeTag {

	public List<BreadcrumbEntry> getBreadcrumbEntries() {
		return _breadcrumbEntries;
	}

	public String getCssClass() {
		return _cssClass;
	}

	public void setBreadcrumbEntries(List<BreadcrumbEntry> breadcrumbEntries) {
		_breadcrumbEntries = breadcrumbEntries;
	}

	public void setCssClass(String cssClass) {
		_cssClass = cssClass;
	}

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected void cleanUp() {
		super.cleanUp();

		_breadcrumbEntries = new ArrayList<>();
		_cssClass = StringPool.BLANK;
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		httpServletRequest.setAttribute(
			"liferay-site-navigation:breadcrumb:breadcrumbEntries",
			BreadcrumbEntryContributorUtil.contribute(
				_breadcrumbEntries, httpServletRequest));
		httpServletRequest.setAttribute(
			"liferay-site-navigation:breadcrumb:cssClass", _cssClass);
	}

	private static final String _PAGE = "/breadcrumb/page.jsp";

	private List<BreadcrumbEntry> _breadcrumbEntries = new ArrayList<>();
	private String _cssClass = StringPool.BLANK;

}