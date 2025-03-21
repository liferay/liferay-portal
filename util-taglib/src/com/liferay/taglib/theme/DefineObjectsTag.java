/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.taglib.theme;

import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.tagext.TagSupport;

/**
 * @author Brian Wing Shun Chan
 */
public class DefineObjectsTag extends TagSupport {

	@Override
	public int doStartTag() {
		HttpServletRequest httpServletRequest =
			(HttpServletRequest)pageContext.getRequest();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return SKIP_BODY;
		}

		if (!GetterUtil.getBoolean(
				pageContext.getAttribute(WebKeys.THEME_DEFINE_OBJECTS), true)) {

			pageContext.setAttribute("themeDisplay", themeDisplay);

			return SKIP_BODY;
		}

		pageContext.setAttribute("colorScheme", themeDisplay.getColorScheme());
		pageContext.setAttribute("company", themeDisplay.getCompany());
		pageContext.setAttribute(
			"contact",
			ProxyUtil.newLazyDelegateProxyInstance(
				Contact.class.getClassLoader(), Contact.class,
				themeDisplay::getContact));

		if (themeDisplay.getLayout() != null) {
			pageContext.setAttribute("layout", themeDisplay.getLayout());
		}

		if (themeDisplay.getLayouts() != null) {
			pageContext.setAttribute("layouts", themeDisplay.getLayouts());
		}

		if (themeDisplay.getLayoutTypePortlet() != null) {
			pageContext.setAttribute(
				"layoutTypePortlet", themeDisplay.getLayoutTypePortlet());
		}

		pageContext.setAttribute("locale", themeDisplay.getLocale());
		pageContext.setAttribute(
			"permissionChecker", themeDisplay.getPermissionChecker());
		pageContext.setAttribute("plid", Long.valueOf(themeDisplay.getPlid()));
		pageContext.setAttribute(
			"portletDisplay", themeDisplay.getPortletDisplay());
		pageContext.setAttribute("realUser", themeDisplay.getRealUser());
		pageContext.setAttribute(
			"scopeGroupId", Long.valueOf(themeDisplay.getScopeGroupId()));
		pageContext.setAttribute("theme", themeDisplay.getTheme());
		pageContext.setAttribute("themeDisplay", themeDisplay);
		pageContext.setAttribute("timeZone", themeDisplay.getTimeZone());
		pageContext.setAttribute("user", themeDisplay.getUser());

		// Deprecated

		pageContext.setAttribute(
			"portletGroupId", themeDisplay.getScopeGroupId());

		return SKIP_BODY;
	}

}