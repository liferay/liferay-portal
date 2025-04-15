/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.display.context;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.UUID;

/**
 * @author Roberto Díaz
 */
public class BaseMBListDisplayContext
	extends BaseMBDisplayContext<MBListDisplayContext>
	implements MBListDisplayContext {

	public BaseMBListDisplayContext(
		UUID uuid, MBListDisplayContext parentDisplayContext,
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse) {

		super(
			uuid, parentDisplayContext, httpServletRequest,
			httpServletResponse);
	}

	@Override
	public int getCategoryEntriesDelta() {
		return parentDisplayContext.getCategoryEntriesDelta();
	}

	@Override
	public String getEmptyResultsMessage() {
		return parentDisplayContext.getEmptyResultsMessage();
	}

	@Override
	public int getThreadEntriesDelta() {
		return parentDisplayContext.getThreadEntriesDelta();
	}

	@Override
	public boolean isShowMyPosts() {
		return parentDisplayContext.isShowMyPosts();
	}

	@Override
	public boolean isShowRecentPosts() {
		return parentDisplayContext.isShowRecentPosts();
	}

	@Override
	public boolean isShowSearch() {
		return parentDisplayContext.isShowSearch();
	}

	@Override
	public void populateCategoriesResultsAndTotal(
			SearchContainer searchContainer)
		throws PortalException {

		parentDisplayContext.populateCategoriesResultsAndTotal(searchContainer);
	}

	@Override
	public void populateThreadsResultsAndTotal(SearchContainer searchContainer)
		throws PortalException {

		parentDisplayContext.populateThreadsResultsAndTotal(searchContainer);
	}

	@Override
	public void setCategoryEntriesDelta(SearchContainer searchContainer) {
		parentDisplayContext.setCategoryEntriesDelta(searchContainer);
	}

	@Override
	public void setThreadEntriesDelta(SearchContainer searchContainer) {
		parentDisplayContext.setThreadEntriesDelta(searchContainer);
	}

}