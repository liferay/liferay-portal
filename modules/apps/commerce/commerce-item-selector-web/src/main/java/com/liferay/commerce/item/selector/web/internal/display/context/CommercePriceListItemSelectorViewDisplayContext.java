/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.item.selector.web.internal.display.context;

import com.liferay.commerce.item.selector.web.internal.search.CommercePriceListItemSelectorChecker;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListService;
import com.liferay.commerce.price.list.util.comparator.CommercePriceListCreateDateComparator;
import com.liferay.commerce.price.list.util.comparator.CommercePriceListDisplayDateComparator;
import com.liferay.commerce.price.list.util.comparator.CommercePriceListPriorityComparator;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public class CommercePriceListItemSelectorViewDisplayContext
	extends BaseCommerceItemSelectorViewDisplayContext<CommercePriceList> {

	public CommercePriceListItemSelectorViewDisplayContext(
		CommercePriceListService commercePriceListService,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName) {

		super(httpServletRequest, portletURL, itemSelectedEventName);

		_commercePriceListService = commercePriceListService;

		setDefaultOrderByCol("create-date");
		setDefaultOrderByType("desc");
	}

	@Override
	public SearchContainer<CommercePriceList> getSearchContainer()
		throws PortalException {

		if (searchContainer != null) {
			return searchContainer;
		}

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		searchContainer = new SearchContainer<>(
			cpRequestHelper.getRenderRequest(), getPortletURL(), null,
			"there-are-no-price-lists");

		searchContainer.setOrderByCol(getOrderByCol());
		searchContainer.setOrderByComparator(
			_getCommercePriceListOrderByComparator(
				getOrderByCol(), getOrderByType()));
		searchContainer.setOrderByType(getOrderByType());

		if (searchContainer.isSearch()) {
			searchContainer.setResultsAndTotal(
				_commercePriceListService.searchCommercePriceLists(
					themeDisplay.getCompanyId(), getKeywords(),
					WorkflowConstants.STATUS_APPROVED,
					searchContainer.getStart(), searchContainer.getEnd(),
					_getCommercePriceListSort(
						getOrderByCol(), getOrderByType())));
		}
		else {
			searchContainer.setResultsAndTotal(
				() -> _commercePriceListService.getCommercePriceLists(
					themeDisplay.getCompanyId(),
					WorkflowConstants.STATUS_APPROVED,
					searchContainer.getStart(), searchContainer.getEnd(),
					searchContainer.getOrderByComparator()),
				_commercePriceListService.getCommercePriceListsCount(
					themeDisplay.getCompanyId(),
					WorkflowConstants.STATUS_APPROVED));
		}

		searchContainer.setRowChecker(
			new CommercePriceListItemSelectorChecker(
				cpRequestHelper.getRenderResponse(),
				_getCheckedCommercePriceListIds()));

		return searchContainer;
	}

	private long[] _getCheckedCommercePriceListIds() {
		return ParamUtil.getLongValues(
			cpRequestHelper.getRenderRequest(), "checkedCommercePriceListIds");
	}

	private OrderByComparator<CommercePriceList>
		_getCommercePriceListOrderByComparator(
			String orderByCol, String orderByType) {

		boolean orderByAsc = false;

		if (orderByType.equals("asc")) {
			orderByAsc = true;
		}

		if (orderByCol.equals("create-date")) {
			return CommercePriceListCreateDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("display-date")) {
			return CommercePriceListDisplayDateComparator.getInstance(
				orderByAsc);
		}
		else if (orderByCol.equals("priority")) {
			return CommercePriceListPriorityComparator.getInstance(orderByAsc);
		}

		return null;
	}

	private Sort _getCommercePriceListSort(
		String orderByCol, String orderByType) {

		boolean reverse = true;

		if (orderByType.equals("asc")) {
			reverse = false;
		}

		if (orderByCol.equals("create-date")) {
			return SortFactoryUtil.create(Field.CREATE_DATE, reverse);
		}
		else if (orderByCol.equals("display-date")) {
			return SortFactoryUtil.create("display-date", reverse);
		}
		else if (orderByCol.equals("priority")) {
			return SortFactoryUtil.create(Field.PRIORITY, reverse);
		}

		return null;
	}

	private final CommercePriceListService _commercePriceListService;

}