/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.web.internal.search;

import com.liferay.journal.constants.JournalPortletKeys;
import com.liferay.journal.model.JournalFeed;
import com.liferay.journal.util.comparator.FeedIDComparator;
import com.liferay.journal.util.comparator.FeedNameComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.OrderByComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Raymond Augé
 */
public class FeedSearch extends SearchContainer<JournalFeed> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-feeds-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("id");
			add("description");
		}
	};

	public FeedSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		super(
			portletRequest, new FeedDisplayTerms(portletRequest),
			new FeedSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		FeedDisplayTerms displayTerms = (FeedDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			FeedDisplayTerms.DESCRIPTION, displayTerms.getDescription());
		iteratorURL.setParameter(
			FeedDisplayTerms.FEED_ID, displayTerms.getFeedId());
		iteratorURL.setParameter(FeedDisplayTerms.NAME, displayTerms.getName());
		iteratorURL.setParameter(
			FeedDisplayTerms.GROUP_ID,
			String.valueOf(displayTerms.getGroupId()));

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			portletRequest, JournalPortletKeys.JOURNAL,
			"feed-search-order-by-col", "name");

		setOrderByCol(orderByCol);

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, JournalPortletKeys.JOURNAL,
			"feed-search-order-by-type", "asc");

		setOrderByComparator(
			_getOrganizationOrderByComparator(orderByCol, orderByType));
		setOrderByType(orderByType);
	}

	private OrderByComparator<JournalFeed> _getOrganizationOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<JournalFeed> orderByComparator = null;

		if (orderByCol.equals("name")) {
			orderByComparator = FeedNameComparator.getInstance(orderByAsc);
		}
		else {
			orderByComparator = FeedIDComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

}