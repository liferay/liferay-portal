/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.teams.web.internal.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.comparator.TeamNameComparator;
import com.liferay.site.teams.web.internal.constants.SiteTeamsPortletKeys;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Brian Wing Shun Chan
 */
public class TeamSearch extends SearchContainer<Team> {

	public static final String EMPTY_RESULTS_MESSAGE = "no-teams-were-found";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("name");
			add("description");
		}
	};

	public TeamSearch(PortletRequest portletRequest, PortletURL iteratorURL) {
		super(
			portletRequest, new TeamDisplayTerms(portletRequest),
			new TeamDisplayTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		TeamDisplayTerms displayTerms = (TeamDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			TeamDisplayTerms.DESCRIPTION, displayTerms.getDescription());
		iteratorURL.setParameter(TeamDisplayTerms.NAME, displayTerms.getName());

		String orderByCol = SearchOrderByUtil.getOrderByCol(
			portletRequest, SiteTeamsPortletKeys.SITE_TEAMS, "name");

		setOrderByCol(orderByCol);

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, SiteTeamsPortletKeys.SITE_TEAMS, "asc");

		setOrderByComparator(_getOrderByComparator(orderByCol, orderByType));
		setOrderByType(orderByType);
	}

	private OrderByComparator<Team> _getOrderByComparator(
		String orderByCol, String orderByType) {

		OrderByComparator<Team> orderByComparator = null;

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("name")) {
			orderByComparator = TeamNameComparator.getInstance(orderByAsc);
		}

		return orderByComparator;
	}

}