/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.admin.web.internal.dao.search;

import com.liferay.account.admin.web.internal.display.AddressDisplay;
import com.liferay.account.admin.web.internal.security.permission.resource.AccountEntryPermission;
import com.liferay.account.constants.AccountActionKeys;
import com.liferay.account.constants.AccountListTypeConstants;
import com.liferay.account.constants.AccountPortletKeys;
import com.liferay.account.model.AccountEntry;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.model.Address;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.portlet.SearchOrderByUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.search.Sort;
import com.liferay.portal.kernel.search.SortFactoryUtil;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.service.AddressLocalServiceUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * @author Pei-Jung Lan
 */
public class AccountEntryAddressDisplaySearchContainerFactory {

	public static SearchContainer<AddressDisplay> create(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		SearchContainer<AddressDisplay> searchContainer = new SearchContainer(
			liferayPortletRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			null, "no-addresses-were-found");

		searchContainer.setId("accountEntryAddresses");
		searchContainer.setOrderByCol(
			SearchOrderByUtil.getOrderByCol(
				liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				"address-order-by-col", "name"));
		searchContainer.setOrderByType(
			SearchOrderByUtil.getOrderByType(
				liferayPortletRequest, AccountPortletKeys.ACCOUNT_ENTRIES_ADMIN,
				"address-order-by-type", "asc"));

		long accountEntryId = ParamUtil.getLong(
			liferayPortletRequest, "accountEntryId");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)liferayPortletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PermissionChecker permissionChecker =
			themeDisplay.getPermissionChecker();

		if (!AccountEntryPermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.MANAGE_ADDRESSES) &&
			!AccountEntryPermission.contains(
				permissionChecker, accountEntryId,
				AccountActionKeys.VIEW_ADDRESSES)) {

			searchContainer.setResultsAndTotal(Collections::emptyList, 0);

			return searchContainer;
		}

		String keywords = ParamUtil.getString(
			liferayPortletRequest, "keywords");

		String type = ParamUtil.getString(liferayPortletRequest, "type");

		LinkedHashMap<String, Object> params = new LinkedHashMap<>();

		if (Validator.isNotNull(type) && !type.equals("all")) {
			params.put(
				"typeNames",
				new String[] {
					type,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING
				});
		}
		else {
			params.put(
				"typeNames",
				new String[] {
					AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING,
					AccountListTypeConstants.
						ACCOUNT_ENTRY_ADDRESS_TYPE_BILLING_AND_SHIPPING,
					AccountListTypeConstants.ACCOUNT_ENTRY_ADDRESS_TYPE_SHIPPING
				});
		}

		BaseModelSearchResult<Address> baseModelSearchResult =
			AddressLocalServiceUtil.searchAddresses(
				themeDisplay.getCompanyId(), AccountEntry.class.getName(),
				accountEntryId, keywords, params, searchContainer.getStart(),
				searchContainer.getEnd(),
				_getSort(
					searchContainer.getOrderByCol(),
					searchContainer.getOrderByType()));

		searchContainer.setResultsAndTotal(
			() -> TransformUtil.transform(
				baseModelSearchResult.getBaseModels(), AddressDisplay::of),
			baseModelSearchResult.getLength());

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(liferayPortletResponse));

		return searchContainer;
	}

	private static Sort _getSort(String orderByCol, String orderByType) {
		return SortFactoryUtil.create(
			orderByCol, Objects.equals(orderByType, "desc"));
	}

}