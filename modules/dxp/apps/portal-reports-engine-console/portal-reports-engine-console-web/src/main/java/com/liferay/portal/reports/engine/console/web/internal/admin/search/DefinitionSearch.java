/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.reports.engine.console.web.internal.admin.search;

import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.reports.engine.console.constants.ReportsEngineConsolePortletKeys;
import com.liferay.portal.reports.engine.console.model.Definition;
import com.liferay.portal.reports.engine.console.util.comparator.DefinitionCreateDateComparator;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rafael Praxedes
 */
public class DefinitionSearch extends SearchContainer<Definition> {

	public static final String EMPTY_RESULTS_MESSAGE =
		"there-are-no-definitions";

	public static List<String> headerNames = new ArrayList<String>() {
		{
			add("definition-name");
			add("source-name");
			add("create-date");
		}
	};

	public DefinitionSearch(
		PortletRequest portletRequest, PortletURL iteratorURL) {

		super(
			portletRequest, new DefinitionDisplayTerms(portletRequest),
			new DefinitionSearchTerms(portletRequest), DEFAULT_CUR_PARAM,
			DEFAULT_DELTA, iteratorURL, headerNames, EMPTY_RESULTS_MESSAGE);

		DefinitionDisplayTerms definitionDisplayTerms =
			(DefinitionDisplayTerms)getDisplayTerms();

		iteratorURL.setParameter(
			DefinitionDisplayTerms.DEFINITION_NAME,
			definitionDisplayTerms.getDefinitionName());
		iteratorURL.setParameter(
			DefinitionDisplayTerms.DESCRIPTION,
			definitionDisplayTerms.getDescription());
		iteratorURL.setParameter(
			DefinitionDisplayTerms.REPORT_NAME,
			definitionDisplayTerms.getReportName());
		iteratorURL.setParameter(
			DefinitionDisplayTerms.SOURCE_ID,
			String.valueOf(definitionDisplayTerms.getSourceId()));

		setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				portletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
				"create-date"));

		String orderByType = SearchOrderByUtil.getOrderByType(
			portletRequest, ReportsEngineConsolePortletKeys.REPORTS_ADMIN,
			"asc");

		setOrderByComparator(_getDefinitionOrderByComparator(orderByType));
		setOrderByType(orderByType);
	}

	private OrderByComparator<Definition> _getDefinitionOrderByComparator(
		String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		return DefinitionCreateDateComparator.getInstance(orderByAsc);
	}

}