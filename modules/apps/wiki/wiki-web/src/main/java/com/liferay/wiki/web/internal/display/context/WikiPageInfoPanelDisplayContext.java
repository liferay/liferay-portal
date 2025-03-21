/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.web.internal.display.context;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;
import com.liferay.wiki.service.WikiPageLocalServiceUtil;
import com.liferay.wiki.web.internal.display.context.helper.WikiPageInfoPanelRequestHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

/**
 * @author Adolfo Pérez
 */
public class WikiPageInfoPanelDisplayContext {

	public WikiPageInfoPanelDisplayContext(
		HttpServletRequest httpServletRequest) {

		_wikiPageInfoPanelRequestHelper = new WikiPageInfoPanelRequestHelper(
			httpServletRequest);
	}

	public WikiPage getFirstPage() {
		List<WikiPage> pages = _wikiPageInfoPanelRequestHelper.getPages();

		if (pages.isEmpty()) {
			return _wikiPageInfoPanelRequestHelper.getPage();
		}

		return pages.get(0);
	}

	public String getPageRSSURL(WikiPage page) {
		ThemeDisplay themeDisplay =
			_wikiPageInfoPanelRequestHelper.getThemeDisplay();

		return StringBundler.concat(
			themeDisplay.getPathMain(), "/wiki/rss?nodeId=", page.getNodeId(),
			"&title=", page.getTitle());
	}

	public int getPagesCount() {
		WikiNode node = _wikiPageInfoPanelRequestHelper.getCurrentNode();

		return WikiPageLocalServiceUtil.getPagesCount(node.getNodeId(), true);
	}

	public int getSelectedPagesCount() {
		List<?> items = _getSelectedPages();

		return items.size();
	}

	public boolean isMultiplePageSelection() {
		List<?> items = _getSelectedPages();

		if (items.size() > 1) {
			return true;
		}

		return false;
	}

	public boolean isShowSidebarHeader() {
		return _wikiPageInfoPanelRequestHelper.isShowSidebarHeader();
	}

	public boolean isSinglePageSelection() {
		List<WikiPage> pages = _wikiPageInfoPanelRequestHelper.getPages();

		if (pages.size() == 1) {
			return true;
		}

		WikiPage page = _wikiPageInfoPanelRequestHelper.getPage();

		if (page != null) {
			return true;
		}

		return false;
	}

	private List<WikiPage> _getSelectedPages() {
		return _wikiPageInfoPanelRequestHelper.getPages();
	}

	private final WikiPageInfoPanelRequestHelper
		_wikiPageInfoPanelRequestHelper;

}