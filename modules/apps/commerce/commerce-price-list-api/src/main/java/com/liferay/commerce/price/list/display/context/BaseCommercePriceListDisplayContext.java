/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.display.context;

import com.liferay.commerce.price.list.constants.CommercePriceListScreenNavigationConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.portlet.action.CommercePriceListActionHelper;
import com.liferay.commerce.product.display.context.helper.CPRequestHelper;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.RowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.security.permission.resource.ModelResourcePermission;
import com.liferay.portal.kernel.security.permission.resource.PortletResourcePermission;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

/**
 * @author Alessio Antonio Rendina
 */
public abstract class BaseCommercePriceListDisplayContext<T> {

	public BaseCommercePriceListDisplayContext(
		CommercePriceListActionHelper commercePriceListActionHelper,
		ModelResourcePermission<CommercePriceList>
			commercePriceListModelResourcePermission,
		HttpServletRequest httpServletRequest) {

		this.commercePriceListActionHelper = commercePriceListActionHelper;
		this.commercePriceListModelResourcePermission =
			commercePriceListModelResourcePermission;
		this.httpServletRequest = httpServletRequest;

		_cpRequestHelper = new CPRequestHelper(httpServletRequest);

		liferayPortletRequest = _cpRequestHelper.getLiferayPortletRequest();
		liferayPortletResponse = _cpRequestHelper.getLiferayPortletResponse();

		_defaultOrderByCol = "create-date";
		_defaultOrderByType = "desc";
	}

	public CommercePriceList getCommercePriceList() throws PortalException {
		if (_commercePriceList != null) {
			return _commercePriceList;
		}

		_commercePriceList = commercePriceListActionHelper.getCommercePriceList(
			liferayPortletRequest);

		return _commercePriceList;
	}

	public long getCommercePriceListId() throws PortalException {
		CommercePriceList commercePriceList = getCommercePriceList();

		if (commercePriceList == null) {
			return 0;
		}

		return commercePriceList.getCommercePriceListId();
	}

	public String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(httpServletRequest, "keywords");

		return _keywords;
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_COL_PARAM,
			_defaultOrderByCol);
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			httpServletRequest, SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM,
			_defaultOrderByType);
	}

	public PortletURL getPortletURL() throws PortalException {
		PortletURL portletURL = liferayPortletResponse.createRenderURL();

		CommercePriceList commercePriceList = getCommercePriceList();

		if (commercePriceList != null) {
			portletURL.setParameter(
				"mvcRenderCommandName",
				"/commerce_price_list/edit_commerce_price_list");
			portletURL.setParameter(
				"commercePriceListId",
				String.valueOf(getCommercePriceListId()));
		}

		String delta = ParamUtil.getString(httpServletRequest, "delta");

		if (Validator.isNotNull(delta)) {
			portletURL.setParameter("delta", delta);
		}

		String deltaEntry = ParamUtil.getString(
			httpServletRequest, "deltaEntry");

		if (Validator.isNotNull(deltaEntry)) {
			portletURL.setParameter("deltaEntry", deltaEntry);
		}

		String keywords = ParamUtil.getString(httpServletRequest, "keywords");

		if (Validator.isNotNull(keywords)) {
			portletURL.setParameter("keywords", keywords);
		}

		String orderByCol = getOrderByCol();

		if (Validator.isNotNull(orderByCol)) {
			portletURL.setParameter("orderByCol", orderByCol);
		}

		String orderByType = getOrderByType();

		if (Validator.isNotNull(orderByType)) {
			portletURL.setParameter("orderByType", orderByType);
		}

		String redirect = ParamUtil.getString(httpServletRequest, "redirect");

		if (Validator.isNotNull(redirect)) {
			portletURL.setParameter("redirect", redirect);
		}

		return portletURL;
	}

	public RowChecker getRowChecker() {
		if (_rowChecker == null) {
			_rowChecker = new EmptyOnClickRowChecker(liferayPortletResponse);
		}

		return _rowChecker;
	}

	public String getScreenNavigationCategoryKey() {
		return CommercePriceListScreenNavigationConstants.CATEGORY_KEY_DETAILS;
	}

	public abstract SearchContainer<T> getSearchContainer()
		throws PortalException;

	public boolean hasPermission(long commercePriceListId, String actionId)
		throws PortalException {

		return commercePriceListModelResourcePermission.contains(
			_cpRequestHelper.getPermissionChecker(), commercePriceListId,
			actionId);
	}

	public boolean hasPermission(String actionId) {
		PortletResourcePermission portletResourcePermission =
			commercePriceListModelResourcePermission.
				getPortletResourcePermission();

		return portletResourcePermission.contains(
			_cpRequestHelper.getPermissionChecker(), null, actionId);
	}

	public boolean isSearch() {
		return Validator.isNotNull(getKeywords());
	}

	public boolean isShowInfoPanel() {
		return !isSearch();
	}

	public void setDefaultOrderByCol(String defaultOrderByCol) {
		_defaultOrderByCol = defaultOrderByCol;
	}

	public void setDefaultOrderByType(String defaultOrderByType) {
		_defaultOrderByType = defaultOrderByType;
	}

	protected String getNavigation() {
		return ParamUtil.getString(httpServletRequest, "navigation");
	}

	protected final CommercePriceListActionHelper commercePriceListActionHelper;
	protected final ModelResourcePermission<CommercePriceList>
		commercePriceListModelResourcePermission;
	protected final HttpServletRequest httpServletRequest;
	protected final LiferayPortletRequest liferayPortletRequest;
	protected final LiferayPortletResponse liferayPortletResponse;
	protected SearchContainer<T> searchContainer;

	private CommercePriceList _commercePriceList;
	private final CPRequestHelper _cpRequestHelper;
	private String _defaultOrderByCol;
	private String _defaultOrderByType;
	private String _keywords;
	private RowChecker _rowChecker;

}