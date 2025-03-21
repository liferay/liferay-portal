/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.frontend.taglib.servlet.taglib;

import com.liferay.account.model.AccountEntry;
import com.liferay.commerce.constants.CommerceWebKeys;
import com.liferay.commerce.context.CommerceContext;
import com.liferay.commerce.frontend.taglib.internal.servlet.ServletContextUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.taglib.util.IncludeTag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.jsp.PageContext;

/**
 * @author Marco Leo
 */
public class SearchResultsTag extends IncludeTag {

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		setServletContext(ServletContextUtil.getServletContext());
	}

	@Override
	protected String getPage() {
		return _PAGE;
	}

	@Override
	protected void setAttributes(HttpServletRequest httpServletRequest) {
		CommerceContext commerceContext =
			(CommerceContext)httpServletRequest.getAttribute(
				CommerceWebKeys.COMMERCE_CONTEXT);

		try {
			AccountEntry accountEntry = null;

			if (commerceContext != null) {
				accountEntry = commerceContext.getAccountEntry();
			}

			if (accountEntry != null) {
				httpServletRequest.setAttribute(
					"liferay-commerce-ui:search-results:commerceAccountId",
					accountEntry.getAccountEntryId());
			}
		}
		catch (PortalException portalException) {
			_log.error(portalException);
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		httpServletRequest.setAttribute(
			"liferay-commerce-ui:search-results:groupId",
			themeDisplay.getScopeGroupId());
		httpServletRequest.setAttribute(
			"liferay-commerce-ui:search-results:plid", themeDisplay.getPlid());

		httpServletRequest.setAttribute(
			"liferay-commerce-ui:search-results:searchURL",
			PortalUtil.getPortalURL(httpServletRequest) +
				PortalUtil.getPathContext() + "/o/commerce-ui/search/");
	}

	private static final String _PAGE = "/search_results/page.jsp";

	private static final Log _log = LogFactoryUtil.getLog(
		SearchResultsTag.class);

}