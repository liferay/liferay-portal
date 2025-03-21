/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.display.context;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.service.JournalFeedLocalServiceUtil;
import com.liferay.journal.web.internal.search.FeedSearch;
import com.liferay.journal.web.internal.search.FeedSearchTerms;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchDisplayStyleUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

/**
 * @author Eudaldo Alonso
 */
public class JournalFeedsDisplayContext {

	public JournalFeedsDisplayContext(
		RenderRequest renderRequest, RenderResponse renderResponse) {

		_renderRequest = renderRequest;
		_renderResponse = renderResponse;
	}

	public String getDisplayStyle() {
		if (_displayStyle != null) {
			return _displayStyle;
		}

		_displayStyle = SearchDisplayStyleUtil.getDisplayStyle(
			_renderRequest, JournalPortletKeys.JOURNAL, "feeds-display-style",
			"list");

		return _displayStyle;
	}

	public SearchContainer<JournalFeed> getFeedsSearchContainer() {
		if (_feedSearch != null) {
			return _feedSearch;
		}

		ThemeDisplay themeDisplay = (ThemeDisplay)_renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		_feedSearch = new FeedSearch(_renderRequest, getPortletURL());

		FeedSearchTerms searchTerms =
			(FeedSearchTerms)_feedSearch.getSearchTerms();

		_feedSearch.setResultsAndTotal(
			() -> JournalFeedLocalServiceUtil.search(
				themeDisplay.getCompanyId(), searchTerms.getGroupId(),
				searchTerms.getKeywords(), _feedSearch.getStart(),
				_feedSearch.getEnd(), _feedSearch.getOrderByComparator()),
			JournalFeedLocalServiceUtil.searchCount(
				themeDisplay.getCompanyId(), searchTerms.getGroupId(),
				searchTerms.getKeywords()));

		_feedSearch.setRowChecker(new EmptyOnClickRowChecker(_renderResponse));

		return _feedSearch;
	}

	public String getOrderByCol() {
		if (Validator.isNotNull(_orderByCol)) {
			return _orderByCol;
		}

		_orderByCol = SearchOrderByUtil.getOrderByCol(
			_renderRequest, JournalPortletKeys.JOURNAL, "feeds-order-by-col",
			"name");

		return _orderByCol;
	}

	public String getOrderByType() {
		if (Validator.isNotNull(_orderByType)) {
			return _orderByType;
		}

		_orderByType = SearchOrderByUtil.getOrderByType(
			_renderRequest, JournalPortletKeys.JOURNAL, "feeds-order-by-type",
			"asc");

		return _orderByType;
	}

	public PortletURL getPortletURL() {
		return PortletURLBuilder.createRenderURL(
			_renderResponse
		).setMVCPath(
			"/view_feeds.jsp"
		).setRedirect(
			getRedirect()
		).setParameter(
			"displayStyle", getDisplayStyle()
		).setParameter(
			"orderByCol", getOrderByCol()
		).setParameter(
			"orderByType", getOrderByType()
		).buildPortletURL();
	}

	public String getRedirect() {
		if (_redirect != null) {
			return _redirect;
		}

		_redirect = ParamUtil.getString(_renderRequest, "redirect");

		return _redirect;
	}

	private String _displayStyle;
	private FeedSearch _feedSearch;
	private String _orderByCol;
	private String _orderByType;
	private String _redirect;
	private final RenderRequest _renderRequest;
	private final RenderResponse _renderResponse;

}