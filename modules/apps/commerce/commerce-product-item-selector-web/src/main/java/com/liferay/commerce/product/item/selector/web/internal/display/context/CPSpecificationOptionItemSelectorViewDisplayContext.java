/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.item.selector.web.internal.display.context;

import com.liferay.commerce.product.item.selector.web.internal.util.CPItemSelectorViewUtil;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.CPSpecificationOptionService;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Andrea Di Giorgi
 * @author Alessio Antonio Rendina
 */
public class CPSpecificationOptionItemSelectorViewDisplayContext
	extends BaseCPItemSelectorViewDisplayContext<CPSpecificationOption> {

	public CPSpecificationOptionItemSelectorViewDisplayContext(
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName,
		CPSpecificationOptionService cpSpecificationOptionService) {

		super(
			httpServletRequest, portletURL, itemSelectedEventName,
			"CPSpecificationOptionItemSelectorView");

		_cpSpecificationOptionService = cpSpecificationOptionService;
	}

	@Override
	public SearchContainer<CPSpecificationOption> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		searchContainer = new SearchContainer<>(
			liferayPortletRequest, getPortletURL(), null,
			"no-specifications-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			CPItemSelectorViewUtil.getCPSpecificationOptionOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			_cpSpecificationOptionService.searchCPSpecificationOptions(
				cpRequestHelper.getCompanyId(), null, null, getKeywords(),
				searchContainer.getStart(), searchContainer.getEnd(),
				CPItemSelectorViewUtil.getCPSpecificationOptionSort(
					getOrderByCol(), getOrderByType())));
		searchContainer.setRowChecker(getRowChecker());

		return searchContainer;
	}

	private final CPSpecificationOptionService _cpSpecificationOptionService;

}