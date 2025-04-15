/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.PortletProvider;
import com.liferay.portal.kernel.portlet.PortletProviderUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.trash.model.TrashEntry;
import com.liferay.trash.util.comparator.EntryCreateDateComparator;
import com.liferay.trash.util.comparator.EntryTypeComparator;
import com.liferay.trash.util.comparator.EntryUserNameComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Provides a <code>SearchContainer</code> (in
 * <code>com.liferay.portal.kernel</code>) implementation for
 * <code>TrashEntry</code> (in <code>com.liferay.portal.kernel</code>) objects.
 * The search container is used to show the list of objects using the
 * <code>SearchIteratorTag</code> (in <code>com.liferay.taglib.ui</code>).
 *
 * @author Sergio González
 */
public class EntrySearch extends SearchContainer<TrashEntry> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"the-recycle-bin-is-empty";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("type");
			add("removed-date");
			add("removed-by");
		}
	};
	public static Map<String, String> orderableHeaders = HashMapBuilder.put(
		"name", "name"
	).put(
		"removed-by", "removed-by"
	).put(
		"removed-date", "removed-date"
	).put(
		"type", "type"
	).build();

	public EntrySearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		super(
			portletRequest, new EntryDisplayTerms(portletRequest),
			new EntrySearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		try {
			String portletId = PortletProviderUtil.getPortletId(
				User.class.getName(), PortletProvider.Action.VIEW);

			setOrderableHeaders(orderableHeaders);

			String orderByCol = SearchOrderByUtil.getOrderByCol(
				portletRequest, portletId, "entries-order-by-col",
				"removed-date");

			setOrderByCol(orderByCol);

			String orderByType = SearchOrderByUtil.getOrderByType(
				portletRequest, portletId, "entries-order-by-type", "asc");

			setOrderByComparator(
				_getEntryOrderByComparator(orderByCol, orderByType));
			setOrderByType(orderByType);
		}
		catch (Exception exception) {
			_log.error("Unable to initialize entry search", exception);
		}
	}

	private OrderByComparator<TrashEntry> _getEntryOrderByComparator(
		String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		OrderByComparator<TrashEntry> orderByComparator = null;

		if (orderByCol.equals("removed-by")) {
			orderByComparator = EntryUserNameComparator.getInstance(orderByAsc);
		}
		else if (orderByCol.equals("removed-date")) {
			orderByComparator = EntryCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("type")) {
			orderByComparator = EntryTypeComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

	private static final Log _log = LogFactoryUtil.getLog(EntrySearch.class);

}