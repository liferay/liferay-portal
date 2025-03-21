/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.options.web.internal.display.context;

import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.options.web.internal.portlet.action.helper.ActionHelper;
import com.liferay.commerce.product.options.web.internal.util.CPOptionsPortletUtil;
import com.liferay.commerce.product.service.CPOptionCategoryService;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CPOptionCategoryDisplayContext
	extends BaseCPOptionsDisplayContext<CPOptionCategory> {

	public CPOptionCategoryDisplayContext(
			ActionHelper actionHelper, HttpServletRequest httpServletRequest,
			CPOptionCategoryService cpOptionCategoryService,
			PortletResourcePermission portletResourcePermission)
		throws PortalException {

		super(
			actionHelper, httpServletRequest,
			CPOptionCategory.class.getSimpleName(), portletResourcePermission);

		_cpOptionCategoryService = cpOptionCategoryService;

		setDefaultOrderByCol("priority");
	}

	@Override
	public PortletURL getPortletURL() throws PortalException {
		return PortletURLBuilder.create(
			super.getPortletURL()
		).setMVCRenderCommandName(
			"/cp_specification_options/view_cp_option_categories"
		).buildPortletURL();
	}

	@Override
	public SearchContainer<CPOptionCategory> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		searchContainer = new SearchContainer<>(
			liferayPortletRequest, getPortletURL(), null,
			"no-specification-groups-were-found");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			CPOptionsPortletUtil.getCPOptionCategoryOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());
		searchContainer.setResultsAndTotal(
			_cpOptionCategoryService.searchCPOptionCategories(
				cpRequestHelper.getCompanyId(), getKeywords(),
				searchContainer.getStart(), searchContainer.getEnd(),
				CPOptionsPortletUtil.getCPOptionCategorySort(
					getOrderByCol(), getOrderByType())));
		searchContainer.setRowChecker(getRowChecker());

		return searchContainer;
	}

	private final CPOptionCategoryService _cpOptionCategoryService;

}