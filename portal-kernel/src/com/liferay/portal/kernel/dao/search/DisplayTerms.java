/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.dao.search;

import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletRequest;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Brian Wing Shun Chan
 */
public class DisplayTerms {

	public static final String ADVANCED_SEARCH = "advancedSearch";

	public static final String AND_OPERATOR = "andOperator";

	public static final String KEYWORDS = "keywords";

	public DisplayTerms(HttpServletRequest httpServletRequest) {
		advancedSearch = ParamUtil.getBoolean(
			httpServletRequest, ADVANCED_SEARCH);
		andOperator = ParamUtil.getBoolean(
			httpServletRequest, AND_OPERATOR, true);
		keywords = ParamUtil.getString(httpServletRequest, KEYWORDS);
	}

	public DisplayTerms(PortletRequest portletRequest) {
		advancedSearch = ParamUtil.getBoolean(portletRequest, ADVANCED_SEARCH);
		andOperator = ParamUtil.getBoolean(portletRequest, AND_OPERATOR, true);
		keywords = ParamUtil.getString(portletRequest, KEYWORDS);
	}

	public String getKeywords() {
		return keywords;
	}

	public boolean isAdvancedSearch() {
		return advancedSearch;
	}

	public boolean isAndOperator() {
		return andOperator;
	}

	public boolean isSearch() {
		if (advancedSearch || Validator.isNotNull(keywords)) {
			return true;
		}

		return false;
	}

	public void setAdvancedSearch(boolean advancedSearch) {
		this.advancedSearch = advancedSearch;
	}

	protected boolean advancedSearch;
	protected boolean andOperator;
	protected String keywords;

}