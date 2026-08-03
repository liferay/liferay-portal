/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.mcp.server.web.internal.display.context;

import com.liferay.frontend.data.set.model.FDSSortItemBuilder;
import com.liferay.frontend.data.set.model.FDSSortItemList;
import com.liferay.frontend.data.set.model.FDSSortItemListBuilder;
import com.liferay.mcp.server.web.internal.constants.MCPServerFDSNames;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Jorge González
 */
public class ViewProfilesDisplayContext {

	public ViewProfilesDisplayContext(
		HttpServletRequest httpServletRequest,
		LiferayPortletResponse liferayPortletResponse) {

		_httpServletRequest = httpServletRequest;
		_liferayPortletResponse = liferayPortletResponse;
	}

	public String getAPIURL() {
		return "/o/mcp/server-profiles";
	}

	public String getFDSName() {
		return MCPServerFDSNames.PROFILES;
	}

	public FDSSortItemList getFDSSortItemList() {
		return FDSSortItemListBuilder.add(
			FDSSortItemBuilder.setDirection(
				"asc"
			).setKey(
				"name"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "title")
			).build()
		).add(
			FDSSortItemBuilder.setDirection(
				"desc"
			).setKey(
				"dateModified"
			).setLabel(
				LanguageUtil.get(_httpServletRequest, "last-modified")
			).build()
		).build();
	}

	private final HttpServletRequest _httpServletRequest;
	private final LiferayPortletResponse _liferayPortletResponse;

}