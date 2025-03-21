/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.taglib.clay.servlet.taglib.util;

import com.liferay.portal.kernel.theme.ThemeDisplay;

import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Brian Wing Shun Chan
 */
public class JSPNavigationItemList extends NavigationItemList {

	public JSPNavigationItemList(PageContext pageContext) {
		currentURL = (String)pageContext.findAttribute("currentURL");
		httpServletRequest = (HttpServletRequest)pageContext.getRequest();
		renderResponse = (RenderResponse)pageContext.findAttribute(
			"renderResponse");
		themeDisplay = (ThemeDisplay)pageContext.findAttribute("themeDisplay");

		request = httpServletRequest;
	}

	protected String currentURL;
	protected HttpServletRequest httpServletRequest;
	protected RenderResponse renderResponse;

	/**
	 * @deprecated As of Athanasius (7.3.x), replaced by {@link
	 *             #httpServletRequest}
	 */
	@Deprecated
	protected HttpServletRequest request;

	protected ThemeDisplay themeDisplay;

}