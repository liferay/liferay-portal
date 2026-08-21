/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.users.admin.web.internal.display.context;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryServiceUtil;
import com.liferay.portal.kernel.dao.search.EmptyOnClickRowChecker;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.PortletURLUtil;
import com.liferay.portal.kernel.search.BaseModelSearchResult;
import com.liferay.portal.kernel.util.ParamUtil;

import java.util.Objects;

/**
 * @author Lianne Louie
 */
public class SelectAccountEntriesDisplayContext {

	public static SearchContainer<AccountEntry> create(
			LiferayPortletRequest liferayPortletRequest,
			LiferayPortletResponse liferayPortletResponse)
		throws PortalException {

		SearchContainer<AccountEntry> searchContainer = new SearchContainer<>(
			liferayPortletRequest,
			PortletURLUtil.getCurrent(
				liferayPortletRequest, liferayPortletResponse),
			null, "no-accounts-were-found");

		searchContainer.setId("accountEntries");

		String orderByCol = ParamUtil.getString(
			liferayPortletRequest, "orderByCol", "name");

		searchContainer.setOrderByCol(orderByCol);

		String orderByType = ParamUtil.getString(
			liferayPortletRequest, "orderByType", "asc");

		searchContainer.setOrderByType(orderByType);

		BaseModelSearchResult<AccountEntry> baseModelSearchResult =
			AccountEntryServiceUtil.searchAccountEntries(
				ParamUtil.getString(liferayPortletRequest, "keywords"), null,
				searchContainer.getStart(), searchContainer.getDelta(),
				orderByCol, _isReverseOrder(orderByType));

		searchContainer.setResultsAndTotal(
			baseModelSearchResult::getBaseModels,
			baseModelSearchResult.getLength());

		searchContainer.setRowChecker(
			new EmptyOnClickRowChecker(liferayPortletResponse));

		return searchContainer;
	}

	private static boolean _isReverseOrder(String orderByType) {
		return Objects.equals(orderByType, "desc");
	}

}