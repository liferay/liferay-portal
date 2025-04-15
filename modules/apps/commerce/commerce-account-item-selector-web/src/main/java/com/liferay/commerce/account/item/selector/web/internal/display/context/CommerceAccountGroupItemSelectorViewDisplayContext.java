/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.account.item.selector.web.internal.display.context;

import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.AccountGroupLocalService;
import com.liferay.commerce.account.item.selector.web.internal.display.context.helper.CommerceAccountItemSelectorRequestHelper;
import com.liferay.commerce.account.item.selector.web.internal.search.CommerceAccountGroupItemSelectorChecker;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.StringUtil;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Alessio Antonio Rendina
 */
public class CommerceAccountGroupItemSelectorViewDisplayContext {

	public CommerceAccountGroupItemSelectorViewDisplayContext(
		AccountGroupLocalService accountGroupLocalService,
		HttpServletRequest httpServletRequest, PortletURL portletURL,
		String itemSelectedEventName) {

		_accountGroupLocalService = accountGroupLocalService;
		_portletURL = portletURL;
		_itemSelectedEventName = itemSelectedEventName;

		_commerceAccountItemSelectorRequestHelper =
			new CommerceAccountItemSelectorRequestHelper(httpServletRequest);
	}

	public String getItemSelectedEventName() {
		return _itemSelectedEventName;
	}

	public String getOrderByCol() {
		return ParamUtil.getString(
			_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
			SearchContainer.DEFAULT_ORDER_BY_COL_PARAM, "createDate_sortable");
	}

	public String getOrderByType() {
		return ParamUtil.getString(
			_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
			SearchContainer.DEFAULT_ORDER_BY_TYPE_PARAM, "desc");
	}

	public PortletURL getPortletURL() {
		_portletURL.setParameter(
			"checkedCommerceAccountGroupIds",
			ParamUtil.getString(
				_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
				"checkedCommerceAccountGroupIds"));

		return _portletURL;
	}

	public SearchContainer<AccountGroup> getSearchContainer()
		throws PortalException {

		if (_searchContainer != null) {
			return _searchContainer;
		}

		_searchContainer = new SearchContainer<>(
			_commerceAccountItemSelectorRequestHelper.
				getLiferayPortletRequest(),
			getPortletURL(), null, "there-are-no-account-groups");

		_searchContainer.setOrderByCol(getOrderByCol());
		_searchContainer.setOrderByType(getOrderByType());

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		long accountEntryId = ParamUtil.getLong(
			_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
			"accountEntryId");

		if (accountEntryId > 0) {
			params.put("accountEntryIds", new long[] {accountEntryId});
		}

		long permissionUserId = ParamUtil.getLong(
			_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
			"permissionUserId");

		if (permissionUserId > 0) {
			params.put("permissionUserId", permissionUserId);
		}

		BaseModelSearchResult<AccountGroup> baseModelSearchResult =
			_accountGroupLocalService.searchAccountGroups(
				_commerceAccountItemSelectorRequestHelper.getCompanyId(),
				getKeywords(), params, QueryUtil.ALL_POS, QueryUtil.ALL_POS,
				null);

		_searchContainer.setResultsAndTotal(
			() -> (List<AccountGroup>)baseModelSearchResult.getBaseModels(),
			baseModelSearchResult.getLength());

		_searchContainer.setRowChecker(
			new CommerceAccountGroupItemSelectorChecker(
				_commerceAccountItemSelectorRequestHelper.getRenderResponse(),
				_getCheckedCommerceAccountGroupIds()));

		return _searchContainer;
	}

	protected String getKeywords() {
		if (_keywords != null) {
			return _keywords;
		}

		_keywords = ParamUtil.getString(
			_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
			"keywords");

		return _keywords;
	}

	private long[] _getCheckedCommerceAccountGroupIds() {
		return StringUtil.split(
			ParamUtil.getString(
				_commerceAccountItemSelectorRequestHelper.getRenderRequest(),
				"checkedCommerceAccountGroupIds"),
			0L);
	}

	private final AccountGroupLocalService _accountGroupLocalService;
	private final CommerceAccountItemSelectorRequestHelper
		_commerceAccountItemSelectorRequestHelper;
	private final String _itemSelectedEventName;
	private String _keywords;
	private final PortletURL _portletURL;
	private SearchContainer<AccountGroup> _searchContainer;

}