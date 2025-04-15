/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context.helper;

import com.liferay.portal.kernel.display.context.helper.BaseRequestHelper;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.wiki.constants.WikiWebKeys;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.List;

/**
 * @author Roberto Díaz
 */
public class WikiPageInfoPanelRequestHelper extends BaseRequestHelper {

	public WikiPageInfoPanelRequestHelper(
		HttpServletRequest httpServletRequest) {

		super(httpServletRequest);
	}

	public WikiNode getCurrentNode() {
		HttpServletRequest httpServletRequest = getRequest();

		WikiNode node = (WikiNode)httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_NODE);

		if (node == null) {
			WikiPage page = getPage();

			if (page != null) {
				return page.getNode();
			}
		}

		return node;
	}

	public WikiPage getPage() {
		if (_page != null) {
			return _page;
		}

		HttpServletRequest httpServletRequest = getRequest();

		_page = (WikiPage)httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_PAGE);

		return _page;
	}

	public List<WikiPage> getPages() {
		if (_pages != null) {
			return _pages;
		}

		HttpServletRequest httpServletRequest = getRequest();

		_pages = (List<WikiPage>)httpServletRequest.getAttribute(
			WikiWebKeys.WIKI_PAGES);

		if (_pages == null) {
			_pages = Collections.emptyList();
		}

		return _pages;
	}

	public boolean isShowSidebarHeader() {
		HttpServletRequest httpServletRequest = getRequest();

		boolean showSidebarHeader = GetterUtil.getBoolean(
			httpServletRequest.getAttribute(WikiWebKeys.SHOW_SIDEBAR_HEADER));

		return ParamUtil.getBoolean(
			httpServletRequest, "showSidebarHeader", showSidebarHeader);
	}

	private WikiPage _page;
	private List<WikiPage> _pages;

}