/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Entry;
import com.liferay.portal.reports.engine.console.util.comparator.EntryCreateDateComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Praxedes
 */
public class EntrySearch extends SearchContainer<Entry> {

	public static final String EMPTY_RESULTS_MESSAGE = "there-are-no-reports";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("definition-name");
			add("requested-by");
			add("create-date");
		}
	};

	public EntrySearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		super(
			portletRequest, new EntryDisplayTerms(portletRequest),
			new EntrySearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		EntryDisplayTerms entryDisplayTerms =
			(EntryDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			EntryDisplayTerms.DEFINITION_NAME,
			entryDisplayTerms.getDefinitionName());
		iteratorURL.setParameter(
			EntryDisplayTerms.END_DATE_DAY,
			String.valueOf(entryDisplayTerms.getEndDateDay()));
		iteratorURL.setParameter(
			EntryDisplayTerms.END_DATE_MONTH,
			String.valueOf(entryDisplayTerms.getEndDateMonth()));
		iteratorURL.setParameter(
			EntryDisplayTerms.END_DATE_YEAR,
			String.valueOf(entryDisplayTerms.getEndDateYear()));
		iteratorURL.setParameter(
			EntryDisplayTerms.START_DATE_DAY,
			String.valueOf(entryDisplayTerms.getStartDateDay()));
		iteratorURL.setParameter(
			EntryDisplayTerms.START_DATE_MONTH,
			String.valueOf(entryDisplayTerms.getStartDateMonth()));
		iteratorURL.setParameter(
			EntryDisplayTerms.START_DATE_YEAR,
			String.valueOf(entryDisplayTerms.getStartDateYear()));
		iteratorURL.setParameter(
			EntryDisplayTerms.USER_NAME, entryDisplayTerms.getUserName());

		setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
				"create-date"));

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
			"asc");

		setOrderByComparator(_getEntryOrderByComparator(orderByType));
		setOrderByType(orderByType);
	}

	private OrderByComparator<Entry> _getEntryOrderByComparator(
		String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		return EntryCreateDateComparator.getInstance(orderByAsc);
	}

}